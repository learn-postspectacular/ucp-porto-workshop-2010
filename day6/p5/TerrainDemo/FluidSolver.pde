/*
* FluidSolver.java
 * Alexander McKenzie
 * 12 March, 2004
 *
 * optimized by toxi 2006-02-09
 *   reduced nesting level of loops
 *   removed need for I() util method by unrolling index calculations
 *   renamed some variables for better legibility
 */

/**
 * Jos Stam style fluid solver with vorticity confinement
 * and buoyancy force.
 *
 * @author Alexander McKenzie
 * @author Karsten Schmidt
 * @version 1.0
 **/

public class FluidSolver {

  public int NUM_ITER = 10;

  int w, w2, h, h2;
  int size;
  float timeStep;

  public float viscosity = 0;
  public float diffusion = 0.000001f;

  float[] tmp;
  float[] d, dOld;
  float[] u, uOld;
  float[] v, vOld;
  float[] curl;

  /**
   * Set the grid size and timestep.
   **/

  public void setup(int w, int h, float timeStep) {
    this.w = w;
    this.w2=w+2;
    this.h = h;
    this.h2=h+2;
    this.timeStep = timeStep;
    size = w2 * h2;
    d    = new float[size];
    dOld = new float[size];
    u    = new float[size];
    uOld = new float[size];
    v    = new float[size];
    vOld = new float[size];
    curl = new float[size];
    reset();
  }


  /**
   * Reset the datastructures.
   * We use 1d arrays for speed.
   **/

  public void reset() {
    for (int i = 0; i < size; i++) {
      u[i] = uOld[i] = v[i] = vOld[i] = 0.0f;
      d[i] = dOld[i] = curl[i] = 0.0f;
    }
  }


  /**
   * Calculate the buoyancy force as part of the velocity solver.
   * Fbuoy = -a*d*Y + b*(T-Tamb)*Y where Y = (0,1). The constants
   * a and b are positive with appropriate (physically meaningful)
   * units. T is the temperature at the current cell, Tamb is the
   * average temperature of the fluid grid. The density d provides
   * a mass that counteracts the buoyancy force.
   *
   * In this simplified implementation, we say that the tempterature
   * is synonymous with density (since smoke is *hot*) and because
   * there are no other heat sources we can just use the density
   * field instead of a new, seperate temperature field.
   *
   * @param Fbuoy Array to store buoyancy force for each cell.
   **/

  public void buoyancy(float[] Fbuoy) {
    float Tamb = 0;
    float a = 0.000625f;
    float b = 0.025f;

    // sum all temperatures
    for (int i = 1,idx=1+w2,j=1; j<=h;) {
      Tamb += d[idx];
      if (i<w) {
        i++;
        idx++;
      } 
      else {
        i=1;
        j++;
        idx+=3; //=j*w2+1;
      }
    }

    // get average temperature
    Tamb /= (w * h);

    // for each cell compute buoyancy force
    for (int i = 1,idx=1+w2,j=1; j<=h;) {
      Fbuoy[idx] = a * d[idx] -b * (d[idx] - Tamb);
      if (i<w) {
        i++;
        idx++;
      } 
      else {
        i=1;
        j++;
        idx+=3; //j*w2+1;
      }
    }
  }


  /**
   * Calculate the curl at position (i, j) in the fluid grid.
   * Physically this represents the vortex strength at the
   * cell. Computed as follows: w = (del x U) where U is the
   * velocity vector at (i, j).
   *
   * @param i The x index of the cell.
   * @param j The y index of the cell.
   **/

  public float curl(int idx) {
    float du_dy = (u[idx+w2] - u[idx-w2]) * 0.5f;
    float dv_dx = (v[idx+1] - v[idx-1]) * 0.5f;
    return du_dy - dv_dx;
  }


  /**
   * Calculate the vorticity confinement force for each cell
   * in the fluid grid. At a point (i,j), Fvc = N x w where
   * w is the curl at (i,j) and N = del |w| / |del |w||.
   * N is the vector pointing to the vortex center, hence we
   * add force perpendicular to N.
   *
   * @param Fvc_x The array to store the x component of the
   *        vorticity confinement force for each cell.
   * @param Fvc_y The array to store the y component of the
   *        vorticity confinement force for each cell.
   **/

  public void vorticityConfinement(float[] Fvc_x, float[] Fvc_y) {
    float dw_dx, dw_dy;
    float length;
    float v;

    // Calculate magnitude of curl(u,v) for each cell. (|w|)
    for (int i = 1,j=1,idx=i+w2; j<=h;) {
      float c=curl(idx);
      curl[idx] = c>0 ? c : -c;
      if (i<w) {
        i++;
        idx++;
      } 
      else {
        i=1;
        j++;
        idx+=3; //j*w2+1;
      }
    }

    for (int i = 2,j=2,idx=i+w2*j; j<h;) {
      // Find derivative of the magnitude (n = del |w|)
      dw_dx = (curl[idx+1] - curl[idx-1]) * 0.5f;
      dw_dy = (curl[idx+w2] - curl[idx-w2]) * 0.5f;

      // Calculate vector length. (|n|)
      // Add small factor to prevent divide by zeros.
      length = 1f/((float) Math.sqrt(dw_dx * dw_dx + dw_dy * dw_dy) + 0.000001f);

      // N = ( n/|n| )
      dw_dx *= length;
      dw_dy *= length;

      v = curl[idx];

      // N x w
      Fvc_x[idx] = dw_dy * -v;
      Fvc_y[idx] = dw_dx *  v;

      if (i<w-1) {
        i++;
        idx++;
      } 
      else {
        i=2;
        j++;
        idx+=5; //j*w2+2;
      }
    }
  }


  /**
   * The basic velocity solving routine as described by Stam.
   **/

  public void velocitySolver() {

    // add velocity that was input by mouse
    addSource(u, uOld);
    addSource(v, vOld);

    // add in vorticity confinement force
    vorticityConfinement(uOld, vOld);
    addSource(u, uOld);
    addSource(v, vOld);

    // add in buoyancy force
    buoyancy(vOld);
    addSource(v, vOld);

    // swapping arrays for economical mem use
    // and calculating diffusionusion in velocity.
    swapU();
    diffusionuse(0, u, uOld, viscosity);

    swapV();
    diffusionuse(0, v, vOld, viscosity);

    // we create an incompressible field
    // for more effective advection.
    project(u, v, uOld, vOld);

    swapU();
    swapV();

    // self advect velocities
    advect(1, u, uOld, uOld, vOld);
    advect(2, v, vOld, uOld, vOld);

    // make an incompressible field
    project(u, v, uOld, vOld);

    // clear all input velocities for next frame
    for (int i = 0; i < size; i++){
      uOld[i] = vOld[i] = 0;
    }
  }


  /**
   * The basic density solving routine.
   **/

  public void densitySolver() {
    // add density inputted by mouse
    addSource(d, dOld);
    swapD();

    diffusionuse(0, d, dOld, diffusion);
    swapD();

    advect(0, d, dOld, u, v);

    // clear input density array for next frame
    for (int i = 0; i < size; i++) dOld[i] = 0;
  }


  private void addSource(float[] x, float[] x0) {
    for (int i = 0; i < size; i++) {
      x[i] += timeStep * x0[i];
    }
  }


  /**
   * Calculate the input array after advection. We start with an
   * input array from the previous timestep and an and output array.
   * For all grid cells we need to calculate for the next timestep,
   * we trace the cell's center position backwards through the
   * velocity field. Then we interpolate from the grid of the previous
   * timestep and assign this value to the current grid cell.
   *
   * @param b Flag specifying how to handle boundries.
   * @param d Array to store the advected field.
   * @param d0 The array to advect.
   * @param du The x component of the velocity field.
   * @param dv The y component of the velocity field.
   **/

  private void advect(int b, float[] d, float[] d0, float[] du, float[] dv) {
    int i0, j0, i1, j1;
    float x, y, s0, t0, s1, t1, timeStep0;

    timeStep0 = timeStep * w;

    for (int i = 1,j=1,idx=i+w2; j<=h;) {
      // go backwards through velocity field
      x = i - timeStep0 * du[idx];
      y = j - timeStep0 * dv[idx];

      // interpolate results
      if (x > w + 0.5) x = w + 0.5f;
      if (x < 0.5)     x = 0.5f;

      i0 = (int) x;

      if (y > h + 0.5) y = h + 0.5f;
      if (y < 0.5)     y = 0.5f;

      j0 = (int) y;

      s1 = x - i0;
      s0 = 1 - s1;
      t1 = y - j0;
      t0 = 1 - t1;

      int idx0=i0+j0*w2;
      d[idx] = s0 * (t0 * d0[idx0] + t1 * d0[idx0+w2]) + s1 * (t0 * d0[idx0+1] + t1 * d0[idx0+w2+1]);

      if (i<w) {
        i++;
        idx++;
      } 
      else {
        i=1;
        j++;
        idx+=3; //j*w2+1;
      }
    }
    setBoundry(b, d);
  }



  /**
   * Recalculate the input array with diffusionusion effects.
   * Here we consider a stable method of diffusionusion by
   * finding the densities, which when diffusionused backward
   * in time yield the same densities we started with.
   * This is achieved through use of a linear solver to
   * solve the sparse matrix built from this linear system.
   *
   * @param b Flag to specify how boundries should be handled.
   * @param c The array to store the results of the diffusionusion
   * computation.
   * @param c0 The input array on which we should compute
   * diffusionusion.
   * @param diffusion The factor of diffusionusion.
   **/

  private void diffusionuse(int b, float[] c, float[] c0, float diffusion) {
    float a = timeStep * diffusion * w * h;
    linearSolver(b, c, c0, a, 1 + 4 * a);
  }


  /**
   * Use project() to make the velocity a mass conserving,
   * incompressible field. Achieved through a Hodge
   * decomposition. First we calculate the divergence field
   * of our velocity using the mean finite diffusionernce approach,
   * and apply the linear solver to compute the Poisson
   * equation and obtain a "height" field. Now we subtract
   * the gradient of this field to obtain our mass conserving
   * velocity field.
   *
   * @param x The array in which the x component of our final
   * velocity field is stored.
   * @param y The array in which the y component of our final
   * velocity field is stored.
   * @param p A temporary array we can use in the computation.
   * @param div Another temporary array we use to hold the
   * velocity divergence field.
   *
   **/

  void project(float[] x, float[] y, float[] p, float[] div) {
    float fact = -0.5f / w;
    for (int i = 1,idx=1+w2,j=1; j<=h;) {
      div[idx] = (x[idx+1] - x[idx-1] + y[idx+w2] - y[idx-w2]) * fact;
      p[idx] = 0;
      if (i<w) {
        i++;
        idx++;
      } 
      else {
        i=1;
        j++;
        idx+=3;
      }
    }

    setBoundry(0, div);
    setBoundry(0, p);

    linearSolver(0, p, div, 1, 4);

    fact= -0.5f*w;
    for (int i = 1,idx=1+w2,j=1; j<=h;) {
      x[idx] += fact * (p[idx+1] - p[idx-1]);
      y[idx] += fact * (p[idx+w2] - p[idx-w2]);
      if (i<w) {
        i++;
        idx++;
      } 
      else {
        i=1;
        idx+=3;
        j++;
      }
    }

    setBoundry(1, x);
    setBoundry(2, y);
  }


  /**
   * Iterative linear system solver using the Gauss-sidel
   * relaxation technique. Room for much improvement here...
   *
   **/

  void linearSolver(int b, float[] x, float[] x0, float a, float c) {
    c=1f/c;
    for (int k = 0; k < NUM_ITER; k++) {
      for (int i = 1,idx=1+w2,j=1; j<=h;) {
        x[idx] = (a * ( x[idx-1] + x[idx+1] + x[idx-w2] + x[idx+w2]) + x0[idx]) * c;
        if (i<w) {
          i++;
          idx++;
        } 
        else {
          i=1;
          j++;
          idx=j*w2+1;
        }
      }
      setBoundry(b, x);
    }
  }


  // specifies simple boundry conditions.
  private void setBoundry(int b, float[] x) {
    int idn=h*w2;
    for (int i = 1,idi=w2; i <= w; i++) {
      x[idi] = b == 1 ? -x[idi+1] : x[idi+1];
      x[w+1+idi] = b == 1 ? -x[idi+w] : x[idi+w];
      x[i] = b == 2 ? -x[w2+i] : x[w2+i];
      x[i+idn+w2] = b == 2 ? -x[idn+i] : x[idn+i];
      idi+=w2;
    }
    x[0] = 0.5f * (x[1] + x[w2]);
    x[idn+w2] = 0.5f * (x[1+w2+idn] + x[idn]);
    x[w+1] = 0.5f * (x[w] + x[w+1+w2]);
    x[w+1+w2+idn] = 0.5f * (x[w+w2+idn] + x[w+1+idn]);
  }

  // util array swapping methods
  public void swapU(){
    tmp = u;
    u = uOld;
    uOld = tmp;
  }
  public void swapV(){
    tmp = v;
    v = vOld;
    vOld = tmp;
  }
  public void swapD(){
    tmp = d;
    d = dOld;
    dOld = tmp;
  }

  public void decay(float decay) {
    for(int i=0; i<size; i++) {
      u[i]*=decay;
      v[i]*=decay;
      d[i]*=decay;
    }
  }
}

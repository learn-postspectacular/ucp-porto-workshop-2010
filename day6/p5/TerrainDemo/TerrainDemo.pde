import toxi.math.conversion.*;
import toxi.geom.*;
import toxi.math.*;
import toxi.geom.mesh2d.*;
import toxi.util.datatypes.*;
import toxi.util.events.*;
import toxi.geom.mesh.*;
import toxi.math.waves.*;
import toxi.util.*;
import toxi.math.noise.*;

import processing.opengl.*;

import toxi.processing.*;


java.util.List points;
ToxiclibsSupport gfx;

Terrain terrain;
TriangleMesh mesh;

// dimensions of the terrain
int WIDTH=100;
int HEIGHT=100;
float NS=0.04;

Vec3D eyePos, camTarget,camOffset;

FluidSolver fluids;

void setup() {
  size(1000,600,OPENGL);
  hint(ENABLE_OPENGL_4X_SMOOTH);
  gfx=new ToxiclibsSupport(this);
  // define terrain elevation
  float[] elevation=new float[WIDTH*HEIGHT];
  //  for(int y=0; y<HEIGHT; y++) {
  //    for(int x=0; x<WIDTH; x++) {
  //      int index=y*WIDTH+x;
  //      elevation[index]=noise(x*NS,y*NS)*800;
  //    }
  //  }
  terrain=new Terrain(WIDTH,HEIGHT,50);
  //  terrain.setElevation(elevation);
  eyePos=new Vec3D();
  camTarget=new Vec3D();
  camOffset=new Vec3D(0,500,800);
  fluids=new FluidSolver();
  fluids.setup(WIDTH-2, HEIGHT-2, 0.2);
}

void draw() {
  // update fluids
  for(int i=(int)(fluids.size-1.75*fluids.w2); i<(fluids.size-1.25*fluids.w2); i+=40) {
    fluids.d[i]=random(15);
  }
  fluids.decay(0.99);
  fluids.velocitySolver();
  fluids.densitySolver();
  // apply fluids to terrain
  float[] elevation=new float[WIDTH*HEIGHT];
  for(int i=0; i<fluids.d.length; i++) {
    elevation[i]=constrain(fluids.d[i]*100,0,100);
  }
  terrain.setElevation(elevation);
  // get updated terrain mesh
  mesh=terrain.toMesh();
  background(255);
  camTarget.x=(width/2-mouseX)*4;
  camTarget.z=(height/2-mouseY)*4;
  camTarget.y=terrain.getHeightAtPoint(camTarget.x,camTarget.z);
  // helicopter mode
  camOffset.rotateY(0.01);
  // add camera motion damping
  Vec3D newEye=camTarget.add(camOffset);
  newEye.y=terrain.getHeightAtPoint(newEye.x,newEye.z)+100;
  // slightly move towards target pos
  eyePos.interpolateToSelf(newEye,0.05);
  // update view port
  camera(eyePos.x, eyePos.y, eyePos.z, camTarget.x, camTarget.y, camTarget.z, 0, -1, 0);
  // specify light position
  directionalLight(247,180,140, 0,-1000,0);
  noStroke();
  fill(255);
  gfx.mesh(mesh);
  fill(255,0,0);
  gfx.box(new AABB(camTarget,10));
}

void keyPressed() {
  if (key=='s') {
    mesh.saveAsSTL(sketchPath("terrain.stl"));
  }
}



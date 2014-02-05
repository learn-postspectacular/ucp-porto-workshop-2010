import processing.opengl.*;

import toxi.processing.*;

import toxi.geom.*;
import toxi.math.*;
import toxi.geom.mesh2d.*;
import toxi.geom.mesh.*;

ToxiclibsSupport gfx;
Voronoi voronoi;
TriangleMesh mesh;

void setup() {
  size(800,600,OPENGL);
  gfx=new ToxiclibsSupport(this);
  initVoronoi();
}

void draw() {
  background(color(#ffffff));
  translate(width/2,height/2,0);
  rotateX(mouseY*0.01);
  rotateY(mouseX*0.01);
  lights();
  gfx.mesh(mesh);
}

void keyPressed() {
  if (key=='r' || key=='R') {
    initVoronoi();
  }
}

void initVoronoi() {
  voronoi=new Voronoi();
  for(int i=0; i<100; i++) {
    Vec2D p=new Vec2D(random(-0.5,0.5)*width, random(-0.5,0.5)*height);
    voronoi.addPoint(p);
  }
  createVoronoi3D();
}

void createVoronoi3D() {
  mesh=new TriangleMesh();
  for(Iterator i=voronoi.getRegions().iterator(); i.hasNext();) {
    Polygon2D region=(Polygon2D)i.next();
    Vec3D centroid=region.getCentroid().to3DXY().add(0,0,50);
    int num=region.vertices.size();
    for(int j=0; j<num; j++) {
      Vec2D a=region.vertices.get(j);
      Vec2D b;
      if (j==num-1) {
        b=region.vertices.get(0);
      } else {
        b=region.vertices.get(j+1);
      }
      mesh.addFace(centroid,a.to3DXY(),b.to3DXY());
    }
  }
  mesh.saveAsSTL(sketchPath("voronoi.stl"));
}


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

void setup() {
  size(1000,600,OPENGL);
  hint(ENABLE_OPENGL_4X_SMOOTH);
  gfx=new ToxiclibsSupport(this);
  // define terrain elevation
  float[] elevation=new float[WIDTH*HEIGHT];
  for(int y=0; y<HEIGHT; y++) {
    for(int x=0; x<WIDTH; x++) {
      int index=y*WIDTH+x;
      elevation[index]=noise(x*NS,y*NS)*800;
    }
  }
  terrain=new Terrain(WIDTH,HEIGHT,50);
  terrain.setElevation(elevation);
  mesh=terrain.toMesh();
  eyePos=new Vec3D();
  camTarget=new Vec3D();
  camOffset=new Vec3D(0,100,300);
}

void draw() {
  background(255);
  camTarget.x=(width/2-mouseX)*4;
  camTarget.z=(height/2-mouseY)*4;
  camTarget.y=terrain.getHeightAtPoint(camTarget.x,camTarget.z);
  camOffset.rotateY(0.01);
  eyePos=camTarget.add(camOffset);
  eyePos.y=terrain.getHeightAtPoint(eyePos.x,eyePos.z)+100;
  
  camera(eyePos.x, eyePos.y, eyePos.z, camTarget.x, camTarget.y, camTarget.z, 0, -1, 0);
  stroke(0);
  noFill();
  gfx.mesh(mesh);
  fill(255,0,0);
  gfx.box(new AABB(camTarget,10));
}





import toxi.processing.*;

import toxi.geom.*;
import toxi.math.*;
import toxi.geom.mesh2d.*;

ToxiclibsSupport gfx;
Voronoi voronoi;

void setup() {
  size(800,600);
  gfx=new ToxiclibsSupport(this);
  initVoronoi();
}

void draw() {
  background(color(#ffffff));
  for(Iterator i=voronoi.getRegions().iterator(); i.hasNext();) {
    Polygon2D region=(Polygon2D)i.next();
    gfx.polygon2D(region);
  }
}

void keyPressed() {
  if (key=='r' || key=='R') {
    initVoronoi();
  }
}

void mousePressed() {
  voronoi.addPoint(new Vec2D(mouseX,mouseY));
}

void initVoronoi() {
  voronoi=new Voronoi();
  for(int i=0; i<10; i++) {
    Vec2D p=new Vec2D(random(width), random(height));
    voronoi.addPoint(p);
  }
}


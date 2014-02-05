import toxi.geom.*;
import toxi.math.*;
import toxi.processing.*;

InterpolateStrategy tween=new LinearInterpolation();

ToxiclibsSupport gfx;
void setup() {
  size(800,800);
  gfx=new ToxiclibsSupport(this);
}

void draw() {
  background(255);
  noStroke();
  fill(0);
  Vec2D a= new Vec2D(0,5);
  Vec2D h1= new Vec2D(30,5);
  Vec2D h2= new Vec2D(80,10);
  Vec2D b= new Vec2D(100,32);

  float maxDist=sqrt(sq(width/2-0)+sq(height/2));
  for(int y=0; y<height; y+=40) {
    for(int x=0; x<width; x+=40) {
      float t=dist(width/2,height/2,x,y)/maxDist;
      float resolution=bezierPoint(a.y,h1.y,h2.y,b.y,t);
      Polygon2D poly=new Circle(new Vec2D(x,y),20).toPolygon2D(round(resolution));
      gfx.polygon2D(poly);
    }
  }  



}



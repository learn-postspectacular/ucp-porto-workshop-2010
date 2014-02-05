import toxi.math.*;

InterpolateStrategy tween=new DecimatedInterpolation(8);

void setup() {
  size(400,400);
  noStroke();
  // pythagoras: c^2=a^2+b^2
  float maxDist=sqrt(sq(width/2-0)+sq(height/2));
  for(int y=0; y<height; y+=10) {
    for(int x=0; x<width; x+=10) {
      float d=dist(width/2,height/2,x,y)/maxDist;
      float col=255-tween.interpolate(0,255,d);
      fill(col);
      ellipse(x,y,10,10);
    }
  }
}

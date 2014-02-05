import toxi.geom.*;

void setup() {
  size(640,480);
  smooth();
  noFill();
}

void draw() {
  background(255);
  Circle dot=new Circle(new Vec2D(mouseX,mouseY),100);
  Rect bounds=new Rect(width/2-100,height/2-40,200,80);

  stroke(0);
  strokeWeight(1);
  ellipseMode(RADIUS);
  ellipse(dot.x,dot.y,dot.getRadius(),dot.getRadius());
  rect(bounds.x,bounds.y,bounds.width,bounds.height);

  PolygonClipper2D clip=new SutherlandHodgemanClipper(bounds);
  Polygon2D dotPoly=dot.toPolygon2D(30);

  dotPoly=clip.clipPolygon(dotPoly);

  stroke(255,0,0);
  strokeWeight(3);
  
  beginShape(POLYGON);
  for(int i=0; i<dotPoly.vertices.size(); i++) {
    vertex(dotPoly.vertices.get(i).x,dotPoly.vertices.get(i).y);
  }
  endShape(CLOSE);
}

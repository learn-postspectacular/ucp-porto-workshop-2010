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

import processing.pdf.*;

String[] lines;
float maxLineLength;

void setup() {
  size(1280,800);
  lines=loadStrings("lgpl.txt");
  // find max line length for later use to map to hues
  for(int i=0; i<lines.length; i++) {
    lines[i]=lines[i].trim();
    if (lines[i].length()>maxLineLength) {
      maxLineLength=lines[i].length();
    }
  }
  println("max length: "+maxLineLength);
}

void draw() {
  beginRecord(PDF,"lgpl.pdf");
  // use system default font at size=9
  textFont(createFont("Arial",9));
  // switch color mode to Hue-Saturation-Brightness
  // and color values in normalized range
  colorMode(HSB,1);
  background(1);
  translate(width/2,height/2);
  noStroke();
  float lineWidth=(float)width/lines.length;
  for(int i=0; i<lines.length; i++) {
    float x=i*lineWidth;
    float h=lines[i].length()/maxLineLength;
    fill(h,1,1);
    //////// new stuff
    float theta=radians(360.0/lines.length*i);
    Vec2D pos=new Vec2D(30,theta).toCartesian();
    pushMatrix();
    translate(pos.x,pos.y);
    rotate(theta);
    rect(0,0,h*140,lineWidth);
    text(lines[i],150,0);
    popMatrix();
  }
  endRecord();
  exit();
}

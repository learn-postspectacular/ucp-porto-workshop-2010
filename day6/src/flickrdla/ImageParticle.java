/*
 * This file is part of the FlickrDLA project, developed at day #6
 * at the Olhares de Processing workshop at UCP Porto in July 2010.
 * 
 * Copyright 2010 Karsten Schmidt (PostSpectacular Ltd.)
 * 
 * FlickrDLA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FlickrDLA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with FlickrDLA. If not, see <http://www.gnu.org/licenses/>.
 */

package flickrdla;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import toxi.math.MathUtils;

public class ImageParticle extends Vec3D {

	private static final int SIZE = 1;
	
	PImage img;
	float theta;

	public ImageParticle(Vec3D pos, PImage img) {
		super(pos);
		this.img=img;
		this.theta=MathUtils.normalizedRandom()*MathUtils.PI;
	}
	
	public void draw(PGraphics g) {
		// TODO draw the image in space at XYZ
		g.beginShape(PConstants.QUAD);
		g.texture(img);
		Vec3D a=new Vec3D(-SIZE,-SIZE,0).rotateY(theta).addSelf(this);
		Vec3D b=new Vec3D(+SIZE,-SIZE,0).rotateY(theta).addSelf(this);
		Vec3D c=new Vec3D(+SIZE,+SIZE,0).rotateY(theta).addSelf(this);
		Vec3D d=new Vec3D(-SIZE,+SIZE,0).rotateY(theta).addSelf(this);
		g.vertex(a.x,a.y,a.z,0,0);
		g.vertex(b.x,b.y,b.z,img.width,0);
		g.vertex(c.x,c.y,c.z,img.width,img.height);
		g.vertex(d.x,d.y,d.z,0,img.height);
		g.endShape();
	}
}

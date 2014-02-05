/*
 * This file is part of the OlharesCASynth project, developed on day #4 of the
 * Olhares de Processing workshop at UCP Porto in July 2010.
 * 
 * For more information about this example & (similar) workshop(s),
 * please visit: http://learn.postspectacular.com/
 * 
 * Copyright 2010 Karsten Schmidt (PostSpectacular Ltd.)
 * 
 * OlharesCASynth is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * OlharesCASynth is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with OlharesCASynth. If not, see <http://www.gnu.org/licenses/>.
 */

package olhares.day4;

import javax.media.opengl.GL;

import toxi.geom.Vec2D;
import toxi.math.MathUtils;
import toxi.processing.ToxiclibsSupport;

public class LineParticle extends Vec2D {

	/**
	 * movement speed 
	 */
	float speed;
	
	/**
	 * Next target point
	 */
	Vec2D target;
	
	int targetID=1;
	
	/**
	 * Parent line to move along
	 */
	Line parent;
	
	public LineParticle(Line p) {
		super(p.history.get(0));
		this.parent=p;
		this.speed=MathUtils.random(0.5f,4f);
		this.target=parent.history.get(targetID).copy().jitter(2, 2);
	}
	
	public void update() {
		addSelf(target.sub(this).normalizeTo(speed));
		if (distanceTo(target)<speed) {
			targetID++;
			if (targetID>=parent.history.size()) {
				targetID=0;
			}
			target=parent.history.get(targetID).copy().jitter(2, 2);
		}
	}
	
	public void draw(ToxiclibsSupport gfx, GL gl) {
//		gfx.point(this);
		gl.glVertex2f(x, y);
	}
	
	public void reduceTarget() {
		targetID--;
		if (targetID<0) {
			targetID=0;
		}
	}
}

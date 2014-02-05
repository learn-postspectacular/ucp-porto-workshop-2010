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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL;

import processing.core.PImage;

import toxi.color.TColor;
import toxi.geom.Line2D;
import toxi.geom.Line2D.LineIntersection.Type;
import toxi.geom.Vec2D;
import toxi.math.MathUtils;
import toxi.processing.ToxiclibsSupport;
import toxi.util.datatypes.IntegerSet;

public class Line {

	IntegerSet rotations;

	Vec2D pos;
	List<Vec2D> history = Collections.synchronizedList(new LinkedList<Vec2D>());

	float step;

	TColor color;

	int resolution;

	List<LineParticle> particles = Collections
			.synchronizedList(new ArrayList<LineParticle>());

	public Line(Vec2D pos, float step, int res) {
		this.pos = pos;
		this.step = step;
		this.resolution = res;
		int[] rot = new int[resolution];
		for (int i = 0; i < resolution; i++) {
			rot[i] = i;
		}
		rotations = new IntegerSet(rot);
		this.color = TColor.newRandom();
	}

	/**
	 * This function is called whenever a new note is being triggered by the
	 * sequencer.
	 * 
	 * @param maskImg
	 * @return
	 */
	public Line update(PImage maskImg) {
		Vec2D dir;
		boolean doReduce = false;
		synchronized (history) {
			if (history.size() > 20) {
				history.remove(0);
				doReduce = true;
			}
			history.add(pos.copy());
			int numTries = 0;
			boolean isOkay;
			do {
				isOkay = true;
				// direction is a unit vector only!!!
				dir = Vec2D.fromTheta(rotations.pickRandomUnique()
						* MathUtils.TWO_PI / resolution);
				// scale direction to correct length
				dir.scaleSelf(step);
				Vec2D newPos = pos.add(dir);
				// check if newPos is inside white mask area
				int col = maskImg.get((int) newPos.x, (int) newPos.y);
				if (0 == (col & 255)) {
					Line2D checkLine = new Line2D(pos.getFloored(),
							newPos.getFloored());
					if (history.size() > 2) {
						for (int i = history.size() - 2; i > 0; i--) {
							Line2D l = new Line2D(history.get(i).getFloored(),
									history.get(i - 1).getFloored());
							Type type = checkLine.intersectLine(l).getType();
							if (type == Type.INTERSECTING
									|| type == Type.COINCIDENT) {
								isOkay = false;
								break;
							}
						}
					}
				}
				numTries++;
			} while (!isOkay && numTries < 10);
		}
		synchronized (particles) {
			if (doReduce) {
				for (LineParticle p : particles) {
					p.reduceTarget();
				}
			}
			// emit particles
			if (particles.size() < 50 && history.size() > 1) {
				for (int i = 0; i < 5; i++) {
					particles.add(new LineParticle(this));
				}
			}
		}
		// move position along direction vector
		pos.addSelf(dir);
		if (MathUtils.random(1f) < 0.05) {
			Line newLine = new Line(pos.copy(), step, resolution);
			return newLine;
		}
		return null;
	}

	public void draw(ToxiclibsSupport gfx, GL gl) {
//		gfx.getGraphics().stroke(color.toARGB());
		gl.glColor3fv(color.toRGBAArray(null), 0);
		// synchronized (history) {
		// if (history.size() > 0) {
		// gfx.lineStrip2D(history);
		// gfx.line(history.get(history.size() - 1), pos);
		// }
		// }
		synchronized (particles) {
			for (LineParticle p : particles) {
				p.update();
				p.draw(gfx,gl);
			}
		}
	}
}

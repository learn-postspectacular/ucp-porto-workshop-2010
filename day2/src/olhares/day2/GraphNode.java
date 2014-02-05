/*
 * This file is part of the TwitterGraph project, developed at day #2 @ the
 * Olhares de Processing workshop at UCP Porto in July 2010.
 *
 * For more information about this example & (similar) workshop(s),
 * please visit: http://learn.postspectacular.com/
 * 
 * Copyright 2010 Karsten Schmidt (PostSpectacular Ltd.)
 * 
 * TwitterGraph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * TwitterGraph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with TwitterGraph. If not, see <http://www.gnu.org/licenses/>.
 */

package olhares.day2;

import processing.core.PGraphics;
import toxi.geom.Circle;
import toxi.geom.Vec2D;
import toxi.math.waves.AbstractWave;
import toxi.math.waves.SineWave;
import toxi.physics2d.VerletParticle2D;

/**
 * Common shared ancestor for both {@link AuthorNode} and {@link EntryNode}
 * subclasses. This class is defined as "abstract" which means it cannot be
 * directly instantiated and forces both subclasses to implement certain types
 * of functionality specified by the abstract methods in here.
 * 
 */
public abstract class GraphNode extends VerletParticle2D {

	protected float radius;
	protected boolean isRollOver;

	/**
	 * An oscillator used to manipulate the node radius during rollover state
	 */
	protected AbstractWave rolloverRadius = new SineWave(0, 0.05f, 10, 0);

	public GraphNode() {
		// call the constructor of VerletParticle2D with
		// a random position as parameter
		super(Vec2D.randomVector());
	}

	/**
	 * Clears the rollover state for this node and unlocks the particle (if
	 * needed).
	 */
	public void clearRollover() {
		if (isRollOver) {
			unlock();
		}
		isRollOver = false;
	}

	/**
	 * Renders the node using the given PGraphics instance as target
	 * 
	 * @param gfx
	 *            Processing graphics
	 */
	abstract void draw(PGraphics gfx);

	/**
	 * Returns the node's default radius (not necessarily the current one (e.g.
	 * if in rollover state)).
	 * 
	 * @return radius
	 */
	abstract float getRadius();

	/**
	 * Checks if the given point is within the node's perimeter.
	 * 
	 * @param mousePos
	 * @return itself if the point is within, else null.
	 */
	public GraphNode isMouseOver(Vec2D mousePos) {
		// keep a backup of current rollover state
		boolean prev = isRollOver;
		// now update rollover state by constructing a circle
		// around the current node position and check if it contains
		// the given point/mouse position
		isRollOver = new Circle(x, y, getRadius()).containsPoint(mousePos);
		// if the new state differs from previous...
		if (prev != isRollOver) {
			// ...then either lock or unlock the particle
			if (isRollOver) {
				lock();
			} else {
				unlock();
			}
		}
		// if we're in rollover state return the node itself or else null
		return isRollOver ? this : null;
	}
}

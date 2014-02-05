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
import toxi.data.feeds.AtomEntry;
import toxi.geom.Vec2D;

/**
 * Specific implementation of the abstract {@link GraphNode} functionality, used
 * to manage & display a single tweet.
 */
public class EntryNode extends GraphNode {

	/**
	 * A reference to the full feed entry containing the tweet
	 */
	public AtomEntry entry;

	/**
	 * Constructs a new node using the supplied feed entry
	 * 
	 * @param e
	 *            feed entry
	 */
	public EntryNode(AtomEntry e) {
		entry = e;
	}

	@Override
	public void draw(PGraphics gfx) {
		if (isRollOver) {
			gfx.fill(0, 255, 255);
		} else {
			gfx.fill(180, 255, 0, 80);
		}
		// set oscillator offset to desired base radius
		rolloverRadius.offset = 80;
		radius += ((isRollOver ? rolloverRadius.update() : getRadius()) - radius) * 0.25;
		gfx.ellipse(x, y, radius, radius);
		if (isRollOver) {
			gfx.fill(51);
			gfx.text(entry.title, x - 50, y - 50, 100, 100);
		}
	}

	@Override
	float getRadius() {
		return 20;
	}

	@Override
	public GraphNode isMouseOver(Vec2D mousePos) {
		boolean prev = isRollOver;
		GraphNode result = super.isMouseOver(mousePos);
		if (prev != isRollOver && isRollOver) {
			rolloverRadius.phase = 0;
		}
		return result;
	}

}

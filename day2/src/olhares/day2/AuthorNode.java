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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import processing.core.PGraphics;
import toxi.geom.Vec2D;
import toxi.physics2d.VerletMinDistanceSpring2D;
import toxi.physics2d.VerletPhysics2D;
import toxi.physics2d.VerletSpring2D;

public class AuthorNode extends GraphNode {

	/**
	 * Spring strength for keeping authors apart
	 */
	private static final float AUTHOR_CLUSTER_STRENGTH = 0.02f;

	/**
	 * Spring strength between author entry nodes
	 */
	private static final float AUTHOR_ENTRY_STRENGTH = 0.01f;

	public AuthorNode(EntryNode e) {
		name = e.entry.author.name;
		entries = new ArrayList<EntryNode>();
		entries.add(e);
	}

	private String name;
	private List<EntryNode> entries;

	@Override
	public void draw(PGraphics gfx) {
		if (isRollOver) {
			gfx.fill(255, 0, 192);
		} else {
			gfx.fill(255, 150, 0, 80);
		}
		// set oscillator offset to desired base radius
		rolloverRadius.offset = getRadius();
		radius += ((isRollOver ? rolloverRadius.update() : getRadius()) - radius) * 0.25;
		gfx.ellipse(x, y, radius, radius);
		gfx.fill(51);
		gfx.text(name, x - radius, y - radius, radius * 2, radius * 2);
		for (EntryNode e : entries) {
			e.draw(gfx);
		}
	}

	/**
	 * Create spring connections between this author node and all of its
	 * entries, as well as secondary springs between all these entries and
	 * between this author and all other author nodes to ensure a minimum
	 * spacing between them and so avoid overlaps.
	 * 
	 * @param physics
	 *            physics instance to use
	 * @param authors
	 *            iterator over all known authors
	 */
	public void createCluster(VerletPhysics2D physics,
			Iterator<AuthorNode> authors) {
		int num = entries.size();
		// iterator over all entries and connect them to this author node
		for (int i = 0; i < num; i++) {
			EntryNode currNode = entries.get(i);
			physics.addSpring(new VerletSpring2D(this, currNode, getRadius(),
					AUTHOR_ENTRY_STRENGTH));
			// furthermore create minimum distance connections between all
			// entries themselves
			for (int j = i + 1; j < num; j++) {
				physics.addSpring(new VerletMinDistanceSpring2D(currNode,
						entries.get(j), getRadius(), AUTHOR_ENTRY_STRENGTH));
			}
		}
		// finally create minimum distance connections to all other authors
		while (authors.hasNext()) {
			AuthorNode other = authors.next();
			// make sure only to connect other authors, not to itself
			if (this != other) {
				// minimum distance is our radius + radius of other author
				float distance = getRadius() + other.getRadius();
				physics.addSpring(new VerletMinDistanceSpring2D(this, other,
						distance, AUTHOR_CLUSTER_STRENGTH));
			}
		}
	}

	@Override
	public GraphNode isMouseOver(Vec2D mousePos) {
		// before checking the author node itself
		// first iterate and check all entry nodes of this author
		// and give them rollover priority: if mouse is over one of them
		// turn off rollover for author itself (if needed) and return
		// child node as result (making it the currently selected node)
		for (EntryNode e : entries) {
			if (e.isMouseOver(mousePos) != null) {
				clearRollover();
				return e;
			}
		}
		// if no child node matched, just use the standard functionality
		// provided by parent class GraphNode
		return super.isMouseOver(mousePos);
	}

	@Override
	float getRadius() {
		return entries.size() * 50;
	}

	/**
	 * Returns the list of related {@link EntryNode}s created by this author.
	 * 
	 * @return entries
	 */
	public List<EntryNode> getEntries() {
		return entries;
	}
}

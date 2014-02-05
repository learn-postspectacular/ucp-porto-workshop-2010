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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import processing.core.PApplet;
import toxi.data.feeds.AtomEntry;
import toxi.data.feeds.AtomFeed;
import toxi.geom.Rect;
import toxi.geom.Vec2D;
import toxi.physics2d.VerletParticle2D;
import toxi.physics2d.VerletPhysics2D;
import toxi.physics2d.VerletSpring2D;

public class TwitterGraph extends PApplet {

	/**
	 * Search query for the live Twitter search API
	 */
	private static final String TWITTER_QUERY = "%23processing";

	/**
	 * Filename of the local offline Twitter search results feed
	 */
	private static final String LOCAL_TWITTER_FEED = "twitter.xml";

	/**
	 * Flag to indicate if to use live feed or local cache
	 */
	private boolean doUseLiveFeed = false;

	/**
	 * Container element of the search results (loaded as Atom format)
	 */
	private AtomFeed feed;

	/**
	 * A map to allow us to refer to authors using their names
	 */
	private HashMap<String, AuthorNode> authors = new HashMap<String, AuthorNode>();

	/**
	 * Toxiclibs physics engine
	 */
	private VerletPhysics2D physics;

	/**
	 * flag if node connections (springs between particles) should be rendered
	 */
	private boolean doShowLines;

	/**
	 * Reference to node currently (if so) selected/rollover
	 */
	private GraphNode selectedNode;

	/**
	 * Main entry point for application
	 */
	public static void main(String[] args) {
		PApplet.main(new String[] { "olhares.day2.TwitterGraph" });
	}

	public void setup() {
		size(1024, 768, OPENGL);
		ellipseMode(RADIUS);
		textFont(createFont("Serif", 10));
		textAlign(CENTER, CENTER);
		initPhysics();
		initFeed();
	}

	public void draw() {
		// update the physical simulation (causes animation of nodes)
		physics.update();
		background(222, 218, 218);
		// move screen origin to center
		translate(width / 2, height / 2);
		// draw node connections if needed
		if (doShowLines) {
			stroke(0, 50);
			for (VerletSpring2D s : physics.springs) {
				line(s.a.x, s.a.y, s.b.x, s.b.y);
			}
		}
		noStroke();
		// render all authors and their associated message nodes
		for (String name : authors.keySet()) {
			authors.get(name).draw(g);
		}
	}

	/**
	 * Loads the twitter data feed, then analyzes all entries and builds a graph
	 * of the extracted information.
	 */
	private void initFeed() {
		if (doUseLiveFeed) {
			// option 1: execute live twitter search using the Atom feed format
			feed = AtomFeed
					.newFromURL("http://search.twitter.com/search.atom?q="
							+ TWITTER_QUERY);
		} else {
			// option 2: load locally cached search results
			try {
				feed = AtomFeed.newFromStream(new FileInputStream(
						sketchPath(LOCAL_TWITTER_FEED)));
			} catch (FileNotFoundException e) {
				System.err.println("couldn't load xml file: "
						+ LOCAL_TWITTER_FEED);
				System.exit(1);
			}
		}
		// check that feed is valid
		if (feed != null) {
			// clear map possibly containing existing authors
			authors.clear();
			// iterate over all feed entries (individual search results)
			for (AtomEntry e : feed.entries) {
				// create a new node for each entry and add as particle to
				// physics simulation
				EntryNode node = new EntryNode(e);
				physics.addParticle(node);
				// is author still unknown?
				if (!authors.containsKey(e.author.name)) {
					// create a new author node and store in map using his/her
					// name as lookup key
					AuthorNode author = new AuthorNode(node);
					authors.put(e.author.name, author);
					// also add to physics sim
					physics.addParticle(author);
				} else {
					// add more entries to existing author
					AuthorNode author = authors.get(e.author.name);
					author.getEntries().add(node);
				}
			}
			// create clusters for all authors,
			// also connecting them with each other itself
			for (String a : authors.keySet()) {
				authors.get(a).createCluster(physics,
						authors.values().iterator());
			}
		}
	}

	/**
	 * Initializes & configures the physics simulation space
	 */
	private void initPhysics() {
		physics = new VerletPhysics2D();
		physics.setWorldBounds(new Rect(-width / 2 + 100, -height / 2 + 100,
				width - 200, height - 200));
		physics.setDrag(0.9f);
	}

	public void keyPressed() {
		switch (key) {
		case 'l':
			// toggle line drawing (true becomes false, false becomes true)
			doShowLines = !doShowLines;
			break;
		case 'r':
			initPhysics();
			initFeed();
			break;
		}
	}

	/**
	 * Checks all nodes for rollover every time the mouse has been moved. A
	 * reference to a node which is active will be kept for use in
	 * {@link #mouseDragged()}
	 */
	public void mouseMoved() {
		selectedNode = null;
		// translate mouse position into same transformed space as used for
		// rendering nodes.
		Vec2D mousePos = new Vec2D(mouseX, mouseY).sub(width / 2, height / 2);
		// then iterate over all particles (nodes) and check if point is matched
		for (VerletParticle2D p : physics.particles) {
			GraphNode node = (GraphNode) p;
			if (selectedNode == null) {
				GraphNode s = node.isMouseOver(mousePos);
				// keep a reference to selected node
				if (s != null) {
					selectedNode = s;
				}
			} else if (node != selectedNode) {
				node.clearRollover();
			}
		}
	}

	/**
	 * First checks if there's a selected node and if so moves it to the current
	 * mouse position (this works fine, because this node will always be locked
	 * (i.e. excluded from spring forces) when it is in the selected/rollover
	 * state.
	 */
	public void mouseDragged() {
		if (selectedNode != null) {
			Vec2D mousePos = new Vec2D(mouseX, mouseY).sub(width / 2,
					height / 2);
			selectedNode.set(mousePos);
		}
	}
}
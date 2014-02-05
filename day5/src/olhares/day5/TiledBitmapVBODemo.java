/*
 * This file is part of the Olhares de Processing project, developed on
 * day #5 of the Olhares de Processing workshop at UCP Porto in July 2010.
 * 
 * For more information about this example & (similar) workshop(s),
 * please visit: http://learn.postspectacular.com/
 * 
 * Copyright 2010 Karsten Schmidt (PostSpectacular Ltd.)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package olhares.day5;

import javax.media.opengl.GL;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;
import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.geom.mesh.TriangleMesh;
import toxi.processing.ToxiclibsSupport;

public class TiledBitmapVBODemo extends PApplet {

	private static final float FOV = radians(60);
	private static final float NEAR = 1;
	private static final float FAR = 10000;

	public static void main(String[] args) {
		PApplet.main(new String[] { "olhares.day5.TiledBitmapVBODemo" });
	}

	private Tiler tiler;
	private ToxiclibsSupport gfx;
	private VBO vbo;
	private PGraphicsOpenGL pgl;
	private GL gl;

	double fps;
	int numFPS;
	
	public void setup() {
		size(1000, 700, OPENGL);
		hint(ENABLE_OPENGL_4X_SMOOTH);
		tiler = new Tiler((PGraphicsOpenGL) g, 8);
		gfx = new ToxiclibsSupport(this);
		// build grid mesh & VBO
		TriangleMesh grid=new TriangleMesh();
		int num = 120;
		for (int y = 0; y < num; y++) {
			for (int x = 0; x < num; x++) {
				Vec3D pos = new Vec3D(-num / 2 + x + 0.5f, -num * 0.5f + y
						+ 0.5f, 0).scale(200);
				TriangleMesh box = new AABB(new Vec3D(), 50).toMesh();
				box.rotateX(y * 0.2f);
				box.rotateY(x * 0.2f);
				box.translate(pos);
				grid.addMesh(box);
			}
		}
		// get a reference to OpenGL object
		pgl = (PGraphicsOpenGL) g;
		gl = pgl.gl;
		
		// build VBO
		vbo=new VBO(gl,grid.getNumFaces()*3);
		float[] vertices = grid.getMeshAsVertexArray(null, 0, 4);
		vbo.updateVertices(vertices);
		vbo.updateNormals(grid.getVertexNormalsAsArray(null, 0, 4));
		// create colors for each vertex
		float[] colors=new float[vertices.length];
		for(int i=0; i<vertices.length; i=i+4) {
			// red
			colors[i]=random(1f);
			// green
			colors[i+1]=random(1f);
			// blue
			colors[i+2]=random(1f);
			// alpha
			colors[i+3]=1;
		}
		vbo.updateColors(colors);
		println("vertex count: "+vertices.length);
		frameRate(999);
	}

	public void draw() {
		long now=System.nanoTime();
		background(0);
		if (!tiler.isTiling()) {

		}
		camera(/* eye */0, 0, mouseY*10, /* target */0, 0, 0, /* up */0, 1, 0);
		perspective(FOV, (float) width / height, NEAR, FAR);
		tiler.pre();
		lights();
		noStroke();
		scale(0.75f);
		// render the contents of the VBO
		pgl.beginGL();
		vbo.render(GL.GL_TRIANGLES);
		pgl.endGL();
		double duration=(System.nanoTime()-now)*1e-6;
		fps+=duration;
		if (numFPS==100) {
			fps=fps/100;
			println("fps: "+fps);
			fps=0;
			numFPS=0;
		} else {
			numFPS++;
		}
		println("fps: "+frameRate);
		// export tiles (if active)
		tiler.post();
	}

	public void keyPressed() {
		if (key == ' ') {
			tiler.initTiles(FOV, NEAR, FAR);
			tiler.save(sketchPath("export"), "highres", "tiff");
		}
	}
}

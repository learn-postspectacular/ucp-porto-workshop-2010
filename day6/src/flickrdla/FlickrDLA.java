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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GL;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import processing.core.PApplet;
import processing.core.PImage;
import processing.opengl.PGraphicsOpenGL;
import toxi.color.AccessCriteria;
import toxi.color.ColorList;
import toxi.color.NamedColor;
import toxi.color.TColor;
import toxi.geom.Circle;
import toxi.geom.Line3D;
import toxi.geom.Polygon2D;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.SuperEllipsoid;
import toxi.geom.mesh.SurfaceFunction;
import toxi.geom.mesh.SurfaceMeshBuilder;
import toxi.geom.mesh.TriangleMesh;
import toxi.sim.dla.DLA;
import toxi.sim.dla.DLAConfiguration;
import toxi.sim.dla.DLAEventListener;
import toxi.sim.dla.DLAGuideLines;
import toxi.sim.dla.DLASegment;
import toxi.util.FileSequenceDescriptor;
import toxi.util.FileUtils;
import toxi.util.datatypes.TypedProperties;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photos.SearchParameters;

import flickrdla.Histogram.HistEntry;

public class FlickrDLA extends PApplet implements DLAEventListener {

	private static final int MAX_PARTICLES = 10000;

	public static void main(String[] args) {
		PApplet.main(new String[] { "flickrdla.FlickrDLA" });
	}

	/**
	 * Our pool of images to assign to particles
	 */
	private List<PImage> images = new ArrayList<PImage>();

	/**
	 * Our growing list of particles created through DLA
	 */
	private List<ImageParticle> particles = new ArrayList<ImageParticle>();

	private DLA dla;

	private ColorList palette;
	private TriangleMesh sphere;

	private float currZoom = 7.6f;

	private boolean doExport = false;

	private PGraphicsOpenGL pgl;
	private GL gl;

	private ArrayList<VBO> vbos;

	private HashMap<TColor, PImage> colorImageMap = new HashMap<TColor, PImage>();

	public void setup() {
		size(1280, 720, OPENGL);
		pgl = (PGraphicsOpenGL) g;
		gl = pgl.gl;
		// initFlickr();
		initFromCache();
		// initVBOs();
		initColors();
		initSphere();
		initDLA();
		textureMode(IMAGE);
		textFont(createFont("Serif", 10));
	}

	private void initVBOs() {
		int numImages = images.size();
		int vboVertCount = (MAX_PARTICLES / numImages + 1) * 4;
		vbos = new ArrayList<VBO>();
		for (int i = 0; i < numImages; i++) {
			vbos.add(new VBO(gl, vboVertCount));
		}
	}

	private void initSphere() {
		SurfaceFunction functor = new SuperEllipsoid(1, 1);
		SurfaceMeshBuilder b = new SurfaceMeshBuilder(functor);
		sphere = b.createMesh(40, 500);
		println(sphere.getBoundingSphere().radius);
	}

	private void initColors() {
		palette = new ColorList();
		for (PImage img : images) {
			ColorList imgPalette = ColorList.createFromARGBArray(img.pixels,
					20, true);
			// calc histogram for image
			Histogram h = new Histogram(imgPalette);
			List<HistEntry> mainColors = h.compute(0.15f, true);
			colorImageMap.put(mainColors.get(0).col,img);
			palette.add(mainColors.get(0).col);
		}
//		palette.sortByProximityTo(NamedColor.ORANGE, false);
		palette.sortByCriteria(AccessCriteria.BRIGHTNESS, false);

	}

	private void initDLA() {
		DLAGuideLines guides = new DLAGuideLines();
//		initOOIO(guides);
		// one big circle
		Polygon2D o = new Circle(new Vec2D(0, 0), 60).toPolygon2D(300);
		ArrayList<Vec3D> verts3D = new ArrayList<Vec3D>();
		for (Vec2D p : o.vertices) {
			verts3D.add(p.to3DXY());
		}
		guides.addPointList(verts3D);
		
		DLAConfiguration config = new DLAConfiguration();
		config.setCurveSpeed(0.001f);
		dla = new DLA(128);
		dla.setConfig(config);
		dla.setGuidelines(guides);
		dla.addListener(this);
	}

	private void initOOIO(DLAGuideLines guides) {
		// 1st O
		Polygon2D o = new Circle(new Vec2D(-44, 0), 16).toPolygon2D(60);
		ArrayList<Vec3D> verts3D = new ArrayList<Vec3D>();
		for (Vec2D p : o.vertices) {
			verts3D.add(p.to3DXY());
		}
		guides.addPointList(verts3D);
		// 2nd O
		o = new Circle(new Vec2D(-10, 0), 16).toPolygon2D(60);
		verts3D.clear();
		for (Vec2D p : o.vertices) {
			verts3D.add(p.to3DXY());
		}
		guides.addPointList(verts3D);
		// 3rd O
		o = new Circle(new Vec2D(44, 0), 16).toPolygon2D(60);
		verts3D.clear();
		for (Vec2D p : o.vertices) {
			verts3D.add(p.to3DXY());
		}
		guides.addPointList(verts3D);
		// number 1
		Line3D line = new Line3D(new Vec3D(21, -16, 0), new Vec3D(21, 16, 0));
		guides.addPointList(line.splitIntoSegments(null, 1, true));
	}

	private void initFromCache() {
		FileSequenceDescriptor fsd = FileUtils
				.getFileSequenceDescriptorFor("assets/img-00001.jpg");
		for (Iterator<String> i = fsd.iterator(); i.hasNext();) {
			String imgPath = i.next();
			println("loading image: " + imgPath);
			images.add(loadImage(imgPath));
		}
	}

	private void initFlickr() {
		TypedProperties flickrConfig = new TypedProperties();
		flickrConfig.load("config/flickr.properties");
		try {
			Flickr flickr = new Flickr(flickrConfig.getProperty("apiKey"),
					new REST());
			PhotosInterface photosAPI = flickr.getPhotosInterface();
			SearchParameters query = new SearchParameters();
			query.setTags(new String[] { "outono" });
			query.setTagMode("all");
			int pageID = 1;
			while (images.size() < flickrConfig.getInt("results.count", 100)) {
				println("requesting page: " + pageID);
				PhotoList photos = photosAPI.search(query,
						flickrConfig.getInt("results.per.page", 50), pageID);
				for (Object o : photos) {
					Photo photo = (Photo) o;
					String photoURL = photo.getSmallSquareUrl();
					println("loading image: " + photoURL);
					PImage img = loadImage(photoURL);
					images.add(img);
					img.save("assets/img-" + nf(images.size(), 5) + ".jpg");
				}
				pageID++;
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (FlickrException e) {
			e.printStackTrace();
		}
	}

	public void draw() {
		dla.update(10000);
		background(255);
		camera(0, 0, 400, 0, 0, 0, 0, 1, 0);
		if (doExport) {
			rotateY(frameCount * 0.005f);
		} else {
			rotateX(mouseY * 0.01f);
			rotateY(mouseX * 0.01f);
		}
		drawBackground();
		scale(currZoom);
		noStroke();
		for (ImageParticle p : particles) {
			p.draw(g);
		}
		if (doExport) {
			saveFrame("export/dla-####.png");
			if (frameCount > 10000) {
				exit();
			}
		}
	}

	private void drawBackground() {
		int numColors = palette.size();
		noStroke();
		beginShape(TRIANGLES);
		for (TriangleMesh.Face f : sphere.faces) {
			fill(palette.get(f.a.id % numColors).toARGB());
			vertex(f.a.x, f.a.y, f.a.z);
			fill(palette.get(f.b.id % numColors).toARGB());
			vertex(f.b.x, f.b.y, f.b.z);
			fill(palette.get(f.c.id % numColors).toARGB());
			vertex(f.c.x, f.c.y, f.c.z);
		}
		endShape();
	}

	public void keyPressed() {
		if (key == '1') {
			currZoom -= 0.1;
		}
		if (key == '2') {
			currZoom += 0.1;
		}
	}

	private void drawImageGrid() {
		int x = 0, y = 0;
		for (PImage img : images) {
			image(img, x, y);
			x += img.width;
			if (x > width - img.width) {
				x = 0;
				y += img.height;
			}
		}
	}

	public void dlaAllSegmentsProcessed(DLA arg0) {
		println("finished");
		dla.reset();
	}

	public void dlaNewParticleAdded(DLA dla, Vec3D pos) {
		if (random(1f) < 0.005) {
			TColor col=palette.get((int)map(pos.x,-64,64,0,palette.size()));
			PImage img = colorImageMap.get(col);
			ImageParticle particle = new ImageParticle(pos, img);
			particles.add(particle);
			println("new particle at: " + particle);
		}
	}

	public void dlaSegmentSwitched(DLA arg0, DLASegment arg1) {
		println("next segment");
	}
}

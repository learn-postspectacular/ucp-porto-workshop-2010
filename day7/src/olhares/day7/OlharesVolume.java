/*
 * This file is part of the OlharesVolumeIdentity project, developed at
 * day #7 of the Olhares de Processing workshop at UCP Porto in July 2010.
 * 
 * Copyright 2010 Karsten Schmidt (PostSpectacular Ltd.)
 * 
 * OlharesVolumeIdentity is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * OlharesVolumeIdentity is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with OlharesVolumeIdentity. If not, see <http://www.gnu.org/licenses/>.
 */

package olhares.day7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GL;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;
import toxi.color.TColor;
import toxi.geom.Line3D;
import toxi.geom.Matrix4x4;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.TriangleMesh;
import toxi.math.waves.AbstractWave;
import toxi.math.waves.SineWave;
import toxi.processing.ToxiclibsSupport;
import toxi.util.datatypes.TypedProperties;
import toxi.volume.IsoSurface;
import toxi.volume.RoundBrush;
import toxi.volume.VolumetricBrush;
import toxi.volume.VolumetricSpace;

public class OlharesVolume extends PApplet {

	private static final Vec3D VOLUME_SIZE = new Vec3D(1600, 900, 300);

	public static final float SCALE = VOLUME_SIZE.x / 128;

	private static final int DIMX = 64;
	private static final int DIMY = 64;
	private static final int DIMZ = 18;

	public static TypedProperties CONFIG;

	public static void main(String[] args) {
		CONFIG = new TypedProperties();
		CONFIG.load("config.properties");
		ArrayList<String> a = new ArrayList<String>();
		if (CONFIG.getBoolean("app.fullscreen", false)) {
			a.add("--present");
		}
		if (CONFIG.getProperty("app.bordercol") != null) {
			a.add("--bgcolor=" + CONFIG.getProperty("app.bordercol"));
		}
		a.add("olhares.day7.OlharesVolume");
		PApplet.main(a.toArray(new String[0]));
	}

	private VolumetricSpace volume;
	private IsoSurface surface;
	private VolumetricBrush brush;
	private TriangleMesh mesh;
	private ToxiclibsSupport gfx;
	private ArrayList<LogoShape> shapes;

	private List<UserPoint> userPoints = new ArrayList<UserPoint>();

	private AbstractWave camModX = new SineWave(0, 0.01f, PI * 0.25f, 0);
	private AbstractWave camModY = new SineWave(0, 0.0172f, PI * 0.1f, 0);

	private QuaternionCam cam = new QuaternionCam();
	private PGraphicsOpenGL pgl;
	private GL gl;
	private Matrix4x4 orientation = new Matrix4x4();

	private List<UserPoint> sessionPoints = new ArrayList<UserPoint>();

	private boolean isProjection;

	private PointLoader loader;

	public void setup() {
		size(CONFIG.getInt("app.width", 1280),
				CONFIG.getInt("app.height", 720), OPENGL);
		isProjection = CONFIG.getBoolean("app.mode.projection", false);
		initOOIO();
		initVolume();
		initPointLoader();
		gfx = new ToxiclibsSupport(this);
		pgl = (PGraphicsOpenGL) g;
		gl = pgl.gl;
	}

	private void initPointLoader() {
		if (isProjection) {
			loader = new PointLoader(this, CONFIG.getProperty("web.poll.url"),
					CONFIG.getInt("web.poll.interval", 10000));
			loader.start();
		}
	}

	private void initVolume() {
		volume = new VolumetricSpace(VOLUME_SIZE, DIMX, DIMY, DIMZ);
		surface = new IsoSurface(volume);
		brush = new RoundBrush(volume, 100);
	}

	public void updateVolume() {
		volume.clear();
		for (LogoShape s : shapes) {
			s.update();
			brush.setSize(s.getSize());
			for (Vec3D p : s.getPoints()) {
				Vec3D q = p.copy();
				q.x += s.getOffset();
				brush.drawAtAbsolutePos(q, 0.5f);
			}

		}
		brush.setSize(100);
		for (UserPoint p : userPoints) {
			brush.drawAtAbsolutePos(p, p.brushValue);
		}
		volume.closeSides();
		surface.reset();
		mesh = surface.computeSurfaceMesh(mesh, 0.25f);
	}

	private void initOOIO() {
		shapes = new ArrayList<LogoShape>();
		shapes.add(new CircleShape(new Vec2D(-44, 0).scale(SCALE), 16 * SCALE,
				0));
		shapes.add(new CircleShape(new Vec2D(-10, 0).scale(SCALE), 16 * SCALE,
				-40));
		shapes.add(new CircleShape(new Vec2D(44, 0).scale(SCALE), 16 * SCALE, 0));
		shapes.add(new LineShape(new Line3D(new Vec3D(21, -16, 2).scale(SCALE),
				new Vec3D(21, 16, 2).scale(SCALE))));
	}

	public void draw() {
		if (isProjection) {
			noCursor();
		}
		background(TColor.newARGB(CONFIG.getHexInt("app.bgcol", 0)).toARGB());
		cam.perspective(90, (float) width / height, 1, 1000);
		cam.lookAt(new Vec3D(0, 0, 1000), new Vec3D(0, 0, 0), Vec3D.Y_AXIS);
		cam.update(1, true);
		orientation.identity();
		orientation.rotateX(camModX.update());
		orientation.rotateY(camModY.update());
		camera(0, 0, 1000, 0, 0, 0, 0, 1, 0);
		pgl.beginGL();
		if (CONFIG.getBoolean("render.isAdditve", false)) {
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
			fill(128);
		} else {
			fill(255);
		}
		pgl.endGL();
		lights();
		noStroke();
		updateScribbles();
		updateVolume();
		mesh.transform(orientation);
		gfx.mesh(mesh);
	}

	private void updateScribbles() {
		if (isProjection) {
			if (loader.getQueue().peek() != null) {
				userPoints.add(loader.getQueue().poll());
			}
		}
		for (Iterator<UserPoint> i = userPoints.iterator(); i.hasNext();) {
			UserPoint p = i.next();
			if (!p.update()) {
				i.remove();
			}
		}
	}

	public void mousePressed() {
		if (!isProjection) {
			sessionPoints.clear();
			addUserPoint();
		}
	}

	private void addUserPoint() {
		int w2 = width / 2;
		int h2 = height / 2;
		float mx = map(mouseX - w2, -w2, w2, -VOLUME_SIZE.x, VOLUME_SIZE.x);
		float my = map(mouseY - h2, -h2, h2, -VOLUME_SIZE.y, VOLUME_SIZE.y);
		Vec3D mousePos = new Vec3D(mx, my, 0);
		cam.viewMatrix.getInverted().applyToSelf(mousePos);
		UserPoint p = new UserPoint(mousePos, 0.5f);
		userPoints.add(p);
		sessionPoints.add(p);
	}

	public void mouseDragged() {
		if (!isProjection) {
			addUserPoint();
		}
	}

	public void mouseReleased() {
		if (!isProjection) {
			sendSessionPoints();
		}
	}

	private void sendSessionPoints() {
		try {
			String data = "num=" + sessionPoints.size() + "&";
			for (int i = 0; i < sessionPoints.size(); i++) {
				UserPoint p = sessionPoints.get(i);
				data += String.format("x%d=%f&y%d=%f&v%d=%f&", i, p.x, i, p.y,
						i, p.brushValue);
			}
			URL url = new URL(CONFIG.getProperty("web.submit.url"));
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(data);
			wr.flush();
			println("sent " + sessionPoints.size() + " points to " + url);
			// waiting for the PHP response
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				System.out.println(line);
			}
			wr.close();
			rd.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

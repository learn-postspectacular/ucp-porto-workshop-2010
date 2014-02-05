/*
 * This file is part of the OlharesPoster project, developed at
 * day #7 of the Olhares de Processing workshop at UCP Porto in July 2010.
 * 
 * Copyright 2010 Karsten Schmidt (PostSpectacular Ltd.)
 * 
 * OlharesPoster is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * OlharesPoster is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with OlharesPoster. If not, see <http://www.gnu.org/licenses/>.
 */

package olhares;

import javax.media.opengl.GL;

import processing.core.PApplet;
import processing.core.PImage;
import processing.opengl.PGraphicsOpenGL;
import toxi.color.NamedColor;
import toxi.color.TColor;
import toxi.geom.Vec3D;
import toxi.geom.mesh.TriangleMesh;
import toxi.math.ScaleMap;
import toxi.processing.ToxiclibsSupport;
import toxi.volume.IsoSurface;
import toxi.volume.RoundBrush;
import toxi.volume.VolumetricBrush;
import toxi.volume.VolumetricSpace;

public class OlharesPosterApp extends PApplet {

    private static final String IMG_ID = "assets/olhares256a";

    public static void main(String[] args) {
        PApplet.main(new String[] { "olhares.OlharesPosterApp" });
    }

    private ToxiclibsSupport gfx;
    private VolumetricSpace volume;
    private TriangleMesh mesh;
    private PGraphicsOpenGL pgl;
    private GL gl;
    private VBO vbo;

    public void draw() {
        background(128);
        translate(width / 2, height / 2, 0);
        float rotX = mouseY * 0.01f;
        float rotY = mouseX * 0.01f;
        rotateX(rotX);
        rotateY(rotY);
        scale(2);
        noStroke();
        pgl.beginGL();
        initLights();
        vbo.render(GL.GL_TRIANGLES);
        pgl.endGL();
    }

    public void initLights() {
        Vec3D l =
                new Vec3D(200, 3000, 1000).rotateZ(frameCount * 0.02f)
                        .normalize();
        gl.glEnable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_LIGHT0);
        gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT,
                NamedColor.NAVY.toRGBAArray(null), 0);
        // set different light colors
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT,
                NamedColor.NAVY.toRGBAArray(null), 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, new float[] { l.x, l.y, l.z,
                0 }, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, TColor.newGray(1)
                .toRGBAArray(null), 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, TColor.newGray(1)
                .toRGBAArray(null), 0);
        gl.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, 2);
        gl.glLightf(GL.GL_LIGHT0, GL.GL_CONSTANT_ATTENUATION, 1);
        gl.glEnable(GL.GL_COLOR_MATERIAL);
        gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT_AND_DIFFUSE);
        gl.glShadeModel(GL.GL_SMOOTH);
    }

    private void initVolume(PImage img, TerrainFunction func, int scale) {
        Vec3D DIM =
                new Vec3D(img.width * scale, img.height * scale, 32 * scale);
        volume = new VolumetricSpace(DIM, img.width, img.height, 32);
        VolumetricBrush brush = new RoundBrush(volume, scale);
        ScaleMap scaleMap = new ScaleMap(0, 1.0, 0, 32);
        for (int y = 0; y < img.height; y += 1) {
            for (int x = 0; x < img.width; x += 1) {
                double col = (img.pixels[y * img.width + x] & 0xff) / 255.0;
                if (col > 0) {
                    if (col > 0.722) {
                        col = 0.92;
                    }
                } else {
                    // col = (terrain.pixels[y * terrain.width + x] & 0xff) /
                    // 255.0 * 1.05;
                    col = func.getTerrainAt(x, y) * 0.66;
                }
                double maxZ = scaleMap.getClippedValueFor(col);
                for (int z = 0; z < maxZ; z++) {
                    brush.drawAtGridPos(x, y, z, 0.5f);
                }
            }
        }
        volume.closeSides();
    }

    public void setup() {
        size(1024, 576, OPENGL);
        gfx = new ToxiclibsSupport(this);
        PImage img = loadImage(IMG_ID + ".png");
        TerrainFunction func = new NoiseTerrain();
        initVolume(img, func, 2);
        IsoSurface surface = new IsoSurface(volume);
        mesh = surface.computeSurfaceMesh(null, 0.1f);
        mesh.computeVertexNormals();
        // mesh.saveAsSTL(IMG_ID + ".stl", true);

        pgl = (PGraphicsOpenGL) g;
        gl = pgl.gl;

        // build VBO
        vbo = new VBO(gl, mesh.getNumFaces() * 3);
        float[] vertices = mesh.getMeshAsVertexArray(null, 0, 4);
        vbo.updateVertices(vertices);
        vbo.updateNormals(mesh.getVertexNormalsAsArray(null, 0, 4));
        float[] colors = new float[vertices.length];
        for (int i = 0; i < vertices.length; i += 4) {
            // red
            colors[i] = vertices[i] / 256 + 0.5f;
            // green
            colors[i + 1] = vertices[i + 1] / 256 + 0.5f;
            // blue
            colors[i + 2] = vertices[i + 2] / 32;
            // alpha
            colors[i + 3] = 1;
        }
        vbo.updateColors(colors);
        println("setup done");
    }
}

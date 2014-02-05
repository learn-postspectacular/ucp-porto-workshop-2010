/*
 * This file is part of the V&A Decode Identity (DecodeIdent).
 * 
 * Copyright 2009 Karsten Schmidt (PostSpectacular Ltd.)
 * 
 * DecodeIdent is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * DecodeIdent is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DecodeIdent. If not, see <http://www.gnu.org/licenses/>.
 */

package flickrdla;

import javax.media.opengl.GL;

import com.sun.opengl.util.BufferUtil;


/**
 * A reusable, barebones wrapper for OpenGL Vertex Buffer Objects. Only handles
 * actual vertex and normal buffers and assumes they can be dynamically
 * modified/updated.
 */
public class VBO {

	final int STRIDE = BufferUtil.SIZEOF_FLOAT * 4;

	int numVertices;
	int[] vertID = new int[1];
	int[] normID = new int[1];
	int[] colorID = new int[1];
	int[] uvID = new int[1];

	private GL gl;

	VBO(GL gl, int num) {
		this.gl = gl;
		numVertices = num;
		initBuffer(vertID);
		initBuffer(normID);
		initBuffer(colorID);
		initBuffer(uvID);
	}

	void initBuffer(int[] bufferID) {
		gl.glGenBuffersARB(1, bufferID, 0);
		gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, bufferID[0]);
		gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, numVertices * STRIDE, null,
				GL.GL_DYNAMIC_DRAW_ARB);
		gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, 0);
	}

	void updateBuffer(int id, float[] data) {
		gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, id);
		gl.glMapBufferARB(GL.GL_ARRAY_BUFFER_ARB, GL.GL_WRITE_ONLY)
				.asFloatBuffer().put(data);
		gl.glUnmapBufferARB(GL.GL_ARRAY_BUFFER_ARB);
		gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, 0);
	}

	void updateColors(float[] colors) {
		updateBuffer(colorID[0], colors);
	}

	void updateVertices(float[] vertices) {
		updateBuffer(vertID[0], vertices);
	}

	void updateNormals(float[] normals) {
		updateBuffer(normID[0], normals);
	}

	void render(int shapeID) {
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		// check if we need to use normals?
		if (normID != null) {
			gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
			gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, normID[0]);
			gl.glNormalPointer(GL.GL_FLOAT, STRIDE, 0);
		}
		// check if we need to use colors?
		if (colorID != null) {
			gl.glEnableClientState(GL.GL_COLOR_ARRAY);
			gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, colorID[0]);
			gl.glColorPointer(4, GL.GL_FLOAT, STRIDE, 0);
		}
		// check if we need to use colors?
		if (uvID != null) {
			gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
			gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, uvID[0]);
			gl.glTexCoordPointer(4, GL.GL_FLOAT, STRIDE, 0);
		}
		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, vertID[0]);
		gl.glVertexPointer(3, GL.GL_FLOAT, STRIDE, 0);
		gl.glDrawArrays(shapeID, 0, numVertices);
		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
		if (normID != null) {
			gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
		}
		if (colorID != null) {
			gl.glDisableClientState(GL.GL_COLOR_ARRAY);
		}
		if (uvID != null) {
			gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
		}
	}

	void cleanup() {
		gl.glDeleteBuffers(1, vertID, 0);
		gl.glDeleteBuffers(1, normID, 0);
		gl.glDeleteBuffers(1, colorID, 0);
		gl.glDeleteBuffers(1, uvID, 0);
	}
}

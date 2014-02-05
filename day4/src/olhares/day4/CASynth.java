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
import java.util.List;

import javax.media.opengl.GL;

import processing.core.PApplet;
import processing.core.PImage;
import processing.opengl.PGraphicsOpenGL;
import toxi.audio.AudioBuffer;
import toxi.audio.AudioSource;
import toxi.audio.JOALUtil;
import toxi.audio.MultiTimbralManager;
import toxi.geom.Vec2D;
import toxi.music.QuantizedTimeHandler;
import toxi.music.QuantizedTimeProvider;
import toxi.music.scale.GenericScale;
import toxi.processing.ToxiclibsSupport;
import toxi.sim.automata.CAMatrix;
import toxi.sim.automata.CAWolfram1D;
import toxi.util.datatypes.IntegerSet;

public class CASynth extends PApplet implements QuantizedTimeHandler {

	public static void main(String[] args) {
		PApplet.main(new String[] { "--present", "olhares.day4.CASynth" });
	}

	/**
	 * The main audio system
	 */
	private JOALUtil audio;
	private MultiTimbralManager manager;
	private QuantizedTimeProvider sequencer;
	private GenericScale scale;

	private List<AudioBuffer> buffers = new ArrayList<AudioBuffer>();
	private AudioSource source;

	private CAMatrix ca;
	private CAWolfram1D wolfram;

	private int barCount;
	private IntegerSet transpose;

	private List<Line> lines = Collections
			.synchronizedList(new ArrayList<Line>());

	private ToxiclibsSupport gfx;
	private PImage maskImg;

	public void setup() {
		size(1024, 768, OPENGL);
		maskImg = loadImage("mask.png");
		textFont(createFont("SansSerif", 60));
		// put the first line into the list as seed
		// start point depends on image loaded!!!
		lines.add(new Line(new Vec2D(286, 384), 10, 5));
		lines.add(new Line(new Vec2D(758, 384), 10, 5));
		gfx = new ToxiclibsSupport(this);
		// audio initialization
		audio = JOALUtil.getInstance();
		audio.init();
		// load samples from text into list of audio buffers
		String[] samples = loadStrings("samples.txt");
		for (String s : samples) {
			if (s.length() > 0) {
				buffers.add(audio.loadBuffer(s));
			}
		}
		manager = new MultiTimbralManager(audio, 50);
		scale = new GenericScale("porto", new byte[] { 0, 2, 4, 7, 9, 11, 12,
				14 });
		transpose = new IntegerSet(new int[] { 0, -2 });

		// setup 1D matrix for a single octave
		ca = new CAMatrix(16);
		ca.setStateAt(4, 0, 1);
		wolfram = new CAWolfram1D(1, 8, true);
		wolfram.setRuleID(110);
		ca.setRule(wolfram);
		// create heart beat for CA to evolve
		sequencer = new QuantizedTimeProvider(240, 4, 2);
		sequencer.getDispatcher().addListener(this);
		sequencer.start();
	}

	public void draw() {
		background(0);
		text("bars: " + barCount, 10, height - 10);
		stroke(255, 255, 0);
		List<Line> drawLines;
		synchronized (lines) {
			drawLines = new ArrayList<Line>(lines);
		}
		PGraphicsOpenGL pgl = (PGraphicsOpenGL) g;
		GL gl = pgl.gl;
		// tell processing to enter OpenGL mode
		pgl.beginGL();
		// start a shape of points
		// basically, interpret all following vertices
		// as individual points only
		gl.glBegin(GL.GL_POINTS);
		for (Line l : drawLines) {
			l.draw(gfx, gl);
		}
		// end the point shape
		gl.glEnd();
		// end OpenGL mode in Processing
		pgl.endGL();
	}

	/**
	 * Event handler called by the sequencer to notify us of a new bar. We only
	 * pick up on 4-bar boundaries to modify the CA matrix and possibly trigger
	 * a key change in the music.
	 */
	public void handleBar(int bar) {
		barCount = bar;
		if ((bar % 4) == 0) {
			ca.addNoise(0.2f);
			wolfram.setStateCount((int) random(2, 10));
			if (random(1) < 0.5) {
				transpose.pickRandomUnique();
			}
		}
	}

	/**
	 * Event handler called by the sequencer to notify us of a single beat. We
	 * use this timing basis to update the CA, play new notes and update the
	 * growth of our lines.
	 */
	public void handleBeat(int arg0) {
		ca.update();
		int[] matrix = ca.getMatrix();
		for (int i = 0; i < scale.tones.length; i++) {
			if (scale.tones[i] < matrix.length
					&& matrix[scale.tones[i] % matrix.length] == 1) {
				source = manager.getNextVoice();
				source.setBuffer(buffers.get((int) random(buffers.size())));
				source.setPitch(scale.getPitchForScaleTone(i, 12,
						transpose.getCurrent()));
				source.setGain(random(0.25f, 1f));
				source.play();
				synchronized (lines) {
					ArrayList<Line> offspring = new ArrayList<Line>();
					int numLines = lines.size();
					for (Line l : lines) {
						Line child = l.update(maskImg);
						if (child != null && numLines < 1000) {
							offspring.add(child);
						}
					}
					lines.addAll(offspring);
				}
			}
		}
	}

	/**
	 * Sequencer event handler used to randomly re-trigger the last played note.
	 */
	public void handleTick(int arg0) {
		if (source != null && random(1f) < 0.25) {
			source.setGain(random(0.25f, 1f));
			source.play();
		}
	}
}

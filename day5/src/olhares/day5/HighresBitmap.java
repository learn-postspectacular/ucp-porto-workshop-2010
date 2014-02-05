package olhares.day5;

import processing.core.PApplet;
import toxi.util.DateUtils;

public class HighresBitmap extends PApplet {

	public static void main(String[] args) {
		PApplet.main(new String[] { "olhares.day5.HighresBitmap" });
	}

	private String session;
	private FrameSequenceExporter exporter;

	public void setup() {
		size(1000, 700);
		exporter = new FrameSequenceExporter(sketchPath("export"), "frame",
				"png");
	}

	public void draw() {
		line(mouseX, mouseY, pmouseX, pmouseY);
		// saveFrame("frame-"+session+"-####.tga");
		exporter.update(g);
		if (exporter.isExporting()) {
			fill(255,0,0);
			ellipse(20,20,10,10);
		}
	}
	
	public void keyPressed() {
		if (key==' ') {
			if (exporter.isExporting()) {
				exporter.stop();
			} else {
				exporter.newSession();
				exporter.start();
			}
		}
	}
}

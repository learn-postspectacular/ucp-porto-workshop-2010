package olhares.day5;

import processing.core.PApplet;
import processing.core.PImage;
import toxi.color.AccessCriteria;
import toxi.color.ColorList;
import toxi.color.TColor;

public class ImageColors extends PApplet {

	public static void main(String[] args) {
		PApplet.main(new String[] { "olhares.day5.ImageColors" });
	}

	private ColorList palette;

	public void setup() {
		size(500, 500);
		// PImage img = loadImage("test.jpg");
		PImage img = loadImage("rainbow.jpg");
		palette = ColorList.createFromARGBArray(img.pixels, 50, true);
		// palette.sortByProximityTo(TColor.RED, true);
		palette.sortByCriteria(AccessCriteria.HUE, true);
	}

	public void draw() {
		background(0);
		int x = 0;
		for (TColor c : palette) {
			fill(c.toARGB());
			rect(x, 0, 10, height);
			x += 10;
		}
	}
}

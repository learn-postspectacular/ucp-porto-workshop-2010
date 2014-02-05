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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import toxi.color.ColorList;
import toxi.color.TColor;

public class Histogram {

	private ColorList palette;

	public Histogram(ColorList palette) {
		this.palette = palette;
	}

	/**
	 * @param tolerance
	 *            color tolerance used to merge similar colors (based on RGB
	 *            distance)
	 * @param blendCols
	 *            switch to enable color blending of binned colors
	 * @return sorted histogram as List
	 */
	List<HistEntry> compute(float tolerance, boolean blendCols) {
		List<HistEntry> hist = new ArrayList<HistEntry>();
		float maxFreq = 1;
		for (Iterator<TColor> i = palette.iterator(); i.hasNext();) {
			TColor c = i.next();
			HistEntry existing = null;
			for (Iterator<HistEntry> j = hist.iterator(); j.hasNext();) {
				HistEntry e = j.next();
				if (e.col.distanceToRGB(c) < tolerance) {
					if (blendCols)
						e.col.blend(c, 1f / (e.freq + 1));
					existing = e;
					break;
				}
			}
			if (existing != null) {
				existing.freq++;
				if (existing.freq > maxFreq)
					maxFreq = existing.freq;
			} else {
				hist.add(new HistEntry(c));
			}
		}
		Collections.sort(hist);
		maxFreq = 1f / palette.size();
		for (HistEntry e : hist) {
			e.freq *= maxFreq;
		}
		return hist;
	}

	/**
	 * A single histogram entry, a coupling of color & frequency Implements a
	 * comparator to sort histogram entries based on freq.
	 */
	public class HistEntry implements Comparable<HistEntry> {
		float freq;
		TColor col;

		HistEntry(TColor c) {
			col = c;
			freq = 1;
		}

		public int compareTo(HistEntry e) {
			return -(int) (freq - e.freq);
		}
	}

}

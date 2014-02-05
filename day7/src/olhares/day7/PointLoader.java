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

import java.util.Date;
import java.util.concurrent.PriorityBlockingQueue;

import processing.core.PApplet;

public class PointLoader extends Thread {

	PriorityBlockingQueue<UserPoint> queuedPoints = new PriorityBlockingQueue<UserPoint>();

	private boolean isAlive = true;
	private long sleepTime;
	private String url;
	private PApplet app;

	private long lastRequestTime;

	public PointLoader(PApplet app, String url, int sleep) {
		this.app = app;
		this.url = url;
		this.sleepTime = sleep;
		lastRequestTime = new Date().getTime()/1000;
	}

	public void run() {
		try {
			while (isAlive) {
				String[] lines = app.loadStrings(url+"?since="+lastRequestTime);
				lastRequestTime = new Date().getTime()/1000;
				System.out.println("last request: "+lastRequestTime);
				for (String line : lines) {
					if (line.length() > 0) {
						String[] vals = PApplet.split(line, ',');
						float x = PApplet.parseFloat(vals[0]);
						float y = PApplet.parseFloat(vals[1]);
						float b = PApplet.parseFloat(vals[2]);
						UserPoint p = new UserPoint(x, y, 0, b);
						queuedPoints.offer(p);
					}
				}
				Thread.sleep(sleepTime);
			}
		} catch (InterruptedException e) {
		}
	}
	
	public void kill() {
		isAlive=false;
	}

	public PriorityBlockingQueue<UserPoint> getQueue() {
		return queuedPoints;
	}
}

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

import java.util.List;

import processing.core.PConstants;
import toxi.geom.Vec3D;
import toxi.math.MathUtils;
import toxi.math.waves.AbstractWave;
import toxi.math.waves.SineWave;

public abstract class LogoShape {

	private AbstractWave offset = new SineWave(
			MathUtils.random(MathUtils.TWO_PI), MathUtils.random(0.01f, 0.05f),
			20*OlharesVolume.SCALE, 0);
	
	private AbstractWave brushSize = new SineWave(
			MathUtils.random(MathUtils.TWO_PI), MathUtils.random(0.05f, 0.1f),
			40, 50);

	float getOffset() {
		return offset.value;
	}

	float getSize() {
		return 50; //brushSize.value;
		
	}
	
	void update() {
		offset.update();
		brushSize.update();
	}

	abstract List<Vec3D> getPoints();
}

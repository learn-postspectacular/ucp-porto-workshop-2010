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

import toxi.geom.Vec3D;

public class UserPoint extends Vec3D {

	public float brushValue;

	public UserPoint(Vec3D p, float v) {
		this(p.x,p.y,p.z,v);
	}
	
	public UserPoint(float x, float y, float z, float brushValue) {
		super(x, y, z);
		this.brushValue = brushValue;
	}

	public boolean update() {
		brushValue *= OlharesVolume.CONFIG.getFloat("brush.decay", 0.99f);
		return brushValue > 0.02;
	}
}

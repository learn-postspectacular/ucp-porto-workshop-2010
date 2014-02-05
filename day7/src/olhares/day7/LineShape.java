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

import toxi.geom.Line3D;
import toxi.geom.Vec3D;

public class LineShape extends LogoShape {

	private List<Vec3D> points;

	public LineShape(Line3D line) {
		this.points=line.splitIntoSegments(null, 1.5f, true);
	}
	
	@Override
	List<Vec3D> getPoints() {
		return points;
	}

}

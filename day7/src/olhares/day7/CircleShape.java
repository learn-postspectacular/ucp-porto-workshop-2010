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

import java.util.ArrayList;
import java.util.List;

import toxi.geom.Circle;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;

public class CircleShape extends LogoShape {

	private Vec2D pos;
	private List<Vec3D> points=new ArrayList<Vec3D>();

	public CircleShape(Vec2D pos, float radius, float z) {
		this.pos=pos;
		List<Vec2D> points2D = new Circle(pos, radius).toPolygon2D(60).vertices;
		for(Vec2D p : points2D) {
			Vec3D p3D=p.to3DXY();
			p3D.z=z;
			points.add(p3D);
		}
	}
	
	@Override
	List<Vec3D> getPoints() {
		return points;
	}

}

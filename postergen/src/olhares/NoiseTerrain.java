/*
 * This file is part of the OlharesPoster project, developed at
 * day #7 of the Olhares de Processing workshop at UCP Porto in July 2010.
 * 
 * Copyright 2010 Karsten Schmidt (PostSpectacular Ltd.)
 * 
 * OlharesPoster is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * OlharesPoster is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with OlharesPoster. If not, see <http://www.gnu.org/licenses/>.
 */

package olhares;

import toxi.math.noise.SimplexNoise;

public class NoiseTerrain implements TerrainFunction {

	private static final float NS = 0.03f;

	public final double getTerrainAt(int x, int y) {
		return (SimplexNoise.noise(x * NS, y * NS) * 0.5 + 0.5);
	}

}

OlharesVolumeIdentity project/exercise
========================================================================

This is a complete & self contained Eclipse project intended as an
installation for the Olhares de Outono 2010 festival in Porto.

For the identity we construct the logo "OO IO" out of basic geometric shapes
and animate their position along the X axis to create intersecting new shapes.
Each shape is represented as a number of points which are then used by the
VolumetricBrush class of the toxiclibs volumeutils package to literally form
intersecting 3D shapes. We then extended the functionality to allow users to
scribble in this volumetric space and send these modifications to a webserver
and MySQL database. These changes are only temporary and will decay over a short
time period.

The app can run in two modes: one interactive, one used simply as presentation/
projection. In the latter mode the app will continuously poll the webserver
for new user modifications and merge and display them.

To enable these two modes of running the software we introduced the toxiclibs
TypedProperties class allowing the application to be easily configured via an
external text file.

For more information about this example & (similar) workshop(s), please
visit: http://learn.postspectacular.com/

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
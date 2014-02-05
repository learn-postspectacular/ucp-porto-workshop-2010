FlickrDLA project/exercise
========================================================================

This is a complete & self contained Eclipse project combining the process
of Diffusion-limited aggregation with images sourced via the Flickr API
and the flickrj library to grow a 3D particle structure out of images.
We introduced the concept of caching data locally and analyzing the
loaded images for their primary colors (using a histogram) in order to
sort them by a given criteria and then were able to map them based on that
criteria (e.g. brightness) along an axis in 3D space. We also created a
sky sphere surrounding the entire scene with each of its vertices tinted
in the main color extracted from each image.

For more information about this example & (similar) workshop(s), please
visit: http://learn.postspectacular.com/

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
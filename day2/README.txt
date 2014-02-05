TwitterGraph workshop project/exercise
========================================================================

This is a complete & self contained Eclipse project demonstrating
basic OOP concepts, the handling of various classes and mechanism of the
Java Collections API, Processing and toxiclibs.

The project uses the Twitter Search API and toxiclibs' AtomFeed class
to parse results. Each feed entry is analyzed and a histogram formed for
each unique author. In parallel we're also building a graph connecting
authors with their messages to form clusters and implement means to keep
the autor nodes/clusters away from each other. The constructed graph is
then placed within a physical simulation space which arranges the nodes
into a semi-stable layout. The final step was then to add some mouse
event handling and have nodes reacting to rollover & dragging events,
allowing them to be moved around on screen and so influence the overall
layout which will automatically adapt.

For more information about this example & (similar) workshop(s), please
visit: http://learn.postspectacular.com/

/*
 * This file is part of the TwitterGraph project, developed at day #2
 * at the Olhares de Processing workshop at UCP Porto in July 2010.
 * 
 * Copyright 2010 Karsten Schmidt (PostSpectacular Ltd.)
 * 
 * TwitterGraph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * TwitterGraph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with TwitterGraph. If not, see <http://www.gnu.org/licenses/>.
 */
 


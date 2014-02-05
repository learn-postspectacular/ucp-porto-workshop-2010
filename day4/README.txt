OlharesCASynth workshop project/exercise
========================================================================

This is a complete & self contained Eclipse project developed during the
Olhares de Processing workshop at UCP Porto. The aim was to introduce students
to the toxiclibs audioutils and simutils packages, which we first combined to
create a simple generative music project using cellular automata to produce
note sequences. The project also introduced the concept of multi-threading
through the use of the audioutils' QuantizedTimeProvider class to which our
main application subscribed via event handlers. To also handle the concept of
recursion we then extended the project with a branching line generator which
only grows at moments when notes are played and so produce a sound-reactive
animation. This synchronized effect was then later diluted when we also
build a simple particle system for each line which would not show the grown
lines directly anymore, but have the particles loosely trace their paths.
Finally, we added a black&white bitmap image to define and constrain the
possible growth area of lines...

For more information about this example & (similar) workshop(s), please
visit: http://learn.postspectacular.com/

/*
 * This file is part of the OlharesCASynth project, developed on day #4 of the
 * Olhares de Processing workshop at UCP Porto in July 2010.
 * 
 * For more information about this example & (similar) workshop(s),
 * please visit: http://learn.postspectacular.com/
 * 
 * Copyright 2010 Karsten Schmidt (PostSpectacular Ltd.)
 * 
 * OlharesCASynth is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * OlharesCASynth is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with OlharesCASynth. If not, see <http://www.gnu.org/licenses/>.
 */
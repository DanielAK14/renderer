
# 3DRenderer

### A 3D renderer programmed for CS480 - Computer Graphics. 

The project features four objects, a cuboid, a cylinder, an ellipsoid, and a torus, of varying sizes and positions.
Although OpenGL is used for displaying to the screen, the mesh updating and line/triangle drawing is programmed from scratch
using custom classes. The user can cycle between three scenes, change the number of vertices for the objects, and change the
shading method between flat, gourard, and Phong.

Controls are as follows:

* Q,q: quit
* C,c: clear polygon (set vertex count=0)
* R,r: randomly change the color
* S,s: toggle the smooth shading
* T,t: show testing examples (toggles between smooth shading and flat shading test cases)
* F,f: render objects with flat shading
* G,g: render objects with Gouraurd shading
* P,p: render objects with Phong shading
* L,l: turn on primer for light switchboard
* A,a: disable/enable ambient coefficient
* D,d: disable/enable diffuse coefficient
* S,s: disable/enable spectral coefficient
* 1: 	 disable/enable ambient lighting
* 2:   disable/enable infinite lighting
* 3:	 disable/enable spotlight 1
* 4:	 disable/enable spotlight 2
* ">":	 increase the step number for examples
* "<":   decrease the step number for examples
* +,-:  increase or decrease spectral exponent
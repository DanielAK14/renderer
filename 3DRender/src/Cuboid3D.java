//****************************************************************************
// Cuboid3D
//****************************************************************************
// Comments : 
//   Class for creating a mesh of a cube or rectangular prism
//
// History :
//  Nov 2020 Created by Daniel Kehr

public class Cuboid3D {
	
	private Point3D center;
	private float l,w,h;
	public Mesh3D mesh;
	
	public Cuboid3D(float _x, float _y, float _z, float _l, float _w, float _h)
	{
		center = new Point3D(_x,_y,_z);
		l = _l;
		w = _w;
		h = _h;
		initMesh();
	}
	
	public void set_center(float _x, float _y, float _z)
	{
		center.x=_x;
		center.y=_y;
		center.z=_z;
		fillMesh();  // update the triangle mesh
	}
	
	public void set_length(float _l)
	{
		l = _l;
		fillMesh(); // update the triangle mesh
	}
	
	public void set_width(float _w)
	{
		w = _w;
		fillMesh(); // update the triangle mesh
	}
	
	public void set_height(int _h)
	{
		h = _h;
		initMesh(); // resized the mesh, must re-initialize
	}
	
	private void initMesh()
	{
		mesh = new Mesh3D(6,4);
		fillMesh();  // set the mesh vertices and normals
	}
		
	// fill the triangle mesh vertices and normals
	// using the current parameters for the sphere
	private void fillMesh()
	{
		// ****************Implement Code here*******************//
		
		// calculate half of the lenght/height/width
		// and add to corresponding center val.
		
		// obtain corners of the box
		float posX = center.x+l/2;
		float negX = center.x-l/2;
		float posY = center.y+w/2;
		float negY = center.y-w/2;
		float posZ = center.z+h/2;
		float negZ = center.z-h/2;
		
		
		// face 1
		mesh.v[1][0] = new Point3D(posX, negY, negZ);
		mesh.v[1][1] = new Point3D(negX, negY, negZ);
		mesh.v[1][2] = new Point3D(negX, negY, posZ);
		mesh.v[1][3] = new Point3D(posX, negY, posZ);
		
		// face 0
		mesh.v[0][0] = new Point3D(posX, negY, posZ);
		mesh.v[0][1] = new Point3D(negX, negY, posZ);
		mesh.v[0][2] = new Point3D(negX, posY, posZ);
		mesh.v[0][3] = new Point3D(posX, posY, posZ);
	
		// face 3
		mesh.v[3][0] = new Point3D(posX, posY, posZ);
		mesh.v[3][1] = new Point3D(negX, posY, posZ);
		mesh.v[3][2] = new Point3D(negX, posY, negZ);
		mesh.v[3][3] = new Point3D(posX, posY, negZ);
		
		// face 2
		mesh.v[2][0] = new Point3D(posX, posY, negZ);
		mesh.v[2][1] = new Point3D(posX, posY, posZ);
		mesh.v[2][2] = new Point3D(posX, negY, posZ);
		mesh.v[2][3] = new Point3D(posX, negY, negZ);
		
		// face 5
		mesh.v[5][0] = new Point3D(posX, negY, negZ);
		mesh.v[5][1] = new Point3D(posX, posY, negZ);
		mesh.v[5][2] = new Point3D(negX, posY, negZ);
		mesh.v[5][3] = new Point3D(negX, negY, negZ);
		
		// face 4
		mesh.v[4][0] = new Point3D(negX, negY, negZ);
		mesh.v[4][1] = new Point3D(negX, negY, posZ);
		mesh.v[4][2] = new Point3D(negX, posY, posZ);
		mesh.v[4][3] = new Point3D(negX, posY, negZ);
		
		
				
	}
}

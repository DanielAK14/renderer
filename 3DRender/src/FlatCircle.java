//****************************************************************************
// FlatCircle
//****************************************************************************
// Comments : 
//   Class for rendering the top and bottom of a cylinder
//	 (may not be perfect, could use some tuning after assignment is due to better understand concepts)	

// History :
//  Nov 2020 Created by Daniel Kehr

public class FlatCircle
{
	private Point3D center;
	private float r;
	private int slices;
	private boolean outward;
	public Mesh3D mesh;
	
	public FlatCircle(float _x, float _y, float _z, float _r, int _slices, boolean _outward)
	{
		center = new Point3D(_x,_y,_z);
		r = _r;
		slices = _slices;
		outward = _outward;
		initMesh();
	}
	
	public void set_center(float _x, float _y, float _z)
	{
		center.x=_x;
		center.y=_y;
		center.z=_z;
		fillMesh();  // update the triangle mesh
	}
	
	public void set_radius(float _r)
	{
		r = _r;
		fillMesh(); // update the triangle mesh
	}
	
	public void set_slices(int _slices)
	{
		slices = _slices;
		initMesh(); // resized the mesh, must re-initialize
	}
	
	public int get_n()
	{
		return slices;
	}
	
	private void initMesh()
	{
		mesh = new Mesh3D(1,slices);
		fillMesh();  // set the mesh vertices and normals
	}
		
	// fill the triangle mesh vertices and normals
	// using the current parameters for the sphere
	private void fillMesh()
	{
		// ****************Implement Code here*******************//
		double pi = Math.PI;
		double theta;
		double dtheta = 2*pi/(slices-1);
		int i;		
		
		for (i = 0, theta = -pi; i < slices; i++,theta+=dtheta) {
			
			mesh.v[0][i].x = (float) (center.x+r*Math.cos(theta)); 
			mesh.v[0][i].y = (float) (center.y+r*Math.sin(theta)); 
			mesh.v[0][i].z = (float) (center.z); 
			
			mesh.n[0][i].x = (float) (0);
			mesh.n[0][i].y = (float) (0);
			mesh.n[0][i].z = (float) (1);
			
		}

	}
}


//****************************************************************************
//      Sphere class
//****************************************************************************
// History :
//   Nov 6, 2014 Created by Stan Sclaroff
//	 Nov 2020 Completed code, created a fill function and variables for a 3D sphere object - Daniel Kehr

public class Sphere3D
{
	private Point3D center;
	private float r;
	private int stacks,slices;
	public Mesh3D mesh;
	
	public Sphere3D(float _x, float _y, float _z, float _r, int _stacks, int _slices)
	{
		center = new Point3D(_x,_y,_z);
		r = _r;
		stacks = _stacks;
		slices = _slices;
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
	
	public void set_stacks(int _stacks)
	{
		stacks = _stacks;
		initMesh(); // resized the mesh, must re-initialize
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
	
	public int get_m()
	{
		return stacks;
	}

	private void initMesh()
	{
		mesh = new Mesh3D(stacks,slices);
		fillMesh();  // set the mesh vertices and normals
	}
		
	// fill the triangle mesh vertices and normals
	// using the current parameters for the sphere
	private void fillMesh()
	{
		// ****************Implement Code here*******************//
		double pi = Math.PI;
		double phi;
		double dphi = pi/(stacks-1);
		double theta;
		double dtheta = 2*pi/(slices-1);
		int i;
		int j;
		
		// cycle around stacks
		for (i = 0, phi = -pi/2; i < stacks; i++,phi+=dphi) {
			double cos_phi = Math.cos(phi);
			double sin_phi = Math.sin(phi);
			
			// cycle around slices
			for (j = 0, theta = -pi; j < slices; j++, theta+=dtheta) {
				double cos_theta = Math.cos(theta);
				double sin_theta = Math.sin(theta);
				
				mesh.v[i][j].x = (float) (center.x+r*cos_phi*cos_theta); 
				mesh.v[i][j].y = (float) (center.y+r*cos_phi*sin_theta); 
				mesh.v[i][j].z = (float) (center.z+r*sin_phi); 
				
				mesh.n[i][j].x = (float) (cos_phi*cos_theta);
				mesh.n[i][j].y = (float) (cos_phi*sin_theta);
				mesh.n[i][j].z = (float) (sin_phi);
			}
		}

	}
}
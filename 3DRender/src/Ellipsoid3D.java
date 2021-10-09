
//****************************************************************************
//      Ellipsoid3D class
//****************************************************************************
// 

// History :
// Created the sphere class Nov 6, 2014 by Stan Sclaroff
// Modidified the sphere class to include ellipsoids Nov 2020 by Daniel Kehr

public class Ellipsoid3D
{
	private Point3D center;
	private float rx, ry, rz;
	private int stacks,slices;
	public Mesh3D mesh;
	
	public Ellipsoid3D(float _x, float _y, float _z, float _rx, float _ry, float _rz, int _stacks, int _slices)
	{
		center = new Point3D(_x,_y,_z);
		rx = _rx;
		ry = _ry;
		rz = _rz;
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
	
	public void set_radiusX(float _r)
	{
		rx = _r;
		fillMesh(); // update the triangle mesh
	}
	
	public void set_radiusY(float _r)
	{
		ry = _r;
		fillMesh(); // update the triangle mesh
	}
	
	public void set_radiusZ(float _r)
	{
		rz = _r;
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
		
		// ACTS THE SAME AS SPHERE3D BUT WITH SPECIFIC RADIUS VALUES PER EACH AXIS
		double pi = Math.PI;
		double phi;
		double dphi = pi/(stacks-1);
		double theta;
		double dtheta = 2*pi/(slices-1);
		int i;
		int j;
		
		// cycle about stacks
		for (i = 0, phi = -pi/2; i < stacks; i++,phi+=dphi) {
			double cos_phi = Math.cos(phi);
			double sin_phi = Math.sin(phi);
			
			// cycle about slices
			for (j = 0, theta = -pi; j < slices; j++, theta+=dtheta) {
				double cos_theta = Math.cos(theta);
				double sin_theta = Math.sin(theta);
				
				mesh.v[i][j].x = (float) (center.x+rx*cos_phi*cos_theta); 
				mesh.v[i][j].y = (float) (center.y+ry*cos_phi*sin_theta); 
				mesh.v[i][j].z = (float) (center.z+rz*sin_phi); 
				
				mesh.n[i][j].x = (float) (cos_phi*cos_theta);
				mesh.n[i][j].y = (float) (cos_phi*sin_theta);
				mesh.n[i][j].z = (float) (sin_phi);
			}
		}

	}
}
//****************************************************************************
// Cylinder3D
//****************************************************************************
// Comments : 
//   Class for creating a 3D cylinder mesh
//
// History :
//  Nov 2020 Created by Daniel Kehr

public class Cylinder3D
{
	private Point3D center;
	private float h,r;
	private int stacks,slices;
	public Mesh3D mesh;
	
	public Cylinder3D(float _x, float _y, float _z, float _h, float _r, int _stacks, int _slices)
	{
		center = new Point3D(_x,_y,_z);
		h = _h;
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
	
	public void set_height(float _h)
	{
		h = _h;
		fillMesh(); // update the triangle mesh
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
		double pi = Math.PI;
		double ups;
		double dups = h/(stacks);
		double theta;
		double dtheta = 2*pi/(slices-1);
		float bottom = center.z-(h/2);
		int i;
		int j;

		for (i = 0, ups = h/stacks; i < stacks; i++,ups+=dups) {
			
			for (j = 0, theta = -pi; j < slices; j++, theta+=dtheta) {
			
				// create bottom fan
				if (i == 0) {
					mesh.v[i][j].x = center.x;
					mesh.v[i][j].y = center.y;
					mesh.v[i][j].z = bottom;
					
					mesh.n[i][j].x = (float) (0);
					mesh.n[i][j].y = (float) (0);
					mesh.n[i][j].z = (float) (-1);
				
					// create top fan
				}else if(i == stacks-1) {
					
					mesh.v[i][j].x = mesh.v[0][0].x;
					mesh.v[i][j].y = mesh.v[0][0].y;
					mesh.v[i][j].z = bottom+h;
					
					mesh.n[i][j].x = (float) (0);
					mesh.n[i][j].y = (float) (0);
					mesh.n[i][j].z = (float) (1);
					
				}else {
					
					double cos_theta = Math.cos(theta);
					double sin_theta = Math.sin(theta);
					
					mesh.v[i][j].x = (float) (center.x+r*cos_theta); 
					mesh.v[i][j].y = (float) (center.y+r*sin_theta);
					
					// ensure side vertices line up with the height
					if(i == 1) {

						mesh.v[i][j].z = (float) (bottom);
					}else if(i == stacks-2) {

						mesh.v[i][j].z = (float) (bottom+h);
					}else {

						mesh.v[i][j].z = (float) (bottom+ups);
					} 
						
					mesh.n[i][j].x = (float) (cos_theta);
					mesh.n[i][j].y = (float) (sin_theta);
					mesh.n[i][j].z = (float) (0);
				}
			}
		}	

	}
		
}

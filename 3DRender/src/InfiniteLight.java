//****************************************************************************
// InfiniteLight
//****************************************************************************
// Comments : 
//   Class for applying illumination to an object assuming the light is an infinite ray
//
// History :
//  Nov 2020 Created by Daniel Kehrimport java.util.Random;

public class InfiniteLight extends Light{

	public InfiniteLight(ColorType _c, Point3D _direction) 
	{
		color = new ColorType(_c);
		direction = new Point3D(_direction);
		position = new Point3D(0, 0, 0); // Not used in this class
	}
	
	
	
	// rotate the light's direction over the viewing quaternion
	public void rotateLight(Quaternion q, Point3D center)
	{
		Quaternion q_inv = q.conjugate();
		Point3D vec;
		
		Quaternion p;
				
		// rotate the normals
		p = new Quaternion((float)0.0,direction);
		p=q.multiply(p);
		p=p.multiply(q_inv);
		direction = p.get_v();
		
	}
	

	// apply this light source to the vertex / normal, given material
	// return resulting color value
	// v: viewing vector
	// n: face normal
	public ColorType applyLight(Material mat, Point3D v, Point3D n){
		ColorType res = new ColorType();
		// ****************Implement Code here*******************//
		
		ColorType diff = new ColorType();
		ColorType spec = new ColorType();
		
		float diffDot = n.dotProduct(direction);
		Point3D specR = direction.reflection(n);
		float specDot = v.dotProduct(specR);
		
		
		if(diffDot > 0) {
			diff.r = mat.kd.r*color.r*diffDot;
			diff.g = mat.kd.g*color.g*diffDot;
			diff.b = mat.kd.b*color.b*diffDot;
			
			if(specDot > 0) {
				spec.r = (float) (mat.ks.r*color.r*Math.pow(specDot, mat.ns));
				spec.g = (float) (mat.ks.g*color.g*Math.pow(specDot, mat.ns));
				spec.b = (float) (mat.ks.b*color.b*Math.pow(specDot, mat.ns));
			}
			
		}
		
		res.r = diff.r+spec.r;
		res.g = diff.g+spec.g;
		res.b = diff.b+spec.b;
		
		res.clamp();
		return res;

	}
}

//****************************************************************************
// PointLight
//****************************************************************************
// Comments : 
//   Class for applying illumination to an object given its and the viewers position
//
// History :
//  Nov 2020 Created by Daniel Kehr

import java.util.Arrays;

public class PointLight extends Light{
	private float ang_lim;
	private float a0, a1, a2;
	
	public PointLight(ColorType _c, Point3D _direction, Point3D _pos, float angle) 
	{
		color = new ColorType(_c);
		direction = new Point3D(_direction);
		position = _pos;
		ang_lim = angle;
		a0 = a1 = a2 = 0.00001f;
	}
	
	public float get_angle() {
		return ang_lim;
	}
	
	public float[] get_hyper_parameters() {
		float[] params = {a0, a1, a2};
		return params;
	}
	
	public void set_angle(float theta) {
		ang_lim = theta;
	}
	
	public void set_hyper_parameters(float _a0, float _a1, float _a2) {
		a0 = _a0;
		a1 = _a1;
		a2 = _a2;
	}
	
	// rotate the light's position and direction over the viewing quaternion
	public void rotateLight(Quaternion q, Point3D center)
	{
		Quaternion q_inv = q.conjugate();
		Point3D vec;
		
		Quaternion p;
		

		// apply pivot rotation to vertices, given center point
		p = new Quaternion((float)0.0,position.minus(center)); 
		p=q.multiply(p);
		p=p.multiply(q_inv);
		vec = p.get_v();
		position=vec.plus(center);
		
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
	public ColorType applyLight(Material mat, Point3D v, Point3D n, Point3D s_pos){
		ColorType res = new ColorType();
		// ****************Implement Code here*******************//

		double n_dot_l;
		Point3D pl = new Point3D(0f, 0f, 0f);

		pl = position.minus(s_pos);
		pl.normalize();
		n_dot_l = pl.dotProduct(n);


		if(n_dot_l > 0)
		{
				
			// calculate the diffuse component
			if(mat.diffuse)
			{
				res.r = (float)(n_dot_l*mat.kd.r*color.r);
				res.g = (float)(n_dot_l*mat.kd.g*color.g);
				res.b = (float)(n_dot_l*mat.kd.b*color.b);
			}
			
			// calculate the specular component
			if(mat.specular)
			{
				Point3D r = direction.reflection(n);
				float v_dot_r = r.dotProduct(v);
				if(v_dot_r > 0)
				{
					res.r += (float)Math.pow((v_dot_r*mat.ks.r*color.r),mat.ns);
					res.g += (float)Math.pow((v_dot_r*mat.ks.g*color.g),mat.ns);
					res.b += (float)Math.pow((v_dot_r*mat.ks.b*color.b),mat.ns);
				}
			}
	
			// calculate the radial attenuation
			float d = (float) Math.sqrt(Math.pow((position.x - s_pos.x),2) + Math.pow((position.y - s_pos.y),2) + Math.pow((position.z - s_pos.z),2));
			float r = 1 / (a0 + a1 * d + a2 * (float) Math.pow(d, 2));


			//calculate the angular attenuation
			float cosAlpha = pl.dotProduct(direction);

			float alpha = 1f;

			if(cosAlpha > Math.cos(Math.toRadians(ang_lim)))
			{
				alpha = (float) Math.pow(cosAlpha, 2);
			}
			
			res.r *= r * alpha;
			res.g *= r * alpha;
			res.b *= r * alpha;
		}
		
		res.clamp();
		return(res);

	}
}

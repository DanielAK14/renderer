//****************************************************************************
//       2D Point Class from PA1
//****************************************************************************
// History :
//   Nov 6, 2014 Created by Stan Sclaroff
//

public class Point2D
{
	public int x, y;
	public float d;
	public Point3D n;
	public float u, v; // uv coordinates for texture mapping
	public ColorType c;
	public Point2D(int _x, int _y, int _d, ColorType _c)
	{
		u = 0;
		v = 0;
		x = _x;
		y = _y;
		d = _d;
		c = _c;
		n = new Point3D(1,1,1);
	}
	public Point2D(int _x, int _y, int _d, ColorType _c, float _u, float _v)
	{
		u = _u;
		v = _v;
		x = _x;
		y = _y;
		d = _d;
		c = _c;
		n = new Point3D(1,1,1);
	}
	public Point2D()
	{
		c = new ColorType(1.0f, 1.0f, 1.0f);
		n = new Point3D(1,1,1);
	}
	public Point2D( Point2D p)
	{
		u = p.u;
		v = p.v;
		x = p.x;
		y = p.y;
		d = p.d;
		c = new ColorType(p.c.r, p.c.g, p.c.b);
		n = new Point3D(p.n.x,p.n.y,p.n.z);
	}
	
	public float distance(Point2D v) {
	    return (float) Math.sqrt(Math.pow((v.x - this.x),2)+Math.pow((v.y - this.y),2)+Math.pow((v.d - this.d),2));
	}
	
}
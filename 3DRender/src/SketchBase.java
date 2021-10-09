//****************************************************************************
// SketchBase.  
//****************************************************************************
// Comments : 
//   Subroutines to manage and draw points, lines an triangles
//
// History :
//   Aug 2014 Created by Jianming Zhang (jimmie33@gmail.com) based on code by
//   Stan Sclaroff (from CS480 '06 poly.c)
// 	 Nov 2020 Filled in drawLine and drawTriangle functions and created accompanying functions for Phong shading - Daniel Kehr

import java.awt.image.BufferedImage;
import java.util.*;

public class SketchBase
{
	public SketchBase()
	{
		// deliberately left blank
	}
	
	/**********************************************************************
	 * Draws a point.
	 * This is achieved by changing the color of the buffer at the location
	 * corresponding to the point. 
	 * 
	 * @param buff
	 *          Buffer object.
	 * @param p
	 *          Point to be drawn.
	 */
	public static void drawPoint(BufferedImage buff, float[][] dBuff, Point2D p)
	{
		if(p.x>=0 && p.x<buff.getWidth() && p.y>=0 && p.y < buff.getHeight() && p.d > dBuff[p.x][p.y])
			buff.setRGB(p.x, buff.getHeight()-p.y-1, p.c.getRGB_int());	
	}
	
	/**********************************************************************
	 * Draws a line segment using Bresenham's algorithm, linearly 
	 * interpolating RGB color along line segment.
	 * This method only uses integer arithmetic.
	 * 
	 * @param buff
	 *          Buffer object.
	 * @param p1
	 *          First given endpoint of the line.
	 * @param p2
	 *          Second given endpoint of the line.
	 */
	public static void drawLine(BufferedImage buff, float[][] dBuff, Point2D p1, Point2D p2)
	{
	    int x0=p1.x, y0=p1.y; float z0=p1.d;
	    int xEnd=p2.x, yEnd=p2.y; float zEnd=p2.d;
	    int dx = Math.abs(xEnd - x0),  dy = Math.abs(yEnd - y0); float dz = Math.abs(zEnd - z0);
	    
	    if(dx==0 && dy==0 )
	    {
	    	drawPoint(buff, dBuff,p1);
	    	return;
	    }
	    
	    // if slope is greater than 1, then swap the role of x and y
	    boolean x_y_role_swapped = (dy > dx); 
	    if(x_y_role_swapped)
	    {	
	    	x0=p1.y; 
	    	y0=p1.x;
	    	xEnd=p2.y; 
	    	yEnd=p2.x;
	    	dx = Math.abs(xEnd - x0);
	    	dy = Math.abs(yEnd - y0);
	    }
	    
	    // initialize the decision parameter and increments
	    int p = 2 * dy - dx;
	    int twoDy = 2 * dy,  twoDyMinusDx = 2 * (dy - dx);
	    int x=x0, y=y0; float z=z0;
	    
	    // set step increment to be positive or negative
	    int step_x = x0<xEnd ? 1 : -1;
	    int step_y = y0<yEnd ? 1 : -1;
	    float step_z = z0<yEnd ? 1 : -1;
	    
	    // deal with setup for color interpolation
	    // first get r,g,b integer values at the end points
	    int r0=p1.c.getR_int(), rEnd=p2.c.getR_int();
	    int g0=p1.c.getG_int(), gEnd=p2.c.getG_int();
	    int b0=p1.c.getB_int(), bEnd=p2.c.getB_int();
	    
	    // compute the change in r,g,b 
	    int dr=Math.abs(rEnd-r0), dg=Math.abs(gEnd-g0), db=Math.abs(bEnd-b0);
	    
	    // set step increment to be positive or negative 
	    int step_r = r0<rEnd ? 1 : -1;
	    int step_g = g0<gEnd ? 1 : -1;
	    int step_b = b0<bEnd ? 1 : -1;
	    
	    // compute whole step in each color that is taken each time through loop
	    int whole_step_r = step_r*(dr/dx);
	    int whole_step_g = step_g*(dg/dx);
	    int whole_step_b = step_b*(db/dx);
	    
	    // compute remainder, which will be corrected depending on decision parameter
	    dr=dr%dx;
	    dg=dg%dx; 
	    db=db%dx;
	    
	    // initialize decision parameters for red, green, and blue
	    int p_r = 2 * dr - dx;
	    int twoDr = 2 * dr,  twoDrMinusDx = 2 * (dr - dx);
	    int r=r0;
	    
	    int p_g = 2 * dg - dx;
	    int twoDg = 2 * dg,  twoDgMinusDx = 2 * (dg - dx);
	    int g=g0;
	    
	    int p_b = 2 * db - dx;
	    int twoDb = 2 * db,  twoDbMinusDx = 2 * (db - dx);
	    int b=b0;
	    
	    // draw start pixel
	    if(x_y_role_swapped)
	    {
	    	// if pixel is closer to camera than previous pixels (via depth buffer) draw and update the depth buffer
	    	// this is repeated for all conditional drawing loops
	    	if(x>=0 && x<buff.getHeight() && y>=0 && y<buff.getWidth() && z > dBuff[y][x]) {
	    		buff.setRGB(y, buff.getHeight()-x-1, (r<<16) | (g<<8) | b);
	    		dBuff[y][x] = z;
	    	}
	    }
	    else
	    {
	    	if(y>=0 && y<buff.getHeight() && x>=0 && x<buff.getWidth() && z > dBuff[x][y]) {
	    		buff.setRGB(x, buff.getHeight()-y-1, (r<<16) | (g<<8) | b);
	    		dBuff[x][y] = z;
	    	}
	    }
	    
	    while (x != xEnd) 
	    {
	    	// increment x and y
	    	x+=step_x;
	    	if (p < 0)
	    		p += twoDy;
	    	else 
	    	{
	    		y += step_y;
	    		p += twoDyMinusDx;
	    		z += step_z;
	    	}
		        
	    	// increment r by whole amount slope_r, and correct for accumulated error if needed
	    	r+=whole_step_r;
	    	if (p_r < 0)
	    		p_r += twoDr;
	    	else 
	    	{
	    		r+=step_r;
	    		p_r += twoDrMinusDx;
	    	}
		    
	    	// increment g by whole amount slope_b, and correct for accumulated error if needed  
	    	g+=whole_step_g;
	    	if (p_g < 0)
	    		p_g += twoDg;
	    	else 
	    	{
	    		g+=step_g;
	    		p_g += twoDgMinusDx;
	    	}
		    
	    	// increment b by whole amount slope_b, and correct for accumulated error if needed
	    	b+=whole_step_b;
	    	if (p_b < 0)
	    		p_b += twoDb;
	    	else 
	    	{
	    		b+=step_b;
	    		p_b += twoDbMinusDx;
	    	}
		    
	    	if(x_y_role_swapped)
	    	{
	    		if(x>=0 && x<buff.getHeight() && y>=0 && y<buff.getWidth() && z > dBuff[y][x]) {
	    			buff.setRGB(y, buff.getHeight()-x-1, (r<<16) | (g<<8) | b);
	    			dBuff[y][x] = z;
	    		}
	    	}
	    	else
	    	{
	    		if(y>=0 && y<buff.getHeight() && x>=0 && x<buff.getWidth() && z > dBuff[x][y]) {
	    			buff.setRGB(x, buff.getHeight()-y-1, (r<<16) | (g<<8) | b);
	    			dBuff[x][y] = z;
	    		}
	    	}
	    }
	}

	/**********************************************************************
	 * Draws a filled triangle. 
	 * The triangle may be filled using flat fill or smooth fill. 
	 * This routine fills columns of pixels within the left-hand part, 
	 * and then the right-hand part of the triangle.
	 *   
	 *	                         *
	 *	                        /|\
	 *	                       / | \
	 *	                      /  |  \
	 *	                     *---|---*
	 *	            left-hand       right-hand
	 *	              part             part
	 *
	 * @param buff
	 *          Buffer object.
	 * @param p1
	 *          First given vertex of the triangle.
	 * @param p2
	 *          Second given vertex of the triangle.
	 * @param p3
	 *          Third given vertex of the triangle.
	 * @param do_smooth
	 *          Flag indicating whether flat fill or smooth fill should be used.                   
	 */
	public static void drawTriangle(BufferedImage buff, float[][] dBuff, Point2D p1, Point2D p2, Point2D p3, boolean do_smooth)
	{
	    // sort the triangle vertices by ascending x value
	    Point2D p[] = sortTriangleVerts(p1,p2,p3);
	    
	    
	    int x; 
	    float y_a, y_b;
	    float dy_a, dy_b;
	    float z_a, z_b;
	    float dz_a, dz_b;
	    float dr_a=0, dg_a=0, db_a=0, dr_b=0, dg_b=0, db_b=0;
	    
	    Point2D side_a = new Point2D(p[0]), side_b = new Point2D(p[0]);
	    
	    if(!do_smooth)
	    {
	    	side_a.c = new ColorType(p1.c);
	    	side_b.c = new ColorType(p1.c);
	    }
	    
	    y_b = p[0].y; z_b = p[0].d;
	    dy_b = ((float)(p[2].y - p[0].y))/(p[2].x - p[0].x);
	    dz_b = (p[2].d - p[0].d)/(p[2].x-p[0].x);
	    
	    if(do_smooth)
	    {
	    	// calculate slopes in r, g, b for segment b
	    	dr_b = ((float)(p[2].c.r - p[0].c.r))/(p[2].x - p[0].x);
	    	dg_b = ((float)(p[2].c.g - p[0].c.g))/(p[2].x - p[0].x);
	    	db_b = ((float)(p[2].c.b - p[0].c.b))/(p[2].x - p[0].x);
	    }
	    
	    // if there is a left-hand part to the triangle then fill it
	    if(p[0].x != p[1].x)
	    {
	    	y_a = p[0].y; z_a = p[0].d;
	    	dy_a = ((float)(p[1].y - p[0].y))/(p[1].x - p[0].x);
	    	dz_a = (p[1].d - p[0].d)/(p[1].x - p[0].x);
	    	
	    	if(do_smooth)
	    	{
	    		// calculate slopes in r, g, b for segment a
	    		dr_a = ((float)(p[1].c.r - p[0].c.r))/(p[1].x - p[0].x);
	    		dg_a = ((float)(p[1].c.g - p[0].c.g))/(p[1].x - p[0].x);
	    		db_a = ((float)(p[1].c.b - p[0].c.b))/(p[1].x - p[0].x);
	    	}
		    
	    	
		    // loop over the columns for left-hand part of triangle
		    // filling from side a to side b of the span
		    for(x = p[0].x; x < p[1].x; ++x)
		    {
		    	drawLine(buff, dBuff, side_a, side_b);
		    	
		    	++side_a.x;
		    	++side_b.x;
		    	y_a += dy_a;
		    	y_b += dy_b;
		    	z_a += dz_a;
		    	z_b += dz_b;
		    	side_a.y = (int)y_a;
		    	side_b.y = (int)y_b;
		    	side_a.d = z_a;
		    	side_b.d = z_b;
		    	if(do_smooth)
		    	{
		    		side_a.c.r +=dr_a;
		    		side_b.c.r +=dr_b;
		    		side_a.c.g +=dg_a;
		    		side_b.c.g +=dg_b;
		    		side_a.c.b +=db_a;
		    		side_b.c.b +=db_b;
		    	}
		    }
	    }
	    
	    // there is no right-hand part of triangle
	    if(p[1].x == p[2].x)
	    	return;
	    
	    // set up to fill the right-hand part of triangle 
	    // replace segment a
	    side_a = new Point2D(p[1]);
	    if(!do_smooth)
	    	side_a.c =new ColorType(p1.c);
	    
	    y_a = p[1].y; z_a = p[1].d;
	    dy_a = ((float)(p[2].y - p[1].y))/(p[2].x - p[1].x);
	    dz_a = (p[2].d - p[1].d)/(p[2].x - p[1].x);
	    
	    if(do_smooth)
	    {
	    	// calculate slopes in r, g, b for replacement for segment a
	    	dr_a = ((float)(p[2].c.r - p[1].c.r))/(p[2].x - p[1].x);
	    	dg_a = ((float)(p[2].c.g - p[1].c.g))/(p[2].x - p[1].x);
	    	db_a = ((float)(p[2].c.b - p[1].c.b))/(p[2].x - p[1].x);
	    }

	    // loop over the columns for right-hand part of triangle
	    // filling from side a to side b of the span
	    for(x = p[1].x; x <= p[2].x; ++x)
	    {
	    	drawLine(buff, dBuff, side_a, side_b);
		    
	    	++side_a.x;
	    	++side_b.x;
	    	y_a += dy_a;
	    	y_b += dy_b;
	    	z_a += dz_a;
	    	z_b += dz_b;
	    	side_a.y = (int)y_a;
	    	side_b.y = (int)y_b;
	    	side_a.d = z_a;
	    	side_b.d = z_b;
	    	if(do_smooth)
	    	{
	    		side_a.c.r +=dr_a;
	    		side_b.c.r +=dr_b;
	    		side_a.c.g +=dg_a;
	    		side_b.c.g +=dg_b;
	    		side_a.c.b +=db_a;
	    		side_b.c.b +=db_b;
	    	}
	    }
	}
	
	
	// interpolates the normal of a point and sums all light sources on the pixel.
	public static void drawPhongLine(BufferedImage buff, float[][] dBuff, Point2D p1, Point2D p2, boolean[] breaker, Material mat, Point3D v, AmbientLight light1, InfiniteLight light2, PointLight light3, PointLight light4)
	{
		int x0=p1.x, y0=p1.y; float z0=p1.d;
	    int xEnd=p2.x, yEnd=p2.y; float zEnd=p2.d;
	    int dx = Math.abs(xEnd - x0),  dy = Math.abs(yEnd - y0); float dz = Math.abs(zEnd - z0);
	    
	    // if slope is greater than 1, then swap the role of x and y
	    boolean x_y_role_swapped = (dy > dx); 
	    if(x_y_role_swapped)
	    {	
	    	x0=p1.y; 
	    	y0=p1.x;
	    	xEnd=p2.y; 
	    	yEnd=p2.x;
	    	dx = Math.abs(xEnd - x0);
	    	dy = Math.abs(yEnd - y0);
	    }
	    
	    // initialize the decision parameter and increments
	    int p = 2 * dy - dx;
	    int twoDy = 2 * dy,  twoDyMinusDx = 2 * (dy - dx);
	    int x=x0, y=y0; float z=z0;
	    
	    // set step increment to be positive or negative
	    int step_x = x0<xEnd ? 1 : -1;
	    int step_y = y0<yEnd ? 1 : -1;
	    float step_z = z0<yEnd ? 1 : -1;
	    
	    // deal with setup for color interpolation
	    // first get x,y,z norm values at the end points
	    float nx0=p1.n.x, nxEnd=p2.n.x;
	    float ny0=p1.n.y, nyEnd=p2.n.y;
	    float nz0=p1.n.z, nzEnd=p2.n.z;
	    
	    // compute the change in x,y,z of the norm 
	    float dnx=Math.abs(nxEnd-nx0), dny=Math.abs(nyEnd-ny0), dnz=Math.abs(nzEnd-nz0);
	    
	    // set step increment to be positive or negative 
	    int step_nx = nx0<nxEnd ? 1 : -1;
	    int step_ny = ny0<nyEnd ? 1 : -1;
	    int step_nz = nz0<nzEnd ? 1 : -1;
	    
	    // compute whole step in each norm axis that is taken each time through loop
	    float whole_step_nx = step_nx*(dnx/dx);
	    float whole_step_ny = step_ny*(dny/dx);
	    float whole_step_nz = step_nz*(dnz/dx);
	    
	    // compute remainder, which will be corrected depending on decision parameter
	    dnx=dnx%dx;
	    dny=dny%dx; 
	    dnz=dnz%dx;
	    
	    // initialize decision parameters for norm x, norm y, and nrom z
	    float p_nx = 2 * dnx - dx;
	    float twoDnx = 2 * dnx,  twoDnxMinusDx = 2 * (dnx - dx);
	    float nx=nx0;
	    
	    float p_ny = 2 * dny - dx;
	    float twoDny = 2 * dny,  twoDnyMinusDx = 2 * (dny - dx);
	    float ny=ny0;
	    
	    float p_nz = 2 * dnz - dx;
	    float twoDnz = 2 * dnz,  twoDnzMinusDx = 2 * (dnz - dx);
	    float nz=nz0;
	    
	    // draw start pixel
	    if(x_y_role_swapped)
	    {
	    	if(x>=0 && x<buff.getHeight() && y>=0 && y<buff.getWidth() && z > dBuff[y][x]) {
	    		
	    		// All conditional blocks of this nature take the current lights that are on,
	    		// and sum the illumination values based on the interpolated pixel's norm.
	    		
	    		// This specific function is a repeat of illuminate() in Lab_PA4.java
	    		ColorType result = new ColorType();
	    		Point3D norm = new Point3D(nx,ny,nz);
    			norm.normalize();
    			
	    		for (int i = 0; i < breaker.length; i++) {
	    			// set RGB values to zero
	    			float rn = 0f;
	    			float gn = 0f;
	    			float bn = 0f;
	    			
	    			// if a specific light is on
	    			if(breaker[i]) {
	    				ColorType temp = new ColorType();
	    				
	    				switch (i) {
	    				case 0:
	    					// ambient light
	    					temp = light1.applyLight(mat);
	    					rn = temp.r;
	    					gn = temp.g;
	    					bn = temp.b;
	    					break;
	    				case 1:
	    					// infinite light
	    					temp = light2.applyLight(mat, v, norm);
	    					rn = temp.r;
	    					gn = temp.g;
	    					bn = temp.b;
	    					break;
	    				case 2:
	    					// point light 1
	    					temp = light3.applyLight(mat, v, norm, new Point3D(x,y,z));
	    					rn = temp.r;
	    					gn = temp.g;
	    					bn = temp.b;
	    					break;
	    				case 3:
	    					//point light 2
	    					temp = light4.applyLight(mat, v, norm, new Point3D(x,y,z));
	    					rn = temp.r;
	    					gn = temp.g;
	    					bn = temp.b;
	    					break;
	    				default:
	    					break;
	    				}
	    			}
	    			
	    			result.r += rn;
	    			result.g += gn;
	    			result.b += bn;
	    		}
	    		result.clamp();
	    		
	    		// set pixel's color to summed illumination's RGB values
	    		buff.setRGB(y, buff.getHeight()-x-1, (result.getR_int()<<16) | (result.getG_int()<<8) | result.getB_int());
	    		dBuff[y][x] = z;
	    	}
	    }
	    else
	    {
	    	if(y>=0 && y<buff.getHeight() && x>=0 && x<buff.getWidth() && z > dBuff[x][y]) {
	    		
	    		ColorType result = new ColorType();
	    		Point3D norm = new Point3D(nx,ny,nz);
    			norm.normalize();
    			
	    		for (int i = 0; i < breaker.length; i++) {
	    			float rn = 0f;
	    			float gn = 0f;
	    			float bn = 0f;
	    			
	    			if(breaker[i]) {
	    				ColorType temp = new ColorType();
	    				
	    				switch (i) {
	    				case 0:
	    					temp = light1.applyLight(mat);
	    					rn = temp.r;
	    					gn = temp.g;
	    					bn = temp.b;
	    					break;
	    				case 1:
	    					temp = light2.applyLight(mat, v, norm);
	    					rn = temp.r;
	    					gn = temp.g;
	    					bn = temp.b;
	    					break;
	    				case 2:
	    					temp = light3.applyLight(mat, v, norm, new Point3D(x,y,z));
	    					rn = temp.r;
	    					gn = temp.g;
	    					bn = temp.b;
	    					break;
	    				case 3:
	    					temp = light4.applyLight(mat, v, norm, new Point3D(x,y,z));
	    					rn = temp.r;
	    					gn = temp.g;
	    					bn = temp.b;
	    					break;
	    				default:
	    					break;
	    				}
	    			}
	    			
	    			result.r += rn;
	    			result.g += gn;
	    			result.b += bn;
	    		}
	    		result.clamp();
	    		
	    		buff.setRGB(x, buff.getHeight()-y-1, (result.getR_int()<<16) | (result.getG_int()<<8) | result.getB_int());
	    		dBuff[x][y] = z;
	    	}
	    }
	    
	    while (x != xEnd) 
	    {
	    	// increment x, y, and z
	    	x+=step_x;
	    	if (p < 0)
	    		p += twoDy;
	    	else 
	    	{
	    		y += step_y;
	    		p += twoDyMinusDx;
	    		z += step_z;
	    	}
		        
	    	// increment norm x by whole amount slope_nx, and correct for accumulated error if needed
	    	nx+=whole_step_nx;
	    	if (p_nx < 0)
	    		p_nx += twoDnx;
	    	else 
	    	{
	    		nx+=step_nx;
	    		p_nx += twoDnxMinusDx;
	    	}
		    
	    	// increment norm y by whole amount slope_ny, and correct for accumulated error if needed  
	    	ny+=whole_step_ny;
	    	if (p_ny < 0)
	    		p_ny += twoDny;
	    	else 
	    	{
	    		ny+=step_ny;
	    		p_ny += twoDnyMinusDx;
	    	}
		    
	    	// increment norm z by whole amount slope_nz, and correct for accumulated error if needed
	    	nz+=whole_step_nz;
	    	if (p_nz < 0)
	    		p_nz += twoDnz;
	    	else 
	    	{
	    		nz+=step_nz;
	    		p_nz += twoDnzMinusDx;
	    	}
		    
	    	if(x_y_role_swapped)
	    	{
	    		if(x>=0 && x<buff.getHeight() && y>=0 && y<buff.getWidth() && z > dBuff[y][x]) {


	    			ColorType result = new ColorType();
	    			Point3D norm = new Point3D(nx,ny,nz);
	    			norm.normalize();
		    		
		    		for (int i = 0; i < breaker.length; i++) {
		    			float rn = 0f;
		    			float gn = 0f;
		    			float bn = 0f;
		    			
		    			if(breaker[i]) {
		    				ColorType temp = new ColorType();
		    				
		    				switch (i) {
		    				case 0:
		    					temp = light1.applyLight(mat);
		    					rn = temp.r;
		    					gn = temp.g;
		    					bn = temp.b;
		    					break;
		    				case 1:
		    					temp = light2.applyLight(mat, v, norm);
		    					rn = temp.r;
		    					gn = temp.g;
		    					bn = temp.b;
		    					break;
		    				case 2:
		    					temp = light3.applyLight(mat, v, norm, new Point3D(x,y,z));
		    					rn = temp.r;
		    					gn = temp.g;
		    					bn = temp.b;
		    					break;
		    				case 3:
		    					temp = light4.applyLight(mat, v, norm, new Point3D(x,y,z));
		    					rn = temp.r;
		    					gn = temp.g;
		    					bn = temp.b;
		    					break;
		    				default:
		    					break;
		    				}
		    			}
		    			
		    			result.r += rn;
		    			result.g += gn;
		    			result.b += bn;
		    		}
		    		result.clamp();
		    		
		    		buff.setRGB(y, buff.getHeight()-x-1, (result.getR_int()<<16) | (result.getG_int()<<8) | result.getB_int());
		    		dBuff[y][x] = z;
	    		}
	    	}
	    	else
	    	{
	    		if(y>=0 && y<buff.getHeight() && x>=0 && x<buff.getWidth() && z > dBuff[x][y]) {
	    			
	    			ColorType result = new ColorType();
	    			Point3D norm = new Point3D(nx,ny,nz);
	    			norm.normalize();
	    			
		    		for (int i = 0; i < breaker.length; i++) {
		    			float rn = 0f;
		    			float gn = 0f;
		    			float bn = 0f;
		    			
		    			if(breaker[i]) {
		    				ColorType temp = new ColorType();
		    				
		    				switch (i) {
		    				case 0:
		    					temp = light1.applyLight(mat);
		    					rn = temp.r;
		    					gn = temp.g;
		    					bn = temp.b;
		    					break;
		    				case 1:
		    					temp = light2.applyLight(mat, v, norm);
		    					rn = temp.r;
		    					gn = temp.g;
		    					bn = temp.b;
		    					break;
		    				case 2:
		    					temp = light3.applyLight(mat, v, norm, new Point3D(x,y,z));
		    					rn = temp.r;
		    					gn = temp.g;
		    					bn = temp.b;
		    					break;
		    				case 3:
		    					temp = light4.applyLight(mat, v, norm, new Point3D(x,y,z));
		    					rn = temp.r;
		    					gn = temp.g;
		    					bn = temp.b;
		    					break;
		    				default:
		    					break;
		    				}
		    			}
		    			
		    			result.r += rn;
		    			result.g += gn;
		    			result.b += bn;
		    		}
		    		result.clamp();
		    		
		    		buff.setRGB(x, buff.getHeight()-y-1, (result.getR_int()<<16) | (result.getG_int()<<8) | result.getB_int());
		    		dBuff[x][y] = z;
	    		}
	    	}
	    }
	}
	
	
	public static void drawPhongTriangle(BufferedImage buff, float[][] dBuff, Point2D p1, Point2D p2, Point2D p3, boolean[] breaker, Material mat, Point3D v, AmbientLight light1, InfiniteLight light2, PointLight light3, PointLight light4)
	{
	    // sort the triangle vertices by ascending x value
		 // sort the triangle vertices by ascending x value
	    Point2D p[] = sortTriangleVerts(p1,p2,p3);
	    
	    
	    int x; 
	    float y_a, y_b;
	    float dy_a, dy_b;
	    float z_a, z_b;
	    float dz_a, dz_b;
	    float dnx_a=0, dny_a=0, dnz_a=0, dnx_b=0, dny_b=0, dnz_b=0;
	    
	    Point2D side_a = new Point2D(p[0]), side_b = new Point2D(p[0]);
	    
	    y_b = p[0].y; z_b = p[0].d;
	    dy_b = ((float)(p[2].y - p[0].y))/(p[2].x - p[0].x);
	    dz_b = (p[2].d - p[0].d)/(p[2].x-p[0].x);
	    
	    // calculate slopes in x, y, z for segment b's norm
	    dnx_b = ((float)(p[2].n.x - p[0].n.x))/(p[2].x - p[0].x);
	    dny_b = ((float)(p[2].n.y - p[0].n.y))/(p[2].x - p[0].x);
	    dnz_b = ((float)(p[2].n.z - p[0].n.z))/(p[2].x - p[0].x);
	    
	    
	    // if there is a left-hand part to the triangle then fill it
	    if(p[0].x != p[1].x)
	    {
	    	y_a = p[0].y; z_a = p[0].d;
	    	dy_a = ((float)(p[1].y - p[0].y))/(p[1].x - p[0].x);
	    	dz_a = (p[1].d - p[0].d)/(p[1].x - p[0].x);
	    	
	    	// calculate slopes in x, y, z for segment a's norm
	    	dnx_a = ((float)(p[1].n.x - p[0].n.x))/(p[1].x - p[0].x);
	    	dny_a = ((float)(p[1].n.y - p[0].n.y))/(p[1].x - p[0].x);
	    	dnz_a = ((float)(p[1].n.z - p[0].n.z))/(p[1].x - p[0].x);
	    	
		    
	    	
		    // loop over the columns for left-hand part of triangle
		    // filling from side a to side b of the span
		    for(x = p[0].x; x < p[1].x; ++x)
		    {
		    	drawPhongLine(buff, dBuff, side_a, side_b, breaker, mat, v, light1, light2, light3, light4);
		    	
		    	++side_a.x;
		    	++side_b.x;
		    	y_a += dy_a;
		    	y_b += dy_b;
		    	z_a += dz_a;
		    	z_b += dz_b;
		    	side_a.y = (int)y_a;
		    	side_b.y = (int)y_b;
		    	side_a.d = z_a;
		    	side_b.d = z_b;

	    		side_a.n.x +=dnx_a;
	    		side_b.n.x +=dnx_b;
	    		side_a.n.y +=dny_a;
	    		side_b.n.y +=dny_b;
	    		side_a.n.z +=dnz_a;
	    		side_b.n.z +=dnz_b;
	    		
	    		side_a.n.normalize();
	    		side_b.n.normalize();

		    }
	    }
	    
	    // there is no right-hand part of triangle
	    if(p[1].x == p[2].x)
	    	return;
	    
	    // set up to fill the right-hand part of triangle 
	    // replace segment a
	    side_a = new Point2D(p[1]);

	    
	    y_a = p[1].y; z_a = p[1].d;
	    dy_a = ((float)(p[2].y - p[1].y))/(p[2].x - p[1].x);
	    dz_a = (p[2].d - p[1].d)/(p[2].x - p[1].x);
	    

    	// calculate slopes in r, g, b for replacement for segment a
    	dnx_a = ((float)(p[2].n.x - p[1].n.x))/(p[2].x - p[1].x);
    	dny_a = ((float)(p[2].n.y - p[1].n.y))/(p[2].x - p[1].x);
    	dnz_a = ((float)(p[2].n.z - p[1].n.z))/(p[2].x - p[1].x);


	    // loop over the columns for right-hand part of triangle
	    // filling from side a to side b of the span
	    for(x = p[1].x; x <= p[2].x; ++x)
	    {
	    	drawPhongLine(buff, dBuff, side_a, side_b, breaker, mat, v, light1, light2, light3, light4);
		    
	    	++side_a.x;
	    	++side_b.x;
	    	y_a += dy_a;
	    	y_b += dy_b;
	    	z_a += dz_a;
	    	z_b += dz_b;
	    	side_a.y = (int)y_a;
	    	side_b.y = (int)y_b;
	    	side_a.d = z_a;
	    	side_b.d = z_b;

    		side_a.n.x +=dnx_a;
    		side_b.n.x +=dnx_b;
    		side_a.n.y +=dny_a;
    		side_b.n.y +=dny_b;
    		side_a.n.z +=dnz_a;
    		side_b.n.z +=dnz_b;

    		side_a.n.normalize();
    		side_b.n.normalize();
	    }
	}

	/**********************************************************************
	 * Helper function to bubble sort triangle vertices by ascending x value.
	 * 
	 * @param p1
	 *          First given vertex of the triangle.
	 * @param p2
	 *          Second given vertex of the triangle.
	 * @param p3
	 *          Third given vertex of the triangle.
	 * @return 
	 *          Array of 3 points, sorted by ascending x value.
	 */
	private static Point2D[] sortTriangleVerts(Point2D p1, Point2D p2, Point2D p3)
	{
	    Point2D pts[] = {p1, p2, p3};
	    Point2D tmp;
	    int j=0;
	    boolean swapped = true;
	         
	    while (swapped) 
	    {
	    	swapped = false;
	    	j++;
	    	for (int i = 0; i < 3 - j; i++) 
	    	{                                       
	    		if (pts[i].x > pts[i + 1].x) 
	    		{                          
	    			tmp = pts[i];
	    			pts[i] = pts[i + 1];
	    			pts[i + 1] = tmp;
	    			swapped = true;
	    		}
	    	}                
	    }
	    return(pts);
	}

}
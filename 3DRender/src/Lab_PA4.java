//****************************************************************************
//       Main Program for 3D renderer PS3 CS480
//****************************************************************************
// Description: 
//   
//     LEFTMOUSE DRAG: Change camera position
//
//     The following keys control the program:
//
//      Q,q: quit 
//      C,c: clear polygon (set vertex count=0)
//		R,r: randomly change the color
//		S,s: toggle the smooth shading
//		T,t: show testing examples (toggles between smooth shading and flat shading test cases)
//		F,f: render objects with flat shading
//		G,g: render objects with Gouraurd shading
// 		P,p: render objects with Phong shading
// 		L,l: turn on primer for light switchboard
//		A,a: disable/enable ambient coefficient
//		D,d: disable/enable diffuse coefficient
//		S,s: disable/enable spectral coefficient
//		1: 	 disable/enable ambient lighting
//      2:   disable/enable infinite lighting
//		3:	 disable/enable spotlight 1
//		4:	 disable/enable spotlight 2
//		>:	 increase the step number for examples
//		<:   decrease the step number for examples
//     +,-:  increase or decrease spectral exponent
//
//****************************************************************************
// History :
//   Aug 2004 Created by Jianming Zhang based on the C
//   code by Stan Sclaroff
//   Nov 2014 modified to include test cases
//   Nov 5, 2019 Updated by Zezhou Sun
//   Dec 4, 2020 Received skeleton code, completed assignment - Daniel Kehr (Completed the project, created test cases)


import javax.swing.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.*; 
import java.awt.image.*;
//import java.io.File;
//import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

//import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;//for new version of gl
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;

import com.jogamp.opengl.util.FPSAnimator;//for new version of gl


public class Lab_PA4 extends JFrame
	implements GLEventListener, KeyListener, MouseListener, MouseMotionListener
{
	
	private static final long serialVersionUID = 1L;
	private final int DEFAULT_WINDOW_WIDTH=512;
	private final int DEFAULT_WINDOW_HEIGHT=512;
	private final float DEFAULT_LINE_WIDTH=1.0f;

	private GLCapabilities capabilities;
	private GLCanvas canvas;
	private FPSAnimator animator;

	private int numTestCase;
	private int testCase;
	private BufferedImage buff;

	// depth-buffer matrix
	private float[][] dBuff;
	@SuppressWarnings("unused")
	private ColorType color;
	private Random rng;
	
	// specular exponent for materials
	private int ns=5; 
	
	private ArrayList<Point2D> lineSegs;
	private ArrayList<Point2D> triangles;
	
	// type of scene rendering (flat/Gouraud/Phong)
	private String rendering;

	// On/Off for material coefficients
	private boolean amb = true;
	private boolean diff = true;
	private boolean spec = true;
	
	// All lights in the scene
	AmbientLight light1;
	InfiniteLight light2;
	PointLight light3;
	PointLight light4;
	
	// Boolean array to determine if given light is on
	private boolean[] breaker = {true, true, true, true};
	// checks if 'L' has been pressed to initiate switching lights
	private boolean primer = false;
	
	
	private int Nsteps;

	/** The quaternion which controls the rotation of the world. */
    private Quaternion viewing_quaternion = new Quaternion();
    private Point3D viewing_center = new Point3D((float)(DEFAULT_WINDOW_WIDTH/2),(float)(DEFAULT_WINDOW_HEIGHT/2),(float)0.0);
    /** The last x and y coordinates of the mouse press. */
    private int last_x = 0, last_y = 0;
    /** Whether the world is being rotated. */
    private boolean rotate_world = false;
    
    /** Random colors **/
    private ColorType[] colorMap = new ColorType[100];
    private Random rand = new Random();
    
	public Lab_PA4()
	{
	    capabilities = new GLCapabilities(null);
	    capabilities.setDoubleBuffered(true);  // Enable Double buffering

	    canvas  = new GLCanvas(capabilities);
	    canvas.addGLEventListener(this);
	    canvas.addMouseListener(this);
	    canvas.addMouseMotionListener(this);
	    canvas.addKeyListener(this);
	    canvas.setAutoSwapBufferMode(true); // true by default. Just to be explicit
	    canvas.setFocusable(true);
	    getContentPane().add(canvas);

	    animator = new FPSAnimator(canvas, 60); // drive the display loop @ 60 FPS

	    numTestCase = 3;
	    testCase = 0;
	    Nsteps = 12;

	    setTitle("CS480/680 Lab for PA4");
	    setSize( DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setVisible(true);
	    setResizable(false);
	    
	    rng = new Random();
	    color = new ColorType(1.0f,0.0f,0.0f);
	    lineSegs = new ArrayList<Point2D>();
	    triangles = new ArrayList<Point2D>();
	    
	    // set rendering to flat initially
	    rendering = "flat";
	    
	    for (int i=0; i<100; i++) {
	    	this.colorMap[i] = new ColorType(i*0.005f+0.5f, i*-0.005f+1f, i*0.0025f+0.75f);
	    }
	}

	public void run()
	{
		animator.start();
	}

	public static void main( String[] args )
	{
		Lab_PA4 P = new Lab_PA4();
	    P.run();
	}

	//*********************************************** 
	//  GLEventListener Interfaces
	//*********************************************** 
	public void init( GLAutoDrawable drawable) 
	{
	    GL gl = drawable.getGL();
	    gl.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f);
	    gl.glLineWidth( DEFAULT_LINE_WIDTH );
	    Dimension sz = this.getContentPane().getSize();
	    buff = new BufferedImage(sz.width,sz.height,BufferedImage.TYPE_3BYTE_BGR);
	    clearPixelBuffer();
	}

	// Redisplaying graphics
	public void display(GLAutoDrawable drawable)
	{
		
		// define a depth buffer matrix of pixels set to infinity
		resetDepthBuffer();
	    GL2 gl = drawable.getGL().getGL2();
	    WritableRaster wr = buff.getRaster();
	    DataBufferByte dbb = (DataBufferByte) wr.getDataBuffer();
	    byte[] data = dbb.getData();

	    gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 1);
	    gl.glDrawPixels (buff.getWidth(), buff.getHeight(),
                GL2.GL_BGR, GL2.GL_UNSIGNED_BYTE,
                ByteBuffer.wrap(data));
        drawTestCase();
	}
	
	// refresh depth buffer
	public void resetDepthBuffer() {
		dBuff = new float[DEFAULT_WINDOW_WIDTH][DEFAULT_WINDOW_HEIGHT];
		
		// fill depth buffer with values of negative infinity
	    for (float[] row: dBuff)
	        Arrays.fill(row, Float.NEGATIVE_INFINITY);
	}

	// Window size change
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h)
	{
		// deliberately left blank
	}
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
	      boolean deviceChanged)
	{
		// deliberately left blank
	}
	
	void clearPixelBuffer()
	{
		resetDepthBuffer();
		lineSegs.clear();
    	triangles.clear();
		Graphics2D g = buff.createGraphics();
	    g.setColor(Color.BLACK);
	    g.fillRect(0, 0, buff.getWidth(), buff.getHeight());
	    g.dispose();
	}
	
	// drawTest
	void drawTestCase()
	{  
		/* clear the window and vertex state */
		clearPixelBuffer();
		
		// objects in the scenes
		Sphere3D sphere;
		Ellipsoid3D ellipsoid;
		Cylinder3D cylinder;
		Cylinder3D cube;
		Torus3D torus;
		
		// material coefficient of scene objects
		
		// ambient
		ColorType sphere_ka;
        ColorType cylinder_ka;
        ColorType cube_ka;
        ColorType torus_ka;
        // diffuse
        ColorType sphere_kd;
        ColorType cylinder_kd;
        ColorType cube_kd;
        ColorType torus_kd;
        // spectral
        ColorType sphere_ks;
        ColorType cylinder_ks;
        ColorType cube_ks;
        ColorType torus_ks;
		
        float radius;
        
		switch(testCase) {
		case 0:
			
			// TEST CASE 1 : SHAPES ORDERED IN GRID WITH VARYING MATERIAL
			
			light1 = new AmbientLight(new ColorType (1.0f, 1.0f, 1.0f));
			light2 = new InfiniteLight(new ColorType(1.0f,1.0f,1.0f), new Point3D(-1f, .03f, 0f));
			light3 = new PointLight(new ColorType(1.0f,1.0f,1.0f), new Point3D(-1f, 0f, 0f), new Point3D(358, 358f, 480f), 40f);
			light4 = new PointLight(new ColorType(1.0f,1.0f,1.0f), new Point3D(0f, 1f, 0f), new Point3D(100f, -100f, 0f), 50f);
			
			radius = (float)50.0;
	        sphere = new Sphere3D((float)128.0, (float)128.0, (float)0.0, (float)1.5*radius, Nsteps, Nsteps);
	        cylinder = new Cylinder3D((float)358.0, (float)128.0, (float)0.0, 100f, (float)1.5*radius, Nsteps, Nsteps);
	        cube = new Cylinder3D((float)128.0, (float)358.0, (float)0.0, 100f, (float)1.5*radius, 4, 5);
	        torus = new Torus3D((float)358.0, (float)358.0, (float)0.0, (float)5*radius, (float)0.5*radius, Nsteps, Nsteps);     
	        
			
	        sphere_ka = (amb) ? new ColorType(0.05f,0.05f,0.05f) : new ColorType();
	        cylinder_ka = (amb) ? new ColorType(0.05f,0.05f,0.05f) : new ColorType();
	        cube_ka = (amb) ? new ColorType(0.05f,0.05f,0.05f) : new ColorType();
	        torus_ka = (amb) ? new ColorType(0.05f,0.05f,0.05f) : new ColorType();
	        
	        sphere_kd = (diff) ? new ColorType(0.02f,0.0f,0.0f) : new ColorType();
	        cylinder_kd = (diff) ? new ColorType(0.5f,0.3f,0.1f) : new ColorType();
	        cube_kd = (diff) ? new ColorType(0.1f,.1f,1f) : new ColorType();
	        torus_kd = (diff) ? new ColorType(0.3f,1f,0.3f) : new ColorType();
	        
	        sphere_ks = (spec) ? new ColorType(1.0f,0.0f, 0.0f) : new ColorType();
	        cylinder_ks = (spec) ? new ColorType(0.3f,0.3f,0.3f) : new ColorType();;
	        cube_ks = (spec) ? new ColorType(0.01f,0.08f,0.02f) : new ColorType();;
	        torus_ks = (spec) ? new ColorType(0.1f,0.6f,0.1f) : new ColorType();;
	               
	        
	        Mesh3D[] objects1 = {sphere.mesh, cylinder.mesh, cube.mesh, torus.mesh};
	        Material[] materials1 = {
	        		new Material(sphere_ka, sphere_kd, sphere_ks,ns),
	        		new Material(cylinder_ka, cylinder_kd, cylinder_ks,ns),
	        		new Material(cube_ka, cube_kd, cube_ks,ns),
	        		new Material(torus_ka, torus_kd, torus_ks,ns),
	        		};
			
			shadeTest(false, materials1, objects1);
			break;
		case 1:
			
			// TEST CASE 2 : DONUT ON TABLE WITH CUP AND BOX
			
			light1 = new AmbientLight(new ColorType (1.0f, 1.0f, 1.0f));
			light2 = new InfiniteLight(new ColorType(.3f,0.0f,.3f), new Point3D(-1f, 0f, 0f));
			light3 = new PointLight(new ColorType(.30f,.30f,.30f), new Point3D(0, 0, -1f), new Point3D(200f, 350f, 200f), 10f);
			light4 = new PointLight(new ColorType(1.0f,1.0f,1.0f), new Point3D(-0.2f, -.02f, 1f), new Point3D(120f, 120f, -200f), 30f);
			
			radius = (float)50.0;
	        ellipsoid = new Ellipsoid3D(300f, 300f, -100f, (float)9*radius,(float)9*radius,(float).7*radius, Nsteps, Nsteps);
	        cylinder = new Cylinder3D(100f, 100f, 50f, 140f, (float)0.9*radius, Nsteps, Nsteps);
	        cube = new Cylinder3D(450f, 200f, 50f, 70f, (float)2*radius, 4, 5);
	        torus = new Torus3D(200f, 350f, 20f, (float)5*radius, (float)0.9*radius, Nsteps, Nsteps);     
	        
			
	        sphere_ka = (amb) ? new ColorType(0.05f,0.05f,0.05f) : new ColorType();
	        cylinder_ka = (amb) ? new ColorType(0.05f,0.05f,0.05f) : new ColorType();
	        cube_ka = (amb) ? new ColorType(0.05f,0.05f,0.05f) : new ColorType();
	        torus_ka = (amb) ? new ColorType(0.05f,0.05f,0.05f) : new ColorType();
	        
	        // The ellipsoid is given mostly mat material with a little gloss
	        sphere_kd = (diff) ? new ColorType(1.0f,1.0f,1.0f) : new ColorType();
	        cylinder_kd = (diff) ? new ColorType(0.5f,0.5f,0.8f) : new ColorType();
	        cube_kd = (diff) ? new ColorType(0.1f,2f,.2f) : new ColorType();
	        // The torus is given nearly entirely mat so as to look more like a donut
	        torus_kd = (diff) ? new ColorType(0.5f,0.3f,0.1f) : new ColorType();
	        
	        sphere_ks = (spec) ? new ColorType(.30f,.30f,.30f) : new ColorType();
	        // The cylinder is given higher spectral coefficients to imitate glass
	        cylinder_ks = (spec) ? new ColorType(0.8f,0.8f,2f) : new ColorType();;
	        // The box is given equal amounts of spectral and diffuse to look semi-glossy
	        cube_ks = (spec) ? new ColorType(0.8f,0.8f,0.2f) : new ColorType();;
	        torus_ks = (spec) ? new ColorType(0.1f,0.6f,0.1f) : new ColorType();;
	               
	        
	        Mesh3D[] objects2 = {ellipsoid.mesh, cylinder.mesh, cube.mesh, torus.mesh};
	        Material[] materials2 = {
	        		new Material(sphere_ka, sphere_kd, sphere_ks,ns),
	        		new Material(cylinder_ka, cylinder_kd, cylinder_ks,ns),
	        		new Material(cube_ka, cube_kd, cube_ks,ns),
	        		new Material(torus_ka, torus_kd, torus_ks,ns),
	        		};
			
			shadeTest(false, materials2, objects2);
			break;
		case 2:
			
			// TEST CASE 3: CYLINDER INSIDE TORUS
			
			light1 = new AmbientLight(new ColorType (0.2f, 0.90f, 0.40f));
			light2 = new InfiniteLight(new ColorType(0.8f,0.0f,0.8f), new Point3D(0f, -1f, 0f));
			light3 = new PointLight(new ColorType(1.0f,1.0f,1.0f), new Point3D(-1f, 0f, 0f), new Point3D(358, 358f, 480f), 40f);
			light4 = new PointLight(new ColorType(.8f,.8f,.8f), new Point3D(1f, 0f, 0f), new Point3D(400, 600f, 0f), 60f);
			
			radius = (float)50.0;
	        ellipsoid = new Ellipsoid3D(100f, 100f, 230f, (float)2*radius, (float)1.3*radius, (float)3*radius, Nsteps, Nsteps);
	        cylinder = new Cylinder3D(220f, 220f, 0f, 300f, (float)0.3*radius, Nsteps, Nsteps);
	        cube = new Cylinder3D(400f, 400f, -130f, 100f, (float)1.5*radius, 4, 5);
	        torus = new Torus3D(220f, 220f, 0f, (float).400*radius, (float)0.5*radius, Nsteps, Nsteps);     
	        
			
	        sphere_ka = (amb) ? new ColorType(0.05f,0.05f,0.05f) : new ColorType();
	        cylinder_ka = (amb) ? new ColorType(0.05f,0.05f,0.05f) : new ColorType();
	        cube_ka = (amb) ? new ColorType(0.05f,0.05f,0.05f) : new ColorType();
	        torus_ka = (amb) ? new ColorType(0.05f,0.05f,0.05f) : new ColorType();
	        
	        sphere_kd = (diff) ? new ColorType(1.0f,0.0f,0.0f) : new ColorType();
	        cylinder_kd = (diff) ? new ColorType(0.5f,0.3f,0.1f) : new ColorType();
	        cube_kd = (diff) ? new ColorType(1f,1f,1f) : new ColorType();
	        torus_kd = (diff) ? new ColorType(0.3f,0f,0.3f) : new ColorType();
	        
	        sphere_ks = (spec) ? new ColorType(2.0f,1.0f,1.0f) : new ColorType();
	        cylinder_ks = (spec) ? new ColorType(0.3f,0.3f,0.3f) : new ColorType();;
	        cube_ks = (spec) ? new ColorType(0.2f,0.2f,0.2f) : new ColorType();;
	        torus_ks = (spec) ? new ColorType(1f,0.1f,1f) : new ColorType();;
	               
	        
	        Mesh3D[] objects3 = {ellipsoid.mesh, cylinder.mesh, cube.mesh, torus.mesh};
	        Material[] materials3 = {
	        		new Material(sphere_ka, sphere_kd, sphere_ks,ns),
	        		new Material(cylinder_ka, cylinder_kd, cylinder_ks,ns),
	        		new Material(cube_ka, cube_kd, cube_ks,ns),
	        		new Material(torus_ka, torus_kd, torus_ks,ns),
	        		};
			
			shadeTest(false, materials3, objects3);
			break;
		}
		
	}


	//*********************************************** 
	//          KeyListener Interfaces
	//*********************************************** 
	public void keyTyped(KeyEvent key)
	{
	//      Q,q: quit 
	//      C,c: clear polygon (set vertex count=0)
	//		R,r: randomly change the color
	//		S,s: toggle the smooth shading
	//		T,t: show testing examples (toggles between smooth shading and flat shading test cases)
	//		F,f: render objects with flat shading
	//		G,g: render objects with Gouraurd shading
	// 		P,p: render objects with Phong shading
	// 		L,l: turn on primer for light switchboard
	//		A,a: disable/enable ambient coefficient
	//		D,d: disable/enable diffuse coefficient
	//		S,s: disable/enable spectral coefficient
	//		1: 	 disable/enable ambient lighting
	//      2:   disable/enable infinite lighting
	//		3:	 disable/enable spotlight 1
	//		4:	 disable/enable spotlight 2
	//		>:	 increase the step number for examples
	//		<:   decrease the step number for examples
	//     +,-:  increase or decrease spectral exponent

	    switch ( key.getKeyChar() ) 
	    {
	    case 'Q' :
	    case 'q' : 
	    	new Thread()
	    	{
	          	public void run() { animator.stop(); }
	        }.start();
	        System.exit(0);
	        break;
	    case 'R' :
	    case 'r' :
	    	color = new ColorType(rng.nextFloat(),rng.nextFloat(),
	    			rng.nextFloat());
	    	break;
	    case 'C' :
	    case 'c' :
	    	clearPixelBuffer();
	    	break;
	    case 'T' :
	    case 't' : 
	    	testCase = (testCase+1)%numTestCase;
	    	drawTestCase(); // loop through test cases
	        break;
	    case 'F' :
	    case 'f' : 
	    	rendering = "flat";
	    	drawTestCase(); // switch to flat rendering
	        break; 
	    case 'G' :
	    case 'g' : 
	    	rendering = "gouraurd";
	    	drawTestCase(); // switch to gouraurd rendering
	        break;
	    case 'P' :
	    case 'p' : 
	    	rendering = "phong";
	    	drawTestCase(); // switch to phong rendering
	        break;
	    case 'L' :
	    case 'l' : 
	    	String notice = (primer) ? "PRIMER OFF" : "PRIMER ON";
	    	System.out.println(notice);
	    	primer = (primer) ? false : true; // allow lights to be turned on or off
	        break; 
	    case 'A' :
	    case 'a' : 
	    	amb = (amb) ? false : true;
	    	drawTestCase(); // disable ambient coefficient
	        break;
	    case 'D' :
	    case 'd' : 
	    	diff= (diff) ? false : true;
	    	drawTestCase(); // disable diffuse coefficient
	        break;
	    case 'S' :
	    case 's' : 
	    	spec = (spec) ? false : true;
	    	drawTestCase(); // disable spectral coefficient
	        break;
	    case '1' :
	    	breaker[0] = (primer) ? (breaker[0] ? false : true) : breaker[0];
	    	displaySwitch();
	    	drawTestCase(); // turn on light 1
	        break; 
	    case '2' : 
	    	breaker[1] = (primer) ? (breaker[1] ? false : true) : breaker[1];
	    	displaySwitch();
	    	drawTestCase(); // turn on light 2
	        break;
	    case '3' : 
	    	breaker[2] = (primer) ? (breaker[2] ? false : true) : breaker[2];
	    	displaySwitch();
	    	drawTestCase(); // turn on light 3
	        break;
	    case '4' : 
	    	breaker[3] = (primer) ? (breaker[3] ? false : true) : breaker[3];
	    	displaySwitch();
	    	drawTestCase(); // turn on light 4
	        break;
	    case '<':  
	        Nsteps = Nsteps < 4 ? Nsteps: Nsteps / 2;
	        System.out.printf( "Nsteps = %d \n", Nsteps);
	        drawTestCase();
	        break;
	    case '>':
	        Nsteps = Nsteps > 190 ? Nsteps: Nsteps * 2;
	        System.out.printf( "Nsteps = %d \n", Nsteps);
	        drawTestCase();
	        break;
	    case '+':
	    	ns++;
	        drawTestCase();
	    	break;
	    case '-':
	    	if(ns>0)
	    		ns--;
	        drawTestCase();
	    	break;
	    default :
	        break;
	    }
	}

	public void keyPressed(KeyEvent key)
	{
	    switch (key.getKeyCode()) 
	    {
	    case KeyEvent.VK_ESCAPE:
	    	new Thread()
	        {
	    		public void run()
	    		{
	    			animator.stop();
	    		}
	        }.start();
	        System.exit(0);
	        break;
	        
	    // controls to translate camera
	    case KeyEvent.VK_LEFT:
	    	viewing_center.x += 10; 
	    	break;
	    case KeyEvent.VK_RIGHT:
	    	viewing_center.x -= 10;
	    	break;
	    case KeyEvent.VK_UP:
	    	viewing_center.y += 10;
	    	break;
	    case KeyEvent.VK_DOWN:
	    	viewing_center.y -= 10;
	    	break;
	    default:
	        break;
	    }
	}
	
	public void keyReleased(KeyEvent key)
	{
		// deliberately left blank
	}

	//************************************************** 
	// MouseListener and MouseMotionListener Interfaces
	//************************************************** 
	public void mouseClicked(MouseEvent mouse)
	{
		// deliberately left blank
	}
	  public void mousePressed(MouseEvent mouse)
	  {
	    int button = mouse.getButton();
	    if ( button == MouseEvent.BUTTON1 )
	    {
	      last_x = mouse.getX();
	      last_y = mouse.getY();
	      rotate_world = true;
	    }
	  }

	  public void mouseReleased(MouseEvent mouse)
	  {
	    int button = mouse.getButton();
	    if ( button == MouseEvent.BUTTON1 )
	    {
	      rotate_world = false;
	    }
	  }

	public void mouseMoved( MouseEvent mouse)
	{
		// Deliberately left blank
	}

	/**
	   * Updates the rotation quaternion as the mouse is dragged.
	   * 
	   * @param mouse
	   *          The mouse drag event object.
	   */
	  public void mouseDragged(final MouseEvent mouse) {
	    if (this.rotate_world) {
	      // get the current position of the mouse
	      final int x = mouse.getX();
	      final int y = mouse.getY();

	      // get the change in position from the previous one
	      final int dx = x - this.last_x;
	      final int dy = y - this.last_y;

	      // create a unit vector in the direction of the vector (dy, dx, 0)
	      final float magnitude = (float)Math.sqrt(dx * dx + dy * dy);
	      if(magnitude > 0.0001)
	      {
	    	  // define axis perpendicular to (dx,-dy,0)
	    	  // use -y because origin is in upper lefthand corner of the window
	    	  final float[] axis = new float[] { -(float) (dy / magnitude),
	    			  (float) (dx / magnitude), 0 };

	    	  // calculate appropriate quaternion
	    	  final float viewing_delta = 3.1415927f / 180.0f;
	    	  final float s = (float) Math.sin(0.5f * viewing_delta);
	    	  final float c = (float) Math.cos(0.5f * viewing_delta);
	    	  final Quaternion Q = new Quaternion(c, s * axis[0], s * axis[1], s
	    			  * axis[2]);
	    	  this.viewing_quaternion = Q.multiply(this.viewing_quaternion);

	    	  // normalize to counteract acccumulating round-off error
	    	  this.viewing_quaternion.normalize();

	    	  // save x, y as last x, y
	    	  
	    	  this.last_x = x;
	    	  this.last_y = y;
	          drawTestCase();
	      }
	    }

	  }
	  
	public void mouseEntered( MouseEvent mouse)
	{
		// Deliberately left blank
	}

	public void mouseExited( MouseEvent mouse)
	{
		// Deliberately left blank
	} 


	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}
	
	//************************************************** 
	// Test Cases
	// Nov 9, 2014 Stan Sclaroff -- removed line and triangle test cases
	//************************************************** 

	void shadeTest(boolean doSmooth, Material[] materials, Mesh3D[] objects){

        // view vector is defined along z axis
        // this example assumes simple othorgraphic projection
        // view vector is used in 
        //   (a) calculating specular lighting contribution
        //   (b) backface culling / backface rejection
        Point3D view_vector = new Point3D((float)0.0,(float)0.0,(float)1.0);
        
        // normal to the plane of a triangle
        // to be used in backface culling / backface rejection
        Point3D triangle_normal = new Point3D();
        
        // a triangle mesh
        Mesh3D mesh;
            
		int i, j, n, m;
		
		// temporary variables for triangle 3D vertices and 3D normals
		Point3D v0,v1, v2, n0, n1, n2;
		
		// projected triangle, with vertex colors
		Point2D[] tri = {new Point2D(), new Point2D(), new Point2D()};
	    
		// rotate the surface's 3D mesh using quaternion

		light2.rotateLight(viewing_quaternion, viewing_center);
		light3.rotateLight(viewing_quaternion, viewing_center);
		light4.rotateLight(viewing_quaternion, viewing_center);
		
		for (int k = 0; k < 4; k++) {
			
			mesh = objects[k];
			n = mesh.cols;
			m = mesh.rows;
			
			mesh.rotateMesh(viewing_quaternion, viewing_center);	
			// draw triangles for the current surface, using vertex colors
			for(i=0; i < m-1; ++i)
		    {
				for(j=0; j < n-1; ++j)
				{
					// ****************Implement Code here*******************//
					v0 = mesh.v[i][j];
					v1 = mesh.v[i+1][j];
					v2 = mesh.v[i+1][j+1];
					
					triangle_normal = computeTriangleNormal(v0,v1,v2);
					
					if(view_vector.dotProduct(triangle_normal) > 0.0)  // front-facing triangle?
					{	
						 
						// if rendering is gouraurd or phong, obtain normals for the three vertices
						if(rendering == "gouraurd" || rendering == "phong") {
							doSmooth = true;
							n0 = mesh.n[i][j];
							n1 = mesh.n[i+1][j];
							n2 = mesh.n[i+1][j+1];
							
							tri[0].c = illuminate(breaker, materials[k], view_vector, n0,v0);
							tri[0].n = n0;
							tri[0].n.normalize();
							tri[1].c = illuminate(breaker, materials[k], view_vector, n1,v1);
							tri[1].n = n1;
							tri[1].n.normalize();
							tri[2].c = illuminate(breaker, materials[k], view_vector, n2,v2);
							tri[2].n = n2;
							tri[2].n.normalize();
						}else {
							// flat shading: use the normal to the triangle
							n2 = n1 = n0 =  triangle_normal;
							tri[2].c = tri[1].c = tri[0].c = illuminate(breaker, materials[k], view_vector, triangle_normal,v0);
						}

						tri[0].x = (int)v0.x;
						tri[0].y = (int)v0.y;
						tri[0].d = v0.z;
						tri[1].x = (int)v1.x;
						tri[1].y = (int)v1.y;
						tri[1].d = v0.z;
						tri[2].x = (int)v2.x;
						tri[2].y = (int)v2.y;
						tri[2].d = v0.z;
						
						if(rendering != "phong") SketchBase.drawTriangle(buff, dBuff, tri[0], tri[1], tri[2], doSmooth);
						// pass light and material information into the drawPhongTriangle function
						else SketchBase.drawPhongTriangle(buff, dBuff, tri[0], tri[1], tri[2], breaker, materials[k], view_vector, light1, light2, light3, light4);
					}
					
					// ****************Implement Code here*******************//
					v0 = mesh.v[i][j];
					v1 = mesh.v[i+1][j+1];
					v2 = mesh.v[i][j+1];
					
					triangle_normal = computeTriangleNormal(v0,v1,v2);
					
					if(view_vector.dotProduct(triangle_normal) > 0.0)  // front-facing triangle?
					{	
						if(rendering == "gouraurd" || rendering == "phong") {
							doSmooth = true;
							n0 = mesh.n[i][j];
							n1 = mesh.n[i+1][j+1];
							n2 = mesh.n[i][j+1];
							
							tri[0].c = illuminate(breaker, materials[k], view_vector, n0, v0);
							tri[0].n = n0;
							tri[0].n.normalize();
							tri[1].c = illuminate(breaker, materials[k], view_vector, n1, v1);
							tri[1].n = n1;
							tri[1].n.normalize();
							tri[2].c = illuminate(breaker, materials[k], view_vector, n2, v2);
							tri[2].n = n2;
							tri[2].n.normalize();
						}else {
							// flat shading: use the normal to the triangle
							n2 = n1 = n0 =  triangle_normal;
							tri[2].c = tri[1].c = tri[0].c = illuminate(breaker, materials[k], view_vector, triangle_normal,v0);
						}
			
						tri[0].x = (int)v0.x;
						tri[0].y = (int)v0.y;
						tri[0].d = v0.z;
						tri[1].x = (int)v1.x;
						tri[1].y = (int)v1.y;
						tri[1].d = v0.z;
						tri[2].x = (int)v2.x;
						tri[2].y = (int)v2.y;
						tri[2].d = v0.z;

						if(rendering != "phong") SketchBase.drawTriangle(buff, dBuff, tri[0], tri[1], tri[2], doSmooth);
						else SketchBase.drawPhongTriangle(buff, dBuff, tri[0], tri[1], tri[2], breaker, materials[k], view_vector, light1, light2, light3, light4);
					}
				}	
		    }
		}

	}
	
	// sums all light sources that are currently on together,
	// ignores light sources that are set to off.
	public ColorType illuminate(boolean[] breaker, Material mat, Point3D v, Point3D n, Point3D s_pos ) {
		
		ColorType result = new ColorType();
		
		for (int i = 0; i < breaker.length; i++) {
			float r = 0f;
			float g = 0f;
			float b = 0f;
			
			if(breaker[i]) {
				ColorType temp = new ColorType();
				
				switch (i) {
				case 0:
					// ambient light
					temp = light1.applyLight(mat);
					r = temp.r;
					g = temp.g;
					b = temp.b;
					break;
				case 1:
					// infinite light
					temp = light2.applyLight(mat, v, n);
					r = temp.r;
					g = temp.g;
					b = temp.b;
					break;
				case 2:
					// point light 1
					temp = light3.applyLight(mat, v, n, s_pos);
					r = temp.r;
					g = temp.g;
					b = temp.b;
					break;
				case 3:
					// point light 2
					temp = light4.applyLight(mat, v, n, s_pos);
					r = temp.r;
					g = temp.g;
					b = temp.b;
					break;
				default:
					break;
				}
			}
			
			result.r += r;
			result.g += g;
			result.b += b;
		}
		result.clamp();
		return result;
	}
	
	
	// outputs a light switch panel to the console
	// specifying which lights in the scene are on.
	public void displaySwitch() {
		System.out.println("==================================");
		System.out.println("| [ " + ( (breaker[0]) ? "X" : " ") + " ]   [ " + ( (breaker[1]) ? "X" : " ") + " ]   [ " + ( (breaker[2]) ? "X" : " ") + " ]   [ " + ( (breaker[3]) ? "X" : " ") + " ] |");
		System.out.println("   AMB     INF    SPOT1   SPOT2");
		System.out.println("==================================");
		System.out.println("\n\n\n");
	}
	
	// helper method that computes the unit normal to the plane of the triangle
	// degenerate triangles yield normal that is numerically zero
	private Point3D computeTriangleNormal(Point3D v0, Point3D v1, Point3D v2)
	{
		Point3D e0 = v1.minus(v2);
		Point3D e1 = v0.minus(v2);
		Point3D norm = e0.crossProduct(e1);
		
		if(norm.magnitude()>0.000001)
			norm.normalize();
		else 	// detect degenerate triangle and set its normal to zero
			norm.set((float)0.0,(float)0.0,(float)0.0);

		return norm;
	}

}
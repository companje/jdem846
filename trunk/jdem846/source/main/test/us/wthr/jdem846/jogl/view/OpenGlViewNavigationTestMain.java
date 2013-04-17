package us.wthr.jdem846.jogl.view;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.swing.JFrame;
import javax.swing.Timer;

import us.wthr.jdem846.graphics.Color;
import us.wthr.jdem846.graphics.ExamineView;
import us.wthr.jdem846.graphics.ExamineView.ModelViewChangeListener;
import us.wthr.jdem846.graphics.IColor;
import us.wthr.jdem846.math.Matrix;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.math.Vectors;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class OpenGlViewNavigationTestMain extends JFrame implements GLEventListener, KeyListener, MouseMotionListener, MouseListener, MouseWheelListener
{

	private GLCanvas canvas;
	private GLCapabilities caps;
	private FPSAnimator animator;
	private GLProfile glProfile;

	//private double distance = 20;
	private double radius = .5;
	private double rotateSpeed = 0.2;
	private double zoomSpeed = 0.05;
	//private Vector camPos;
	//private Vector camUp;
	//private Matrix modelView;
	
	//private Quaternion orientation;
	
	private int planetCallList = 0;
	private int starCallList = 0;
	private int borderCallList = 0;
	
	private ExamineView examineView;
	
	private int lastX = -1;
	private int lastY = -1;

	private Timer timer;

	private Texture planetTexture;
	private Texture starTexture;

	private GLU glu = new GLU();
	private GLUT glut = new GLUT();
	
	private boolean hideMouse = false;
	private boolean captureMouse = false;
	
	private boolean mouse1Down = false;
	private boolean mouse2Down = false;
	private boolean mouse3Down = false;
	
	private AtmosphereHalo halo = new AtmosphereHalo();
	
	private List<Path2D.Double> borders = new ArrayList<Path2D.Double>();
	
	public OpenGlViewNavigationTestMain()
	{
		super("Tester");
		glProfile = GLProfile.getDefault();
		caps = new GLCapabilities(glProfile);
		caps.setDoubleBuffered(true);
		caps.setSampleBuffers(true);
		caps.setNumSamples(4);
		canvas = new GLCanvas(caps);
		canvas.addGLEventListener(this);
		canvas.addKeyListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addMouseListener(this);
		canvas.addMouseWheelListener(this);

		animator = new FPSAnimator(canvas, 60);

		examineView = new ExamineView();
		examineView.setMinDistance(.52);
		examineView.setMaxDistance(20);
		examineView.setDistance(2);
		
		examineView.setModelRadius(.5);
		examineView.setMaxScale((examineView.getDistance() - radius) / radius);
		examineView.rotate(-90, 0);
		examineView.rotate(0, 90);
		examineView.rotate(-30, 0);
		//examineView.setMinScale(1.0 / radius);
		
		examineView.addModelViewChangeListener(new ModelViewChangeListener() {
			public void onModelViewChanged(Matrix modelView)
			{
				canvas.repaint();
			}
		});
		
		getContentPane().add(canvas);
	}

	public void run()
	{

		loadResources();

		if (hideMouse) {
			Dimension d = Toolkit.getDefaultToolkit().getBestCursorSize(1, 1);
			Image invisibleImage = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
			Cursor invisibleCursor = Toolkit.getDefaultToolkit().createCustomCursor(invisibleImage, new Point(), "Invisible cursor");
			canvas.setCursor(invisibleCursor);
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1000, 700);
		setLocationRelativeTo(null);
		setVisible(true);
		canvas.requestFocusInWindow();

		animator.start();
	}

	public void loadResources()
	{

	}

	@Override
	public void init(GLAutoDrawable drawable)
	{
		GL2 gl = drawable.getGL().getGL2();

		gl.glEnable(GL.GL_BLEND);
		gl.glEnable(GL.GL_TEXTURE_2D);

		gl.glEnable(GL.GL_MULTISAMPLE);

		gl.glShadeModel(GL2.GL_SMOOTH);
		// gl.glEnable(GL2.GL_POLYGON_SMOOTH);

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);

		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_FASTEST);
		gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL.GL_FASTEST);
		gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL.GL_FASTEST);

		//gl.glEnable(GL.GL_CULL_FACE);
		//gl.glCullFace(GL.GL_BACK);

		// gl.glHint(GL2.GL_CLIP_VOLUME_CLIPPING_HINT_EXT, GL2.GL_FASTEST);

		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
		
		gl.glPolygonOffset(0,1);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		try {
			//List<Path2D.Double> nations = ShapeFileToPath.load("C:\\jdem\\jdem846\\jdem846\\resources\\boundaries\\level1\\level1.shp");
			List<Path2D.Double> states = ShapeFileToPath.load("C:\\jdem\\jdem846\\jdem846\\resources\\boundaries\\states\\statesp020.shp");
			
			//borders.addAll(nations);
			borders.addAll(states);
			System.err.println("Loaded Borders.");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		
		try {
			InputStream stream = getClass().getResourceAsStream("world.topo.bathy.200408.3x5400x2700.jpg");
			TextureData data = TextureIO.newTextureData(glProfile, stream, false, "jpg");
			planetTexture = TextureIO.newTexture(data);
			System.err.println("Loaded Planet Texture.");
		} catch (IOException exc) {
			exc.printStackTrace();
			System.exit(1);
		}

		try {
			InputStream stream = getClass().getResourceAsStream("starfield2.jpg");
			TextureData data = TextureIO.newTextureData(glProfile, stream, false, "jpg");
			starTexture = TextureIO.newTexture(data);
			System.err.println("Loaded Star Texture.");
		} catch (IOException exc) {
			exc.printStackTrace();
			System.exit(1);
		}
		
		mouseExited(null);

		timer = new Timer(10, new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				canvas.repaint();
			}
		});
		timer.start();

	}

	public void setLighting(GLAutoDrawable drawable)
	{
		GL2 gl = drawable.getGL().getGL2();

		gl.glEnable(GLLightingFunc.GL_LIGHTING);
		gl.glEnable(GLLightingFunc.GL_LIGHT0);

		Vector lightPosition = new Vector(10000, 0, 0);
		// Vectors.rotate(60, 0, 60, lightPosition, Vectors.XYZ);

		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, new float[] { (float) lightPosition.x, (float) lightPosition.y, (float) lightPosition.z, 1 }, 0);
		gl.glShadeModel(GL2.GL_SMOOTH);

		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, new float[] { 0.3f, 0.3f, 0.3f, 1.0f }, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, new float[] { 1.9f, 1.9f, 1.9f, 1.0f }, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, new float[] { 0.4f, 0.4f, 0.4f, 1.0f }, 0);
		gl.glLightModeli(GL2.GL_LIGHT_MODEL_COLOR_CONTROL, GL2.GL_SEPARATE_SPECULAR_COLOR);
		// gl.glLightModelfv(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, new float[] { 0.0f
		// }, 0);

	}

	@Override
	public void display(GLAutoDrawable drawable)
	{
		GL2 gl = drawable.getGL().getGL2();
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);

		gl.glLoadIdentity();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		glu.gluLookAt(0, 0, examineView.getDistance() - radius, 0, 0, 0, 0, 1, 0);
		
		gl.glMultMatrixd(examineView.getModelView().matrix, 0);
		
		
		
		//renderer.enableFog(fog.color, fog.type, fog.density, nearDistance * fog.nearFactor, farDistance * fog.farFactor);
		
		
		
		gl.glPushMatrix();
		
		//gl.glDisable(GL2.GL_LIGHTING);
		
		
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glDisable(GL2.GL_COLOR_MATERIAL);
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		renderStars(gl, glu);
		
		setLighting(drawable);
		enableFog(gl, glu);
		
		float[] rgba = { 0.3f, 0.5f, 1f };

		gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, rgba, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, rgba, 0);
		gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, 0.5f);
		renderPlanet(gl, glu);
		
		gl.glDisable(GL2.GL_FOG);
		gl.glDisable(GL2.GL_LIGHTING);
		
		renderBorders(gl, glu);
		
		
		
		halo.render(gl, glu, examineView);
		
		gl.glPopMatrix();

		gl.glFlush();
	}
	
	public void renderStars(GL2 gl, GLU glu)
	{
		//planetCallList
		
		if (starCallList == 0) {
			starCallList = gl.glGenLists(1);
			gl.glNewList(starCallList, GL2.GL_COMPILE);
			
			starTexture.enable(gl);
			starTexture.bind(gl);

			GLUquadric space = glu.gluNewQuadric();
			glu.gluQuadricDrawStyle(space, GLU.GLU_FILL);
			glu.gluQuadricNormals(space, GLU.GLU_FLAT);
			glu.gluQuadricOrientation(space, GLU.GLU_INSIDE);
			glu.gluQuadricTexture(space, true);
			final int slices = 64;
			final int stacks = 64;
			glu.gluSphere(space, 10, slices, stacks);
			glu.gluDeleteQuadric(space);
			gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
			gl.glEndList();
		} else {
			gl.glCallList(starCallList);
		}
		
	}
	
	public void enableFog(GL2 gl, GLU glu)
	{
		IColor fogColor = new Color("#688AB0FF");
		gl.glEnable(GL2.GL_FOG);
		gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_LINEAR);
		
		float[] c = {0, 0, 0, 0};
		fogColor.toArray(c);
		gl.glFogfv(GL2.GL_FOG_COLOR, c, 0);

		gl.glFogf(GL2.GL_FOG_DENSITY, (float) 100);
		gl.glHint(GL2.GL_FOG_HINT, GL2.GL_FASTEST);
		gl.glFogf(GL2.GL_FOG_START, (float) (examineView.getDistance() - radius - radius + 0.1));
		gl.glFogf(GL2.GL_FOG_END, (float) (examineView.getDistance() - radius - 0.05));
	}
	
	public void renderBorders(GL2 gl, GLU glu)
	{
		if (borderCallList == 0) {
			borderCallList = gl.glGenLists(1);
			gl.glNewList(borderCallList, GL2.GL_COMPILE);
			
			gl.glColor3f(1.0f, 1.0f, 0f);
			
			double[] coords = {0, 0};
			Vector v0 = new Vector();
			
			for (Path2D.Double path : borders) {
				gl.glBegin(GL2.GL_LINE_STRIP);
				
				PathIterator iter = path.getPathIterator(null);
				while(!iter.isDone()) {
					iter.currentSegment(coords);
					
					Spheres.getPoint3D(coords[0], coords[1], radius, v0);
					v0.rotate(-90, Vectors.X_AXIS);

					gl.glVertex3d(v0.x, v0.y, v0.z);
					
					iter.next();
				}
				
				gl.glEnd();
			}
			
			
			gl.glEndList();
		} else {
			gl.glCallList(borderCallList);
		}
	}
	
	public void renderPlanet(GL2 gl, GLU glu)
	{
		//planetCallList
		
		if (planetCallList == 0) {
			planetCallList = gl.glGenLists(1);
			gl.glNewList(planetCallList, GL2.GL_COMPILE);
			
			planetTexture.enable(gl);
			planetTexture.bind(gl);

			GLUquadric earth = glu.gluNewQuadric();
			glu.gluQuadricDrawStyle(earth, GLU.GLU_FILL);
			glu.gluQuadricNormals(earth, GLU.GLU_FLAT);
			glu.gluQuadricOrientation(earth, GLU.GLU_OUTSIDE);
			glu.gluQuadricTexture(earth, true);
			final int slices = 64;
			final int stacks = 64;
			glu.gluSphere(earth, radius, slices, stacks);
			glu.gluDeleteQuadric(earth);
			gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
			gl.glEndList();
		} else {
			gl.glCallList(planetCallList);
		}
		
	}
	
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h)
	{
		GL2 gl = drawable.getGL().getGL2();
		GLU glu = new GLU();
		gl.glViewport(0, 0, w, h);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		double aspect = (double) w / (double) h;

		glu.gluPerspective(45.0, aspect, 0.01, 2000.0);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged)
	{
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
	}

	@Override
	public void keyPressed(KeyEvent key)
	{

		Vector moveVector = null;
		switch (key.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			new Thread()
			{
				@Override
				public void run()
				{
					animator.stop();
				}
			}.start();
			System.exit(0);
			break;
		case 38: // Forward
			moveVector = new Vector(0, 0, -0.1);
			break;
		case 40: // Backward
			moveVector = new Vector(0, 0, 0.1);
			break;
		case 37: // Left
			moveVector = new Vector(-0.1, 0, 0);
			break;
		case 39: // Right
			moveVector = new Vector(0.1, 0, 0);
			break;
		default:
			break;
		}

		if (moveVector != null) {
			// Vectors.rotateY(viewPoint.getYaw(), moveVector);
			// viewPoint.setZ(viewPoint.getZ() + (float) moveVector.z);
			// viewPoint.setX(viewPoint.getX() + (float) moveVector.x);

		}
	}

	@Override
	public void keyReleased(KeyEvent key)
	{

	}

	@Override
	public void dispose(GLAutoDrawable arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();

		if (lastX != -1 && lastY != -1) {

			int deltaX = x - lastX;
			int deltaY = y - lastY;
			
			
			if (e.isShiftDown() || mouse3Down) {
				examineView.setPitch(examineView.getPitch() + deltaY * rotateSpeed);
				examineView.setRoll(examineView.getRoll() + deltaX * rotateSpeed);
			} else {
				examineView.rotate(-deltaY * rotateSpeed, -deltaX * rotateSpeed);
			}
			
			canvas.repaint();
		}

		lastX = x;
		lastY = y;
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{

	}

	@Override
	public void mouseClicked(MouseEvent e)
	{

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{

	}

	@Override
	public void mouseExited(MouseEvent e)
	{

		Point locOnScreen = this.getLocationOnScreen();
		int middleX = locOnScreen.x + (this.getWidth() / 2);
		int middleY = locOnScreen.y + (this.getHeight() / 2);

		if (captureMouse) {
			try {
				Robot rob = new Robot();
				rob.mouseMove(middleX, middleY);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		lastX = -1;
		lastY = -1;
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		lastX = e.getX();
		lastY = e.getY();
		
		if (e.getButton() == MouseEvent.BUTTON1) {
			mouse1Down = true;
		} else if (e.getButton() == MouseEvent.BUTTON2) {
			mouse2Down = true;
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			mouse3Down = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		lastX = -1;
		lastY = -1;
		
		if (e.getButton() == MouseEvent.BUTTON1) {
			mouse1Down = false;
		} else if (e.getButton() == MouseEvent.BUTTON2) {
			mouse2Down = false;
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			mouse3Down = false;
		}
	}
	
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		double c = e.getWheelRotation();
		//examineView.scale(examineView.getScale() + (c * 0.1));
		examineView.setDistance(examineView.getDistance() + (c * zoomSpeed));
		examineView.setMaxScale((examineView.getDistance() - radius) / radius);
		//examineView.setMinScale(1.0 / radius);
	}

	public static void main(String[] args)
	{
		OpenGlViewNavigationTestMain testMain = new OpenGlViewNavigationTestMain();
		testMain.run();
	}



}

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
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

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

import us.wthr.jdem846.math.Vector;

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
	private double radius = 5;
	private double rotateSpeed = 0.5;
	//private Vector camPos;
	//private Vector camUp;
	//private Matrix modelView;
	
	//private Quaternion orientation;
	
	private ExamineView examineView;
	
	private int lastX = -1;
	private int lastY = -1;

	private Timer timer;

	private Texture planetTexture;

	
	
	private boolean hideMouse = false;
	private boolean captureMouse = false;

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
		examineView.setDistance(20);
		examineView.setModelRadius(5);
		examineView.setMaxScale((examineView.getDistance() - radius) / radius);
		examineView.setMinScale(1 / radius);
		
		
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

		// gl.glEnable(GL.GL_BLEND);
		gl.glEnable(GL.GL_TEXTURE_2D);

		gl.glEnable(GL.GL_MULTISAMPLE);

		gl.glShadeModel(GL2.GL_SMOOTH);
		// gl.glEnable(GL2.GL_POLYGON_SMOOTH);

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);

		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_FASTEST);
		gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL.GL_FASTEST);
		gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL.GL_FASTEST);

		gl.glEnable(GL.GL_CULL_FACE);
		gl.glCullFace(GL.GL_BACK);

		// gl.glHint(GL2.GL_CLIP_VOLUME_CLIPPING_HINT_EXT, GL2.GL_FASTEST);

		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// gl.glEnable(GL2.GL_FOG);
		// gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_EXP);

		// gl.glFogfv(GL2.GL_FOG_COLOR, new float[] { 0.9f, 0.9f, 0.9f }, 0);
		// /gl.glFogf(GL2.GL_FOG_DENSITY, 0.02f);
		// gl.glHint(GL2.GL_FOG_HINT, GL2.GL_FASTEST);
		// gl.glFogf(GL2.GL_FOG_START, 25.1f);
		// gl.glFogf(GL2.GL_FOG_END, 50.0f);

		try {
			InputStream stream = getClass().getResourceAsStream("world.topo.bathy.200408.3x5400x2700.jpg");
			TextureData data = TextureIO.newTextureData(glProfile, stream, false, "jpg");
			planetTexture = TextureIO.newTexture(data);
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
		GLU glu = new GLU();
		GLUT glut = new GLUT();
		gl.glMatrixMode(GL2.GL_MODELVIEW);

		gl.glLoadIdentity();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		/*
		 * glu.gluLookAt(viewPoint.getX() // Eye X , viewPoint.getY() // Eye Y ,
		 * viewPoint.getZ() // Eye Z , 0 // Center X , 0 // Center Y , 0 //
		 * Center Z , 0 // Up X , 1 // Up Y , 0); // Up Z
		 */
		//glu.gluLookAt(camPos.x, camPos.y, camPos.z, 0, 0, 0, camUp.x, camUp.y, camUp.z);
		
		//Matrix modelView = new Matrix(true);
		//orientation.toMatrix(modelView);
		
		//Vector up = new Vector(0, 1, 0);
		//Vectors.rotate(examineView.getPitch(), 0, 0, up);
		
		//Vector position = new Vector(0, 0, examineView.getDistance());
		//Vectors.rotate(examineView.getPitch(), 0, 0, position);
		
		//glu.gluLookAt(position.x, position.y, position.z, 0, 0, 0, 0, 1, 0);
		glu.gluLookAt(0, 0, examineView.getDistance() - radius, 0, 0, 0, 0, 1, 0);
		
		gl.glMultMatrixd(examineView.getModelView().matrix, 0);
		//gl.glTranslated(0, 0, radius);
		//gl.glRotated(examineView.getPitch(), 1, 0, 0);
		//gl.glTranslated(0, 0, -radius);
		
		setLighting(drawable);

		gl.glPushMatrix();
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);

		float[] rgba = { 0.3f, 0.5f, 1f };
		gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, rgba, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, rgba, 0);
		gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, 0.5f);

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

		gl.glPopMatrix();

		gl.glFlush();
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

		glu.gluPerspective(45.0, aspect, 0.01, 200.0);

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
			
			
			if (e.isShiftDown()) {
				examineView.setPitch(examineView.getPitch() + deltaY * rotateSpeed);
			} else {
				examineView.rotate(deltaY * rotateSpeed, deltaX * rotateSpeed);
			}
			
			canvas.repaint();
		}

		lastX = x;
		lastY = y;

		canvas.repaint();
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

		canvas.repaint();
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		lastX = e.getX();
		lastY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		lastX = -1;
		lastY = -1;
	}
	
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		double c = e.getWheelRotation();
		//examineView.scale(examineView.getScale() + (c * 0.1));
		examineView.setDistance(examineView.getDistance() + (c * 0.1));
		canvas.repaint();
	}

	public static void main(String[] args)
	{
		OpenGlViewNavigationTestMain testMain = new OpenGlViewNavigationTestMain();
		testMain.run();
	}



}

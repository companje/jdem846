package us.wthr.planets;

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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.swing.Timer;

import us.wthr.jdem846.graphics.Color;
import us.wthr.jdem846.graphics.ExamineView;
import us.wthr.jdem846.graphics.ExamineView.ModelViewChangeListener;
import us.wthr.jdem846.graphics.IColor;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Matrix;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.math.Vectors;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

public class PlanetsMain extends JFrame implements GLEventListener, KeyListener, MouseMotionListener, MouseListener, MouseWheelListener
{
	// private Planet earth;
	// private Planet mars;
	// private Planet saturn;

	private Queue<Planet> planets = new LinkedList<Planet>();
	private Planet usePlanet;

	private GLCanvas canvas;
	private GLCapabilities caps;
	private FPSAnimator animator;
	private GLProfile glProfile;

	private double radius = .5;
	private double rotateSpeed = 0.2;
	private double zoomSpeed = 0.05;

	private int borderCallList = 0;

	private ExamineView examineView;

	private int lastX = -1;
	private int lastY = -1;

	private Timer timer;

	private TexturedSphere planet;
	private TexturedSphere clouds;
	private TexturedSphere stars;

	private GLU glu = new GLU();
	private GLUT glut = new GLUT();

	private boolean hideMouse = false;
	private boolean captureMouse = false;

	private boolean mouse1Down = false;
	private boolean mouse2Down = false;
	private boolean mouse3Down = false;
	private List<Path2D.Double> borders = new ArrayList<Path2D.Double>();

	public PlanetsMain()
	{
		super("Planets");
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
		examineView.setMinDistance(1);
		examineView.setMaxDistance(20);
		examineView.setDistance(2);

		examineView.setModelRadius(.5);
		examineView.setMaxScale((examineView.getDistance() - radius) / radius);
		examineView.rotate(-90, 0);
		examineView.rotate(0, 90);
		examineView.rotate(-30, 0);

		examineView.addModelViewChangeListener(new ModelViewChangeListener()
		{
			public void onModelViewChanged(Matrix modelView)
			{
				canvas.repaint();
			}
		});

		getContentPane().add(canvas);
	}

	public void run()
	{
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
		Planet earth = null;
		try {
			earth = new Planet(glProfile, "assets/world.topo.bathy.200408.3x2500x1250.jpg", new Color("#688AB0FF"), "assets/cloud_combined_2500.png", new Color("#688AB0FF"), 0.0033528, true);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		earth.getObjects().add(new AtmosphereHalo(new HaloColoring()));
		planets.add(earth);

		Planet saturn = null;
		HaloColoring saturnHaloColoring = new HaloColoring();
		saturnHaloColoring.setColorLower(new Color(209, 204, 183));
		saturnHaloColoring.setColorUpper(new Color(209, 204, 183));
		saturnHaloColoring.setColorFaded(new Color(209, 204, 183, 0));
		try {
			saturn = new Planet(glProfile, "assets/th_saturn.png", null, null, null, 0.09796, true);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		saturn.getObjects().add(new AtmosphereHalo(saturnHaloColoring));
		try {
			saturn.getObjects().add(new PlanetaryRing(glProfile));
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		planets.add(saturn);

		Planet mars = null;
		HaloColoring marsHaloColoring = new HaloColoring();
		marsHaloColoring.setColorLower(new Color("#DBAA79C8"));
		marsHaloColoring.setColorUpper(new Color("#DBAA79C8"));
		marsHaloColoring.setColorFaded(new Color("#DBAA7900"));
		try {
			mars = new Planet(glProfile, "assets/red-dust-bg.jpg", new Color("#DBAA79FF"), null, new Color("#DBAA79FF"), 0.00589, true);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		mars.getObjects().add(new AtmosphereHalo(marsHaloColoring));
		planets.add(mars);

		Planet sun = null;
		HaloColoring sunHaloColoring = new HaloColoring();
		sunHaloColoring.setColorLower(new Color("#FFFF00C8"));
		sunHaloColoring.setColorUpper(new Color("#FFC800C8"));
		sunHaloColoring.setColorFaded(new Color("#DBAA7900"));
		try {
			sun = new Planet(glProfile, "assets/th_sun.png", null, null, null, 0.0, false);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		sun.getObjects().add(new AtmosphereHalo(sunHaloColoring));
		planets.add(sun);

		Planet pluto = null;
		try {
			pluto = new Planet(glProfile, "assets/JVV_Pluto.png", null, null, null, 0.0, true);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		planets.add(pluto);

		Planet jupiter = null;
		try {
			jupiter = new Planet(glProfile, "assets/realj4k.jpg", null, null, null, 0.0, true);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		planets.add(jupiter);

		usePlanet = planets.poll();

		try {
			stars = new TexturedSphere(glProfile, "assets/stars_4096x2048.png", 20, false);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		/*
		 * try {
		 * 
		 * for (String shapePath : usePlanet.getShapes()) { List<Path2D.Double>
		 * paths = ShapeFileToPath.load(shapePath); borders.addAll(paths); }
		 * System.err.println("Loaded Borders."); } catch (Exception e) {
		 * e.printStackTrace(); System.exit(1); }
		 */

	}

	@Override
	public void init(GLAutoDrawable drawable)
	{

		loadResources();

		GL2 gl = drawable.getGL().getGL2();

		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		gl.glEnable(GL.GL_TEXTURE_2D);

		gl.glEnable(GL.GL_MULTISAMPLE);

		gl.glShadeModel(GL2.GL_SMOOTH);

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);

		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_FASTEST);
		gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL.GL_FASTEST);
		gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL.GL_FASTEST);

		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);

		gl.glPolygonOffset(0, 1);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

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

		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, new float[] { (float) lightPosition.x, (float) lightPosition.y, (float) lightPosition.z, 1 }, 0);
		gl.glShadeModel(GL2.GL_SMOOTH);

		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, new float[] { 0.3f, 0.3f, 0.3f, 1.0f }, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, new float[] { 1.9f, 1.9f, 1.9f, 1.0f }, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, new float[] { 0.4f, 0.4f, 0.4f, 1.0f }, 0);
		gl.glLighti(GL2.GL_LIGHT0, GL2.GL_SPOT_EXPONENT, 70);
		gl.glLightModeli(GL2.GL_LIGHT_MODEL_COLOR_CONTROL, GL2.GL_SEPARATE_SPECULAR_COLOR);
	}

	@Override
	public void display(GLAutoDrawable drawable)
	{
		GL2 gl = drawable.getGL().getGL2();

		gl.glMatrixMode(GL2.GL_MODELVIEW);

		gl.glLoadIdentity();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		glu.gluLookAt(0, 0, examineView.getDistance(), 0, 0, 0, 0, 1, 0);

		gl.glMultMatrixd(examineView.getModelView().matrix, 0);

		gl.glPushMatrix();

		gl.glDisable(GL2.GL_LIGHTING);
		gl.glDisable(GL2.GL_COLOR_MATERIAL);
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		stars.render(gl, glu, examineView);

		if (usePlanet.isUseLighting()) {
			setLighting(drawable);
		} else {
			gl.glDisable(GL2.GL_LIGHTING);
		}
		enableFog(gl, glu);

		IColor materialLightColor = usePlanet.getMaterialLightColor();
		if (materialLightColor != null) {
			float[] rgba = { 0.0f, 0.0f, 0.0f };
			materialLightColor.toArray(rgba);
			gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, rgba, 0);
			gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, rgba, 0);
			gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, 0.7f);
		}

		/*
		 * if (planet != null) { planet.render(gl, glu, examineView); }
		 * 
		 * if (clouds != null) { clouds.render(gl, glu, examineView); }
		 */
		if (usePlanet != null) {
			usePlanet.render(gl, glu, examineView);
		}

		gl.glPopMatrix();

		gl.glFlush();
	}

	public void enableFog(GL2 gl, GLU glu)
	{
		IColor fogColor = usePlanet.getFogColor();
		if (fogColor != null) {
			gl.glEnable(GL2.GL_FOG);
			gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_LINEAR);

			float[] c = { 0, 0, 0, 0 };
			fogColor.toArray(c);
			gl.glFogfv(GL2.GL_FOG_COLOR, c, 0);

			double distanceToCenter = examineView.getEyeDistanceToCenter();
			double f = MathExt.sqrt((distanceToCenter - radius) * (2 * radius + (distanceToCenter - radius)));

			gl.glFogf(GL2.GL_FOG_DENSITY, (float) 100);
			gl.glHint(GL2.GL_FOG_HINT, GL2.GL_NICEST);
			gl.glFogf(GL2.GL_FOG_START, (float) (f - 0.5));
			gl.glFogf(GL2.GL_FOG_END, (float) (f - 0.4));
		}
	}

	public void renderBorders(GL2 gl, GLU glu)
	{
		if (borderCallList == 0) {
			borderCallList = gl.glGenLists(1);
			gl.glNewList(borderCallList, GL2.GL_COMPILE);

			gl.glColor3f(1.0f, 1.0f, 0f);

			double[] coords = { 0, 0 };
			Vector v0 = new Vector();

			for (Path2D.Double path : borders) {
				gl.glBegin(GL2.GL_LINE_STRIP);

				PathIterator iter = path.getPathIterator(null);
				while (!iter.isDone()) {
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
		case KeyEvent.VK_N:
			System.err.println("Next Planet...");
			if (usePlanet != null) {
				planets.add(usePlanet);
			}
			usePlanet = planets.poll();
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
		examineView.setDistance(examineView.getDistance() + (c * zoomSpeed));
		examineView.setMaxScale((examineView.getDistance() - radius) / radius);
	}

	public static void main(String[] args)
	{
		PlanetsMain main = new PlanetsMain();
		main.run();
	}
}

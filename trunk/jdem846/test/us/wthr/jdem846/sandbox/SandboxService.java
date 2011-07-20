/*
 * Copyright (C) 2011 Kevin M. Gill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.wthr.jdem846.sandbox;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.Timer;

import us.wthr.jdem846.AbstractLockableService;
import us.wthr.jdem846.Perspectives;
import us.wthr.jdem846.annotations.Destroy;
import us.wthr.jdem846.annotations.Initialize;
import us.wthr.jdem846.annotations.Service;
import us.wthr.jdem846.annotations.ServiceRuntime;

import us.wthr.jdem846.render.gfx.*;

/*
sun.java2d.SunGraphics2D

public void fillPolygon(int xPoints[], int yPoints[], int nPoints)
Line: 2233
fillpipe.fillPolygon(this, xPoints, yPoints, nPoints);


BufferedRenderPipe
 public void fill(SunGraphics2D sg2d, Shape s)
Line: 521
 */


@Service(name="us.wthr.jdem846.sandbox", enabled=false)
public class SandboxService extends AbstractLockableService
{
	List<Renderable> renderObjects = new LinkedList<Renderable>();
	
	Color background = Color.WHITE;

	public SandboxService()
	{
		
	}
	
	@Initialize
	public void init()
	{
		System.out.println("SandboxService.init()");
		
		double w = 30;
		Color lineColor = Color.BLACK;
		
		int sideAlpha = 255;
		Color sideColor0 = new Color(Color.GREEN.getRed(), Color.GREEN.getGreen(), Color.GREEN.getBlue(), sideAlpha);
		Color sideColor1 = new Color(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue(), sideAlpha);
		Color sideColor2 = new Color(Color.BLUE.getRed(), Color.BLUE.getGreen(), Color.BLUE.getBlue(), sideAlpha);
		Color sideColor3 = new Color(Color.ORANGE.getRed(), Color.ORANGE.getGreen(), Color.ORANGE.getBlue(), sideAlpha);
		Color sideColor4 = new Color(Color.YELLOW.getRed(), Color.YELLOW.getGreen(), Color.YELLOW.getBlue(), sideAlpha);
		Color sideColor5 = new Color(Color.MAGENTA.getRed(), Color.MAGENTA.getGreen(), Color.MAGENTA.getBlue(), sideAlpha);
		
		Color wireColor = Color.BLACK;
		Color polyColor = Color.gray;
		
		//double strips = 90.0;
		//double slices = 180.0;
        
		double strips = 100.0;
		double slices = 100.0;
		
		double strip_step = 90.0 / strips;
		double slice_step = 360.0 / slices;
		double radius = 100;

		double p_tl[] = {0, 0, 0};
		double p_tr[] = {0, 0, 0};
		double p_bl[] = {0, 0, 0};
		double p_br[] = {0, 0, 0};
		
		
		//renderObjects.add(new Line(wireColor, new Vector(-.2, 110, -.2), new Vector(-.2, -110, -.2)));
		//renderObjects.add(new Line(wireColor, new Vector(-.2, 110, .2), new Vector(-.2, -110, .2)));
		//renderObjects.add(new Line(wireColor, new Vector(.2, 110, .2), new Vector(.2, -110, .2)));
	//	renderObjects.add(new Line(wireColor, new Vector(.2, 110, -.2), new Vector(.2, -110, -.2)));
		
		Color ballColorBottom = Color.BLUE;
		Color ballColorMiddle = Color.LIGHT_GRAY;
		Color ballColorTop = Color.RED;
		
		
		for (double phi = -90; phi <= 90 - strip_step; phi +=strip_step) {
            for (double theta = 0; theta <= 360 + slice_step; theta+=slice_step) {
            	
            	Color ballColor = ballColorBottom;
            	if (phi > -30)
            		ballColor = ballColorMiddle;
            	if (phi > 30)
            		ballColor = ballColorTop;
            	
            	getPoints(theta, phi, radius, p_tl);
                getPoints(theta + slice_step, phi, radius, p_tr);
                getPoints(theta, phi + strip_step, radius, p_bl);
                getPoints(theta + slice_step, phi + strip_step, radius, p_br);
            	
                
                
                /*
                renderObjects.add(new Triangle(ballColor, new Vector(p_tr),
						new Vector(p_br),
						new Vector(p_tl)));
                
                renderObjects.add(new Triangle(ballColor, new Vector(p_br),
						new Vector(p_bl),
						new Vector(p_tl)));
               	*/
                double[] normal = {0.0, 0.0, 0.0};
                /*
                Perspectives.calcNormal(p_tr, p_br, p_bl, normal);
                Renderable renderable = new Square(ballColor, new Vector(p_tr),
						new Vector(p_br),
						new Vector(p_bl),
						new Vector(p_tl));
                renderable.setNormal(normal);
                
                renderObjects.add(renderable);
                */
                
                //getPoints(theta, phi, radius+1, p_tl);
               // getPoints(theta + slice_step, phi, radius+1, p_tr);
               // getPoints(theta, phi + strip_step, radius+1, p_bl);
                //getPoints(theta + slice_step, phi + strip_step, radius+1, p_br);
                
                //renderObjects.add(new Line(wireColor, new Vector(p_tl), new Vector(p_tr)));
               // renderObjects.add(new Line(wireColor, new Vector(p_tl), new Vector(p_bl)));
               

            }
		}
		
		
        
		/*
		// Back
		renderObjects.add(new Square(sideColor0, new Vector(w, w, -w),
												new Vector(w, -w, -w),
												new Vector(-w, -w, -w),
												new Vector(-w, w, -w))); // 1
		
		// Left
		renderObjects.add(new Square(sideColor1, new Vector(w, w, -w), 
												new Vector(w, w, w), 
												new Vector(w, -w, w), 
												new Vector(w, -w, -w))); // 2
		
		// Front
		renderObjects.add(new Square(sideColor2, new Vector(-w, w, w), 
												new Vector(-w, -w, w), 
												new Vector(w, -w, w), 
												new Vector(w, w, w))); // 3
		
		// Right
		renderObjects.add(new Square(sideColor3, new Vector(-w, w, -w), 
												new Vector(-w, -w, -w), 
												new Vector(-w, -w, w), 
												new Vector(-w, w, w))); // 4
		
		// Top
		renderObjects.add(new Square(sideColor4, new Vector(-w, w, -w), 
												new Vector(-w, w, w), 
												new Vector(w, w, w), 
												new Vector(w, w, -w))); // 5
		
		// Bottom
		renderObjects.add(new Square(sideColor5, new Vector(-w, -w, w),
												new Vector(-w, -w, -w), 
												new Vector(w, -w, -w), 
												new Vector(w, -w, w))); // 6
		
		renderObjects.add(new Line(lineColor, new Vector(-w, w, -w), new Vector(-w, w, w))); // A
		renderObjects.add(new Line(lineColor, new Vector(w, w, -w), new Vector(w, w, w))); // B
		renderObjects.add(new Line(lineColor, new Vector(w, -w, -w), new Vector(w, -w, w))); // C
		renderObjects.add(new Line(lineColor, new Vector(-w, -w, -w), new Vector(-w, -w, w))); // D
		
		renderObjects.add(new Line(lineColor, new Vector(-w, w, w), new Vector(w, w, w))); // E
		renderObjects.add(new Line(lineColor, new Vector(w, w, w), new Vector(w, -w, w))); // F
		renderObjects.add(new Line(lineColor, new Vector(w, -w, w), new Vector(-w, -w, w))); // G
		renderObjects.add(new Line(lineColor, new Vector(-w, -w, w), new Vector(-w, w, w))); // H
		
		renderObjects.add(new Line(lineColor, new Vector(-w, w, -w), new Vector(w, w, -w))); // I
		renderObjects.add(new Line(lineColor, new Vector(w, w, -w), new Vector(w, -w, -w))); // J
		renderObjects.add(new Line(lineColor, new Vector(w, -w, -w), new Vector(-w, -w, -w))); // K
		renderObjects.add(new Line(lineColor, new Vector(-w, -w, -w), new Vector(-w, w, -w))); // L
		*/
	}
	
	
	protected void getPoints(double theta, double phi, double radius, double[] points)
	{
		double _y = sqrt(pow(radius, 2) - pow(radius * cos(phi), 2));
		double r0 = sqrt(pow(radius, 2) - pow(_y, 2));

		double _b = r0 * cos(theta );
        double _z = sqrt(pow(r0, 2) - pow(_b, 2));
        double _x = sqrt(pow(r0, 2) - pow(_z, 2));
        if (theta <= 90.0) {
                _z *= -1.0;
        } else if (theta  <= 180.0) {
                _x *= -1.0;
                _z *= -1.0;
        } else if (theta  <= 270.0) {
                _x *= -1.0;
        }

        if (phi >= 0) { 
                _y = abs(_y);
        } else {
                _y = abs(_y) * -1;
        }


        points[0] = _x;
        points[1] = _y;
        points[2] = _z;

       // double mag = sqrt(sqr(_x)+sqr(_y)+sqr(_z));   
       // _x /= mag;   
        //_y /= mag;   
       // _z /= mag; 
        
        //points[3] = (atan2(_x, _z)/(Math.PI*2)) + 0.5f;   
       // points[4] =  (asin(_y) / Math.PI) + 0.5f;
	}
	
	
	@ServiceRuntime
	public void runtime()
	{
		System.out.println("SandboxService.runtime()");

		this.setLocked(true);
		ProjectionTesting frame = new ProjectionTesting();
		
		
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e)
			{
				setLocked(false);
			}
			public void windowClosing(WindowEvent e)
			{
				setLocked(false);
			}
		});
		
		frame.setVisible(true);
		
		while (this.isLocked()) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Destroy
	public void destroy()
	{
		System.out.println("SandboxService.destroy()");
		//vectors = null;
	}
	

	
	class ProjectionTesting extends JFrame 
	{
		
		private int lastMouseX = -1;
		private int lastMouseY = -1;
		
		private double rotateX = 0;
		private double rotateY = 0;
		
		private double translateX = 0;
		private double translateY = 0;
		private double translateZ = 0;
		
		double solarAzimuth = 183.0;
		double solarElevation = 71.0;
		
		//private List<Renderable> rotated = new LinkedList<Renderable>();
		private BufferedImage prerendered = null;
		
		
		public ProjectionTesting()
		{
			this.setTitle("Projection Testing");
			this.setSize(500, 500);
			this.setLocationRelativeTo(null);
			
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			
			Timer timer = new Timer(100, new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					//repaint();
					
					solarAzimuth+=1.0;
					if (solarAzimuth >= 360)
						solarAzimuth = 0;
					
					//solarElevation+=1.0;
					if (solarElevation > 90)
						solarElevation = 0;
					
					
					update(false);
				}
			});
			
			
			KeyAdapter keyAdapter = new KeyAdapter() {
				public void keyPressed(KeyEvent e)
				{
					switch (e.getKeyCode()) {
					case KeyEvent.VK_UP:
						translateY += 1;
						break;
					case KeyEvent.VK_DOWN:
						translateY -= 1;
						break;
					case KeyEvent.VK_LEFT:
						translateX -= 1;
						break;
					case KeyEvent.VK_RIGHT:
						translateX += 1;
						break;
					case KeyEvent.VK_R:
						rotateX = 0;
						rotateY = 0;
						translateY = 0;
						translateX = 0;
						translateZ = 0;
						break;
					}
					//update(true);
					//doRotations();
					//repaint();
				}
			};
			
			
			
			MouseAdapter mouseAdapter = new MouseAdapter() {
				public void mousePressed(MouseEvent event) 
				{
					lastMouseX = event.getX();
					lastMouseY = event.getY();
				}
				public void mouseReleased(MouseEvent event)
				{
					lastMouseX = -1;
					lastMouseY = -1;
				}
				public void mouseDragged(MouseEvent event)
				{
					if (lastMouseX == -1 || lastMouseY == -1)
						return;
					
					int x = event.getX();
					int y = event.getY();
					
					int deltaX = x - lastMouseX;
					int deltaY = y - lastMouseY;
					
					rotateY += -deltaX; // X (horizontal) mouse movement translates to rotation about the Y axis
					rotateX += deltaY; // Y (vertical) mouse movement translates to rotation about the X axis
					
					if (rotateY < 0)
						rotateY = 360;
					if (rotateY > 360)
						rotateY = 0;
					
					if (rotateX < 0)
						rotateX = 360;
					if (rotateX > 360)
						rotateX = 0;
					
					//doRotations();
					
					lastMouseX = x;
					lastMouseY = y;
					
					//repaint();
					//update(true);
				}
				public void mouseWheelMoved(MouseWheelEvent event) 
				{
					translateZ += (event.getWheelRotation() * 20);
					//update(true);
					//doRotations();
					//repaint();
				}
			};
			
			ComponentAdapter componentAdapter = new ComponentAdapter() {
				public void componentResized(ComponentEvent e)
				{
					//prerendered = null;
					//update(true);
					//doRotations();
					//repaint();
				}
			};
			
			
			this.addMouseListener(mouseAdapter);
			this.addMouseMotionListener(mouseAdapter);
			this.addMouseWheelListener(mouseAdapter);
			this.addKeyListener(keyAdapter);
			this.addComponentListener(componentAdapter);
			
			
			//this.getGlassPane().addKeyListener(keyAdapter);
			//this.getContentPane().addKeyListener(keyAdapter);
			//doRotations();
			
			update(false);
			timer.start();
		}
		
		
		
		public void update(boolean threaded)
		{
			
			Thread updateThread = new Thread() {
				public void run()
				{
					List<Renderable> rotated = new LinkedList<Renderable>();
					doRotations(rotated);
					preRender(rotated);
					repaint();
				}
			};
			
			if (threaded)
				updateThread.start();
			else
				updateThread.run();
			
		}
		
		protected void doRotations(List<Renderable> rotated)
		{

			for (Renderable renderObject : renderObjects) {
				Renderable copy = renderObject.copy();
				
				copy.rotate(new Vector(40, 0, 0));
				//copy.translate(new Vector(-translateX, translateY, translateZ));
				
				
				rotated.add(copy);
			}
			
			
		}
		
		public void preRender(List<Renderable> rotated)
		{
			
			BufferedImage canvas = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
			
			//if (prerendered == null) {
			//	prerendered = 
			//}
			
			//double solarAzimuth = 183.97;
			//double solorElevation = 71.04;
			
			Vector eye = new Vector(0, 0, 1000);
			Vector near = new Vector(0, 0, 1000);
			double nearWidth = 50;
			double nearHeight = 50;
			double farDistance = 50;
			double sunsource[] = {-100.0, -100.0, 100.0};
			
			Vector sun = new Vector(0.0, 0.0, -1.0);
			Vector rotation = new Vector(solarElevation+180.0, 0.0, solarAzimuth);
			sun.rotate(rotation);
			//sun.rotate(-solarAzimuth, Vector.Z_AXIS);
			//sun.rotate(-solarElevation, Vector.X_AXIS);
			sunsource[0] = sun.getX();
			sunsource[1] = sun.getY();
			sunsource[2] = sun.getZ();
			
			
			Graphics2D g2d = (Graphics2D) canvas.getGraphics();
			//g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			g2d.setColor(background);
			g2d.fillRect(0, 0, getWidth(), getHeight());
			
			ViewportBuffer buffer = new ViewportBuffer(getWidth(), getHeight());
			
			for (Renderable renderObject : rotated) {
				renderObject.projectTo(eye, near);//, nearWidth, nearHeight, farDistance);
				renderObject.prepareForRender(sunsource, 1.0);
				renderObject.render(buffer, getWidth(), getHeight());
			}
			
			//buffer.paint(g2d);
			//g2d.dispose();
			
			//synchronized (prerendered) {
				prerendered = canvas;
			//}
			
		}
		
		
		@Override
		public void paint(Graphics g)
		{
			Graphics2D g2d = (Graphics2D)g;
			if (prerendered != null) {
				
				//g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.drawImage(prerendered, 0, 0, getWidth(), getHeight(), this);
			}
		}
		
	}
	
	
	
	protected double asin(double a)
	{
		return Math.asin(a);
	}
	
	protected double atan2(double a, double b)
	{
		return Math.atan2(a, b);
	}
	
	protected double sqr(double a)
	{
		return (a*a);
	}
	
	protected double abs(double a)
	{
		return Math.abs(a);
	}
	
	protected double pow(double a, double b)
	{
		return Math.pow(a, b);
	}
	
	protected double sqrt(double d)
	{
		return Math.sqrt(d);
	}
	
	protected double cos(double d)
	{
		return Math.cos(Math.toRadians(d));
	}
	
	protected double sin(double d)
	{
		return Math.sin(Math.toRadians(d));
	}
	
}

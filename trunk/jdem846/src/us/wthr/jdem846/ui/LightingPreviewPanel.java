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

package us.wthr.jdem846.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.wthr.jdem846.Perspectives;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.gfx.Renderable;
import us.wthr.jdem846.render.gfx.Square;
import us.wthr.jdem846.render.gfx.Vector;
import us.wthr.jdem846.render.gfx.ViewportBuffer;

@SuppressWarnings("serial")
public class LightingPreviewPanel extends JPanel
{
	
	private static Log log = Logging.getLog(LightingPreviewPanel.class);
	
	private BufferedImage prerendered = null;
	private List<Renderable> renderObjects = new LinkedList<Renderable>();
	private Color background = Color.BLACK;
	
	private double solarAzimuth = 183.0;
	private double solarElevation = 71.0;
	
	private List<ChangeListener> changeListeners = new LinkedList<ChangeListener>();
	
	public LightingPreviewPanel()
	{

		MouseAdapter mouseAdapter = new MouseAdapter() {
			public void mouseDragged(MouseEvent e)
			{
				onMouseDragged(e);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				fireChangeListeners();
			}
		};
		this.addMouseListener(mouseAdapter);
		this.addMouseMotionListener(mouseAdapter);
		
	}
	
	protected void onMouseDragged(MouseEvent e)
	{
		
		
		int x = e.getX();
		int y = e.getY();
		
		int size = (getWidth() < getHeight()) ? getWidth() : getHeight();
		
		int xMid = (int)Math.round(((double)getWidth()/2.0));
		int yMid = (int)Math.round(((double)getHeight()/2.0));
		
		double mX = x - xMid;
		double mY = y - yMid;
		double radius = sqrt(pow(Math.abs(mX), 2) + pow(Math.abs(mY), 2));
		double angle = Math.toDegrees(asin(Math.abs(mY) / radius));
		
		if (mX >= 0 && mY < 0) { 			// Top right
			angle = 90 - angle;
		} else if (mX >= 0 && mY >= 0) { 	// Bottom right
			angle += 90;
		} else if (mX < 0 && mY >= 0) { 	// bottom left
			angle = 180 +  90 - angle;
		} else { 							// Top right
			angle = 270 + angle;
		}

		double pctElev = radius / ((double) size / 2.0);
		double newElev = 90.0 * (1.0 - pctElev);
		
		if (newElev < 0)
			newElev = 0;
		if (newElev > 90)
			newElev = 90;
		
		this.setSolarAzimuth(angle);
		this.setSolarElevation(newElev);
		update(true);
		
	}
	
	

	protected void getPoints3D(double theta, double phi, double radius, double[] points)
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
	}

	public void update(boolean recreatePolygons)
	{
		//List<Renderable> polygons = new LinkedList<Renderable>();
		if (recreatePolygons) {
			renderObjects.clear();
			createPolygons(renderObjects);
		}
		
		preRender(renderObjects);
		repaint();
	}
	
	protected void createPolygons(List<Renderable> polygons)
	{
		int size = (getWidth() < getHeight()) ? getWidth() : getHeight();
		polygons.clear();
		
		double strips = 100.0;
		double slices = 100.0;
		
		double strip_step = 90.0 / strips;
		double slice_step = 360.0 / slices;
		double radius = ((double)size / 2.0) - 4;

		double p_tl[] = {0, 0, 0};
		double p_tr[] = {0, 0, 0};
		double p_bl[] = {0, 0, 0};
		double p_br[] = {0, 0, 0};
		
		//Color ballColor = Color.LIGHT_GRAY;
		int[] ballColor = {Color.LIGHT_GRAY.getRed(), Color.LIGHT_GRAY.getGreen(), Color.LIGHT_GRAY.getBlue()};

		for (double phi = -90; phi <= 90 - strip_step; phi +=strip_step) {
           for (double theta = 180; theta <= 360 + slice_step; theta+=slice_step) {
            //for (double theta = 0; theta <= 360 + slice_step; theta+=slice_step) {
            	
            	getPoints3D(theta, phi, radius, p_tl);
                getPoints3D(theta + slice_step, phi, radius, p_tr);
                getPoints3D(theta, phi + strip_step, radius, p_bl);
                getPoints3D(theta + slice_step, phi + strip_step, radius, p_br);
            	
                double[] normal = {0.0, 0.0, 0.0};
                
                Perspectives.calcNormal(p_tr, p_br, p_bl, normal);

                Square square = new Square(ballColor, new Vector(p_tr),
						new Vector(p_br),
						new Vector(p_bl),
						new Vector(p_tl));
                square.setNormal(normal);
                polygons.add(square);
                
            }
		}
		
	}
	
	protected void preRender(List<Renderable> rotated)
	{
		
		
		int size = (getWidth() < getHeight()) ? getWidth() : getHeight();

		BufferedImage canvas = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

		Vector eye = new Vector(0, 0, 1000);
		Vector near = new Vector(0, 0, 1000);
		//double nearWidth = 50;
		//double nearHeight = 50;
		//double farDistance = 50;
		double sunsource[] = {0.0, 0.0, 0.0};
		
		Vector sun = new Vector(0.0, 0.0, -1.0);
		Vector rotation = new Vector((solarElevation+90.0), 0.0, solarAzimuth);
		sun.rotate(rotation);
		//sun.rotate(-solarAzimuth, Vector.Z_AXIS);
		//sun.rotate(-solarElevation, Vector.X_AXIS);
		sunsource[0] = sun.getX();
		sunsource[1] = sun.getY();
		sunsource[2] = sun.getZ();
		
		
		Graphics2D g2d = (Graphics2D) canvas.getGraphics();

		g2d.setColor(background);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		
		
		
		
		ViewportBuffer buffer = new ViewportBuffer(size, size);
		
		for (Renderable renderObject : rotated) {

			renderObject.projectTo(eye, near);//, nearWidth, nearHeight, farDistance);
			renderObject.prepareForRender(sunsource, 2.0);
			renderObject.render(buffer, size, size);
		}
		
		BufferedImage image = buffer.paint(null, 0);
		g2d.drawImage(image, 0, 0, size, size, this);
		
		buffer.dispose();
		
		//buffer.paint(g2d);
		//g2d.dispose();
		
		//synchronized (prerendered) {
			prerendered = canvas;
		//}
		
	}
	
	
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		if (enabled) {
			background = Color.BLACK;
		} else {
			background = Color.LIGHT_GRAY;
		}
	}
	
	protected void getPoints2D(double angle, double radius, double[] points)
	{
		double b = radius * cos(angle);
        double x = sqrt(pow(radius, 2) - pow(b, 2));
        double y = sqrt(pow(radius, 2) - pow(x, 2));
        
        if (angle <= 90.0) {
        	y = radius - y;
        } else if (angle  <= 180.0) {
        	y = y + radius;
        } else if (angle  <= 270.0) {
        	x = -1 * x;
        	y = y + radius;
        } else if (angle <= 360.0) {
        	x = -1 * x;
        	y = radius - y;
        }
        points[0] = x;
        points[1] = y;

	}
	
	@Override
	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		if (prerendered != null) {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			g2d.setColor(background);
			g2d.fillRect(0, 0, getWidth(), getHeight());
			
			int size = (getWidth() < getHeight()) ? getWidth() : getHeight();

			int drawX = (int) (((double)getWidth() / 2.0) - (size / 2.0));
			int drawY = (int) (((double)getHeight() / 2.0) - (size / 2.0));
			
			g2d.drawImage(prerendered, drawX, drawY, size, size, this);
			
			
			
			int xMid = (int)Math.round(((double)getWidth()/2.0));
			int yMid = (int)Math.round(((double)getHeight()/2.0));
			
			double radius = ((double)(size) / 2.0);
			
			
			g2d.setColor(Color.RED);
			g2d.drawOval(drawX, drawY, (int) size,  (int)size);
			
			
			double angle = this.getSolarAzimuth();

			double[] points = {0.0, 0.0};
			
			getPoints2D(angle, radius, points);
			double x = points[0];
			double y = points[1];
			
			double xP = x + xMid;
			double yP = drawY + y;
			
			g2d.drawLine((int)xP, (int)yP, xMid, yMid);
			
			
			g2d.fillOval((int)Math.round(xMid - 5), (int)Math.round(yMid - 5), 10, 10);
			g2d.fillOval((int)Math.round(xP - 5), (int)Math.round(yP - 5), 10, 10);
			
			double pctElev = 1.0 - ( this.getSolarElevation() / 90.0);
			
			
			double elevRadius = radius*pctElev;
			size = (int) Math.round((double) size * pctElev);

			getPoints2D(angle, elevRadius , points);
			
			
			x = points[0];
			y = points[1];
			
			xP = x + xMid;
			yP = y + (((double)getHeight() / 2.0) - (size / 2.0));

			g2d.setColor(Color.BLACK);
			g2d.drawOval((int)Math.round(xP - 5), (int)Math.round(yP - 5), 10, 10);
			
			g2d.setColor(Color.YELLOW);
			g2d.fillOval((int)Math.round(xP - 5), (int)Math.round(yP - 5), 10, 10);


			int iAzimuth = (int)Math.round(getSolarAzimuth());
			int iElevation = (int) Math.round(getSolarElevation());
			
			String label = "" + iAzimuth + "\u00B0, " + iElevation + "\u00B0";
			FontMetrics fontMetrics = g2d.getFontMetrics();
			
			
			g2d.setColor(Color.BLACK);
			if (xP < xMid) {
				g2d.drawString(label, (int)Math.round(xP + 7), (int)Math.round(yP + 5));
				//log.info(label);
			} else {
				int lblWidth = fontMetrics.stringWidth(label);
				g2d.drawString(label, (int)Math.round(xP - 7 - lblWidth), (int)Math.round(yP + 5));
			}
			
			
			
			
		} else {
			log.warn("No preview to render");
		}
	}
	
	public double getSolarAzimuth()
	{
		return solarAzimuth;
	}


	public void setSolarAzimuth(double solarAzimuth)
	{
		this.solarAzimuth = solarAzimuth;
	}


	public double getSolarElevation()
	{
		return solarElevation;
	}


	public void setSolarElevation(double solarElevation)
	{
		this.solarElevation = solarElevation;
	}

	
	
	public void addChangeListener(ChangeListener listener)
	{
		changeListeners.add(listener);
	}
	
	public boolean removeChangeListener(ChangeListener listener)
	{
		return changeListeners.remove(listener);
	}
	
	public void fireChangeListeners()
	{
		ChangeEvent event = new ChangeEvent(this);
		
		for (ChangeListener listener : changeListeners) {
			listener.stateChanged(event);
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

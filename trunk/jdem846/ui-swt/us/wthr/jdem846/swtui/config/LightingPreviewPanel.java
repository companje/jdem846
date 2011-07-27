package us.wthr.jdem846.swtui.config;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
//import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import us.wthr.jdem846.Perspectives;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.gfx.Renderable;
import us.wthr.jdem846.render.gfx.Square;
import us.wthr.jdem846.render.gfx.Vector;
import us.wthr.jdem846.render.gfx.ViewportBuffer;
import us.wthr.jdem846.swtui.JDemShell;
import us.wthr.jdem846.swtui.SwtAwtImageUtil;

public class LightingPreviewPanel extends Canvas
{
	private static Log log = Logging.getLog(LightingPreviewPanel.class);
	
	private BufferedImage prerendered = null;
	private List<Renderable> renderObjects = new LinkedList<Renderable>();
	private Color background = Color.BLACK;
	
	private double solarAzimuth = 183.0;
	private double solarElevation = 71.0;
	
	
	public LightingPreviewPanel(Composite parent)
	{
		super(parent, SWT.FLAT);
		
		this.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event)
			{
				log.info("Paint Control");
				
				BufferedImage bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = (Graphics2D) bufferedImage.getGraphics().create();
				paint(g2d);
				
				ImageData swtImageData = SwtAwtImageUtil.convertToSWT(bufferedImage);
				Image image = new Image(JDemShell.getDisplayInstance(), swtImageData);
				event.gc.drawImage(image, 0, 0);
				g2d.dispose();
				
				//event.gc.drawImage(arg0, arg1, arg2)
			}
		});
		
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

	public void updatePreview(boolean recreatePolygons)
	{
		//List<Renderable> polygons = new LinkedList<Renderable>();
		if (recreatePolygons) {
			renderObjects.clear();
			createPolygons(renderObjects);
		}
		
		preRender(renderObjects);
		//repaint();
	}
	
	protected void createPolygons(List<Renderable> polygons)
	{
		Point point = this.getSize();
		int size = (point.x > point.y) ? point.x : point.y;
		//int size = (getWidth() < getHeight()) ? getWidth() : getHeight();
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
		Point point = this.getSize();
		int size = (point.x > point.y) ? point.x : point.y;
		
		//int size = (getWidth() < getHeight()) ? getWidth() : getHeight();

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
		g2d.fillRect(0, 0, point.x, point.y);
		
		
		
		
		ViewportBuffer buffer = new ViewportBuffer(size, size);
		
		for (Renderable renderObject : rotated) {

			renderObject.projectTo(eye, near);//, nearWidth, nearHeight, farDistance);
			renderObject.prepareForRender(sunsource, 2.0);
			renderObject.render(buffer, size, size);
		}
		
		BufferedImage image = buffer.paint(null, 0);
		g2d.drawImage(image, 0, 0, size, size, new ImageObserver() {
			public boolean imageUpdate(java.awt.Image img, int infoflags, int x, int y, int width, int height)
			{
				return true;
			}
		});
		
		buffer.dispose();
		
		//buffer.paint(g2d);
		//g2d.dispose();
		
		//synchronized (prerendered) {
			prerendered = canvas;
		//}
		
	}
	
	public int getWidth()
	{
		Point point = this.getSize();
		return point.x;
	}
	
	public int getHeight()
	{
		Point point = this.getSize();
		return point.y;
	}
	
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
			
			g2d.drawImage(prerendered, drawX, drawY, size, size, new ImageObserver() {
				public boolean imageUpdate(java.awt.Image img, int infoflags, int x, int y, int width, int height)
				{
					return true;
				}
			});
			
			
			
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

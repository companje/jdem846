package us.wthr.jdem846.geom;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.render.Canvas3d;
import us.wthr.jdem846.render.RenderPipeline;
import us.wthr.jdem846.render.gfx.Vector;


public class GeomRenderTestMain extends AbstractTestMain
{
	
	private static Log log = null;
	
	
	
	public static void main(String[] args)
	{
		try {
			initialize(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		log = Logging.getLog(GeomRenderTestMain.class);
		
		try {
			GeomRenderTestMain testMain = new GeomRenderTestMain();
			testMain.doTesting();
		} catch (Exception ex) {
			log.error("Uncaught exception while running test main: " + ex.getMessage(), ex);
		}
		
	}
	
	
	public void doTesting() throws Exception
	{
		Canvas3d canvas = new Canvas3d(500, 500, 500000, -500000, 1, null);
		
		
		double strips = 10.0;
		double slices = 20.0;
		
		double strip_step = 90.0 / strips;
		double slice_step = 360.0 / slices;
		double radius = 100;
		
		double p_tl[] = {0, 0, 0};
		double p_tr[] = {0, 0, 0};
		double p_bl[] = {0, 0, 0};
		double p_br[] = {0, 0, 0};
		
		int[] lineColor = {255, 0, 0, 255};
		
		
		int buffer = 200;
		
		for (double phi = -90; phi <= 90 - strip_step; phi +=strip_step) {
            for (double theta = 0; theta <= 360 + slice_step; theta+=slice_step) {
				
            	Spheres.getPoint3D(theta, phi, radius, p_tl);
            	Spheres.getPoint3D(theta + slice_step, phi, radius, p_tr);
            	Spheres.getPoint3D(theta, phi + strip_step, radius, p_bl);
            	Spheres.getPoint3D(theta + slice_step, phi + strip_step, radius, p_br);
            	
            	
            	Vertex v0 = new Vertex(p_tl[0]+buffer, p_tl[1]+buffer, p_tl[2], lineColor);
            	Vertex v1 = new Vertex(p_tr[0]+buffer, p_tr[1]+buffer, p_tr[2], lineColor);
            	
            	Vertex v2 = new Vertex(p_bl[0]+buffer, p_bl[1]+buffer, p_bl[2], lineColor);
            	Vertex v3 = new Vertex(p_tl[0]+buffer, p_tl[1]+buffer, p_tl[2], lineColor);
            	
            	
            	
            	Edge e0 = new Edge(v0, v1);
            	canvas.draw(e0);
            	
            	Edge e1 = new Edge(v2, v3);
            	canvas.draw(e1);
            	
			}
			
		}
		
		
		
		
		
		BufferedImage image = canvas.getImage();
		File writeTo = new File(JDem846Properties.getProperty("us.wthr.jdem846.testOutputPath") + "/polygon-testing.png");
		ImageIO.write(image, "PNG", writeTo);
	}
	
	public void __doTesting() throws Exception
	{
		Canvas3d canvas = new Canvas3d(500, 500, 500000, -500000, 1, null);
		
		
		
		
		
		int[] poly1Color = {255, 0, 0, 255};
		Polygon poly1 = new Polygon();
		poly1.addEdge(20, 20, 20, 40); // 20,20 -> 20,40
		poly1.addEdge(20, 40, 40, 40); // 20,40 -> 40,40
		poly1.addEdge(40, 40, 40, 20); // 40,40 -> 40,20
		//poly1.addEdge(40, 20, 20, 20); // 40,20 -> 20,20 (closes polygon)
		canvas.fill(poly1, poly1Color);
		
		Polygon poly2 = new Polygon();
		int[] poly2FillColor = {255, 0, 0, 255};
		int[] poly2DrawColor = {0, 0, 0, 255};
		int lastY = 0;
		int lastX = 0;
		for (int y = 20; y <= 400; y+=20) {
			int x = 100;
			
			if (y % 40 == 0) {
				x = 110;
			}
			if (lastY != 0 && lastX != 0) {
				poly2.addEdge(lastX, lastY, x, y);
				
				poly2.addEdge(lastX+30, lastY, x+30, y);
			}
			
			lastY = y;
			lastX = x;
		}
		canvas.draw(poly2, poly2DrawColor);
		canvas.fill(poly2, poly2FillColor);
		
		
		
		poly2 = new Polygon();

		lastY = 0;
		lastX = 0;
		for (int y = 20; y <= 400; y+=10) {
			int x = 140;
			
			if (y % 20 == 0) {
				x = 170;
			}
			if (lastY != 0 && lastX != 0) {
				poly2.addEdge(lastX, lastY, x, y);
				
				poly2.addEdge(lastX+30, lastY, x+30, y);
			}
			
			lastY = y;
			lastX = x;
		}
		canvas.draw(poly2, poly2DrawColor);
		canvas.fill(poly2, poly2FillColor);
		
		
		
		
		
		
		
		
		
		
		
		double[] vector0 = new double[3];
		double[] vector1 = new double[3];
		double[] eyeVector = {0, 0, 500};
		double[] nearVector = {0, 0, 250};
		
		
		
		Polygon poly3 = new Polygon();
		Polygon poly4 = new Polygon();
		vector0[0] = 0; // X
		vector0[1] = 0; // Z (in this 2d space)
		vector0[2] = 0; // Y (in this 2d space)
		vector1[0] = 0; // X
		vector1[1] = 0; // Z (in this 2d space)
		vector1[2] = 0; // Y (in this 2d space)
		
		double[][] points = {
				{200, 200, 200, 400},
				{200, 400, 400, 400},
				{400, 400, 400, 200}
		};
		
		for (double[] edgePoints : points) {
			vector0[0] = edgePoints[0]; 
			vector0[1] = 0; 
			vector0[2] = edgePoints[1]; 
			
			vector1[0] = edgePoints[2]; 
			vector1[1] = 0;
			vector1[2] = edgePoints[3];
			
			Vector.translate(-300, 0, -300, vector0);
			Vector.rotate(0, 20, 0, vector0);
			Vector.rotate(30, 0, 0, vector0);
			
			Vector.translate(-300, 0, -300, vector1);
			Vector.rotate(0, 20, 0, vector1);
			Vector.rotate(30, 0, 0, vector1);
			
			projectTo(vector0, eyeVector, nearVector);
			projectTo(vector1, eyeVector, nearVector);
			//log.info("Edge X/Y: " + vector0[0] + "/" + vector0[1] + ", X/Y: " + vector1[0] + "/" + vector1[1]);
			
			
			Vector.translate(300, 300, 0, vector0);
			Vector.translate(300, 300, 0, vector1);
			
			double x0 = (int) vector0[0];
			double y0 = (int) vector0[1];
			double z0 = (int) vector0[2];
			
			double x1 = (int) vector1[0];
			double y1 = (int) vector1[1];
			double z1 = (int) vector1[2];
			
			log.info("Edge X/Y/Z: " + x0 + "/" + y0 + "/" + z0 + ", X/Y/Z: " + x1 + "/" + y1 + "/" + z1);
			
			
			poly3.addEdge(x0, y0, z0, x1, y1, z1);
		}

		canvas.fill(poly3, poly1Color);
		
		
		int[] lineColor = {0, 0, 255, 255};
		Line line0 = new Line();
		line0.addEdge(0, 0, 0, 500, 500, 0);
		canvas.draw(line0, lineColor);
		
		Line line1 = new Line();
		line1.addEdge(500, 0, 0, 0, 500, 0);
		canvas.draw(line1, lineColor);
		
		BufferedImage image = canvas.getImage();
		File writeTo = new File(JDem846Properties.getProperty("us.wthr.jdem846.testOutputPath") + "/polygon-testing.png");
		ImageIO.write(image, "PNG", writeTo);
	}
	
	public void projectTo(double[] vector, double[] eye, double[] near) //Vector eye, Vector near)
	{
		double[] a = vector;   // 3D position of points being projected
		double[] e = near;     // Viewer's position relative to the display surface
		double[] c = eye;      // Camera position
		
		//eyeVector[2] = width;			// Camera position
		//nearVector[2] = (width/2.0f);	// Viewer's position relative to the display surface
		
		//double[] c = {1.0, 1.0, getWidth() * 20};
		
		
		vector[0] = ((a[0] - c[0]) - e[0]) * (e[2] / (a[2] - c[2]));
		vector[1] = ((a[1] - c[1]) - e[1]) * (e[2] / (a[2] - c[2]));
		
		
	}
	
	
}

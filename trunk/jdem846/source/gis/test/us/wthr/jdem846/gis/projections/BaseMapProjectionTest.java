package us.wthr.jdem846.gis.projections;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import junit.framework.TestCase;
import us.wthr.jdem846.exception.ImageException;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.image.ImageWriter;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class BaseMapProjectionTest extends TestCase
{
	private static Log log = Logging.getLog(BaseMapProjectionTest.class);
	
	double north;
	double south;
	double east;
	double west;
	
	double height;
	double width;
	
	String saveImagesTo;
	

	
	protected void setUp(double north, double south, double east, double west, double width, double height, String saveImagesTo) throws Exception
	{
		super.setUp();
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		this.width = width;
		this.height = height;
		this.saveImagesTo = System.getProperty("us.wthr.jdem846.testOutputPath") + "/" + saveImagesTo;
	}
	
	protected void __testProjection(MapProjection mapProjection, double latitude, double longitude, double columnShouldBe, double rowShouldBe)
	{
		MapPoint point = new MapPoint();

		point.row = point.column = -1;
		
		// Boston, MA, USA
		//double latitude = 42.357778;
		//double longitude = -71.061667;

		try {
			mapProjection.getPoint(latitude, longitude, 0.0, point);
		} catch (MapProjectionException ex) {
			fail("Failed to project coordinates: " + ex.getMessage());
		}
		
		assertEquals(rowShouldBe, point.row);
		assertEquals(columnShouldBe, point.column);
		
		log.info("Row: " + point.row);
		log.info("Column: " + point.column);
		
	}
	

	
	
	public void __testGenerateMap(MapProjection mapProjection)
	{

		MapPoint point = new MapPoint();

		BufferedImage image = new BufferedImage((int)width+1, (int)height+1, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D) image.createGraphics();
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, (int)width+1, (int)height+1);
		
		//g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		//g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//int pixelWidth = (int) Math.ceil((double)width / (Math.abs(east) + Math.abs(west)));
		//int pixelHeight = (int) Math.ceil((double)height / (Math.abs(north) + Math.abs(south)));
		
		Path2D.Double shape = new Path2D.Double();
		g2d.setColor(Color.BLACK);
		for (double latitude = north; latitude > south; latitude--) {
			//log.info("Writing pixels for latitude: " + latitude);
			//mapProjection.getPoint(latitude, 0.0, 0.0, point);
			//log.info("Coordinate: " + latitude + "/" + 0.0 + ", " + point.row + ", " + point.column);
			
			for (double longitude = west; longitude < east; longitude++) {
				
				try {
					mapProjection.getPoint(latitude, longitude, 0.0, point);
				} catch (MapProjectionException ex) {
					fail("Failed to project coordinates: " + ex.getMessage());
				}
				int c1 = (int) Math.round(point.column);
				int r1 = (int) Math.round(point.row); 
				
				
				assertTrue("Column " + point.column + " is less than zero at " + latitude + "/" + longitude, point.column >= 0);
				assertTrue("Column " + point.column + " exceeds image width at " + latitude + "/" + longitude, point.column < width);
				assertTrue("Row " + point.row + " is less than zero at " + latitude + "/" + longitude, point.row >= 0);
				assertTrue("Row " + point.row + " exceeds image height at " + latitude + "/" + longitude, point.row < height);
	

				
				
				try {
					mapProjection.getPoint(latitude-1.0, longitude, 0.0, point);
				} catch (MapProjectionException ex) {
					fail("Failed to project coordinates: " + ex.getMessage());
				}
				int c2 = (int) Math.round(point.column);
				int r2 = (int) Math.round(point.row); 
				
				try {
					mapProjection.getPoint(latitude-1.0, longitude+1.0, 0.0, point);
				} catch (MapProjectionException ex) {
					fail("Failed to project coordinates: " + ex.getMessage());
				}
				int c3 = (int) Math.round(point.column);
				int r3 = (int) Math.round(point.row); 
				
				try {
					mapProjection.getPoint(latitude, longitude+1.0, 0.0, point);
				} catch (MapProjectionException ex) {
					fail("Failed to project coordinates: " + ex.getMessage());
				}
				int c4 = (int) Math.round(point.column);
				int r4 = (int) Math.round(point.row); 
				
				shape.reset();
				shape.moveTo(c1, r1);
				shape.lineTo(c2, r2);
				shape.lineTo(c3, r3);
				shape.lineTo(c4, r4);
				shape.closePath();
				g2d.fill(shape);
			}
			
		}
		
		g2d.setColor(Color.RED);
		
		double coordWidthLat = (Math.abs(north) + Math.abs(south)) / 12;
		double coordWidthLon = (Math.abs(west) + Math.abs(east)) / 24;
		
		for (double latitude = north; latitude >= south; latitude-=coordWidthLat) {
			for (double longitude = west; longitude <= east; longitude+=coordWidthLon) {
				
				int c1, r1, c2, r2;
				
				if (latitude > south) {
					try {
						mapProjection.getPoint(latitude, longitude, 0.0, point);
					} catch (MapProjectionException ex) {
						fail("Failed to project coordinates: " + ex.getMessage());
					}
					
					c1 = (int) Math.floor(point.column);
					r1 = (int) Math.floor(point.row); 
					
					try {
						mapProjection.getPoint(latitude-coordWidthLat, longitude, 0.0, point);
					} catch (MapProjectionException ex) {
						fail("Failed to project coordinates: " + ex.getMessage());
					}
					
					c2 = (int) Math.floor(point.column);
					r2 = (int) Math.floor(point.row); 
					
					g2d.drawLine(c1, r1, c2, r2);
				}
				
				if (longitude < east) {
					
					try {
						mapProjection.getPoint(latitude, longitude, 0.0, point);
					} catch (MapProjectionException ex) {
						fail("Failed to project coordinates: " + ex.getMessage());
					}
					
					c1 = (int) Math.floor(point.column);
					r1 = (int) Math.floor(point.row); 
					
					try {
						mapProjection.getPoint(latitude, longitude+coordWidthLon, 0.0, point);
					} catch (MapProjectionException ex) {
						fail("Failed to project coordinates: " + ex.getMessage());
					}
					c2 = (int) Math.floor(point.column);
					r2 = (int) Math.floor(point.row); 
					
					g2d.drawLine(c1, r1, c2, r2);
				}
				
				
				
				
			}
			
		}
		
		g2d.setColor(Color.YELLOW);
		g2d.drawLine(0, (int)(height / 2), (int)width, (int)(height / 2));
		
		try {
			ImageWriter.saveImage(image, saveImagesTo.replace("{test}", "testGenerateMap"));
		} catch (ImageException ex) {
			log.error("Failed to save image: " + ex.getMessage(), ex);
		}
		
		
	}
	
}

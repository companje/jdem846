package us.wthr.jdem846.render.mapprojection;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import us.wthr.jdem846.exception.ImageException;
import us.wthr.jdem846.image.ImageWriter;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import junit.framework.TestCase;

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
		this.saveImagesTo = saveImagesTo;
	}
	
	protected void __testProjection(MapProjection mapProjection, double latitude, double longitude, double columnShouldBe, double rowShouldBe)
	{
		MapPoint point = new MapPoint();

		point.row = point.column = -1;
		
		// Boston, MA, USA
		//double latitude = 42.357778;
		//double longitude = -71.061667;

		
		mapProjection.getPoint(latitude, longitude, 0.0, point);
		
		assertEquals(rowShouldBe, point.row);
		assertEquals(columnShouldBe, point.column);
		
		log.info("Row: " + point.row);
		log.info("Column: " + point.column);
		
	}
	
	
	public void __testGenerateMap(MapProjection mapProjection)
	{
		
		MapPoint point = new MapPoint();

		BufferedImage image = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D) image.createGraphics();
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, (int)width, (int)height);
		
		int pixelWidth = (int) Math.ceil((double)width / (Math.abs(east) + Math.abs(west)));
		int pixelHeight = (int) Math.ceil((double)height / (Math.abs(north) + Math.abs(south)));
		
		g2d.setColor(Color.BLACK);
		for (double latitude = north; latitude > south; latitude--) {
			//log.info("Writing pixels for latitude: " + latitude);
			mapProjection.getPoint(latitude, 0.0, 0.0, point);
			log.info("Coordinate: " + latitude + "/" + 0.0 + ", " + point.row + ", " + point.column);
			
			for (double longitude = west; longitude < east; longitude++) {
				
				mapProjection.getPoint(latitude, longitude, 0.0, point);
				
				assertTrue("Column " + point.column + " is less than zero at " + latitude + "/" + longitude, point.column >= 0);
				assertTrue("Column " + point.column + " exceeds image width at " + latitude + "/" + longitude, point.column < width);
				assertTrue("Row " + point.row + " is less than zero at " + latitude + "/" + longitude, point.row >= 0);
				assertTrue("Row " + point.row + " exceeds image height at " + latitude + "/" + longitude, point.row < height);
				
				int column = (int) Math.round(point.column);
				int row = (int) Math.round(point.row);


				g2d.fillRect(column, row, pixelWidth, pixelHeight);
			}
			
		}
		
		g2d.setColor(Color.RED);
		g2d.drawLine(0, (int)(height / 2), (int)width, (int)(height / 2));
		
		try {
			ImageWriter.saveImage(image, saveImagesTo.replace("{test}", "testGenerateMap"));
		} catch (ImageException ex) {
			log.error("Failed to save image: " + ex.getMessage(), ex);
		}
		
	}
	
}

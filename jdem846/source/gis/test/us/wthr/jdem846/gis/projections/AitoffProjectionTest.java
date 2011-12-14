package us.wthr.jdem846.gis.projections;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import us.wthr.jdem846.exception.ImageException;
import us.wthr.jdem846.image.ImageWriter;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import junit.framework.TestCase;

public class AitoffProjectionTest extends BaseMapProjectionTest
{
	private static Log log = Logging.getLog(AitoffProjectionTest.class);
	
	
	@Override
	protected void setUp() throws Exception
	{
		String saveImagesTo = "test-image-AitoffProjectionTest-{test}.png";
		setUp(90, -90, 180, -180, 800, 400, saveImagesTo);
	}
	
	

	public void testProjection1()
	{
		AitoffProjection projection = new AitoffProjection(90, -90, 180, -180, width, height);
		this.__testProjection(projection, 0.0, 0.0, 400.0, 200.0);

		// Boston, MA, USA
		//double latitude = 42.357778;
		//double longitude = -71.061667;
	}
	
	public void testGenerateMap()
	{
		//AitoffProjection projection = new AitoffProjection(90, -90, 180, -180, width, height);
		this.__testGenerateMap(MapProjectionEnum.AITOFF);
	}
	
	

}

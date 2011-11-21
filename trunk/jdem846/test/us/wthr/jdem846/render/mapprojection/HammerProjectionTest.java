package us.wthr.jdem846.render.mapprojection;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class HammerProjectionTest extends BaseMapProjectionTest
{
	private static Log log = Logging.getLog(HammerProjectionTest.class);
	
	

	@Override
	protected void setUp() throws Exception
	{
		String saveImagesTo = "C:/srv/elevation/DataRaster-Testing/test-image-HammerProjection-{test}.png";
		setUp(90, -90, 180, -180, 800, 403, saveImagesTo);
	}
	
	

	public void testProjection1()
	{
		HammerProjection projection = new HammerProjection(90, -90, 180, -180, width, height);
		this.__testProjection(projection, 0.0, 0.0, 400.0, 200.0);

		// Boston, MA, USA
		//double latitude = 42.357778;
		//double longitude = -71.061667;
	}
	
	public void testGenerateMap()
	{
		HammerProjection projection = new HammerProjection(90, -90, 180, -180, width, height);
		this.__testGenerateMap(projection);
	}
}

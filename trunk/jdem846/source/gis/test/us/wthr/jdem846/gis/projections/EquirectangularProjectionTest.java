package us.wthr.jdem846.gis.projections;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class EquirectangularProjectionTest extends BaseMapProjectionTest
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(EquirectangularProjectionTest.class);
	

	//private String saveImagesTo;

	
	
	@Override
	protected void setUp() throws Exception
	{
		String saveImagesTo = "test-image-EquirectangularProjection-{test}.png";
		setUp(90, -90, 180, -180, 800, 400, saveImagesTo);
	}
	
	

	public void testProjection1()
	{
		MapProjection projection = new EquirectangularProjection(90, -90, 180, -180, width, height);
		this.__testProjection(projection, 0.0, 0.0, 400.0, 200.0);

		// Boston, MA, USA
		//double latitude = 42.357778;
		//double longitude = -71.061667;
	}
	

	
	
	
}

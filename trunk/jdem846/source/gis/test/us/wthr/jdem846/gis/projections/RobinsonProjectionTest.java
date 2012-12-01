package us.wthr.jdem846.gis.projections;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class RobinsonProjectionTest extends BaseMapProjectionTest
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(RobinsonProjectionTest.class);


	
	@Override
	protected void setUp() throws Exception
	{
		String saveImagesTo = "test-image-RobinsonProjectionTest-{test}.png";
		setUp(90, -90, 180, -180, 800, 408, saveImagesTo);
	}
	
	

	public void testProjection1()
	{
		MapProjection projection = new RobinsonProjection(90, -90, 180, -180, width, height);
		this.__testProjection(projection, 0.0, 0.0, 400.0, 204.0);

		// Boston, MA, USA
		//double latitude = 42.357778;
		//double longitude = -71.061667;
	}
	

	
}

package us.wthr.jdem846.gis.projections;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class WinkelTripelProjectionTest extends BaseMapProjectionTest
{
	
	private static Log log = Logging.getLog(WinkelTripelProjectionTest.class);
	

	@Override
	protected void setUp() throws Exception
	{
		String saveImagesTo = "C:/srv/elevation/DataRaster-Testing/test-image-WinkelTripelProjectionTest-{test}.png";
		setUp(90, -90, 180, -180, 800, 491, saveImagesTo);
	}
	
	

	public void testProjection1()
	{
		MapProjection projection = new WinkelTripelProjection(90, -90, 180, -180, width, height);
		this.__testProjection(projection, 0.0, 0.0, width, height);

		// Boston, MA, USA
		//double latitude = 42.357778;
		//double longitude = -71.061667;
	}
	
	public void testGenerateMap()
	{
		//MapProjection projection = new WinkelTripelProjection(90, -90, 180, -180, width, height);
		this.__testGenerateMap(MapProjectionEnum.WINKELTRIPEL);
	}
	
	
}	

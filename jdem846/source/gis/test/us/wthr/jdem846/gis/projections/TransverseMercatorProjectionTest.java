package us.wthr.jdem846.gis.projections;

public class TransverseMercatorProjectionTest extends BaseMapProjectionTest
{

	double north = 90;
	double south = -90;
	
	@Override
	protected void setUp() throws Exception
	{
		String saveImagesTo = "C:/srv/elevation/DataRaster-Testing/test-image-TransverseMercatorProjection-{test}.png";
		setUp(north, south, 180, -180, 597, 600, saveImagesTo);
	}
	
	public void testGenerateMap()
	{
		MapProjection projection = new TransverseMercatorProjection(north, south, 180, -180, width, height);
		this.__testGenerateMap(projection);
	}

	public void testProjection1()
	{
		MapProjection projection = new TransverseMercatorProjection(north, south, 180, -180, width, height);
		this.__testProjection(projection, 0.0, 0.0, 400.0, 200.0);

		// Boston, MA, USA
		//double latitude = 42.357778;
		//double longitude = -71.061667;
	}
	
	
}

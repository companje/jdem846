package us.wthr.jdem846.gis.projections;

public class MercatorProjectionTest extends BaseMapProjectionTest
{

	double north = 85;
	double south = -85;
	
	@Override
	protected void setUp() throws Exception
	{
		String saveImagesTo = "test-image-MercatorProjection-{test}.png";
		setUp(north, south, 180, -180, 707, 600, saveImagesTo);
	}
	
	

	
	
	public void testGenerateMap()
	{
		MapProjection projection = new MercatorProjection(north, south, 180, -180, width, height);
		this.__testGenerateMap(projection);
	}
	
	public void testProjection1()
	{
		MapProjection projection = new MercatorProjection(north, south, 180, -180, width, height);
		this.__testProjection(projection, -89.0, 0.0, 400.0, 200.0);

		// Boston, MA, USA
		//double latitude = 42.357778;
		//double longitude = -71.061667;
	}
}

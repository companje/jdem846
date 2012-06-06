package us.wthr.jdem846.canvas;

public class GeoRasterBuffer3d extends RasterBuffer3d
{
	
	private GeoMatrix geoMatrix;
	
	public GeoRasterBuffer3d(int width, int height, int pixelStackDepth, int subpixelWidth)
	{
		super(width, height, pixelStackDepth, subpixelWidth);
		
		geoMatrix = new GeoMatrix(width, height);
		
	}

	@Override
	public void reset(int backgroundColor)
	{
		super.reset(backgroundColor);
		geoMatrix.reset();
	}
	
	@Override
	public void dispose()
	{
		if (!isDisposed()) {
			geoMatrix.dispose();
		}
		super.dispose();
	}
	
	public void set(double x, double y, double z, int rgba, double latitude, double longitude, double elevation)
	{
		if (pixelMatrix.isVisibleAbsolute(x, y, z)) {
			geoMatrix.set(x, y, latitude, longitude, elevation);
		}
		super.set(x, y, z, rgba);
	}
	
	
	public double getLatitude(double x, double y)
	{
		return geoMatrix.getLatitude(x, y);
	}
	
	public double getLongitude(double x, double y)
	{
		return geoMatrix.getLongitude(x, y);
	}
	
	
	public double getElevation(double x, double y)
	{
		return geoMatrix.getElevation(x, y);
	}
	
	
}

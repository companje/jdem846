package us.wthr.jdem846;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import us.wthr.jdem846.graphics.ImageCapture;
import us.wthr.jdem846.image.ImageTypeEnum;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.model.ElevationHistogramModel;
import us.wthr.jdem846.util.ColorUtil;

public class SimpleImageElevationModel implements ElevationModel
{

	private ImageCapture imageCapture;
	
	public SimpleImageElevationModel(ImageCapture imageCapture)
	{
		this.imageCapture = imageCapture;
	}
	
	
	@Override
	public void load()
	{
		
	}
	
	@Override
	public void unload()
	{
		
	}
	
	@Override
	public boolean isLoaded()
	{
		return (imageCapture != null);
	}
	
	@Override
	public void dispose()
	{
		imageCapture.dispose();
		this.imageCapture = null;
	}

	@Override
	public void reset()
	{
		
		
	}

	@Override
	public boolean hasProperty(String key)
	{
		
		return false;
	}

	@Override
	public void setProperty(String key, String value)
	{
		
	}

	@Override
	public String getProperty(String key)
	{
		return null;
	}

	@Override
	public Map<String, String> getProperties()
	{
		return null;
	}

	@Override
	public int getRgba(double x, double y)
	{
		return imageCapture.get((int)MathExt.round(x), (int)MathExt.round(y));
	}

	@Override
	public void getRgba(double x, double y, int[] fill)
	{
		int c = getRgba(x, y);
		ColorUtil.intToRGBA(c, fill);
	}

	@Override
	public double getLatitude(double x, double y)
	{
		return 0;
	}

	@Override
	public double getLongitude(double x, double y)
	{
		return 0;
	}

	@Override
	public double getElevation(double x, double y)
	{
		return 0;
	}

	@Override
	public boolean getMask(double x, double y)
	{
		int c = getRgba(x, y);
		return (c != 0x0); // Not really
	}
	
	@Override
	public int getWidth()
	{
		return imageCapture.getWidth();
	}
	
	@Override
	public int getHeight()
	{
		return imageCapture.getHeight();
	}
	
	@Override
	public BufferedImage getImage()
	{
		BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		WritableRaster raster = image.getRaster();

		int[] rgba = new int[4];
		rgba[3] = 0xFF;
		
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				getRgba(x, y, rgba);
				raster.setPixel(x, y, rgba);
			}
		}
		
		return image;
	}

	@Override
	public ElevationHistogramModel getElevationHistogramModel()
	{
		return null;
	}
	
	@Override
	public void setElevationHistogramModel(ElevationHistogramModel elevationHistogramModel)
	{
		
	}


	@Override
	public void writeImageData(OutputStream zos,
			ImageTypeEnum imageTypeFromFormatName) throws IOException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void writeModelData(OutputStream zos) throws IOException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void writeProperties(OutputStream zos) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
}

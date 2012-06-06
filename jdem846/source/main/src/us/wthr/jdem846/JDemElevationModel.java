package us.wthr.jdem846;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import us.wthr.jdem846.canvas.AbstractBuffer;
import us.wthr.jdem846.canvas.GeoRasterBuffer3d;
import us.wthr.jdem846.canvas.util.ColorUtil;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.model.ElevationHistogramModel;

public class JDemElevationModel extends AbstractBuffer
{
	public final static float NO_VALUE = -999999.999f;
	
	private boolean[] maskBuffer;
	private int[] rgbaBuffer;
	private float[] longitudeBuffer;
	private float[] latitudeBuffer;
	private float[] elevationBuffer;
	
	
	public JDemElevationModel(GeoRasterBuffer3d geoRasterBuffer)
	{
		this(geoRasterBuffer.getWidth(), geoRasterBuffer.getHeight());
		
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				int index = getIndex(x, y);
				
				rgbaBuffer[index] = geoRasterBuffer.get(x, y);
				maskBuffer[index] = geoRasterBuffer.isPixelFilled(x, y);
				
				latitudeBuffer[index] = (float) geoRasterBuffer.getLatitude(x, y);
				longitudeBuffer[index] = (float) geoRasterBuffer.getLongitude(x, y);
				elevationBuffer[index] = (float) geoRasterBuffer.getElevation(x, y);
			}
		}
		
		
	}
	
	public JDemElevationModel(int width, int height)
	{
		super(width, height, 1);
		
		maskBuffer = new boolean[getBufferLength()];
		rgbaBuffer = new int[getBufferLength()];
		longitudeBuffer = new float[getBufferLength()];
		latitudeBuffer = new float[getBufferLength()];
		elevationBuffer = new float[getBufferLength()];
		
		reset();
	}
	
	
	

	public void dispose()
	{
		maskBuffer = null;
		rgbaBuffer = null;
		longitudeBuffer = null;
		latitudeBuffer = null;
		elevationBuffer = null;
	}


	@Override
	public void reset()
	{
		
	}
	
	
	public ElevationHistogramModel getElevationHistogramModel(int bins)
	{
		
		double minimum = 10000000;
		double maximum = -10000000;
		
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				
				if (this.getMask(x, y)) {
					double e = this.getElevation(x, y);
					minimum = MathExt.min(minimum, e);
					maximum = MathExt.max(maximum, e);
				}
				
			}
		}
		
		
		ElevationHistogramModel histogramModel = new ElevationHistogramModel(bins, minimum, maximum);
		
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				
				if (this.getMask(x, y)) {
					double e = this.getElevation(x, y);
					histogramModel.add(e);
				}
			}
		}
		
		return histogramModel;
	}
	
	public int getRgba(double x, double y)
	{
		int index = this.getIndex(x, y);
		if (index >= 0 && index < getBufferLength()) {
			return rgbaBuffer[index];
		} else {
			return 0x0;
			// TODO: Throw
		}
	}
	
	public void getRgba(double x, double y, int[] fill)
	{
		int rgba = getRgba(x, y);
		ColorUtil.intToRGBA(rgba, fill);
	}
	
	
	
	public double getLatitude(double x, double y)
	{
		int index = this.getIndex(x, y);
		if (index >= 0 && index < getBufferLength()) {
			return latitudeBuffer[index];
		} else {
			return NO_VALUE;
			// TODO: Throw
		}
	}
	
	public double getLongitude(double x, double y)
	{
		int index = this.getIndex(x, y);
		if (index >= 0 && index < getBufferLength()) {
			return longitudeBuffer[index];
		} else {
			return NO_VALUE;
			// TODO: Throw
		}
	}
	
	public double getElevation(double x, double y)
	{
		int index = this.getIndex(x, y);
		if (index >= 0 && index < getBufferLength()) {
			return elevationBuffer[index];
		} else {
			return NO_VALUE;
			// TODO: Throw
		}
	}
	
	public boolean getMask(double x, double y)
	{
		int index = this.getIndex(x, y);
		if (index >= 0 && index < getBufferLength()) {
			return maskBuffer[index];
		} else {
			return false;
			// TODO: Throw
		}
	}
	
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
	

	
	
}

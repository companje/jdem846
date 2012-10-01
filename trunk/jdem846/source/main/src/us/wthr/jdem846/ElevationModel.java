package us.wthr.jdem846;

import java.awt.image.BufferedImage;
import java.util.Map;

import us.wthr.jdem846.model.ElevationHistogramModel;

public interface ElevationModel
{
	public void dispose();
	public void reset();
	public boolean hasProperty(String key);
	public void setProperty(String key, String value);
	public String getProperty(String key);
	public Map<String, String> getProperties();
	public int getRgba(double x, double y);
	public void getRgba(double x, double y, int[] fill);
	public double getLatitude(double x, double y);
	public double getLongitude(double x, double y);
	
	public double getElevation(double x, double y);
	public boolean getMask(double x, double y);
	
	public BufferedImage getImage();
	public ElevationHistogramModel getElevationHistogramModel();
	
	public int getWidth();
	public int getHeight();
	
	public void setElevationHistogramModel(ElevationHistogramModel elevationHistogramModel);
	
	
	
	
	
	
	
}

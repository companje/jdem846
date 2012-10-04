package us.wthr.jdem846.scaling;

public interface ElevationScaler
{
	
	public void setElevationMultiple(double elevationMultiple);
	public void setElevationMinimum(double elevationMinimum);
	public void setElevationMaximum(double elevationMaximum);
	public double scale(double elevation);
	
	public ElevationScaler copy();
	
}

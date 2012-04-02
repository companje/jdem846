package us.wthr.jdem846.render.render3;

public interface ModelPointHandler
{
	public void onModelLatitudeStart(double latitude);
	public void onModelPoint(double latitude, double longitude);
	public void onModelLatitudeEnd(double latitude);
}

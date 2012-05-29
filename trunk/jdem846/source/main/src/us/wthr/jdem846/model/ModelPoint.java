package us.wthr.jdem846.model;

public interface ModelPoint
{
	


	public double getElevation();
	public void setElevation(double elevation);
	public void setElevation(float elevation);
	public void getRgba(int[] fill);
	public int getRgba();
	public void setRgba(int rgba);
	public void setRgba(int[] rgba);
	public void setRgba(int r, int g, int b, int a);
	public void dispose();

}

package us.wthr.jdem846.graphics;


public interface IColor
{
	
	public int getRed();
	public int getGreen();
	public int getBlue();
	public int getAlpha();
	
	public int asInt();
	
	public void toArray(int[] array);
	public void toArray(int[] array, int offset);

	public void toArrayGl(int[] array);
	public void toArrayGl(int[] array, int offset);
	
	public void toArray(float[] array);
	public void toArray(float[] array, int offset);
	
	public void toArray(double[] array);
	public void toArray(double[] array, int offset);
}

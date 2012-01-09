package us.wthr.jdem846.render;

public class ZBuffer
{
	private final static double NO_VALUE = Double.NaN;
	
	private double[][] zBuffer;
	
	private int width;
	private int height;
	
	public ZBuffer(int width, int height)
	{
		this.width = width;
		this.height = height;
		
		zBuffer = new double[height][width];
		reset();
	}
	
	public void reset()
	{
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				zBuffer[y][x] = ZBuffer.NO_VALUE;
			}
		}
	}
	
	public boolean isVisible(int x, int y, double z)
	{
		if (x < 0 || x >= getWidth())
			return false;
		if (y < 0 || y >= getHeight())
			return false;
		
		
		if (zBuffer[y][x] == ZBuffer.NO_VALUE) {
			zBuffer[y][x] = z;
			return true;
		} else if (zBuffer[y][x] >= z) {
			return false;
		} else { //(zBuffer[y][x] < z) {
			zBuffer[y][x] = z;
			return true;
		}
	}
	
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
}

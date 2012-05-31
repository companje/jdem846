package us.wthr.jdem846.canvas;


public class ZBuffer extends AbstractBuffer
{
	private final static float NO_VALUE = -999999.999f;
	

	private float[] buffer;
	
	public ZBuffer(int width, int height, int subpixelWidth)
	{
		super(width, height, subpixelWidth);
		
		buffer = new float[getBufferLength()];
		
		reset();
	}
	
	public void reset()
	{
		if (buffer == null) {
			return;
		}
		
		for (int i = 0; i < getBufferLength(); i++) {
			buffer[i] = NO_VALUE;
		}
	}
	
	
	
	
	public void set(double x, double y, double z)
	{

		int index = this.getIndex(x, y);
		
		if (index >= 0 && index < getBufferLength()) {
			buffer[index] = (float) z;
		} else {
			// TODO: Throw
		}
	}
	
	public double get(double x, double y)
	{
		int index = this.getIndex(x, y);
		if (index >= 0 && index < getBufferLength()) {
			return buffer[index];
		} else {
			return NO_VALUE;
			// TODO: Throw
		}
	}
	
	
	
	public boolean isVisible(double x, double y, double z)
	{
		double _z = get(x, y);
		if (z == NO_VALUE || (z >= _z)) {
			return true;
		} else {
			return false;
		}
	}

}

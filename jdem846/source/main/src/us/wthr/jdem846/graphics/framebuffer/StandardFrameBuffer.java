package us.wthr.jdem846.graphics.framebuffer;

public class StandardFrameBuffer extends AbstractFrameBuffer implements FrameBuffer
{


	protected BufferPoint[] buffer = null;
	
	
	public StandardFrameBuffer(int width, int height)
	{
		super(width, height);
		buffer = new BufferPoint[bufferLength];
		for (int i = 0; i < bufferLength; i++) {
			buffer[i] = null;
			//buffer[i] = new BufferPoint(0x0, FrameBuffer.FB_MINIMUM_Z_INDEX);
		}
		
	}


	@Override
	public boolean isVisible(double x, double y, double z, int rgba) 
	{
		if ((0xFF & (rgba >>> 24)) == 0x0) {
			return false;
		}
		
		int index = this.index(x, y);
		
		if (index < 0 || index >= this.bufferLength) {
			return false;
		}
		

		
		if (this.buffer[index] != null && this.buffer[index].z > z) {
			return false;
		}
		
		return true;
		
	}


	@Override
	public void reset(boolean setBackground, int background) 
	{
		for (int i = 0; i < this.bufferLength; i++) {

			if (setBackground) {
				this.buffer[i] = new BufferPoint(background, FrameBuffer.FB_MINIMUM_Z_INDEX);
			} else if (this.buffer[i] != null) {
				this.buffer[i].z = FrameBuffer.FB_MINIMUM_Z_INDEX;
				this.buffer[i].rgba = 0x0;
			}
			
		}
	}


	@Override
	public void set(double x, double y, double z, int rgba) 
	{
		if (!isVisible(x, y, z, rgba)) {
			return;
		}
		
		int index = this.index(x, y);
		
		if (index >= 0 && index < this.bufferLength) {
		
			if (buffer[index] == null) {
				buffer[index] = new BufferPoint(rgba, z);
			} else {
				buffer[index].z = z;
				buffer[index].rgba = rgba;
			}
		
		}
	}


	@Override
	public int get(double x, double y) 
	{
		int index = this.index(x, y);
		
		if (index >= 0 && index < this.bufferLength) {
			
			if (buffer[index] == null) {
				return 0x0;
			} else {
				return buffer[index].rgba;
			}
			
		} else {
			return 0x0;
		}
	}

	
	
	
	
}

package us.wthr.jdem846.graphics.framebuffer;


public class IndexedBufferPoint extends BufferPoint
{
	public int x;
	public int y;
	
	public IndexedBufferPoint(int rgba, int x, int y, double z)
	{
		super(rgba, z);
		this.x = x;
		this.y = y;
	}
	
	public int index(int width)
	{
		return (y * width) + x;
	}
	
	
	public int compareTo(IndexedBufferPoint bp)
	{
		if (bp == null) {
			return -1;
		} else {
			if (bp.y > this.y) {
				return -1;
			} else if (bp.y == this.y) {
				
				if (bp.x > this.x) {
					return -1;
				} else if (bp.x == this.x) {
					
					if (bp.z > this.z) {
						return -1;
					} else if (bp.z == this.z) {
						return 0;
					} else { // bp.z > this.z
						return 1;
					}
					
				} else { // bp.x > this.x
					return 1;
				}
				
			} else { // bp.y > this.y
				return 1;
			}
		}
	}
	
	
}

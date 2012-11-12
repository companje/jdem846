package us.wthr.jdem846.graphics.framebuffer;


public class BufferPoint implements Comparable<BufferPoint>
{
	
	public int rgba = 0;
	public double z = 0;
	
	public BufferPoint left = null;
	public BufferPoint right = null;
	
	public BufferPoint()
	{
		
	}
	
	public BufferPoint(int rgba, double z)
	{
		this.rgba = rgba;
		this.z = z;
	}
	
	public boolean isOpaque()
	{
		return (0xFF & (rgba >>> 24)) == 0xFF;
	}
	
	
	public void addLeaf(BufferPoint leaf)
	{
		if (leaf == null) {
			return;
		}
		
		int compare = this.compareTo(leaf);
		
		if ((compare > 0) || (compare == 0 && this.rgba != leaf.rgba)) {
			if (this.left == null) {
				this.left = leaf;
			} else {
				this.left.addLeaf(leaf);
			}
		} else if (compare < 0){
			if (this.right == null) {
				this.right = leaf;
			} else {
				this.right.addLeaf(leaf);
			}
		}
	}
	
	@Override
	public int compareTo(BufferPoint bp)
	{
		if (bp == null) {
			return -1;
		} else {
			if (bp.z > this.z) {
				return -1;
			} else if (bp.z == this.z) {
				return 0;
			} else {
				return 1;
			}
		}

	}
	
}


package us.wthr.jdem846.graphics.framebuffer;

public interface FrameBuffer {
	public static final double FB_MINIMUM_Z_INDEX = -9999999999.99;
	
	public boolean isVisible(double x, double y, double z, int rgba);
	public void reset();
	public void reset(boolean setBackground, int background);
	public void set(double x, double y, double z, int rgba);
	public int get(double x, double y);
	public int getWidth();
	public int getHeight();
}

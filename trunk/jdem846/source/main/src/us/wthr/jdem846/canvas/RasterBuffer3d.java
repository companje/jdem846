package us.wthr.jdem846.canvas;

import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.canvas.util.ColorUtil;

public class RasterBuffer3d
{
	
	private int width;
	private int height;
	
	private int subpixelWidth;
	private int pixelStackDepth;
	
	//private PixelBuffer pixelBuffer;
	//private ZBuffer zBuffer;
	private PixelMatrix pixelMatrix;
	
	private boolean isDisposed = false;
	
	private int[] rgbaBuffer = new int[4];
	private int backgroundColor = 0x0;
	
	public RasterBuffer3d(int width, int height, int pixelStackDepth, int subpixelWidth)
	{
		this.width = width;
		this.height = height;
		this.subpixelWidth = subpixelWidth;
		this.pixelStackDepth = pixelStackDepth;
		
		//pixelBuffer = new PixelBuffer(width, height, subpixelWidth);
		//zBuffer = new ZBuffer(width, height, subpixelWidth);
		
		pixelMatrix = new PixelMatrix(width, height, pixelStackDepth, subpixelWidth);
	}
	
	public void reset()
	{
		reset(0x0);
	}
	
	public void reset(int backgroundColor)
	{
		this.backgroundColor = backgroundColor;
		//pixelBuffer.reset(backgroundColor);
		//zBuffer.reset();
		pixelMatrix.reset(backgroundColor);
	}
	
	public void dispose()
	{
		if (!isDisposed()) {
			
			//pixelBuffer = null;
			//zBuffer = null;
			pixelMatrix.dispose();
			
			isDisposed = true;
		}
	}
	
	public boolean isDisposed()
	{
		return isDisposed;
	}
	
	public void set(double x, double y, double z, int r, int g, int b, int a)
	{
		set(x, y, z, ColorUtil.rgbaToInt(r, g, b, a));
	}
	
	public void set(double x, double y, double z, int[] rgba)
	{
		set(x, y, z, ColorUtil.rgbaToInt(rgba));
	}
	
	public void set(double x, double y, double z, int rgba)
	{
		
		//if (zBuffer.isVisible(x, y, z)) {
		//	pixelBuffer.set(x, y, rgba);
		//	zBuffer.set(x, y, z);
		//}
		pixelMatrix.set(x, y, z, rgba);
	}
	
	public boolean isPixelFilled(double x, double y)
	{
		return pixelMatrix.isPixelFilled(x, y);
	}
	
	public void get(int x, int y, int[] rgba)
	{
		rgba[0] = rgba[1] = rgba[2] = rgba[3] = 0x0;
		
		double f = 1.0 / this.subpixelWidth;

		for (double xS = 0; xS < 1; xS += f) {
			for (double yS = 0; yS < 1; yS += f) {
				
				double _x = (double)x + xS;
				double _y = (double)y + yS;
				
				get(_x, _y, this.rgbaBuffer);
				
				rgba[0] += this.rgbaBuffer[0];
				rgba[1] += this.rgbaBuffer[1];
				rgba[2] += this.rgbaBuffer[2];
				rgba[3] += this.rgbaBuffer[3];

			}
		}
		

		
		rgba[0] = (int) Math.round((double) rgba[0] / MathExt.sqr(this.subpixelWidth));
		rgba[1] = (int) Math.round((double) rgba[1] / MathExt.sqr(this.subpixelWidth));
		rgba[2] = (int) Math.round((double) rgba[2] / MathExt.sqr(this.subpixelWidth));
		rgba[3] = (int) Math.round((double) rgba[3] / MathExt.sqr(this.subpixelWidth));
		//rgba[3] = 0xFF; // TODO: Mess with alpha later
	}
	
	public void get(double x, double y, int[] rgba)
	{
		ColorUtil.intToRGBA(get(x, y), rgba);
	}
	
	public int get(double x, double y)
	{
		
		int[] rgbaStack = pixelMatrix.getRgbaStack(x, y);
		
		int rgba = backgroundColor;
		
		for (int i = rgbaStack.length - 1; i >= 0; i--) {
			
			rgba = ColorUtil.overlayColor(rgbaStack[i], rgba);
			
		}
		
		return rgba;
		
		//return pixelBuffer.get(x, y);
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public int getSubpixelWidth()
	{
		return subpixelWidth;
	}
	
	

}

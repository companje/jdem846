package us.wthr.jdem846.canvas;

import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.canvas.util.ColorUtil;

public class RasterBuffer3d
{
	
	private int width;
	private int height;
	
	private int subpixelWidth;
	private int pixelStackDepth;
	
	protected PixelMatrix pixelMatrix;
	
	private boolean isDisposed = false;
	
	private int[] rgbaBufferA = new int[4];
	private int[] rgbaBufferB = new int[4];
	private int backgroundColor = 0x0;
	
	private AntiAliasPixelResampler pixelResampler;

	public RasterBuffer3d(int width, int height, int pixelStackDepth, int subpixelWidth)
	{
		this.width = width;
		this.height = height;
		this.subpixelWidth = subpixelWidth;
		this.pixelStackDepth = pixelStackDepth;

		this.pixelResampler = new AntiAliasPixelResampler(subpixelWidth, new AntiAliasPixelResampler.ColorFetcher() {
			public int get(double x, double y)
			{
				return _get(x, y);
			}
		});
		
		pixelMatrix = new PixelMatrix(width, height, pixelStackDepth, subpixelWidth);
	}
	
	public void reset()
	{
		reset(0x0);
	}
	
	public void reset(int backgroundColor)
	{
		this.backgroundColor = backgroundColor;
		pixelMatrix.reset(backgroundColor);
	}
	
	public void dispose()
	{
		if (!isDisposed()) {
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

		pixelMatrix.set(x, y, z, rgba);
	}
	
	public boolean isPixelFilled(double x, double y)
	{
		return pixelMatrix.isPixelFilled(x, y);
	}
	
	public int get(int x, int y)
	{
		this.get(x, y, rgbaBufferA);
		return ColorUtil.rgbaToInt(rgbaBufferA);
	}
	
	public void get(int x, int y, int[] rgba)
	{

		pixelResampler.get(x, y, rgba);
		
	}
	
	public void get(double x, double y, int[] rgba)
	{
		ColorUtil.intToRGBA(get(x, y), rgba);
	}
	

	public int get(double x, double y)
	{
		return _get(x, y);
	}
	
	public int _get(double x, double y)
	{
		
		int[] rgbaStack = pixelMatrix.getRgbaStack(x, y);
		//float[] zStack = pixelMatrix.getZStack(x, y);

		int rgba = backgroundColor;
		if (rgbaStack != null) {
			for (int i = rgbaStack.length - 1; i >= 0; i--) {
				rgba = ColorUtil.overlayColor(rgbaStack[i], rgba);
			}
		}
		return rgba;
		
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

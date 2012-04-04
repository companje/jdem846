package us.wthr.jdem846.animate;

import us.wthr.jdem846.exception.AnimateException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;


public class AnimationSpecification
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(AnimationSpecification.class);

	public static final AnimationSpecification STANDARD_16_9_1080p = new AnimationSpecification(1920, 1080, 30, 1.0);
	public static final AnimationSpecification STANDARD_16_9_720p = new AnimationSpecification(1280, 720, 30, 1.0);
	public static final AnimationSpecification STANDARD_16_9_480p = new AnimationSpecification(640, 480, 30, 1.0);
	//public static final AnimationSpecification STANDARD_16_9_360p = new AnimationSpecification(640, 360, 30, 1.0);
	//public static final AnimationSpecification STANDARD_16_9_240p = new AnimationSpecification(320, 240, 30, 1.0);
	
	
	
	
	private double quality;
	private int width;
	private int height;
	private int fps;
	private int vbitrate;
	
	public AnimationSpecification() 
	{
		
	}
	
	public AnimationSpecification(int width, int height, int fps)
	{
		init(width, height, fps, 0.5, 0);
	}
	
	public AnimationSpecification(int width, int height, int fps, double quality)
	{
		init(width, height, fps, quality, 0);
	}
	
	protected void init(int width, int height, int fps, double quality, int vbitrate)
	{
		
		if (quality < 0)
			quality = 0;
		if (quality > 1)
			quality = 1;
		
		this.width = width;
		this.height = height;
		this.fps = fps;
		this.quality = quality;
		
		if (vbitrate > 0) {
			this.vbitrate = vbitrate;
		} else {
			this.vbitrate = calcBitrate();
		}
		
		
	}
	
	
	protected int calcBitrate()
	{
		double br = (40.0 + (20.0 * quality)) * ((double)fps) * ((double)width) * ((double)height) / 256.0;
		return (int) MathExt.round(br);
	}

	public double getQuality()
	{
		return quality;
	}

	public void setQuality(double quality) throws AnimateException
	{
		init(width, height, fps, quality, 0);
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width) throws AnimateException
	{
		init(width, height, fps, quality, 0);
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height) throws AnimateException
	{
		init(width, height, fps, quality, 0);
	}

	public int getFps()
	{
		return fps;
	}

	public void setFps(int fps) throws AnimateException
	{
		init(width, height, fps, quality, 0);
	}

	public int getVbitrate()
	{
		return vbitrate;
	}


	
	
	
}

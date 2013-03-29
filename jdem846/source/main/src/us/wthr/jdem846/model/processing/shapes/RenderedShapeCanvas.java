package us.wthr.jdem846.model.processing.shapes;

import java.awt.image.BufferedImage;

import us.wthr.jdem846.math.MathExt;

public class RenderedShapeCanvas
{
	private BufferedImage shapeImage = null;
	private int shapeSetHash = 0;
	
	
	public RenderedShapeCanvas(BufferedImage shapeImage, int shapeSetHash)
	{
		this.shapeImage = shapeImage;
		this.shapeSetHash = shapeSetHash;
	}


	public BufferedImage getShapeImage()
	{
		return shapeImage;
	}


	public int getShapeSetHash()
	{
		return shapeSetHash;
	}
	
	@Override
	public int hashCode()
	{
		return createHashCode(shapeSetHash, shapeImage.getWidth(), shapeImage.getHeight());
	}
	
	public static int createHashCode(int setHashCode, int width, int height)
	{
		return setHashCode + (int) MathExt.pow(width, 3) + (int) MathExt.pow(height, 2);
	}
	
	
}

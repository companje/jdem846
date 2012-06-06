package us.wthr.jdem846.geom.util;

import us.wthr.jdem846.math.MathExt;

public class RgbaTriangleInterpolator
{
	
	
	private TriangleInterpolator rInterpolator;
	private TriangleInterpolator gInterpolator;
	private TriangleInterpolator bInterpolator;
	private TriangleInterpolator aInterpolator;
	
	public RgbaTriangleInterpolator(double x00, double y00, int[] rgba00,
									double x01, double y01, int[] rgba01,
									double x10, double y10, int[] rgba10)
	{
		
		rInterpolator = new TriangleInterpolator(x00, y00, rgba00[0],
												 x01, y01, rgba01[0],
												 x10, y10, rgba10[0]);
		
		gInterpolator = new TriangleInterpolator(x00, y00, rgba00[1],
												 x01, y01, rgba01[1],
												 x10, y10, rgba10[1]);
		
		bInterpolator = new TriangleInterpolator(x00, y00, rgba00[2],
												 x01, y01, rgba01[2],
												 x10, y10, rgba10[2]);
		
		aInterpolator = new TriangleInterpolator(x00, y00, rgba00[3],
												 x01, y01, rgba01[3],
												 x10, y10, rgba10[3]);
		

	}
	
	
	public void getInterpolatedColor(double x, double y, int[] color)
	{
		color[0] = (int) MathExt.round(rInterpolator.getInterpolatedValue(x, y));
		color[1] = (int) MathExt.round(gInterpolator.getInterpolatedValue(x, y));
		color[2] = (int) MathExt.round(bInterpolator.getInterpolatedValue(x, y));
		color[3] = (int) MathExt.round(aInterpolator.getInterpolatedValue(x, y));
	}

}

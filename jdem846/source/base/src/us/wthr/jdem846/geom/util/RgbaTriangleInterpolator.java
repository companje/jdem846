package us.wthr.jdem846.geom.util;

import us.wthr.jdem846.canvas.util.ColorUtil;
import us.wthr.jdem846.math.MathExt;

public class RgbaTriangleInterpolator
{
	
	
	private TriangleInterpolator rInterpolator = new TriangleInterpolator();
	private TriangleInterpolator gInterpolator = new TriangleInterpolator();
	private TriangleInterpolator bInterpolator = new TriangleInterpolator();
	private TriangleInterpolator aInterpolator = new TriangleInterpolator();
	
	
	public RgbaTriangleInterpolator()
	{
		
	}
	
	public RgbaTriangleInterpolator(double x00, double y00, int[] rgba00,
									double x01, double y01, int[] rgba01,
									double x10, double y10, int[] rgba10)
	{
		set(x00, y00, rgba00,
			x01, y01, rgba01,
			x10, y10, rgba10);
		
	}
	
	
	public RgbaTriangleInterpolator(double x00, double y00, int rgba00,
			double x01, double y01, int rgba01,
			double x10, double y10, int rgba10)
	{
		set(x00, y00, rgba00,
			x01, y01, rgba01,
			x10, y10, rgba10);
	}
	
	public void set(double x00, double y00, int rgba00,
			double x01, double y01, int rgba01,
			double x10, double y10, int rgba10)
	{
		int[] _rgba00 = {0, 0, 0, 0};
		ColorUtil.intToRGBA(rgba00, _rgba00);
		
		int[] _rgba01 = {0, 0, 0, 0};
		ColorUtil.intToRGBA(rgba01, _rgba01);
		
		int[] _rgba10 = {0, 0, 0, 0};
		ColorUtil.intToRGBA(rgba10, _rgba10);
		
		set(x00, y00, _rgba00
			, x01, y01, _rgba01
			, x10, y10, _rgba10);
	}
	
	
	public void set(double x00, double y00, int[] rgba00,
									double x01, double y01, int[] rgba01,
									double x10, double y10, int[] rgba10)
	{
		rInterpolator.set(x00, y00, rgba00[0],
							x01, y01, rgba01[0],
							x10, y10, rgba10[0]);
		
		gInterpolator.set(x00, y00, rgba00[1],
							x01, y01, rgba01[1],
							x10, y10, rgba10[1]);
		
		bInterpolator.set(x00, y00, rgba00[2],
							x01, y01, rgba01[2],
							x10, y10, rgba10[2]);
		
		aInterpolator.set(x00, y00, rgba00[3],
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

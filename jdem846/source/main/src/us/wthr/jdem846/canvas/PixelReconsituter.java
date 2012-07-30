package us.wthr.jdem846.canvas;

import us.wthr.jdem846.canvas.util.ColorUtil;
import us.wthr.jdem846.math.MathExt;

public class PixelReconsituter
{
	private int gridWidth = 0;
	private int backgroundColor;
	private int[][] grid = null;
	
	public PixelReconsituter(int gridWidth, int backgroundColor)
	{
		this.gridWidth = gridWidth;
		this.backgroundColor = backgroundColor;
		grid = new int[gridWidth][gridWidth];
		
	}
	
	protected void reset()
	{
		for (int y = 0; y < gridWidth; y++) {
			for (int x = 0; x < gridWidth; x++) {
				grid[y][x] = this.backgroundColor;
			}
		}
	}
	
	public int reconstitutePixel(int[] rgbaStack, float[] zStack, byte[] horizBiasStack, byte[] vertBiasStack)
	{
		this.reset();
		
		for (int i = rgbaStack.length - 1; i >= 0; i--) {
			if (zStack[i] != PixelMatrix.NO_Z_VALUE) {
				fill(rgbaStack[i], horizBiasStack[i], vertBiasStack[i]);
			}
			
		}
		
		
		int[] rgbaA = {0, 0, 0, 0};
		int[] rgbaB = {0, 0, 0, 0};
		
		for (int v = 1; v <= this.gridWidth; v++) {
			for (int h = 1; h <= this.gridWidth; h++) {
				ColorUtil.intToRGBA(this.grid[v - 1][h - 1], rgbaA);
				rgbaB[0] += rgbaA[0];
				rgbaB[1] += rgbaA[1];
				rgbaB[2] += rgbaA[2];
				rgbaB[3] += rgbaA[3];
			}
		}
		
		rgbaB[0] = (int)MathExt.round((double)rgbaB[0] / (double)(MathExt.sqr(gridWidth)));
		rgbaB[1] = (int)MathExt.round((double)rgbaB[1] / (double)(MathExt.sqr(gridWidth)));
		rgbaB[2] = (int)MathExt.round((double)rgbaB[2] / (double)(MathExt.sqr(gridWidth)));
		rgbaB[3] = (int)MathExt.round((double)rgbaB[3] / (double)(MathExt.sqr(gridWidth)));
		
		return ColorUtil.rgbaToInt(rgbaB);
	}
	
	
	protected void fill(int rgba, byte horizBias, byte vertBias)
	{
		
		double horizCoverage = PixelCoverPattern.getCoverage(horizBias);
		double vertCoverage = PixelCoverPattern.getCoverage(vertBias);
		

		
		boolean top = (vertBias & PixelCoverPattern.LEFT_TOP_BIAS) == PixelCoverPattern.LEFT_TOP_BIAS;
		boolean left = (horizBias & PixelCoverPattern.LEFT_TOP_BIAS) == PixelCoverPattern.LEFT_TOP_BIAS;
		
		double step = 1.0 / (double)this.gridWidth;
		
		
		for (int v = 1; v <= this.gridWidth; v++) {
			
			for (int h = 1; h <= this.gridWidth; h++) {
				
				double fromTop = step * (double)v;
				double fromLeft = step * (double)h;
				
				if (top && left) {			// Top Left
					if (fromTop <= vertCoverage && fromLeft <= horizCoverage) {
						grid[v - 1][h - 1] = ColorUtil.overlayColor(rgba, grid[v - 1][h - 1]);
					}
				} else if (top && !left) { // Top right
					if (fromTop <= vertCoverage && (1.0 - fromLeft) <= horizCoverage) {
						grid[v - 1][h - 1] = ColorUtil.overlayColor(rgba, grid[v - 1][h - 1]);
					}
				} else if (!top && left) { // Bottom left
					if ((1.0 - fromTop) <= vertCoverage && fromLeft <= horizCoverage) {
						grid[v - 1][h - 1] = ColorUtil.overlayColor(rgba, grid[v - 1][h - 1]);
					}
				} else if (!top && !left) { // Bottom right
					if ((1.0 - fromTop) <= vertCoverage && (1.0 - fromLeft) <= horizCoverage) {
						grid[v - 1][h - 1] = ColorUtil.overlayColor(rgba, grid[v - 1][h - 1]);
					}
				}
				
				
			}
			
		}
		
		
		
		
		
		
	}

	public int getBackgroundColor()
	{
		return backgroundColor;
	}

	public void setBackgroundColor(int backgroundColor)
	{
		this.backgroundColor = backgroundColor;
	}
	
	
	
}

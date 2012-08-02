package us.wthr.jdem846.canvas;

import java.util.Random;

import us.wthr.jdem846.canvas.util.ColorUtil;
import us.wthr.jdem846.color.ColorAdjustments;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;

public class AntiAliasPixelResampler
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(AntiAliasPixelResampler.class);
	
	private ColorFetcher colorFetcher;
	private int samplingWidth;
	private int backgroundColor;
	private int[][] grid;
	
	private int[][] grid8x8 = new int[8][8];
	private int[][] grid4x4 = new int[4][4];
	private int[][] grid2x2 = new int[2][2];
	private int[][] grid1x1 = new int[1][1];
	
	private Random random;
	
	public AntiAliasPixelResampler(int samplingWidth, ColorFetcher colorFetcher)
	{
		this.samplingWidth = samplingWidth;
		this.colorFetcher = colorFetcher;
		
		grid = new int[samplingWidth][samplingWidth];
		
		random = new Random(System.currentTimeMillis());
	}
	
	
	public void reset()
	{
		reset(backgroundColor);
	}
	
	public void reset(int backgroundColor)
	{
		this.backgroundColor = backgroundColor;
		for (int y = 0; y < samplingWidth; y++) {
			for (int x = 0; x < samplingWidth; x++) {
				grid[y][x] = backgroundColor;
			}
		}
		
	}
	
	
	public void get(int x, int y, int[] rgba)
	{
		reset();
		
		double f = 1.0 / (double)this.samplingWidth;

		for (int gY = 0; gY < samplingWidth; gY++) {
			for (int gX = 0; gX < samplingWidth; gX++) {
				
				double _x = (double)x + ((double)gX * f);
				double _y = (double)y + ((double)gY * f);
				
				int c = colorFetcher.get(_x, _y);
				
				grid[gY][gX] = c;
				
			}
		}
		

		int downSampledGrid = downSample(grid);

		ColorUtil.intToRGBA(downSampledGrid, rgba);

		
		/*
		int[][] downSampledGrid = downSample(grid);
		if (downSampledGrid != null) {
			ColorUtil.intToRGBA(downSampledGrid[0][0], rgba);
		} else {
			ColorUtil.intToRGBA(0x0, rgba);
		}
		*/
	}
	
	
	protected int downSample(int[][] grid)
	{
		int l = grid.length;
		if (l == 1) {
			return grid[0][0];
		}
		
		int halfL = (int) ((double)grid.length / 2.0);
		
		
		int x00 = (int)MathExt.round((random.nextDouble() * (double)(halfL - 1)));
		int y00 = (int)MathExt.round((random.nextDouble() * (double)(halfL - 1)));
		
		int x01 = (int)MathExt.round((random.nextDouble() * (double)(halfL - 1))) + halfL;
		int y01 = (int)MathExt.round((random.nextDouble() * (double)(halfL - 1)));
		
		int x10 = (int)MathExt.round((random.nextDouble() * (double)(halfL - 1)));
		int y10 = (int)MathExt.round((random.nextDouble() * (double)(halfL - 1))) + halfL;
		
		int x11 = (int)MathExt.round((random.nextDouble() * (double)(halfL - 1))) + halfL;
		int y11 = (int)MathExt.round((random.nextDouble() * (double)(halfL - 1))) + halfL;
		
		double xJitter = random.nextDouble();
		double yJitter = random.nextDouble();
		
		int c = ColorAdjustments.interpolateColor(grid[x00][y00]
												, grid[x01][y01]
												, grid[x10][y10]
												, grid[x11][y11]
												, xJitter
												, yJitter);
		
		return c;
	}
	
	protected int[][] ___downSample(int[][] grid)
	{
		
		int[][] downGrid;
		int downWidth = 0;
		
		if (grid.length == 16) {
			downGrid = grid8x8;
			downWidth = 8;
		} else if (grid.length == 8) {
			downGrid = grid4x4;
			downWidth = 4;
		} else if (grid.length == 4) {
			downGrid = grid2x2;
			downWidth = 2;
		} else if (grid.length == 2) {
			downGrid = grid1x1;
			downWidth = 1;
		} else if (grid.length == 1) {
			return grid;
		} else {
			return null;
		}
		
		
		for (int y = 0; y < downWidth; y++) {
			
			for (int x = 0; x < downWidth; x++) {
				
				int gX = x * 2;
				int gY = y * 2;
				
				double xJitter = random.nextDouble();
				double yJitter = random.nextDouble();
				
				//double xJitter = random.nextGaussian();
				//double yJitter = random.nextGaussian();
				
				downGrid[y][x]     = ColorAdjustments.interpolateColor(grid[gY][gX],     grid[gY][gX+1],   grid[gY+1][gX],   grid[gY+1][gX+1], xJitter, yJitter);

			}
			
		}
		
		if (downWidth > 1) {
			return ___downSample(downGrid);
		} else {
			return downGrid;
		}
		
	}
	
	 
	
	
	public void setBackgroundColor(int backgroundColor)
	{
		this.backgroundColor = backgroundColor;
	}
	
	
	
	public interface ColorFetcher
	{
		public int get(double x, double y);
	}
	
}

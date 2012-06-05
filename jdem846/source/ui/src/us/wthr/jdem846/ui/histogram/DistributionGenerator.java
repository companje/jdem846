package us.wthr.jdem846.ui.histogram;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;

import us.wthr.jdem846.math.MathExt;

public class DistributionGenerator
{
	
	public static TonalHistogramModel generateHistogramModelFromImage(BufferedImage image)
	{
		return generateHistogramModelFromImage(image, null);
	}
	
	public static TonalHistogramModel generateHistogramModelFromImage(BufferedImage image, boolean[][] modelMask)
	{
		TonalDistribution channel0 = new TonalDistribution(0);
		TonalDistribution channel1 = new TonalDistribution(1);
		TonalDistribution channel2 = new TonalDistribution(2);
		TonalDistribution channel3 = new TonalDistribution(3);
		
		Raster raster = image.getRaster();
		int[] rgba = new int[4];
		
		int w = raster.getWidth();
		int h = raster.getHeight();
		
		
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				
				if (modelMask == null || modelMask[y][x] == true) {
					raster.getPixel(x, y, rgba);
					
					channel0.distribution[rgba[0]]++;
					channel1.distribution[rgba[1]]++;
					channel2.distribution[rgba[2]]++;
					channel3.distribution[rgba[3]]++;
				}
			}
		}
		

		
		TonalHistogramModel hm = new TonalHistogramModel(channel0, channel1, channel2, channel3);

		return hm;
	}
	
}

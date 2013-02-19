package us.wthr.jdem846ui.editors.renderedmodel;

import us.wthr.jdem846.ElevationModel;

public class DistributionGenerator
{
	

	
	public static TonalHistogramModel generateHistogramModelFromImage(ElevationModel jdemElevationModel2)
	{
		TonalDistribution channel0 = new TonalDistribution(0);
		TonalDistribution channel1 = new TonalDistribution(1);
		TonalDistribution channel2 = new TonalDistribution(2);
		TonalDistribution channel3 = new TonalDistribution(3);
		
		//Raster raster = image.getRaster();
		int[] rgba = new int[4];
		
		//int w = raster.getWidth();
		//int h = raster.getHeight();
		int w = jdemElevationModel2.getWidth();
		int h = jdemElevationModel2.getHeight();
		
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				
				//if (modelMask == null || modelMask[y][x] == true) {
					//raster.getPixel(x, y, rgba);
					jdemElevationModel2.getRgba(x, y, rgba);
					
					channel0.distribution[rgba[0]]++;
					channel1.distribution[rgba[1]]++;
					channel2.distribution[rgba[2]]++;
					channel3.distribution[rgba[3]]++;
				//}
			}
		}
		

		TonalHistogramModel hm = new TonalHistogramModel(channel0, channel1, channel2, channel3);

		return hm;
	}
	
}

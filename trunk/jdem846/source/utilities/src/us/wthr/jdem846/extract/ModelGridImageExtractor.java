package us.wthr.jdem846.extract;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.exception.ImageException;
import us.wthr.jdem846.graphics.IColor;
import us.wthr.jdem846.image.ImageWriter;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.modelgrid.UserProvidedModelGrid;
import us.wthr.jdem846.util.TempFiles;

public class ModelGridImageExtractor extends AbstractTestMain
{
	
	
private static Log log = null;
	
	
	public static void main(String[] args)
	{
		try {
			AbstractTestMain.initialize(false);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		log = Logging.getLog(ModelGridImageExtractor.class);

		String modelGridFile = "C:/jdem/Data/Saturn//saturn-modelgrid.jdemgrid";
		String imageFile = "C:/jdem/Data/Saturn//extracted-image.jpg";
		
		
		UserProvidedModelGrid modelGrid = new UserProvidedModelGrid(modelGridFile);
		modelGrid.load();
		
		int width = modelGrid.getWidth();
		int height = modelGrid.getHeight();
		
		int[] rgba = {0, 0, 0, 0};
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		WritableRaster raster = image.getRaster();
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				
				IColor color = modelGrid.getRgba(x, y);
				
				if (color != null) {
					color.toArray(rgba);
					if (color.asInt() == 0x0) {
						int i = 0;
					}
					raster.setPixel(x, y, rgba);
				}

			}
		}
		
		
		try {
			ImageWriter.saveImage(image, imageFile);
		} catch (ImageException e) {
			e.printStackTrace();
		}
		
		modelGrid.unload();
		modelGrid.dispose();
		
		
		TempFiles.cleanUpTemporaryFiles(false, true);
				
				
	}
	
}

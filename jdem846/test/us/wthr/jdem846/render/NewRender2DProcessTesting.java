package us.wthr.jdem846.render;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.RegistryKernel;
import us.wthr.jdem846.exception.ImageException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.image.ImageWriter;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.input.gridfloat.GridFloat;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.SimpleImageViewFrame;

public class NewRender2DProcessTesting
{
	
	private static Log log = Logging.getLog(NewRender2DProcessTesting.class);
	
	public void doTesting()
	{
		String outputLocation = "C:/srv/elevation/testing.png";
		
		List<String> inputDataList = new LinkedList<String>();
		inputDataList.add("C:/srv/elevation/Maui/15749574.flt");
		inputDataList.add("C:/srv/elevation/Maui/58273983.flt");
		
		
		try {
			RegistryKernel regKernel = new RegistryKernel();
			regKernel.init();
		} catch (Exception ex) {
			log.error("Failed to initialize registry configuration: " + ex.getMessage(), ex);
			return;
		}
		
		
		
		
		DataPackage dataPackage = new DataPackage();
		
		for (String inputDataPath : inputDataList) {
			GridFloat previewData = new GridFloat(inputDataPath);
			dataPackage.addDataSource(previewData);
		}
		
		dataPackage.prepare();

		ModelOptions modelOptions = new ModelOptions();
		modelOptions.setColoringType("hypsometric-etopo1-tint");
		//modelOptions.setHillShadeType(DemConstants.HILLSHADING_NONE);
		modelOptions.setPrecacheStrategy(DemConstants.PRECACHE_STRATEGY_TILED);
		modelOptions.setRelativeLightIntensity(0.5);
		modelOptions.setTileSize(1000);
		modelOptions.setGridSize(1);
		modelOptions.setHeight(3000);
		modelOptions.setWidth(3000);
		//modelOptions.setHeight((int)dataPackage.getRows());
		//modelOptions.setWidth((int)dataPackage.getColumns());
		
		try {
			//dataPackage.calculateElevationMinMax(true);
		} catch (Exception ex) {
			log.error("Failed to calculate elevation min/max: " + ex.getMessage(), ex);
			return;
		} 
		
		
		ModelContext modelContext = ModelContext.createInstance(dataPackage, modelOptions);
		
		OutputProduct<DemCanvas> product = null;
		Dem2dGenerator dem2d = new Dem2dGenerator(modelContext);
		try {
			product = dem2d.generate();
		} catch (RenderEngineException ex) {
			log.error("Failed to generate 2d model: " + ex.getMessage(), ex);
			return;
		}
		
		
		BufferedImage image = (BufferedImage) product.getProduct().getImage();
		
		try {
			ImageWriter.saveImage(image, outputLocation);
		} catch (ImageException ex) {
			log.error("Failed to write image to " + ex.getMessage(), ex);
			return;
		}
		
		SimpleImageViewFrame imageViewer = new SimpleImageViewFrame(image);
		imageViewer.setVisible(true);
	}
	
	public static void main(String[] args)
	{
		NewRender2DProcessTesting testing = new NewRender2DProcessTesting();
		
		testing.doTesting();
	}
}

package us.wthr.jdem846.render;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.RegistryKernel;
import us.wthr.jdem846.canvas.ModelCanvas;
import us.wthr.jdem846.exception.ImageException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.image.ImageWriter;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.rasterdata.RasterDataProviderFactory;
import us.wthr.jdem846.ui.SimpleImageViewFrame;

public class NewRender2DProcessTesting
{
	
	private static Log log = Logging.getLog(NewRender2DProcessTesting.class);
	
	public void doTesting() throws Exception
	{
		String outputLocation = "C:/srv/elevation/testing.png";
		
		List<String> inputDataList = new LinkedList<String>();
		inputDataList.add("C:/srv/elevation/Maui/15749574.flt");
		inputDataList.add("C:/srv/elevation/Maui/58273983.flt");
		//inputDataList.add("C:/srv/elevation/Shapefiles/Nashua NH 1-3 Arc Second//77591663.flt");
		
		try {
			RegistryKernel regKernel = new RegistryKernel();
			regKernel.init();
		} catch (Exception ex) {
			log.error("Failed to initialize registry configuration: " + ex.getMessage(), ex);
			return;
		}
		
		
		
		
		RasterDataContext rasterDataContext = new RasterDataContext();
		
		for (String inputDataPath : inputDataList) {
			RasterData rasterData = RasterDataProviderFactory.loadRasterData(inputDataPath);
			rasterDataContext.addRasterData(rasterData);
		}
		
		rasterDataContext.prepare();

		ModelOptions modelOptions = new ModelOptions();
		//modelOptions.setColoringType("hypsometric-etopo1-tint");
		//modelOptions.setHillShadeType(DemConstants.HILLSHADING_NONE);
		//modelOptions.setPrecacheStrategy(DemConstants.PRECACHE_STRATEGY_TILED);
		//modelOptions.setRelativeLightIntensity(0.5);
		//modelOptions.setTileSize(1000);
		//modelOptions.setGridSize(1);
		//modelOptions.setHeight(3000);
		//modelOptions.setWidth(3000);
		//modelOptions.setRelativeLightIntensity(-1.0);
		//modelOptions.setDoublePrecisionHillshading(false);
		//modelOptions.setHillShadeType(DemConstants.HILLSHADING_NONE);
		//modelOptions.setHeight((int)dataPackage.getRows());
		//modelOptions.setWidth((int)dataPackage.getColumns());
		
		/*
		try {
			rasterDataContext.calculateElevationMinMax(true);
			// TODO: Replace
		} catch (Exception ex) {
			log.error("Failed to calculate elevation min/max: " + ex.getMessage(), ex);
			return;
		} 
		*/
		
		
		ModelContext modelContext = ModelContext.createInstance(rasterDataContext, modelOptions);
		
		OutputProduct<ModelCanvas> product = null;
		Dem2dGenerator dem2d = new Dem2dGenerator(modelContext);
		
		double start = 0;
		double end = 0;
		
		start = System.currentTimeMillis();
		try {
			product = dem2d.generate();
		} catch (RenderEngineException ex) {
			log.error("Failed to generate 2d model: " + ex.getMessage(), ex);
			return;
		}
		end = System.currentTimeMillis();
		log.info("Completed render process in " + ((end - start) / 1000) + " seconds");
		
		 BufferedImage image = (BufferedImage) product.getProduct().getImage();

		try {
			ImageWriter.saveImage(image, outputLocation);
		} catch (ImageException ex) {
			log.error("Failed to write image to " + ex.getMessage(), ex);
			return;
		}

		final SimpleImageViewFrame imageViewer = new SimpleImageViewFrame(image);
		imageViewer.setVisible(true);
		
		
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				if(imageViewer.getState()!=JFrame.NORMAL) { imageViewer.setState(JFrame.NORMAL); }
				imageViewer.toFront();
				imageViewer.repaint();
			}
		});
	}
	
	public static void main(String[] args)
	{
		NewRender2DProcessTesting testing = new NewRender2DProcessTesting();
		
		try {
			testing.doTesting();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}

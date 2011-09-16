package us.wthr.jdem846.profiling;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.RegistryKernel;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.input.gridfloat.GridFloat;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.Dem2dGenerator;
import us.wthr.jdem846.render.DemCanvas;
import us.wthr.jdem846.render.OutputProduct;
import us.wthr.jdem846.util.TempFiles;

public class JDemProfileMain
{
	private static Log log = Logging.getLog(JDemProfileMain.class);
	
	
	public static void main(String[] args)
	{
		
		/*
		try {
			
			RegistryKernel regKernel = new RegistryKernel();
			regKernel.init();
		
			List<String> inputDataList = new LinkedList<String>();
			//inputDataList.add("C:/srv/elevation/Maui//15749574.flt");
			//inputDataList.add("C:/srv/elevation/Maui//58273983.flt");
			inputDataList.add("C:/srv/elevation/Shapefiles/Nashua NH 1-3 Arc Second//77591663.flt");
			
			
			boolean fullPrecaching = JDem846Properties.getProperty("us.wthr.jdem846.modelOptions.precacheStrategy").equalsIgnoreCase("full");
			if (fullPrecaching) {
				log.info("Data Precaching Strategy Set to FULL");
			}
			
			ModelOptions modelOptions = new ModelOptions();
			modelOptions.setColoringType("hypsometric-etopo1-tint");
			modelOptions.setPrecacheStrategy(DemConstants.PRECACHE_STRATEGY_TILED);
			modelOptions.setTileSize(1000);
			
			DataPackage dataPackage = new DataPackage();
			
			
			for (String inputDataPath : inputDataList) {
				GridFloat previewData = new GridFloat(inputDataPath);
				
				////if (fullPrecaching) {
				//	previewData.setDataPrecached(true);
				//}
				
				dataPackage.addDataSource(previewData);
			}
			
			dataPackage.prepare();
			dataPackage.calculateElevationMinMax(true);
			
			Dem2dGenerator dem2d = new Dem2dGenerator(dataPackage, modelOptions);
			
			long start = System.currentTimeMillis();
			OutputProduct<DemCanvas> product = generate(dem2d);
			long complete = System.currentTimeMillis();
			
			log.info("Completed rendering in " + (((double)complete - (double)start) / 1000.0) + " second(s)");
			
			//BufferedImage prerendered = (BufferedImage) product.getProduct().getImage();
			
			product.getProduct().save("C:\\srv\\testing.png");
			
			TempFiles.cleanUpTemporaryFiles();
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}
		*/
		
		try {
			RegistryKernel regKernel = new RegistryKernel();
			regKernel.init();
		} catch (Exception ex) {
			log.error("Failed to initialize registry configuration: " + ex.getMessage(), ex);
			return;
		}
		
		
		List<String> inputDataList = new LinkedList<String>();
		//inputDataList.add("C:/srv/elevation/Maui//15749574.flt");
		//inputDataList.add("C:/srv/elevation/Maui//58273983.flt");
		inputDataList.add("C:/srv/elevation/Shapefiles/Nashua NH 1-3 Arc Second//77591663.flt");
		
		boolean doingTesting = true;
		
		if (!doingTesting) {
			try {
				doTest(inputDataList, DemConstants.PRECACHE_STRATEGY_TILED, 100, "C:/srv/testing.png");
			} catch (Exception ex) {
				log.error("Fail!", ex);
			}
		} else {
			
			int minTileSize = 100;
			int maxTileSize = 1600;
			int tileSizeInterval = 100;
			int tileSizeRepetitions = 1;
			
			int tile = 0;
			
			List<TileSizeTest> testList = new LinkedList<TileSizeTest>();
			
			try {
				
				
				for (int tileSize = minTileSize; tileSize <= maxTileSize; tileSize+=tileSizeInterval) {
					double defaulted = 0;
					double tiled = 0;
					
					for (int repetition = 0; repetition < tileSizeRepetitions; repetition++) {
						defaulted += doTest(inputDataList, DemConstants.PRECACHE_STRATEGY_DEFAULT, tileSize, null);
						tiled += doTest(inputDataList, DemConstants.PRECACHE_STRATEGY_TILED, tileSize, null);
						
					}
					
					defaulted = defaulted / tileSizeRepetitions;
					tiled = tiled / tileSizeRepetitions;
					
					testList.add(new TileSizeTest(tileSize, defaulted, tiled));
				}
				
				StringBuffer out = new StringBuffer();
				out.append("\n");
				for (TileSizeTest tileSizeTest : testList) {
					String outLine = "" + tileSizeTest.tileSize + "	" + tileSizeTest.defaulted + "	" + tileSizeTest.tiled + "\n";
					out.append(outLine);
				}
				log.info("Results: " + out.toString());
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	
	protected static double doTest(List<String> inputDataList, String precacheStrategy, int tileSize, String saveTo) throws Exception
	{
		//List<String> inputDataList = new LinkedList<String>();
		//inputDataList.add("C:/srv/elevation/Maui//15749574.flt");
		//inputDataList.add("C:/srv/elevation/Maui//58273983.flt");
		//inputDataList.add("C:/srv/elevation/Shapefiles/Nashua NH 1-3 Arc Second//77591663.flt");
		
		
		boolean fullPrecaching = JDem846Properties.getProperty("us.wthr.jdem846.modelOptions.precacheStrategy").equalsIgnoreCase("full");
		if (fullPrecaching) {
			log.info("Data Precaching Strategy Set to FULL");
		}
		
		ModelOptions modelOptions = new ModelOptions();
		modelOptions.setColoringType("hypsometric-etopo1-tint");
		modelOptions.setPrecacheStrategy(precacheStrategy);
		modelOptions.setTileSize(tileSize);
		
		DataPackage dataPackage = new DataPackage();
		
		
		for (String inputDataPath : inputDataList) {
			GridFloat previewData = new GridFloat(inputDataPath);

			
			dataPackage.addDataSource(previewData);
		}
		
		dataPackage.prepare();
		dataPackage.calculateElevationMinMax(true);
		
		Dem2dGenerator dem2d = new Dem2dGenerator(dataPackage, modelOptions);
		
		long start = System.currentTimeMillis();
		OutputProduct<DemCanvas> product = generate(dem2d);
		long complete = System.currentTimeMillis();
		
		double elapsed = (((double)complete - (double)start) / 1000.0);
		log.info("Completed rendering in " + (((double)complete - (double)start) / 1000.0) + " second(s)");
		
		if (saveTo != null) {
			product.getProduct().save(saveTo);
		}
		
		return elapsed;
	}
	
	
	protected static OutputProduct<DemCanvas> generate(Dem2dGenerator dem2d) throws Exception
	{
		OutputProduct<DemCanvas> product = dem2d.generate();
		return product;
	}

	public static class TileSizeTest
	{
		public int tileSize = 0;
		public double defaulted = 0;
		public double tiled = 0;
		
		public TileSizeTest(int tileSize, double defaulted, double tiled)
		{
			this.tileSize = tileSize;
			this.defaulted = defaulted;
			this.tiled = tiled;
		}
	}
}

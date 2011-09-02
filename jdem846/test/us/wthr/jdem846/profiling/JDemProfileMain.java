package us.wthr.jdem846.profiling;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

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
		try {
			
			RegistryKernel regKernel = new RegistryKernel();
			regKernel.init();
			
			/*
			File tmpGridFloatData = TempFiles.getTemporaryFile("lghtprv", ".flt", "jar://" + JDem846Properties.getProperty("us.wthr.jdem846.previewData") + "/raster-data.flt");
			
			File tmpTempGridFloatHeader = TempFiles.getTemporaryFile("lghtprv", ".hdr", "jar://" + JDem846Properties.getProperty("us.wthr.jdem846.previewData") + "/raster-data.hdr");
			
			String tmpHdrPath = tmpGridFloatData.getAbsolutePath();
			tmpHdrPath = tmpHdrPath.replaceAll("\\.flt", ".hdr");
			log.info("New Header Path: " + tmpHdrPath);
			File tmpGridFloatHeader = new File(tmpHdrPath);
			
			tmpTempGridFloatHeader.renameTo(tmpGridFloatHeader);
			
			GridFloat previewData = new GridFloat(tmpGridFloatData.getAbsolutePath());
			*/
			
			List<String> inputDataList = new LinkedList<String>();
			inputDataList.add("C:/srv/elevation/Maui//15749574.flt");
			inputDataList.add("C:/srv/elevation/Maui//58273983.flt");
			
			
			
			boolean fullPrecaching = JDem846Properties.getProperty("us.wthr.jdem846.modelOptions.precacheStrategy").equalsIgnoreCase("full");
			if (fullPrecaching) {
				log.info("Data Precaching Strategy Set to FULL");
			}
			
			ModelOptions modelOptions = new ModelOptions();

			DataPackage dataPackage = new DataPackage();
			
			
			for (String inputDataPath : inputDataList) {
				GridFloat previewData = new GridFloat(inputDataPath);
				
				if (fullPrecaching) {
					previewData.setDataPrecached(true);
				}
				
				dataPackage.addDataSource(previewData);
			}
			
			dataPackage.prepare();
			dataPackage.calculateElevationMinMax(true);
			
			Dem2dGenerator dem2d = new Dem2dGenerator(dataPackage, modelOptions);
			
			long start = System.currentTimeMillis();
			OutputProduct<DemCanvas> product = generate(dem2d);
			long complete = System.currentTimeMillis();
			
			log.info("Completed rendering in " + ((complete - start) / 1000) + " second(s)");
			
			BufferedImage prerendered = (BufferedImage) product.getProduct().getImage();
			
			TempFiles.cleanUpTemporaryFiles();
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}
	}
	
	protected static OutputProduct<DemCanvas> generate(Dem2dGenerator dem2d) throws Exception
	{
		OutputProduct<DemCanvas> product = dem2d.generate();
		return product;
	}
	
}

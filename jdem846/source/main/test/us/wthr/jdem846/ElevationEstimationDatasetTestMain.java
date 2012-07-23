package us.wthr.jdem846;

import java.io.File;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.gis.elevation.ElevationMinMax;
import us.wthr.jdem846.lighting.LightingContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.rasterdata.RasterDataProviderFactory;
import us.wthr.jdem846.render.ElevationMinMaxCalculator;
import us.wthr.jdem846.shapedata.ShapeDataContext;

public class ElevationEstimationDatasetTestMain extends AbstractTestMain
{
	private static Log log = null;
	
	
	public static void main(String[] args)
	{
		try {
			initialize(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		log = Logging.getLog(ElevationEstimationDatasetTestMain.class);
		
		try {
			ElevationEstimationDatasetTestMain testMain = new ElevationEstimationDatasetTestMain();
			testMain.doTesting();
		} catch (Exception ex) {
			log.error("Uncaught exception while running test main: " + ex.getMessage(), ex);
		}
		
	}
	
	
	
	public void doTesting() throws Exception
	{
		List<String> inputDataList = new LinkedList<String>();
		
		//inputDataList.add("F:\\GEBCO_08\\gebco_08.flt");
		//String saveOutputTo = "C:\\srv\\elevation\\Wet Mars\\mola64_oc180\\elevation-minmax-samples.csv";
		String saveOutputTo = "C:\\srv\\visual studio 2010\\Projects\\DemKit\\elevation-minmax-samples.csv";
		//inputDataList.add("C:\\srv\\elevation\\Wet Mars\\mola64_oc180\\mola64_oc180.flt");
		inputDataList.add("C:\\srv\\elevation\\Earth\\srtm_ramp2.world.86400x43200.bil");
		//inputDataList.add("C:\\srv\\elevation\\Earth\\gebco_08.flt");
		//String saveOutputTo = JDem846Properties.getProperty("us.wthr.jdem846.testOutputPath") + "/elevation-minmax-samples.csv";
		
		File csvFile = new File(saveOutputTo);
		PrintWriter out = new PrintWriter(csvFile);
		
		
		RasterDataContext dataProxy = new RasterDataContext();
		
		for (String inputDataPath : inputDataList) {
			log.info("Adding raster data @ '" + inputDataPath + "'");
			RasterData rasterData = RasterDataProviderFactory.loadRasterData(inputDataPath);
			dataProxy.addRasterData(rasterData);
			
		}
		
		double latitudeResolution = 1.0;
		double longitudeResolution = 1.0;
		
		//dataProxy.setEffectiveLatitudeResolution(latitudeResolution);
		//dataProxy.setEffectiveLongitudeResolution(longitudeResolution);
		
		
		ShapeDataContext shapeContext = new ShapeDataContext();
		LightingContext lightingContext = new LightingContext();
		ModelOptions modelOptions = new ModelOptions();
		modelOptions.setWidth(dataProxy.getDataColumns());
		modelOptions.setHeight(dataProxy.getDataRows());
		
		
		ModelContext modelContext = ModelContext.createInstance(dataProxy, shapeContext, lightingContext, modelOptions);
		modelContext.updateContext();
		
		
		for (double latitude = 90.0; latitude > -90.0; latitude -= latitudeResolution) {
			
			for (double longitude = -180.0; longitude < 180; longitude += longitudeResolution) {
				
				modelContext.setNorthLimit(latitude);
				modelContext.setSouthLimit(latitude - latitudeResolution);
				
				modelContext.setWestLimit(longitude);
				modelContext.setEastLimit(longitude + longitudeResolution);
				
				ElevationMinMaxCalculator minMaxCalc = new ElevationMinMaxCalculator(modelContext);
				ElevationMinMax minMax = minMaxCalc.calculateMinAndMax();
				
				double min = minMax.getMinimumElevation();
				double max = minMax.getMaximumElevation();
				double mean = minMax.getMeanElevation();
				double median = minMax.getMedianElevation();
				
				
				out.printf("%f, %f, %f, %f, %f, %f\n", latitude, longitude, min, max, mean, median);
				log.info("" + latitude + ", " + longitude + ", " + min + ", " + max + ", " + mean + ", " + median);
				
				//break;
			}
			
			//break;
		}

		
		
		out.flush();
		out.close();
	}
	
}

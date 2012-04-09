package us.wthr.jdem846.rasterdata;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.RegistryKernel;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.image.ImageWriter;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.canvas.ModelCanvas;
import us.wthr.jdem846.render.render2d.ModelRenderer;

@SuppressWarnings("deprecation")
public class RasterDataTesting
{
	private static Log log = Logging.getLog(RasterDataTesting.class);
	
	
	/** Sets base system property values
	 * 
	 */
	protected static void bootstrapSystemProperties()
	{
		
		if (System.getProperty("us.wthr.jdem846.installPath") == null) {
			System.setProperty("us.wthr.jdem846.installPath", System.getProperty("user.dir"));
		}
		if (System.getProperty("us.wthr.jdem846.resourcesPath") == null) {
			System.setProperty("us.wthr.jdem846.resourcesPath", System.getProperty("us.wthr.jdem846.installPath"));
		}
		
		if (System.getProperty("us.wthr.jdem846.userSettingsPath") == null) {
			System.setProperty("us.wthr.jdem846.userSettingsPath", System.getProperty("user.home") + "/.jdem846");
		}
		
	}
	
	public static void main(String[] args)
	{
		
		bootstrapSystemProperties();
		
		try {
			RegistryKernel regKernel = new RegistryKernel();
			regKernel.init();
		} catch (Exception ex) {
			log.error("Failed to initialize registry configuration: " + ex.getMessage(), ex);
			return;
		}
		
		
		List<String> inputDataList = new LinkedList<String>();
		//inputDataList.add("C:/srv/elevation/Pawtuckaway/74339812.flt");
		
		//inputDataList.add("C:/srv/elevation/Maui/15749574.flt");
		//inputDataList.add("C:/srv/elevation/Maui/58273983.flt");
		//String saveOutputTo = "C:/srv/elevation/Maui/test-output.png";
		
		//inputDataList.add("C:/srv/elevation/DataRaster-Testing/PresRange_1-3as.flt");
		//inputDataList.add("C:/srv/elevation/DataRaster-Testing/PresRange_1as.flt");
		//String saveOutputTo = "C:/srv/elevation/DataRaster-Testing/test-output.png";
		
		
		//inputDataList.add("C:/srv/elevation/etopo1_ice_g_f4/etopo1_ice_g_f4.flt");
		//String saveOutputTo = "C:/srv/elevation/etopo1_ice_g_f4/test-output.png";
		
		
		inputDataList.add("/elev/DataRaster-Testing/PresRange_1-3as.flt");
		inputDataList.add("/elev/DataRaster-Testing/PresRange_1as.flt");
		String saveOutputTo = "/elev/DataRaster-Testing/test-output.png";
		
		RasterDataTesting testing = new RasterDataTesting();
		
		
		try {
			long start = System.currentTimeMillis();
			//testing.doTest(inputDataList, true);
			testing.doModelRenderTest(inputDataList, saveOutputTo);
			long done = System.currentTimeMillis();
			
			long duration = (done - start);
			log.info("Duration: " + duration);
			
			
		} catch (Exception ex) {
			log.error("Failed during test: " + ex.getMessage(), ex);
		}
		
		
		
		
		
	}
	
	
	public void doModelRenderTest(List<String> inputDataList, String saveOutputTo) throws Exception
	{
		RasterDataContext dataProxy = new RasterDataContext();
		
		for (String inputDataPath : inputDataList) {
			log.info("Adding raster data @ '" + inputDataPath + "'");
			RasterData rasterData = RasterDataProviderFactory.loadRasterData(inputDataPath);
			dataProxy.addRasterData(rasterData);
			
		}
		
		/*
		dataProxy.calculateElevationMinMax(true);
		// TODO: Replace
		log.info("Raster Data Maximum Value: " + dataProxy.getDataMaximumValue());
		log.info("Raster Data Minimum Value: " + dataProxy.getDataMinimumValue());
		*/
		
		ModelOptions modelOptions = new ModelOptions();
		
		//modelOptions.setTileSize(1000);
		modelOptions.setWidth(dataProxy.getDataColumns());
		modelOptions.setHeight(dataProxy.getDataRows());
		//modelOptions.setDoublePrecisionHillshading(false);
		//modelOptions.setUseSimpleCanvasFill(true);
		modelOptions.setAntialiased(false);
		//modelOptions.setMapProjection(MapProjectionEnum.EQUIRECTANGULAR3D);
		ModelContext modelContext = ModelContext.createInstance(dataProxy, modelOptions);
		
		
		ModelCanvas canvas = ModelRenderer.render(modelContext);
		
		ImageWriter.saveImage((BufferedImage)canvas.getFinalizedImage(), saveOutputTo);
		
	}
	
	
	public void doTest(List<String> inputDataList, boolean buffered) throws Exception
	{
		RasterDataContext dataProxy = new RasterDataContext();
		
		for (String inputDataPath : inputDataList) {
			log.info("Adding raster data @ '" + inputDataPath + "'");
			RasterData rasterData = RasterDataProviderFactory.loadRasterData(inputDataPath);
			dataProxy.addRasterData(rasterData);
			
		}
		
		ModelOptions modelOptions = new ModelOptions();
		
		///modelOptions.setTileSize(1000);
		
		ModelContext modelContext = ModelContext.createInstance(dataProxy, modelOptions);
		
		
		rasterTest(modelContext, buffered);
		
	}
	
	
	public void rasterTest(ModelContext modelContext, boolean buffered) throws Exception
	{
		RasterDataContext dataProxy = modelContext.getRasterDataContext();
		
		double northLimit = dataProxy.getNorth();
		double southLimit = dataProxy.getSouth();
		double eastLimit = dataProxy.getEast();
		double westLimit = dataProxy.getWest();
		
		double latitudeResolution = dataProxy.getLatitudeResolution();
		double longitudeResolution = dataProxy.getLongitudeResolution();
		
		double tileSize = JDem846Properties.getIntProperty("us.wthr.jdem846.performance.tileSize");
		
		double tileLatitudeHeight = latitudeResolution * tileSize - latitudeResolution;
		double tileLongitudeWidth = longitudeResolution * tileSize - longitudeResolution;
		
		log.info("Tile Size: " + tileSize);
		log.info("Tile Latitude Height: " + tileLatitudeHeight);
		log.info("Tile Longitude Width: " + tileLongitudeWidth);
		
		int tileNumber = 0;
		int tileRow = 0;
		int tileColumn = 0;
		// Latitude
		for (double tileNorth = northLimit; tileNorth > southLimit; tileNorth -= tileLatitudeHeight) {
			double tileSouth = tileNorth - tileLatitudeHeight;
			if (tileSouth <= southLimit) {
				tileSouth = southLimit + latitudeResolution;
			}
			tileRow++;
			tileColumn = 0;
			
			// Longitude
			for (double tileWest = westLimit; tileWest < eastLimit; tileWest += tileLongitudeWidth) {
				double tileEast = tileWest + tileLongitudeWidth;
				
				if (tileEast >= eastLimit) {
					tileEast = eastLimit - longitudeResolution;
				}
				
				tileColumn++;
				tileNumber++;
				
				log.info("Tile #" + tileNumber + ", Row #" + tileRow + ", Column #" + tileColumn);
				log.info("    North: " + tileNorth);
				log.info("    South: " + tileSouth);
				log.info("    East: " + tileEast);
				log.info("    West: " + tileWest);
				
				
				rasterTileTest(modelContext, buffered, tileNorth, tileSouth, tileEast, tileWest);
			}
			
		}
		
	}
	
	
	public void rasterTileTest(ModelContext modelContext, boolean buffered, double northLimit, double southLimit, double eastLimit, double westLimit) throws Exception
	{

		double latitudeResolution = modelContext.getRasterDataContext().getLatitudeResolution();
		double longitudeResolution = modelContext.getRasterDataContext().getLongitudeResolution();
		

		
		RasterDataContext dataProxy = modelContext.getRasterDataContext();//.getSubSet(northLimit, southLimit, eastLimit, westLimit);

		
		if (buffered) {
			try {
				dataProxy.fillBuffers(northLimit, southLimit, eastLimit, westLimit);
			} catch (Exception ex) {
				throw new DataSourceException("Failed to buffer data: " + ex.getMessage(), ex);
			}
		}

		int validDataPoints = 0;
		int invalidDataPoints = 0;
		
		double minData = Double.MAX_VALUE;
		double maxData = Double.MIN_VALUE;
		

		for (double latitude = northLimit; latitude > southLimit; latitude -= latitudeResolution) {
			
			for (double longitude = westLimit; longitude < eastLimit; longitude += longitudeResolution) {
				
				double data = dataProxy.getData(latitude, longitude);
				
				if (data == DemConstants.ELEV_NO_DATA) {
					invalidDataPoints++;
				} else {
					validDataPoints++;
					
					if (data < minData)
						minData = data;
					if (data > maxData)
						maxData = data;
					
				}
				
			}
			
		}
		
		if (buffered) {
			dataProxy.clearBuffers();
		}
		
		log.info("Points Processed: " + (invalidDataPoints + validDataPoints));
		log.info("Valid Data Points: " + validDataPoints);
		log.info("Invalid Data Points: " + invalidDataPoints);
		log.info("Max Data Value: " + maxData);
		log.info("Min Data Value: " + minData);
	}
	
}

package us.wthr.jdem846.rasterdata;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class RasterDataTesting
{
	private static Log log = Logging.getLog(RasterDataTesting.class);
	
	
	
	public static void main(String[] args)
	{
		
		List<String> inputDataList = new LinkedList<String>();
		//inputDataList.add("C:/srv/elevation/Pawtuckaway/74339812.flt");
		
		//inputDataList.add("C:/srv/elevation/Maui/15749574.flt");
		//inputDataList.add("C:/srv/elevation/Maui/58273983.flt");
		
		inputDataList.add("C:/srv/elevation/DataRaster-Testing/PresRange_1-3as.flt");
		inputDataList.add("C:/srv/elevation/DataRaster-Testing/PresRange_1as.flt");
		
		RasterDataTesting testing = new RasterDataTesting();
		
		
		try {
			long start = System.currentTimeMillis();
			testing.doTest(inputDataList, true);
			long done = System.currentTimeMillis();
			
			long duration = (done - start);
			log.info("Duration: " + duration);
			
			
		} catch (Exception ex) {
			log.error("Failed during test: " + ex.getMessage(), ex);
		}
		
		
		
		
		
	}
	
	
	public void doTest(List<String> inputDataList, boolean buffered) throws Exception
	{
		RasterDataProxy dataProxy = new RasterDataProxy();
		
		for (String inputDataPath : inputDataList) {
			log.info("Adding raster data @ '" + inputDataPath + "'");
			RasterData rasterData = RasterDataProviderFactory.loadRasterData(inputDataPath);
			dataProxy.addRasterData(rasterData);
			
		}
		
		ModelOptions modelOptions = new ModelOptions();
		
		modelOptions.setTileSize(1000);
		
		ModelContext modelContext = ModelContext.createInstance(dataProxy, modelOptions);
		
		
		rasterTest(modelContext, buffered);
		
	}
	
	
	public void rasterTest(ModelContext modelContext, boolean buffered) throws Exception
	{
		RasterDataProxy dataProxy = modelContext.getRasterDataProxy();
		
		double northLimit = dataProxy.getNorth();
		double southLimit = dataProxy.getSouth();
		double eastLimit = dataProxy.getEast();
		double westLimit = dataProxy.getWest();
		
		double latitudeResolution = dataProxy.getLatitudeResolution();
		double longitudeResolution = dataProxy.getLongitudeResolution();
		
		double tileSize = modelContext.getModelOptions().getTileSize();
		
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

		double latitudeResolution = modelContext.getRasterDataProxy().getLatitudeResolution();
		double longitudeResolution = modelContext.getRasterDataProxy().getLongitudeResolution();
		

		
		RasterDataProxy dataProxy = modelContext.getRasterDataProxy();//.getSubSet(northLimit, southLimit, eastLimit, westLimit);

		
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

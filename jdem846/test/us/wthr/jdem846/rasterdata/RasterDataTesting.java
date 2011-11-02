package us.wthr.jdem846.rasterdata;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class RasterDataTesting
{
	private static Log log = Logging.getLog(RasterDataTesting.class);
	
	
	
	public static void main(String[] args)
	{
		
		List<String> inputDataList = new LinkedList<String>();
		//inputDataList.add("C:/srv/elevation/Pawtuckaway/74339812.flt");
		
		inputDataList.add("C:/srv/elevation/Maui/15749574.flt");
		inputDataList.add("C:/srv/elevation/Maui/58273983.flt");
		
		RasterDataTesting testing = new RasterDataTesting();
		
		
		try {
			long start = System.currentTimeMillis();
			//testing.doTest(inputDataList, false);
			long done = System.currentTimeMillis();
			
			long noBufferDuration = (done - start);
			log.info("No Buffer Duration: " + noBufferDuration);
			
			log.info("=================================================================");
			
			start = System.currentTimeMillis();
			testing.doTest(inputDataList, true);
			done = System.currentTimeMillis();
			
			long bufferingDuration = (done - start);
			log.info("Buffering Duration: " + bufferingDuration);
			
			double durationPercentDiff = ((double) (noBufferDuration - bufferingDuration) / (double) noBufferDuration) * 100.0;
			
			log.info("Difference in duration: " + (noBufferDuration - bufferingDuration) + " (" + durationPercentDiff + "% improvement)");
			
			
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
		
		
		
		
		double northLimit = dataProxy.getNorth();
		double southLimit = dataProxy.getSouth();
		double eastLimit = dataProxy.getEast();
		double westLimit = dataProxy.getWest();
		
		double latitudeResolution = dataProxy.getLatitudeResolution();
		double longitudeResolution = dataProxy.getLongitudeResolution();
		
		RasterDataProxy subsetDataProxy = dataProxy.getSubSet(northLimit, southLimit, eastLimit, westLimit);

		RasterDataProxy testDataProxy = subsetDataProxy;
		
		if (buffered) {
			testDataProxy.fillBuffers(northLimit, southLimit, eastLimit, westLimit);
		}
		
		int pointsProcessed = 0;
		int validDataPoints = 0;
		int invalidDataPoints = 0;
		
		double minData = Double.MAX_VALUE;
		double maxData = Double.MIN_VALUE;
		
		
		
		
		for (double latitude = northLimit; latitude > southLimit; latitude -= latitudeResolution) {
			
			for (double longitude = westLimit; longitude < eastLimit; longitude += longitudeResolution) {
				
				double data = testDataProxy.getData(latitude, longitude);
				
				if (data == DemConstants.ELEV_NO_DATA) {
					invalidDataPoints++;
				} else {
					validDataPoints++;
					
					if (data < minData)
						minData = data;
					if (data > maxData)
						maxData = data;
					
				}
				
				pointsProcessed++;
			}
			
		}
		
		if (buffered) {
			testDataProxy.clearBuffers();
		}
		
		log.info("Points Processed: " + pointsProcessed);
		log.info("Valid Data Points: " + validDataPoints);
		log.info("Invalid Data Points: " + invalidDataPoints);
		log.info("Max Data Value: " + maxData);
		log.info("Min Data Value: " + minData);
		
	}
	
	
	
}

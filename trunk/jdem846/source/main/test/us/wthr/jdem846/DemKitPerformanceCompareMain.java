package us.wthr.jdem846;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.model.ModelProcessingTestMain;
import us.wthr.jdem846.rasterdata.gridfloat.GridFloatRasterDataProvider;

public class DemKitPerformanceCompareMain extends AbstractTestMain
{
	private static Log log = null;
	
	public static void main(String[] args)
	{
		try {
			initialize(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		log = Logging.getLog(DemKitPerformanceCompareMain.class);
		
		//String testData = "C:\\srv\\visual studio 2010\\Projects\\DemKit\\DemKitTesting\\PresRange_1-3as.flt";
		String testData = "C:\\srv\\elevation\\Earth\\etopo1_ice_g_f4.flt";
		
		try {
			DemKitPerformanceCompareMain testMain = new DemKitPerformanceCompareMain();
			testMain.doTest(testData);
		} catch (Exception ex) {
			log.error("Uncaught exception while running test main: " + ex.getMessage(), ex);
		}
		
	}
	
	
	protected void doTest(String testData) throws Exception
	{
		
		GridFloatRasterDataProvider data = new GridFloatRasterDataProvider();
		data.create(testData);
		
		log.info("North: " + data.getNorth());
		log.info("South: " + data.getSouth());
		log.info("East: " + data.getEast());
		log.info("West: " + data.getWest());
		log.info("Columns: " + data.getColumns());
		log.info("Rows: " + data.getRows());
		log.info("Latitude Resolution: " + data.getLatitudeResolution());
		log.info("Longitude Resolution: " + data.getLongitudeResolution());
		
		long start = System.currentTimeMillis();
		
		data.fillBuffer(data.getNorth(), data.getSouth(), data.getEast(), data.getWest());
		
		int valid = 0;
		int attempts = 0;
		double min = 50000000;
		double max = -50000000;
		for (double lat = data.getSouth(); lat <= data.getNorth(); lat += data.getLatitudeResolution()) {
			
			for (double lon = data.getWest(); lon <= data.getEast(); lon += data.getLongitudeResolution()) {
				
				int row = (int) MathExt.floor(data.latitudeToRow(lat));
				int column = (int) MathExt.floor(data.longitudeToColumn(lon));

				double elevation = data.getData(row, column);
				if (elevation != DemConstants.ELEV_NO_DATA) {
					valid++;

					min = MathExt.min(min, elevation);
					max = MathExt.max(max, elevation);
				}
				
				attempts++;
			}

		}

		data.clearBuffer();
		
		long end = System.currentTimeMillis();
		
		double duration = ((double)end - (double)start) / 1000.0;
		
		log.info("Test took " + duration + " seconds to complete");
		log.info("Minimum: " + min);
		log.info("Maximum: " + max);
		log.info("Valid Points: " + valid);
		log.info("Total Points: " + attempts + " (" + (attempts - valid) + " NO_DATA points)");
		//LOG_INFO << "Total Points: " << attempts << " (" << (attempts - valid) << " NO_VALUE points)" << END;
		
	}
	

	
}

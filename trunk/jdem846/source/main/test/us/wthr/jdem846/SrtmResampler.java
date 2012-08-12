package us.wthr.jdem846;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataProviderFactory;
import us.wthr.jdem846.util.ByteConversions;

public class SrtmResampler extends AbstractTestMain {
	private static Log log = null;
	
	public static void main(String[] args)
	{
		try {
			initialize(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		log = Logging.getLog(DemKitPerformanceCompareMain.class);
		

		String srtmInputPath = "F:\\bluemarble\\srtm_ramp2.world.86400x43200.bil";
		String srtmOutputPath = "F:\\bluemarble\\srtm_ramp2.world.86400x43200.resampled.flt";
		
		try {
			SrtmResampler main = new SrtmResampler();
			main.doWork(srtmInputPath, srtmOutputPath);
		} catch (Exception ex) {
			log.error("Uncaught exception while running test main: " + ex.getMessage(), ex);
		}
		
	}
	
	
	protected void doWork(String srtmInputPath, String srtmOutputPath) throws Exception
	{
		RasterData rasterData = RasterDataProviderFactory.loadRasterData(srtmInputPath);
		
		OutputStream out = new BufferedOutputStream(new FileOutputStream(srtmOutputPath));
		
		
		double latRes = rasterData.getLatitudeResolution();
		double lonRes = rasterData.getLongitudeResolution();
		
		double rows = rasterData.getRows();
		double columns = rasterData.getColumns();
		
		byte[] buffer4 = new byte[4];
		
		long byteWritten = 0;
		
		float max = -500000;
		float min = 500000;
		
		for (int y = 0; y < rows; y++) {
			double latitude = 90 - (y * latRes);
			for (int x = 0; x < columns; x++) {
				double longitude = -180.0 + (x * lonRes);
				
				float elevation = (float) rasterData.getData(latitude, longitude);
				if (elevation > 20000) {
					elevation = -(65535 - elevation);
				}
				
				ByteConversions.floatToBytes(elevation, buffer4);
				
				out.write(buffer4);
				
				max = (float) MathExt.max(max, elevation);
				min = (float) MathExt.min(min, elevation);
				byteWritten+=4;
			}
		}
		
		out.flush();
		out.close();
		
		log.info("Wrote " + byteWritten + " bytes");
		log.info("Maximum: " + max);
		log.info("Minimum: " + min);
	}
}

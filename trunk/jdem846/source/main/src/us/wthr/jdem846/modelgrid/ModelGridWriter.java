package us.wthr.jdem846.modelgrid;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

/**
 * @see https://docs.google.com/spreadsheet/ccc?key=0ApgEWNRhmLMTdHNnQWxXYktueVlJM202ODdlZnBlMEE#gid=0
 * @author kmsmgill@gmail.com
 * 
 */
public class ModelGridWriter extends ModelGridFileIO
{
	private static Log log = Logging.getLog(ModelGridWriter.class);

	
	
	public static void write(String path, IModelGrid modelGrid) throws IOException, DataSourceException
	{
		File f = new File(path);
		write(f, modelGrid);
	}
	
	public static void write(File file, IModelGrid modelGrid) throws IOException, DataSourceException
	{
		OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		write(out, modelGrid);
		out.close();
	}
	
	public static void write(OutputStream out, IModelGrid modelGrid) throws IOException, DataSourceException
	{
		long bytesWritten = 0;
		
		bytesWritten += writeHeader(modelGrid, out);

		int gridLength = (int) modelGrid.getGridLength();
		for (int i = 0; i < gridLength; i++) {
			bytesWritten += writeCell(modelGrid, i, out);
		}
		
		
		long estimatedFileSize = calculateEstimatedFileSize(modelGrid);
		
		if (bytesWritten != estimatedFileSize) {
			log.warn("A different number of bytes were written than were expected. Expected: " + estimatedFileSize + ", Written: " + bytesWritten);
		} else {
			log.info("Wrote " + bytesWritten + " to disk");
		}
		
	}	

	protected static long writeCell(IModelGrid modelGrid, int index, OutputStream out) throws IOException, DataSourceException
	{
		double elevation = modelGrid.getElevationByIndex(index);
		int rgba = modelGrid.getRgbaByIndex(index);
		
		long bytesWritten = 0;
		
		bytesWritten += write(elevation, out);
		bytesWritten += write(rgba, out);
		
		return bytesWritten;
	}
	
	protected static long writeHeader(IModelGrid modelGrid, OutputStream out) throws IOException
	{
		long bytesWritten = 0;
		
		//bytesWritten += write(ModelGridFileIO.FILE_HEADER_PREFIX, out);
		bytesWritten += write(modelGrid.getHeight(), out);
		bytesWritten += write(modelGrid.getWidth(), out);
		bytesWritten += write(modelGrid.getMinimum(), out);
		bytesWritten += write(modelGrid.getMaximum(), out);
		bytesWritten += write(modelGrid.getNorth(), out);
		bytesWritten += write(modelGrid.getSouth(), out);
		bytesWritten += write(modelGrid.getEast(), out);
		bytesWritten += write(modelGrid.getWest(), out);
		bytesWritten += write(modelGrid.getLatitudeResolution(), out);
		bytesWritten += write(modelGrid.getLongitudeResolution(), out);
		//bytesWritten += write(System.currentTimeMillis(), out);
		
		return bytesWritten;
	}
	
	
	

}

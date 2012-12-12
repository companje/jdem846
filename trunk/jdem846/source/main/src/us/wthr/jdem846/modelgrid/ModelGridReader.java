package us.wthr.jdem846.modelgrid;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

/**
 * @see https://docs.google.com/spreadsheet/ccc?key=0ApgEWNRhmLMTdHNnQWxXYktueVlJM202ODdlZnBlMEE#gid=0
 * @author kmsmgill@gmail.com
 * 
 */
public class ModelGridReader extends ModelGridFileIO {
	private static Log log = Logging.getLog(ModelGridReader.class);
	
	
	public static IModelGrid read(String filePath) throws IOException
	{
		File f = new File(filePath);
		return read(f);
	}
	
	public static IModelGrid read(File filePath) throws IOException
	{
		InputStream in = new BufferedInputStream(new FileInputStream(filePath));
		IModelGrid modelGrid = read(in);
		in.close();
		return modelGrid;
	}
	
	public static IModelGrid read(InputStream in) throws IOException
	{
		IModelGrid modelGrid = null;
		
		ModelGridHeader header = readHeader(in);
		
		modelGrid = ModelGridFactory.createBufferedModelGrid(header.north, header.south, header.east, header.west, header.latitudeResolution, header.longitudeResolution, header.minimum, header.maximum);
		
		ModelGridCell cell = new ModelGridCell();
		
		for (int y = 0; y < modelGrid.getHeight(); y++) {
			double latitude = modelGrid.getNorth() - modelGrid.getLatitudeResolution() * (double)y;
			for (int x = 0; x < modelGrid.getWidth(); x++) {
				double longitude = modelGrid.getWest() + modelGrid.getLongitudeResolution() * (double)x;
				readCell(in, cell);
				modelGrid.setElevation(latitude, longitude, cell.elevation);
				modelGrid.setRgba(latitude, longitude, cell.rgba);
			}
		}
		
		return modelGrid;
	}
	
	public static ModelGridHeader readHeader(String filePath) throws IOException
	{
		return readHeader(new File(filePath));
	}
	
	public static ModelGridHeader readHeader(File f) throws IOException
	{
		InputStream in = new BufferedInputStream(new FileInputStream(f));
		ModelGridHeader header = readHeader(in);
		in.close();
		return header;
	}
	
	public static ModelGridHeader readHeader(InputStream in) throws IOException
	{
		ModelGridHeader header = new ModelGridHeader();
		header.gridPrefix = readString(ModelGridFileIO.FILE_HEADER_PREFIX.getBytes().length, in);
		
		header.height = readInt(in);
		header.width = readInt(in);
		header.minimum = readDouble(in);
		header.maximum = readDouble(in);
		header.north = readDouble(in);
		header.south = readDouble(in);
		header.east = readDouble(in);
		header.west = readDouble(in);
		header.latitudeResolution = readDouble(in);
		header.longitudeResolution = readDouble(in);
		
		header.dateCreated = readLong(in);
		
		return header;
	}
	
	protected static void readCell(InputStream in, ModelGridCell cell) throws IOException
	{
		cell.elevation = readDouble(in);
		cell.rgba = readInt(in);
	}
	


}

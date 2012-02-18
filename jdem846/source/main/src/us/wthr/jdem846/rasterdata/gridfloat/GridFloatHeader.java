package us.wthr.jdem846.rasterdata.gridfloat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class GridFloatHeader
{
		
	private static Log log = Logging.getLog(GridFloatHeader.class);
	
	private int columns = 0;
	private int rows = 0;
	private double xLowerLeft = -180.0;
	private double yLowerLeft = -90.0;
	private double cellSize = 0;
	private double noData = 0;
	private ByteOrder byteOrder = ByteOrder.LSBFIRST;
	
	

	public GridFloatHeader(String file_path) throws DataSourceException
	{
		init(file_path);
	}


	private void init(String filePath) throws DataSourceException
	{
		File headerFile = new File(filePath);
		
		if (!headerFile.exists()) {
			throw new DataSourceException("GridFloat header file not found at " + filePath);
		}
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(headerFile));
			String line = null;
			while ((line = reader.readLine()) != null) {
				readHeaderLine(line);
			}
			reader.close();
		} catch (FileNotFoundException ex) {
			log.error("File Not Found error opening GridFloat data file: " + ex.getMessage(), ex);
			throw new DataSourceException("File Not Found error opening GridFloat data file: " + ex.getMessage(), ex);
		} catch (IOException ex) {
			log.error("IO error when opening GridFloat data file: " + ex.getMessage(), ex);
			throw new DataSourceException("IO error when opening GridFloat data file: " + ex.getMessage(), ex);
		}
	}


	private void readHeaderLine(String line)
	{
		
		line = line.replaceAll("[ ]+", " ");
		String[] parts = line.split(" ");
		if (parts.length != 2) {
			return;
		}
		String title = parts[0];
		String value = parts[1];
		title = title.trim();
		value = value.trim();
		

		if (title.equalsIgnoreCase("ncols"))
			this.columns = Integer.parseInt(value);
		if (title.equalsIgnoreCase("nrows"))
			this.rows = Integer.parseInt(value);
		if (title.equalsIgnoreCase("xllcorner") || title.equalsIgnoreCase("xllcenter"))
			this.xLowerLeft = Float.parseFloat(value);
		if (title.equalsIgnoreCase("yllcorner") || title.equalsIgnoreCase("yllcenter"))
			this.yLowerLeft = Float.parseFloat(value);
		if (title.equalsIgnoreCase("cellsize") || title.equalsIgnoreCase("xdim"))
			this.cellSize = Float.parseFloat(value);
		if (title.equalsIgnoreCase("NODATA_value") || title.equalsIgnoreCase("NODATA"))
			this.noData = Float.parseFloat(value);
		if (title.equalsIgnoreCase("byteorder")) {
			
			if (value.equalsIgnoreCase("LSBFIRST")) 
				byteOrder = ByteOrder.LSBFIRST;
			if (value.equalsIgnoreCase("MSBFIRST"))
				byteOrder = ByteOrder.MSBFIRST;
			if (value.equalsIgnoreCase("INTEL_BYTE_ORDER"))
				byteOrder = ByteOrder.INTEL_BYTE_ORDER;
			if (value.equalsIgnoreCase("INTEL_OR_MOTOROLA"))
				byteOrder = ByteOrder.INTEL_OR_MOTOROLA;
			
		}
		
	}
	
	public int getColumns()
	{
		return columns;
	}


	public void setColumns(int columns)
	{
		this.columns = columns;
	}


	public int getRows() 
	{
		return rows;
	}


	public void setRows(int rows) 
	{
		this.rows = rows;
	}


	public double getxLowerLeft() 
	{
		return xLowerLeft;
	}


	public void setxLowerLeft(double xLowerLeft)
	{
		this.xLowerLeft = xLowerLeft;
	}


	public double getyLowerLeft()
	{
		return yLowerLeft;
	}


	public void setyLowerLeft(double yLowerLeft) 
	{
		this.yLowerLeft = yLowerLeft;
	}


	public double getCellSize() 
	{
		return cellSize;
	}


	public void setCellSize(double cellSize) 
	{
		this.cellSize = cellSize;
	}


	public double getNoData() {
		return noData;
	}


	public void setNoData(double noData) 
	{
		this.noData = noData;
	}


	public ByteOrder getByteOrder()
	{
		return byteOrder;
	}


	public void setByteOrder(ByteOrder byteOrder) 
	{
		this.byteOrder = byteOrder;
	}
	
	
	
}

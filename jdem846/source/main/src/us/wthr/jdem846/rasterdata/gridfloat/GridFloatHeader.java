package us.wthr.jdem846.rasterdata.gridfloat;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.gis.exceptions.ParseException;
import us.wthr.jdem846.gis.input.esri.EsriHeader;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class GridFloatHeader
{
		
	@SuppressWarnings("unused")
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
		EsriHeader esriHeader = null;
		
		try {
			esriHeader = new EsriHeader(filePath);
		} catch (ParseException ex) {
			throw new DataSourceException("Error loading ESRI Header file: " + ex.getMessage(), ex);
		}
		
		this.columns = esriHeader.getIntAttribute("ncols");
		this.rows = esriHeader.getIntAttribute("nrows");
		
		
		if (esriHeader.hasAttribute("cellsize"))
			this.cellSize = esriHeader.getDoubleAttribute("cellsize");
		else if (esriHeader.hasAttribute("xdim"))
			this.cellSize = esriHeader.getDoubleAttribute("xdim");
		
		
		// TODO: Adjust between corner and center for x/y
		if (esriHeader.hasAttribute("xllcorner"))
			this.xLowerLeft = esriHeader.getDoubleAttribute("xllcorner");
		else if (esriHeader.hasAttribute("xllcenter"))
			this.xLowerLeft = esriHeader.getDoubleAttribute("xllcenter");
		else if (esriHeader.hasAttribute("ulxmap"))
			this.xLowerLeft = esriHeader.getDoubleAttribute("ulxmap");
		
		if (esriHeader.hasAttribute("yllcorner"))
			this.yLowerLeft = esriHeader.getDoubleAttribute("yllcorner");
		else if (esriHeader.hasAttribute("yllcenter")) 
			this.yLowerLeft = esriHeader.getDoubleAttribute("yllcenter");
		else if (esriHeader.hasAttribute("ulymap")) {
			double ulymap = esriHeader.getDoubleAttribute("ulymap");
			this.yLowerLeft = ulymap - (rows * cellSize);
		}
		
		
		
		
		
		
		
		if (esriHeader.hasAttribute("NODATA_value"))
			this.noData = esriHeader.getDoubleAttribute("NODATA_value");
		else if (esriHeader.hasAttribute("NODATA"))
			this.noData = esriHeader.getDoubleAttribute("NODATA");


		String _bo = esriHeader.getAttribute("byteorder", "MSBFIRST");
		if (_bo != null && _bo.equalsIgnoreCase("LSBFIRST")) 
			byteOrder = ByteOrder.LSBFIRST;
		else if (_bo != null && _bo.equalsIgnoreCase("MSBFIRST"))
			byteOrder = ByteOrder.MSBFIRST;
		else if (_bo != null && _bo.equalsIgnoreCase("I"))
			byteOrder = ByteOrder.LSBFIRST;
		else if (_bo != null && _bo.equalsIgnoreCase("M"))
			byteOrder = ByteOrder.LSBFIRST;

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

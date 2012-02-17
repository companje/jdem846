package us.wthr.jdem846.rasterdata.bil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class Bil16IntHeader
{
	private static Log log = Logging.getLog(Bil16IntHeader.class);
	
	private int columns = 0;
	private int rows = 0;
	private double xLowerLeft = -999999;
	private double yLowerLeft = -999999;
	private double cellSize = -999999;
	private double noData = 0;
	private int skipBytes = 0;
	private int nbands = 0;
	private int nbits = 0;
	private int bandRowBytes = 0;
	private int totalRowBytes = 0;
	private int pixelType = DemConstants.PIXELTYPE_SIGNED_INT;
	private ByteOrder byteOrder = ByteOrder.LSBFIRST;
	
	
	public Bil16IntHeader(String filePath) throws DataSourceException
	{
		init(filePath);
	}
	
	
	private void init(String filePath) throws DataSourceException
	{
		File headerFile = new File(filePath);
		
		if (!headerFile.exists()) {
			throw new DataSourceException("BIL16INT header file not found at " + filePath);
		}
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(headerFile));
			String line = null;
			while ((line = reader.readLine()) != null) {
				readHeaderLine(line);
			}
			reader.close();
			correctBounds();
		} catch (FileNotFoundException ex) {
			log.error("File Not Found error opening BIL16INT data file: " + ex.getMessage(), ex);
			throw new DataSourceException("File Not Found error opening BIL16INT data file: " + ex.getMessage(), ex);
		} catch (IOException ex) {
			log.error("IO error when opening BIL16INT data file: " + ex.getMessage(), ex);
			throw new DataSourceException("IO error when opening BIL16INT data file: " + ex.getMessage(), ex);
		}
	}
	
	private void correctBounds()
	{
		if (xLowerLeft != -999999) {
			xLowerLeft = xLowerLeft - (rows * cellSize);
		}
	}
	
	private void readHeaderLine(String line)
	{
		
		line = line.replaceAll("[ ]+", " ");
		String[] parts = line.split(" ");
		if (parts.length < 2) {
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
		if (title.equalsIgnoreCase("nbands"))
			this.nbands = Integer.parseInt(value);
		if (title.equalsIgnoreCase("nbits"))
			this.nbits = Integer.parseInt(value);
		if (title.equalsIgnoreCase("skipbytes"))
			this.skipBytes = Integer.parseInt(value);
		if (title.equalsIgnoreCase("bandrowbytes"))
			this.bandRowBytes = Integer.parseInt(value);
		if (title.equalsIgnoreCase("totalrowbytes"))
			this.totalRowBytes = Integer.parseInt(value);
		if (title.equalsIgnoreCase("ulxmap"))
			this.xLowerLeft = Float.parseFloat(value);
		if (title.equalsIgnoreCase("ulymap"))
			this.yLowerLeft = Float.parseFloat(value);
		if (title.equalsIgnoreCase("xdim"))
			this.cellSize = Float.parseFloat(value);
		if (title.equalsIgnoreCase("NODATA"))
			this.noData = Float.parseFloat(value);
		if (title.equalsIgnoreCase("byteorder")) {
			if (value.equalsIgnoreCase("LSBFIRST")) 
				byteOrder = ByteOrder.LSBFIRST;
			if (value.equalsIgnoreCase("MSBFIRST"))
				byteOrder = ByteOrder.MSBFIRST;
			if (value.equalsIgnoreCase("I"))
				byteOrder = ByteOrder.INTEL_BYTE_ORDER;
			if (value.equalsIgnoreCase("M"))
				byteOrder = ByteOrder.INTEL_OR_MOTOROLA;
		}
		if (title.equalsIgnoreCase("pixeltype")) {
			if (value.equalsIgnoreCase("SIGNEDINT"))
				pixelType = DemConstants.PIXELTYPE_SIGNED_INT;
			if (value.equalsIgnoreCase("UNSIGNEDINT"))
				pixelType = DemConstants.PIXELTYPE_UNSIGNED_INT;
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


	public double getNoData()
	{
		return noData;
	}


	public void setNoData(double noData)
	{
		this.noData = noData;
	}


	public int getSkipBytes()
	{
		return skipBytes;
	}


	public void setSkipBytes(int skipBytes)
	{
		this.skipBytes = skipBytes;
	}


	public int getNbands()
	{
		return nbands;
	}


	public void setNbands(int nbands)
	{
		this.nbands = nbands;
	}


	public int getNbits()
	{
		return nbits;
	}


	public void setNbits(int nbits)
	{
		this.nbits = nbits;
	}


	public int getBandRowBytes()
	{
		return bandRowBytes;
	}


	public void setBandRowBytes(int bandRowBytes)
	{
		this.bandRowBytes = bandRowBytes;
	}


	public int getTotalRowBytes()
	{
		return totalRowBytes;
	}


	public void setTotalRowBytes(int totalRowBytes)
	{
		this.totalRowBytes = totalRowBytes;
	}


	public int getPixelType()
	{
		return pixelType;
	}


	public void setPixelType(int pixelType)
	{
		this.pixelType = pixelType;
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

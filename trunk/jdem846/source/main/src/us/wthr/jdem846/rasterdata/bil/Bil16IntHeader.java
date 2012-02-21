package us.wthr.jdem846.rasterdata.bil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.gis.exceptions.ParseException;
import us.wthr.jdem846.gis.input.esri.EsriHeader;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class Bil16IntHeader
{
	private static Log log = Logging.getLog(Bil16IntHeader.class);
	
	private int columns = 0;
	private int rows = 0;
	private double xUpperLeft = DemConstants.ELEV_NO_DATA;
	private double yUpperLeft = DemConstants.ELEV_NO_DATA;
	private double xLowerLeft = DemConstants.ELEV_NO_DATA;
	private double yLowerLeft = DemConstants.ELEV_NO_DATA;
	private double xdim = DemConstants.ELEV_NO_DATA;
	private double ydim = DemConstants.ELEV_NO_DATA;
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
		EsriHeader esriHeader = null;
		
		try {
			esriHeader = new EsriHeader(filePath);
		} catch (ParseException ex) {
			throw new DataSourceException("Error loading ESRI Header file: " + ex.getMessage(), ex);
		}
		
		this.columns = esriHeader.getIntAttribute("ncols");
		this.rows = esriHeader.getIntAttribute("nrows");
		this.nbands = esriHeader.getIntAttribute("nbands");
		this.nbits = esriHeader.getIntAttribute("nbits");
		this.skipBytes = esriHeader.getIntAttribute("skipbytes", 0);
		this.bandRowBytes = esriHeader.getIntAttribute("bandrowbytes");
		this.totalRowBytes = esriHeader.getIntAttribute("totalrowbytes");
		this.xUpperLeft = esriHeader.getDoubleAttribute("ulxmap");
		this.yUpperLeft = esriHeader.getDoubleAttribute("ulymap");
		this.xdim = esriHeader.getDoubleAttribute("xdim");
		this.ydim = esriHeader.getDoubleAttribute("ydim");
		this.noData = esriHeader.getDoubleAttribute("NODATA", DemConstants.ELEV_NO_DATA);
		
		String _bo = esriHeader.getAttribute("byteorder", "MSBFIRST");
		if (_bo != null && _bo.equalsIgnoreCase("LSBFIRST")) 
			byteOrder = ByteOrder.LSBFIRST;
		else if (_bo != null && _bo.equalsIgnoreCase("MSBFIRST"))
			byteOrder = ByteOrder.MSBFIRST;
		else if (_bo != null && _bo.equalsIgnoreCase("I"))
			byteOrder = ByteOrder.INTEL_BYTE_ORDER;
		else if (_bo != null && _bo.equalsIgnoreCase("M"))
			byteOrder = ByteOrder.INTEL_OR_MOTOROLA;
		
		String _pt = esriHeader.getAttribute("pixeltype", "SIGNEDINT");

		if (_pt != null && _pt.equalsIgnoreCase("SIGNEDINT"))
			pixelType = DemConstants.PIXELTYPE_SIGNED_INT;
		else if (_pt != null && _pt.equalsIgnoreCase("UNSIGNEDINT"))
			pixelType = DemConstants.PIXELTYPE_UNSIGNED_INT;
		
		correctBounds();
		int i = 0;
	}
	
	private void correctBounds()
	{
		if (xdim == DemConstants.ELEV_NO_DATA) {
			xdim = (yUpperLeft - yLowerLeft) / (double) rows;
		}
		
		if (ydim == DemConstants.ELEV_NO_DATA) {
			ydim = xdim;
		}

		if (xLowerLeft == DemConstants.ELEV_NO_DATA
				&& yLowerLeft == DemConstants.ELEV_NO_DATA
				&& xUpperLeft == DemConstants.ELEV_NO_DATA
				&& yUpperLeft == DemConstants.ELEV_NO_DATA) {
			
			xdim = 1.0;
			ydim = 1.0;
			xUpperLeft = 0;
			yUpperLeft = 0;
			
			xLowerLeft = 0;
			yLowerLeft = -rows;
			
		} else {
			if (xLowerLeft == DemConstants.ELEV_NO_DATA && xUpperLeft != DemConstants.ELEV_NO_DATA) {
				xLowerLeft = xUpperLeft;
			} if (yLowerLeft == DemConstants.ELEV_NO_DATA && yUpperLeft != DemConstants.ELEV_NO_DATA) {
				yLowerLeft = yUpperLeft - (rows * ydim);
			}
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

	

	public double getxUpperLeft() {
		return xUpperLeft;
	}


	public void setxUpperLeft(double xUpperLeft) {
		this.xUpperLeft = xUpperLeft;
	}


	public double getyUpperLeft() {
		return yUpperLeft;
	}


	public void setyUpperLeft(double yUpperLeft) {
		this.yUpperLeft = yUpperLeft;
	}


	public double getXdim() {
		return xdim;
	}


	public void setXdim(double xdim) {
		this.xdim = xdim;
	}


	public double getYdim() {
		return ydim;
	}


	public void setYdim(double ydim) {
		this.ydim = ydim;
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

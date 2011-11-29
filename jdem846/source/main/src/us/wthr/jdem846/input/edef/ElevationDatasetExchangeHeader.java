/*
 * Copyright (C) 2011 Kevin M. Gill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.wthr.jdem846.input.edef;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.Projections;
import us.wthr.jdem846.input.DataSourceHeader;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.util.ByteArrayReader;
import us.wthr.jdem846.util.ByteArrayWriter;


@Deprecated
public class ElevationDatasetExchangeHeader  implements DataSourceHeader 
{
	private static Log log = Logging.getLog(ElevationDatasetExchangeHeader.class);
	
	public static final int BYTE_SIZE = 896;
	
	
	private int columns = 0;
	private int rows = 0;
	private double xLowerLeft = 0;
	private double yLowerLeft = 0;
	private double xCellSize = 0;
	private double yCellSize = 0;
	private double noData = DemConstants.ELEV_NO_DATA;
	private ByteOrder byteOrder = ByteOrder.LSBFIRST;
	private double maxElevation = 0;
	private double minElevation = 0;
	private double meanElevation = 0;
	private Projections projection = Projections.Proj_Geographic;
	private Projections datum = Projections.DatumE_GRS1980;
	private Projections zUnits = Projections.Linear_Meter;
	private Projections units = Projections.Linear_Decimal_Degrees;
	private Projections ellipse = Projections.Ellipse_GRS_1980;
	private Projections primeMeridian = Projections.PM_Greenwich;
	private String description = "";
	private String author = "";
	private byte[] reserved = new byte[64];
	
	public ElevationDatasetExchangeHeader()
	{
		author = (System.getProperty("user.name") != null) ? System.getProperty("user.name") : "";
	}
	
	
	public ElevationDatasetExchangeHeader(byte[] binData)
	{
		ByteArrayReader reader = new ByteArrayReader(binData);
		this.rows =  reader.getNextInt();
		this.columns = reader.getNextInt();
		this.xLowerLeft = reader.getNextDouble();
		this.yLowerLeft = reader.getNextDouble();
		this.xCellSize = reader.getNextDouble();
		this.yCellSize = reader.getNextDouble();
		this.noData = reader.getNextDouble();
		this.maxElevation = reader.getNextDouble();
		this.minElevation = reader.getNextDouble();
		this.meanElevation = reader.getNextDouble();
		
		this.projection = getProjectionInstance(reader.getNextInt());
		this.datum = getProjectionInstance(reader.getNextInt());
		this.zUnits = getProjectionInstance(reader.getNextInt());
		this.units = getProjectionInstance(reader.getNextInt());
		this.ellipse = getProjectionInstance(reader.getNextInt());
		this.primeMeridian = getProjectionInstance(reader.getNextInt());
		
		this.reserved = reader.getNextByteArray(64);
		
		this.description = reader.getNextString(256);
		this.author = reader.getNextString(128);
		
		
		log.info("Rows: " + rows);
		log.info("Columns: " + columns);
		
	}
	
	private Projections getProjectionInstance(int id)
	{
		for (Projections instance : Projections.values()) {
			if (instance.id() == id) {
				return instance;
			}
		}
		return null;
	}
	
	
	public byte[] toBytes()
	{
		ByteArrayWriter buffer = new ByteArrayWriter(ElevationDatasetExchangeHeader.BYTE_SIZE);
		
		buffer.putInt(rows);
		buffer.putInt(columns);
		buffer.putDouble(xLowerLeft);
		buffer.putDouble(yLowerLeft);
		buffer.putDouble(xCellSize);
		buffer.putDouble(yCellSize);
		buffer.putDouble(noData);
		buffer.putDouble(maxElevation);
		buffer.putDouble(minElevation);
		buffer.putDouble(meanElevation);
		buffer.putInt(this.projection.id());
		buffer.putInt(this.datum.id());
		buffer.putInt(this.zUnits.id());
		buffer.putInt(this.units.id());
		buffer.putInt(this.ellipse.id());
		buffer.putInt(this.primeMeridian.id());
		buffer.putByteArray(reserved);
		buffer.putString(description, 256);
		buffer.putString(author, 128);
		
		return buffer.getByteArray();
	}
	
	/*
	 * Standard Getters & Setters
	 *
	 */
	

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
		return xCellSize;
	}

	public void setCellSize(double xCellSize) 
	{
		this.xCellSize = xCellSize;
	}

	public double getNoData() 
	{
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
	
	
	/*
	 * Extra Getters & Setters
	 */
	
	
	
	public double getxCellSize()
	{
		return xCellSize;
	}

	public void setxCellSize(double xCellSize)
	{
		this.xCellSize = xCellSize;
	}

	public double getyCellSize()
	{
		return yCellSize;
	}

	public void setyCellSize(double yCellSize)
	{
		this.yCellSize = yCellSize;
	}

	public double getMaxElevation()
	{
		return maxElevation;
	}

	public void setMaxElevation(double maxElevation)
	{
		this.maxElevation = maxElevation;
	}

	public double getMinElevation()
	{
		return minElevation;
	}

	public void setMinElevation(double minElevation)
	{
		this.minElevation = minElevation;
	}

	public double getMeanElevation()
	{
		return meanElevation;
	}

	public void setMeanElevation(double meanElevation)
	{
		this.meanElevation = meanElevation;
	}

	public Projections getProjection()
	{
		return projection;
	}

	public void setProjection(Projections projection)
	{
		this.projection = projection;
	}

	public Projections getDatum()
	{
		return datum;
	}

	public void setDatum(Projections datum)
	{
		this.datum = datum;
	}

	public Projections getzUnits()
	{
		return zUnits;
	}

	public void setzUnits(Projections zUnits)
	{
		this.zUnits = zUnits;
	}

	public Projections getUnits()
	{
		return units;
	}

	public void setUnits(Projections units)
	{
		this.units = units;
	}	/*
	 * Extra Getters & Setters
	 */

	public Projections getEllipse()
	{
		return ellipse;
	}

	public void setEllipse(Projections ellipse)
	{
		this.ellipse = ellipse;
	}

	public Projections getPrimeMeridian()
	{
		return primeMeridian;
	}

	public void setPrimeMeridian(Projections primeMeridian)
	{
		this.primeMeridian = primeMeridian;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getAuthor()
	{
		return author;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

	public byte[] getReserved()
	{
		return reserved;
	}

	public void setReserved(byte[] reserved)
	{
		this.reserved = reserved;
	}
	
	
	

	
	
	
	
}

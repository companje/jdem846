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

package us.wthr.jdem846.input;

import us.wthr.jdem846.exception.DataSourceException;

public class PackagedReader 
{
	private double rows;
	private double columns;
	private double latitude;
	private double longitude;
	private double xDim;
	private double yDim;
	
	private double rightLongitude;
	private double bottomLatitude;
	
	private double centerLongitude;
	private double centerLatitude;
	
	private double centerLongitudePositionPercent;
	private double centerLatitudePositionPercent;
	
	private double moveLongitudeX;
	private double moveLatitudeZ;
	
	private double fieldRows;
	private double fieldColumns;
	
	private int rowStart;
	private int rowEnd;
	private int columnStart;
	private int columnEnd;
	
	private DataSource dataSource;
	
	private boolean isDisposed = false;
	
	public PackagedReader(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}
	
	public void init(double minLon, double maxLon, double minLat, double maxLat, double fieldCols, double fieldRows, double avgXDim, double avgYDim)
	{
		this.fieldRows = fieldRows;
		this.fieldColumns = fieldCols;
		
		this.rows = dataSource.getHeader().getRows();
		this.columns = dataSource.getHeader().getColumns();
		this.longitude = dataSource.getHeader().getxLowerLeft();
		this.latitude = dataSource.getHeader().getyLowerLeft() + (dataSource.getHeader().getCellSize() * dataSource.getHeader().getRows());
		this.xDim = dataSource.getHeader().getCellSize();
		this.yDim = dataSource.getHeader().getCellSize();

		if ((this.latitude == 0.0f) || (this.longitude == 0.0f)) {

			this.centerLongitudePositionPercent = 0.5f;
			this.centerLatitudePositionPercent = 0.5f;
		
			this.moveLongitudeX = 0;
			this.moveLatitudeZ = 0;

			this.columnStart = 0;
			this.columnEnd = (int) this.columns;

			this.rowStart = 0;
			this.rowEnd = (int) this.rows;

		} else {

			this.rightLongitude = longitude + (columns * avgXDim);
			this.bottomLatitude = dataSource.getHeader().getyLowerLeft();

			this.centerLongitude = (longitude + this.rightLongitude) / 2.0f;
			this.centerLatitude = (latitude + this.bottomLatitude) / 2.0f;
			this.centerLongitudePositionPercent = (this.centerLongitude - minLon) / (maxLon - minLon);

			this.moveLongitudeX = -(this.fieldColumns / 2.0f) + (this.fieldColumns * this.centerLongitudePositionPercent);
			this.columnStart = (int) (this.moveLongitudeX + (0.0 - (columns / 2.0f)));
			this.columnEnd = (int) (this.columnStart + columns);

			//double fieldMidLat = ((maxLat - minLat) / 2) + minLat;
			double fieldLatRange = maxLat - minLat;
			double latMidDiff = centerLatitude - minLat;

			this.centerLatitudePositionPercent = (double) ((1 - (latMidDiff / fieldLatRange)) - 0.5);
			this.moveLatitudeZ = this.centerLatitudePositionPercent * fieldRows;
			this.rowStart = (int) ((-1 * (rows / 2)) + moveLatitudeZ);
			this.rowEnd = (int) ((this.rows / 2) + this.moveLatitudeZ);

		}
		
	}
	
	public double getElevation(int row, int column) throws DataSourceException
	{
		return dataSource.getElevation(row - rowStart, column - columnStart);
	}

	
	public boolean setDataPrecached(boolean precached)
	{
		return dataSource.setDataPrecached(precached);
	}
	
	public double getRows() 
	{
		return rows;
	}

	public void setRows(double rows) 
	{
		this.rows = rows;
	}

	public double getColumns() 
	{
		return columns;
	}

	public void setColumns(double columns)
	{
		this.columns = columns;
	}

	public double getLatitude()
	{
		return latitude;
	}

	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

	public double getLongitude() 
	{
		return longitude;
	}

	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}

	public double getxDim() 
	{
		return xDim;
	}

	public void setxDim(double xDim) 
	{
		this.xDim = xDim;
	}

	public double getyDim() 
	{
		return yDim;
	}

	public void setyDim(double yDim)
	{
		this.yDim = yDim;
	}

	public double getRightLongitude() 
	{
		return rightLongitude;
	}

	public void setRightLongitude(double rightLongitude) 
	{
		this.rightLongitude = rightLongitude;
	}

	public double getBottomLatitude() 
	{
		return bottomLatitude;
	}

	public void setBottomLatitude(double bottomLatitude) 
	{
		this.bottomLatitude = bottomLatitude;
	}

	public double getCenterLongitude() 
	{
		return centerLongitude;
	}

	public void setCenterLongitude(double centerLongitude)
	{
		this.centerLongitude = centerLongitude;
	}

	public double getCenterLatitude()
	{
		return centerLatitude;
	}

	public void setCenterLatitude(double centerLatitude)
	{
		this.centerLatitude = centerLatitude;
	}

	public double getCenterLongitudePositionPercent()
	{
		return centerLongitudePositionPercent;
	}

	public void setCenterLongitudePositionPercent(
			double centerLongitudePositionPercent) 
	{
		this.centerLongitudePositionPercent = centerLongitudePositionPercent;
	}

	public double getCenterLatitudePositionPercent() 
	{
		return centerLatitudePositionPercent;
	}

	public void setCenterLatitudePositionPercent(double centerLatitudePositionPercent) 
	{
		this.centerLatitudePositionPercent = centerLatitudePositionPercent;
	}

	public double getMoveLongitudeX()
	{
		return moveLongitudeX;
	}

	public void setMoveLongitudeX(double moveLongitudeX) 
	{
		this.moveLongitudeX = moveLongitudeX;
	}

	public double getMoveLatitudeZ() 
	{
		return moveLatitudeZ;
	}

	public void setMoveLatitudeZ(double moveLatitudeZ) 
	{
		this.moveLatitudeZ = moveLatitudeZ;
	}

	public double getFieldRows() 
	{
		return fieldRows;
	}

	public void setFieldRows(double fieldRows) 
	{
		this.fieldRows = fieldRows;
	}

	public double getFieldColumns()
	{
		return fieldColumns;
	}

	public void setFieldColumns(double fieldColumns) 
	{
		this.fieldColumns = fieldColumns;
	}

	public int getRowStart() 
	{
		return rowStart;
	}

	public void setRowStart(int rowStart)
	{
		this.rowStart = rowStart;
	}

	public int getRowEnd() 
	{
		return rowEnd;
	}

	public void setRowEnd(int rowEnd) 
	{
		this.rowEnd = rowEnd;
	}

	public int getColumnStart() 
	{
		return columnStart;
	}

	public void setColumnStart(int columnStart) 
	{
		this.columnStart = columnStart;
	}

	public int getColumnEnd() 
	{
		return columnEnd;
	}

	public void setColumnEnd(int columnEnd) 
	{
		this.columnEnd = columnEnd;
	}

	public DataSource getDataSource() 
	{
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) 
	{
		this.dataSource = dataSource;
	}
	
	/** Creates a field by field copy of the current object instance.
	 * 
	 * @return
	 */
	public PackagedReader copy()
	{
		PackagedReader copy = new PackagedReader(this.dataSource.copy());
		
		copy.rows = this.rows;
		copy.columns = this.columns;
		copy.latitude = this.latitude;
		copy.longitude = this.longitude;
		copy.xDim = this.xDim;
		copy.yDim = this.yDim;
		
		copy.rightLongitude = this.rightLongitude;
		copy.bottomLatitude = this.bottomLatitude;
		
		copy.centerLongitude = this.centerLongitude;
		copy.centerLatitude = this.centerLatitude;
		
		copy.centerLongitudePositionPercent = this.centerLongitudePositionPercent;
		copy.centerLatitudePositionPercent = this.centerLatitudePositionPercent;
		
		copy.moveLongitudeX = this.moveLongitudeX;
		copy.moveLatitudeZ = this.moveLatitudeZ;
		
		copy.fieldRows = this.fieldRows;
		copy.fieldColumns = this.fieldColumns;
		
		copy.rowStart = this.rowStart;
		copy.rowEnd = this.rowEnd;
		copy.columnStart = this.columnStart;
		copy.columnEnd = this.columnEnd;
		
		
		return copy;
	}
	
	public void dispose() throws DataSourceException
	{
		if (isDisposed) {
			throw new DataSourceException("Object already disposed of");
		}
		
		dataSource.dispose();
		dataSource = null;
		
		isDisposed = true;
	}
	
}

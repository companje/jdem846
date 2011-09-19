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
	private float rows;
	private float columns;
	private float latitude;
	private float longitude;
	private float xDim;
	private float yDim;
	
	private float rightLongitude;
	private float bottomLatitude;
	
	private float centerLongitude;
	private float centerLatitude;
	
	private float centerLongitudePositionPercent;
	private float centerLatitudePositionPercent;
	
	private float moveLongitudeX;
	private float moveLatitudeZ;
	
	private float fieldRows;
	private float fieldColumns;
	
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
	
	public void init(float minLon, float maxLon, float minLat, float maxLat, float fieldCols, float fieldRows, float avgXDim, float avgYDim)
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

			//float fieldMidLat = ((maxLat - minLat) / 2) + minLat;
			float fieldLatRange = maxLat - minLat;
			float latMidDiff = centerLatitude - minLat;

			this.centerLatitudePositionPercent = (float) ((1 - (latMidDiff / fieldLatRange)) - 0.5);
			this.moveLatitudeZ = this.centerLatitudePositionPercent * fieldRows;
			this.rowStart = (int) ((-1 * (rows / 2)) + moveLatitudeZ);
			this.rowEnd = (int) ((this.rows / 2) + this.moveLatitudeZ);

		}
		
	}
	
	public float getElevation(int row, int column) throws DataSourceException
	{
		return dataSource.getElevation(row - rowStart, column - columnStart);
	}

	
	public boolean setDataPrecached(boolean precached)
	{
		return dataSource.setDataPrecached(precached);
	}
	
	public float getRows() 
	{
		return rows;
	}

	public void setRows(float rows) 
	{
		this.rows = rows;
	}

	public float getColumns() 
	{
		return columns;
	}

	public void setColumns(float columns)
	{
		this.columns = columns;
	}

	public float getLatitude()
	{
		return latitude;
	}

	public void setLatitude(float latitude)
	{
		this.latitude = latitude;
	}

	public float getLongitude() 
	{
		return longitude;
	}

	public void setLongitude(float longitude)
	{
		this.longitude = longitude;
	}

	public float getxDim() 
	{
		return xDim;
	}

	public void setxDim(float xDim) 
	{
		this.xDim = xDim;
	}

	public float getyDim() 
	{
		return yDim;
	}

	public void setyDim(float yDim)
	{
		this.yDim = yDim;
	}

	public float getRightLongitude() 
	{
		return rightLongitude;
	}

	public void setRightLongitude(float rightLongitude) 
	{
		this.rightLongitude = rightLongitude;
	}

	public float getBottomLatitude() 
	{
		return bottomLatitude;
	}

	public void setBottomLatitude(float bottomLatitude) 
	{
		this.bottomLatitude = bottomLatitude;
	}

	public float getCenterLongitude() 
	{
		return centerLongitude;
	}

	public void setCenterLongitude(float centerLongitude)
	{
		this.centerLongitude = centerLongitude;
	}

	public float getCenterLatitude()
	{
		return centerLatitude;
	}

	public void setCenterLatitude(float centerLatitude)
	{
		this.centerLatitude = centerLatitude;
	}

	public float getCenterLongitudePositionPercent()
	{
		return centerLongitudePositionPercent;
	}

	public void setCenterLongitudePositionPercent(
			float centerLongitudePositionPercent) 
	{
		this.centerLongitudePositionPercent = centerLongitudePositionPercent;
	}

	public float getCenterLatitudePositionPercent() 
	{
		return centerLatitudePositionPercent;
	}

	public void setCenterLatitudePositionPercent(float centerLatitudePositionPercent) 
	{
		this.centerLatitudePositionPercent = centerLatitudePositionPercent;
	}

	public float getMoveLongitudeX()
	{
		return moveLongitudeX;
	}

	public void setMoveLongitudeX(float moveLongitudeX) 
	{
		this.moveLongitudeX = moveLongitudeX;
	}

	public float getMoveLatitudeZ() 
	{
		return moveLatitudeZ;
	}

	public void setMoveLatitudeZ(float moveLatitudeZ) 
	{
		this.moveLatitudeZ = moveLatitudeZ;
	}

	public float getFieldRows() 
	{
		return fieldRows;
	}

	public void setFieldRows(float fieldRows) 
	{
		this.fieldRows = fieldRows;
	}

	public float getFieldColumns()
	{
		return fieldColumns;
	}

	public void setFieldColumns(float fieldColumns) 
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

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

package us.wthr.jdem846.render.kml;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class Tile
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(Tile.class);
	
	private File file;
	private int fromRow = 0;
	private int toRow = 0;
	private int fromColumn = 0;
	private int toColumn = 0;
	
	private double north;
	private double south;
	private double east;
	private double west;
	
	private CoordinateSpace coordinateSpace;
	private GridSpace gridSpace;
	
	public Tile(File file, int fromRow, int fromColumn, int toRow, int toColumn, double north, double south, double east, double west)
	{
		setFile(file);
		setFromRow(fromRow);
		setToRow(toRow);
		setFromColumn(fromColumn);
		setToColumn(toColumn);
		
		setNorth(north);
		setSouth(south);
		setEast(east);
		setWest(west);
		
		//CoordinateSpace(double north, double south, double east, double west)
		coordinateSpace = new CoordinateSpace(north, south, east, west);
		
		//GridSpace(int topRow, int bottomRow, int leftColumn, int rightColumn)
		gridSpace = new GridSpace(fromRow, toRow, fromColumn, toColumn);
		
		
	}

	
	/** Checks whether the specified coordinate is contained within the bounds of this
	 * instance. 
	 * 
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public boolean contains(double latitude, double longitude)
	{
		return coordinateSpace.contains(longitude, latitude);
	}
	
	/** Checks whether the specified grid coordinate is contained within the bounds of
	 * this instance.
	 * 
	 * @param row
	 * @param column
	 * @return
	 */
	public boolean contains(int row, int column)
	{
		return gridSpace.contains(column, row);
	}

	/** Checks if the specified grid coordinates intersect with those in this instance.
	 * Uses fromColumn/fromRow as X/Y (top left), toColumn-fromColumn as width and 
	 * toRow-fromRow as height.
	 * 
	 * @param fromRow
	 * @param fromColumn
	 * @param toRow
	 * @param toColumn
	 * @return
	 */
	public boolean intersects(int fromRow, int fromColumn, int toRow, int toColumn)
	{
		int width = toColumn - fromColumn;
		int height = toRow - fromRow;
		
		return gridSpace.intersects(fromColumn, fromRow, width, height);
	}
	
	/** Checks if the specified coordinates intersect with those in this instance. Uses the 
	 * West/North coordinate as X/Y, abs(east-west) as width and abs(north-south) as height.
	 * 
	 * @param _north
	 * @param _south
	 * @param _east
	 * @param _west
	 * @return True if the coordinate parameters intersect.
	 */
	public boolean intersects(double _north, double _south, double _east, double _west)
	{
		
		double width = Math.abs(_east - _west);
		double height = Math.abs(_north - _south);
		
		return coordinateSpace.intersects(_west, _south, width, height);
		//return true;
	}
	
	/** A File object representing the tile image.
	 * 
	 * @return
	 */
	public File getFile()
	{
		return file;
	}


	public void setFile(File file)
	{
		this.file = file;
	}


	public int getFromRow()
	{
		return fromRow;
	}


	public void setFromRow(int fromRow)
	{
		this.fromRow = fromRow;
	}


	public int getToRow()
	{
		return toRow;
	}


	public void setToRow(int toRow)
	{
		this.toRow = toRow;
	}


	public int getFromColumn()
	{
		return fromColumn;
	}


	public void setFromColumn(int fromColumn)
	{
		this.fromColumn = fromColumn;
	}


	public int getToColumn()
	{
		return toColumn;
	}


	public void setToColumn(int toColumn)
	{
		this.toColumn = toColumn;
	}
	
	
	public int getWidth()
	{
		return toColumn - fromColumn;
	}
	
	public int getHeight()
	{
		return toRow - fromRow;
	}
	
	public double getNorth()
	{
		return north;
	}
	
	public void setNorth(double north)
	{
		this.north = north;
	}

	public double getSouth()
	{
		return south;
	}

	public void setSouth(double south)
	{
		this.south = south;
	}

	public double getEast()
	{
		return east;
	}

	public void setEast(double east)
	{
		this.east = east;
	}

	public double getWest()
	{
		return west;
	}

	public void setWest(double west)
	{
		this.west = west;
	}



	public BufferedImage loadImage() throws IOException
	{
		BufferedImage image = null;
		
		image = ImageIO.read(file);
		
		return image;
	}

	public boolean deleteImageFile()
	{
		if (file.exists()) {
			return file.delete();
		} else {
			return false;
		}
	}
}

package us.wthr.jdem846.kml;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Tile
{
	private File file;
	private int fromRow = 0;
	private int toRow = 0;
	private int fromColumn = 0;
	private int toColumn = 0;
	private java.awt.Polygon polygon;
	
	public Tile(File file, int fromRow, int fromColumn, int toRow, int toColumn)
	{
		setFile(file);
		setFromRow(fromRow);
		setToRow(toRow);
		setFromColumn(fromColumn);
		setToColumn(toColumn);
		
		polygon = new java.awt.Polygon();
		polygon.addPoint(fromRow, fromColumn);
		polygon.addPoint(toRow,  fromColumn);
		polygon.addPoint(toRow, toColumn);
		polygon.addPoint(fromRow, toColumn);
		
		
	}


	
	public boolean intersects(int fromRow, int fromColumn, int toRow, int toColumn)
	{
		int width = toColumn - fromColumn;
		int height = toRow - fromRow;
		
		return polygon.intersects(fromColumn, fromRow, width, height);
	}
	
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
	
	
	public BufferedImage loadImage() throws IOException
	{
		BufferedImage image = null;
		
		image = ImageIO.read(file);
		
		return image;
	}
	
}

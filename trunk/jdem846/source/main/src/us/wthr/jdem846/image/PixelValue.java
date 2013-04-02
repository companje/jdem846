package us.wthr.jdem846.image;

import us.wthr.jdem846.graphics.IColor;

public class PixelValue
{
	private IColor color;
	private double columnDecimal;
	private double rowDecimal;
	private int column;
	private int row;
	
	public PixelValue(IColor color, int column, double columnDecimal, int row, double rowDecimal)
	{
		this.color = color;
		this.column = column;
		this.columnDecimal = columnDecimal;
		this.row = row;
		this.rowDecimal = rowDecimal;
	}

	public IColor getColor()
	{
		return color;
	}

	public double getColumnDecimal()
	{
		return columnDecimal;
	}

	public double getRowDecimal()
	{
		return rowDecimal;
	}

	public int getColumn()
	{
		return column;
	}

	public int getRow()
	{
		return row;
	}
	
	
	
	
}

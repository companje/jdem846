package us.wthr.jdem846.render.render2d;

import us.wthr.jdem846.canvas.ModelCanvas;

@Deprecated
public class RenderedTile 
{
	private ModelCanvas modelCanvas;
	private double northLimit; 
	private double southLimit;
	private double eastLimit;
	private double westLimit;
	
	private int tileColumn = 0;
	private int tileRow = 0;
	
	public RenderedTile(ModelCanvas modelCanvas, double northLimit, double southLimit, double eastLimit, double westLimit, int tileColumn, int tileRow )
	{
		this.modelCanvas = modelCanvas;
		this.northLimit = northLimit;
		this.southLimit = southLimit;
		this.eastLimit = eastLimit;
		this.westLimit = westLimit;
		this.tileColumn = tileColumn;
		this.tileRow = tileRow;
	}


	public ModelCanvas getModelCanvas()
	{
		return modelCanvas;
	}


	public double getNorthLimit()
	{
		return northLimit;
	}


	public double getSouthLimit()
	{
		return southLimit;
	}


	public double getEastLimit() 
	{
		return eastLimit;
	}


	public double getWestLimit() 
	{
		return westLimit;
	}


	public int getTileColumn()
	{
		return tileColumn;
	}


	public void setTileColumn(int tileColumn)
	{
		this.tileColumn = tileColumn;
	}


	public int getTileRow()
	{
		return tileRow;
	}


	public void setTileRow(int tileRow)
	{
		this.tileRow = tileRow;
	}
	
	
	
	
}

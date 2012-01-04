package us.wthr.jdem846.render.render2d;

import us.wthr.jdem846.render.ModelCanvas;

public class RenderedTile 
{
	private ModelCanvas modelCanvas;
	private double northLimit; 
	private double southLimit;
	private double eastLimit;
	private double westLimit;
	
	
	public RenderedTile(ModelCanvas modelCanvas, double northLimit, double southLimit, double eastLimit, double westLimit)
	{
		this.modelCanvas = modelCanvas;
		this.northLimit = northLimit;
		this.southLimit = southLimit;
		this.eastLimit = eastLimit;
		this.westLimit = westLimit;
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
	
	
	
	
}

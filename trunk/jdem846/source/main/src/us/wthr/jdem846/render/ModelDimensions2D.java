package us.wthr.jdem846.render;

import java.awt.Color;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.RasterDataContext;

public class ModelDimensions2D
{
	private static Log log = Logging.getLog(ModelDimensions2D.class);
	
	private int tileSize;
	private int dataRows;
	private int dataColumns;
	private double sizeRatio;
	private int outputWidth;
	private int outputHeight;
	private double xDim;
	private double yDim;
	private double numTilesHorizontal;
	private double numTilesVertical;
	private long tileOutputWidth;
	private long tileOutputHeight;
	private long tileCount;
	
	protected ModelDimensions2D()
	{
		
	}
	
	
	public static ModelDimensions2D getModelDimensions(ModelContext modelContext)
	{
		RasterDataContext dataContext = modelContext.getRasterDataContext();
		ModelOptions modelOptions = modelContext.getModelOptions();
		
		ModelDimensions2D modelDimensions = new ModelDimensions2D();
		
		modelDimensions.tileSize = modelOptions.getTileSize();
		
		
		modelDimensions.dataRows = dataContext.getDataRows();
		modelDimensions.dataColumns = dataContext.getDataColumns();
		
		if (modelDimensions.tileSize > modelDimensions.dataRows && modelDimensions.dataRows > modelDimensions.dataColumns)
			modelDimensions.tileSize = modelDimensions.dataRows;
		
		if (modelDimensions.tileSize > modelDimensions.dataColumns && modelDimensions.dataColumns > modelDimensions.dataRows)
			modelDimensions.tileSize = modelDimensions.dataColumns;

	
		modelDimensions.sizeRatio = 1.0f;
		modelDimensions.outputWidth = modelOptions.getWidth();
		modelDimensions.outputHeight = modelOptions.getHeight();

		if (modelDimensions.dataRows > modelDimensions.dataColumns) {
			modelDimensions.sizeRatio = (double)modelDimensions.dataColumns / (double)modelDimensions.dataRows;
			modelDimensions.outputWidth = (int) Math.round(((double) modelDimensions.outputHeight) * modelDimensions.sizeRatio);
		} else if (modelDimensions.dataColumns > modelDimensions.dataRows) {
			modelDimensions.sizeRatio = (double)modelDimensions.dataRows / (double)modelDimensions.dataColumns;
			modelDimensions.outputHeight = (int) Math.round(((double)modelDimensions.outputWidth) * modelDimensions.sizeRatio);
		}
		
		log.info("Output width/height: " + modelDimensions.outputWidth + "/" + modelDimensions.outputHeight);
		
		double xdimRatio = (double)modelDimensions.outputWidth / (double)modelDimensions.dataColumns;
		modelDimensions.xDim = dataContext.getLongitudeResolution() / xdimRatio;
		//dataPackage.setAvgXDim(xDim);

		double ydimRatio = (double)modelDimensions.outputHeight / (double)modelDimensions.dataRows;
		modelDimensions.yDim = dataContext.getLatitudeResolution() / ydimRatio;
		//dataPackage.setAvgYDim(yDim);
		//log.info("X/Y Dimension (cellsize): " + xDim + "/" + yDim);
		

		modelDimensions.numTilesHorizontal = ((double)modelDimensions.dataColumns) / ((double)modelDimensions.tileSize);
		modelDimensions.numTilesVertical = ((double)modelDimensions.dataRows) / ((double)modelDimensions.tileSize);
		
		modelDimensions.tileOutputWidth = Math.round(((double)modelDimensions.outputWidth) / modelDimensions.numTilesHorizontal);
		modelDimensions.tileOutputHeight = Math.round(((double)modelDimensions.outputHeight) / modelDimensions.numTilesVertical);
		
		modelDimensions.tileCount = (int) (Math.ceil(((double)modelDimensions.dataRows / (double)modelDimensions.tileSize)) * Math.ceil(((double)modelDimensions.dataColumns / (double)modelDimensions.tileSize)));
		
		return modelDimensions;
	}
	
	public int getTileSize()
	{
		return tileSize;
	}


	public int getDataRows()
	{
		return dataRows;
	}


	public int getDataColumns()
	{
		return dataColumns;
	}


	public double getSizeRatio()
	{
		return sizeRatio;
	}


	public int getOutputWidth()
	{
		return outputWidth;
	}


	public int getOutputHeight()
	{
		return outputHeight;
	}


	public double getxDim()
	{
		return xDim;
	}


	public double getyDim()
	{
		return yDim;
	}


	public double getNumTilesHorizontal()
	{
		return numTilesHorizontal;
	}


	public double getNumTilesVertical()
	{
		return numTilesVertical;
	}


	public long getTileOutputWidth()
	{
		return tileOutputWidth;
	}


	public long getTileOutputHeight()
	{
		return tileOutputHeight;
	}


	public long getTileCount()
	{
		return tileCount;
	}
	
	
	
}

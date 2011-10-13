package us.wthr.jdem846.render;

import java.awt.Color;
import java.awt.image.ImageObserver;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.DemPoint;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.color.ColorRegistry;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.input.DataBounds;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.input.SubsetDataPackage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.scripting.ScriptProxy;
import us.wthr.jdem846.util.ColorSerializationUtil;

public abstract class BasicRenderEngine extends RenderEngine
{
	private static Log log = Logging.getLog(Dem3dGenerator.class);
	
	
	//protected DataPackage dataPackage;
	protected SubsetDataPackage dataSubset;
	//protected ModelOptions modelOptions;
	//protected ScriptProxy scriptProxy;
	

	
	public BasicRenderEngine(ModelContext modelContext)
	{
		super.initialize(modelContext);
		//this.dataPackage = modelContext.getDataPackage();
		//this.modelOptions = modelContext.getModelOptions();
		//this.scriptProxy = modelContext.getScriptProxy();
	}
	
	/*
	public BasicRenderEngine(DataPackage dataPackage, ModelOptions modelOptions)
	{
		this.dataPackage = dataPackage;
		this.modelOptions = modelOptions;
	}
	*/
	
	public void precacheData() throws DataSourceException
	{
		if (dataSubset != null) {
			dataSubset.precacheData();
		}
	}
	
	public void unloadData() throws DataSourceException
	{
		if (dataSubset != null) {
			dataSubset.unloadData();
		}
	}
	
	public void loadDataSubset(int fromCol, int fromRow, int width, int height)
	{
		DataBounds tileBounds = new DataBounds(fromCol, fromRow, width, height);
		dataSubset = getDataPackage().getDataSubset(tileBounds);
	}

	protected float getElevation(int row, int col) throws DataSourceException
	{
		if (dataSubset != null) {
			return dataSubset.getElevation(row, col);
		} else {
			return getDataPackage().getElevation(row, col);
		}
	}
	
	
	protected void getPoint(int row, int column, DemPoint point) throws DataSourceException
	{
		getPoint(row, column, 1, point);
	}
	
	protected void getPoint(int row, int column, int gridSize, DemPoint point) throws DataSourceException
	{
		//DemPoint point = new DemPoint();


		if (getDataPackage() == null) {
			point.setCondition(DemConstants.STAT_NO_DATA_PACKAGE);
			return;
			//return point;
		}

		//float elevationMax = dataPackage.getMaxElevation();
		//float elevationMin = dataPackage.getMinElevation();

		float elevation_bl = getElevation(row, column);
		float elevation_br = getElevation(row, column + gridSize);
		float elevation_fl = getElevation(row + gridSize, column);
		//float elevation_fr = getElevation(row + gridSize, column + gridSize);

		if (elevation_bl == DemConstants.ELEV_NO_DATA) {
			point.setCondition(DemConstants.STAT_INVALID_ELEVATION);
			return;
		}
		
		point.setBackLeftElevation(elevation_bl);

		if (elevation_br != DemConstants.ELEV_NO_DATA) {
			point.setBackRightElevation(elevation_br);
		} else {
			point.setBackRightElevation(point.getBackLeftElevation());
		}

		if (elevation_fl != DemConstants.ELEV_NO_DATA) {
			point.setFrontLeftElevation(elevation_fl);
		} else {
			point.setFrontLeftElevation(point.getBackLeftElevation());
		}

	//	if (elevation_fr != DemConstants.ELEV_NO_DATA) {
		//	point.setFrontRightElevation(elevation_fr);
		//} else {
		//	point.setFrontRightElevation(point.getBackLeftElevation());
	//	}
		
		
		if (point.getBackLeftElevation() == 0 
			&& point.getBackRightElevation() == 0 
			&& point.getFrontLeftElevation() == 0) {
			point.setCondition(DemConstants.STAT_FLAT_SEA_LEVEL);
			return;
		}
		

		point.setMiddleElevation((point.getBackLeftElevation() + point.getBackRightElevation() + point.getFrontLeftElevation() + point.getFrontRightElevation()) / 4.0f);
		
		point.setCondition(DemConstants.STAT_SUCCESSFUL);
		
		return;
	}
	

	public Color getDefinedColor(String identifier)
	{
		log.info("COLOR: " + identifier);
		return ColorSerializationUtil.stringToColor(identifier);
	}
	
	
	//public ScriptProxy getScriptProxy()
	//{
	//	return getModelContext().getScriptProxy();
	//}
	
	//public DataPackage getDataPackage()
	//{
	////	return getModelContext().getDataPackage();
	//}

	//public void setDataPackage(DataPackage dataPackage)
	//{
	//	this.dataPackage = dataPackage;
	//}

	//public ModelOptions getModelOptions()
	//{
	//	return getModelContext().getModelOptions();
	//}

	//public void setModelOptions(ModelOptions modelOptions)
	//{
	//	this.modelOptions = modelOptions;
	//}
	
	
	protected static double cos(double d)
	{
		return Math.cos(Math.toRadians(d));
	}
	
	protected static double sin(double d)
	{
		return Math.sin(Math.toRadians(d));
	}
}

package us.wthr.jdem846.render;

import java.awt.Color;
import java.math.BigDecimal;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.DemPoint;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.util.ColorSerializationUtil;

public abstract class BasicRenderEngine extends RenderEngine
{
	private static Log log = Logging.getLog(BasicRenderEngine.class);
	
	
	//protected DataPackage dataPackage;
	protected RasterDataContext dataRasterContextSubset;
	//protected ModelOptions modelOptions;
	//protected ScriptProxy scriptProxy;
	
	public BasicRenderEngine()
	{
		
	}
	
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
		if (dataRasterContextSubset != null) {
			dataRasterContextSubset.fillBuffers();
		}
	}
	
	public void unloadData() throws DataSourceException
	{
		if (dataRasterContextSubset != null) {
			dataRasterContextSubset.clearBuffers();
		}
	}
	
	
	public void loadDataSubset(double north, double south, double east, double west) throws DataSourceException
	{
		dataRasterContextSubset = getRasterDataContext().getSubSet(north, south, east, west);
	}

	protected double getRasterDataRaw(double latitude, double longitude) throws DataSourceException
	{
		if (dataRasterContextSubset != null) {
			return dataRasterContextSubset.getData(latitude, longitude);
		} else {
			return getRasterDataContext().getData(latitude, longitude);
		}
	}
	

	protected double getRasterData(double latitude, double longitude) throws DataSourceException, RenderEngineException
	{
		double elevation = 0;
		
		
		try {
			Object before = null;//onGetElevationBefore(latitude, longitude);
			
			if (before instanceof Double) {
				return (Double) before;
			} else if (before instanceof BigDecimal) {
				return ((BigDecimal)before).doubleValue();
			} else if (before instanceof Integer) {
				return ((Integer)before).doubleValue();
			}
			
		} catch (Exception ex) {
			throw new RenderEngineException("Error executing onGetElevationBefore(" + latitude + ", " + longitude + ")", ex);
		}
		
		elevation = getRasterDataRaw(latitude, longitude);

		try {
			Object after = null;//onGetElevationAfter(latitude, longitude, elevation);
			
			if (after instanceof Double) {
				elevation = (Double) after;
			} else if (after instanceof BigDecimal) {
				elevation = ((BigDecimal)after).doubleValue();
			} else if (after instanceof Integer) {
				elevation = ((Integer)after).doubleValue();
			}
			
		} catch (Exception ex) {
			throw new RenderEngineException("Error executing onGetElevationAfter(" + latitude + ", " + longitude + ", " + elevation + ")", ex);
		}
		
		return elevation;
	}
	

	protected void getPoint(double latitude, double longitude, DemPoint point) throws DataSourceException, RenderEngineException
	{
		getPoint(latitude, longitude, 1, point);
	}
	
	
	
	protected void getPoint(double latitude, double longitude, int gridSize, DemPoint point) throws DataSourceException, RenderEngineException
	{
		if (getRasterDataContext() == null) {
			point.setCondition(DemConstants.STAT_NO_DATA_PACKAGE);
			return;
		}

		double latitudeGridSize = gridSize * getRasterDataContext().getLatitudeResolution();
		double longitudeGridSize = gridSize * getRasterDataContext().getLongitudeResolution(); 
		
		double elevation_bl = getRasterData(latitude, longitude);
		if (elevation_bl == DemConstants.ELEV_NO_DATA) {
			point.setCondition(DemConstants.STAT_INVALID_ELEVATION);
			return;
		}
		
		
		double elevation_br = getRasterData(latitude, longitude + longitudeGridSize);
		double elevation_fl = getRasterData(latitude + latitudeGridSize, longitude);
		double elevation_fr = getRasterData(latitude + latitudeGridSize, longitude + longitudeGridSize);

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
		
		if (elevation_fr != DemConstants.ELEV_NO_DATA) {
			point.setFrontRightElevation(elevation_fr);
		} else {
			point.setFrontRightElevation(point.getBackLeftElevation());
		}
		
		
		if (point.getBackLeftElevation() == 0 
			&& point.getBackRightElevation() == 0 
			&& point.getFrontLeftElevation() == 0
			&& point.getFrontRightElevation() == 0) {
			//point.setMiddleElevation(0);
			point.setCondition(DemConstants.STAT_FLAT_SEA_LEVEL);
		} else {
			//point.setMiddleElevation((point.getBackLeftElevation() + point.getBackRightElevation() + point.getFrontLeftElevation() + point.getFrontRightElevation()) / 4.0f);
			point.setCondition(DemConstants.STAT_SUCCESSFUL);
		}
		

		
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

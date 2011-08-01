package us.wthr.jdem846.render;

import java.awt.Color;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.DemPoint;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.color.ColorRegistry;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public abstract class BasicRenderEngine extends RenderEngine
{
	private static Log log = Logging.getLog(Dem3dGenerator.class);
	
	protected DataPackage dataPackage;
	protected ModelOptions modelOptions;
	
	public BasicRenderEngine()
	{
		
	}
	
	public BasicRenderEngine(DataPackage dataPackage, ModelOptions modelOptions)
	{
		this.dataPackage = dataPackage;
		this.modelOptions = modelOptions;
	}

	
	

	protected void getPoint(int row, int column, DemPoint point)
	{
		getPoint(row, column, 1, point);
	}
	
	protected void getPoint(int row, int column, int gridSize, DemPoint point)
	{
		//DemPoint point = new DemPoint();


		if (dataPackage == null) {
			point.setCondition(DemConstants.STAT_NO_DATA_PACKAGE);
			return;
			//return point;
		}

		float elevMax = dataPackage.getMaxElevation();
		float elevMin = dataPackage.getMinElevation();
		float nodata = dataPackage.getNoData();

		float elevation = dataPackage.getElevation(row, column);


		if (elevation == DemConstants.ELEV_NO_DATA) {
			point.setCondition(DemConstants.STAT_INVALID_ELEVATION);
			return;
			//return point;
		}
		
		point.setBackLeftElevation(elevation);


		elevation = dataPackage.getElevation(row, column + gridSize);
		if (elevation >= elevMin && elevation <= elevMax && elevation != DemConstants.ELEV_NO_DATA && elevation != nodata) {
			point.setBackRightElevation(elevation);
		} else {
			point.setBackRightElevation(point.getBackLeftElevation());
		}

		elevation = dataPackage.getElevation(row + gridSize, column);
		if (elevation >= elevMin && elevation <= elevMax && elevation != DemConstants.ELEV_NO_DATA && elevation != nodata) {
			point.setFrontLeftElevation(elevation);
		} else {
			point.setFrontLeftElevation(point.getBackLeftElevation());
		}

		//this->data_package->get(row + 1, column + 1, &elevation);
		elevation = dataPackage.getElevation(row + gridSize, column + gridSize);
		if (elevation >= elevMin && elevation <= elevMax && elevation != DemConstants.ELEV_NO_DATA && elevation != nodata) {
			point.setFrontRightElevation(elevation);
		} else {
			point.setFrontRightElevation(point.getBackLeftElevation());
		}
		
		
		if (point.getBackLeftElevation() == 0 
			&& point.getBackRightElevation() == 0 
			&& point.getFrontLeftElevation() == 0 
			&& point.getFrontRightElevation() == 0) {
			point.setCondition(DemConstants.STAT_FLAT_SEA_LEVEL);
			return;
			//return point;
		}
		

		elevation = (point.getBackLeftElevation() + point.getBackRightElevation() + point.getFrontLeftElevation() + point.getFrontRightElevation()) / 4.0f;
		point.setMiddleElevation(elevation);
		
		point.setCondition(DemConstants.STAT_SUCCESSFUL);
		//return point;
		return;
	}
	

	public Color getDefinedColor(String identifier)
	{
		return ColorRegistry.getInstance(identifier).getColor();
	}
	
	public Color getDefinedColor(int colorConstant)
	{
		Color color = null;
		switch(colorConstant) {
		case DemConstants.BACKGROUND_BLACK:
			color = Color.BLACK;
			break;
		case DemConstants.BACKGROUND_WHITE:
			color = Color.WHITE;
			break;
		case DemConstants.BACKGROUND_BLUE:
			color = new Color(0x2C, 0x49, 0x80, 0xFF);
			break;
		case DemConstants.BACKGROUND_TRANSPARENT:
			color = new Color(0x0, 0x0, 0x0, 0x0);
			break;	
		}
		return color;
	}
	
	public DataPackage getDataPackage()
	{
		return dataPackage;
	}

	public void setDataPackage(DataPackage dataPackage)
	{
		this.dataPackage = dataPackage;
	}

	public ModelOptions getModelOptions()
	{
		return modelOptions;
	}

	public void setModelOptions(ModelOptions modelOptions)
	{
		this.modelOptions = modelOptions;
	}
	
	
	protected static double cos(double d)
	{
		return Math.cos(Math.toRadians(d));
	}
	
	protected static double sin(double d)
	{
		return Math.sin(Math.toRadians(d));
	}
}

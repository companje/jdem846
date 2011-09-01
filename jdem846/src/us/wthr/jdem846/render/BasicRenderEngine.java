package us.wthr.jdem846.render;

import java.awt.Color;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.DemPoint;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.color.ColorRegistry;
import us.wthr.jdem846.input.DataBounds;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.input.SubsetDataPackage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public abstract class BasicRenderEngine extends RenderEngine
{
	private static Log log = Logging.getLog(Dem3dGenerator.class);
	
	protected DataPackage dataPackage;
	protected SubsetDataPackage dataSubset;
	protected ModelOptions modelOptions;
	
	
	public BasicRenderEngine()
	{
		
	}
	
	public BasicRenderEngine(DataPackage dataPackage, ModelOptions modelOptions)
	{
		this.dataPackage = dataPackage;
		this.modelOptions = modelOptions;
	}

	
	protected void loadDataSubset(int fromCol, int fromRow, int width, int height)
	{
		DataBounds tileBounds = new DataBounds(fromCol, fromRow, width, height);
		dataSubset = dataPackage.getDataSubset(tileBounds);
	}

	protected float getElevation(int row, int col)
	{
		if (dataSubset != null) {
			return dataSubset.getElevation(row, col);
		} else {
			return dataPackage.getElevation(row, col);
		}
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

		float elevationMax = dataPackage.getMaxElevation();
		float elevationMin = dataPackage.getMinElevation();

		float elevation_bl = getElevation(row, column);
		float elevation_br = getElevation(row, column + gridSize);
		float elevation_fl = getElevation(row + gridSize, column);
		float elevation_fr = getElevation(row + gridSize, column + gridSize);

		if (elevation_bl == DemConstants.ELEV_NO_DATA) {
			point.setCondition(DemConstants.STAT_INVALID_ELEVATION);
			return;
		}
		
		point.setBackLeftElevation(elevation_bl);

		if (elevation_br >= elevationMin && elevation_br <= elevationMax && elevation_br != DemConstants.ELEV_NO_DATA) {
			point.setBackRightElevation(elevation_br);
		} else {
			point.setBackRightElevation(point.getBackLeftElevation());
		}

		if (elevation_fl >= elevationMin && elevation_fl <= elevationMax && elevation_fl != DemConstants.ELEV_NO_DATA) {
			point.setFrontLeftElevation(elevation_fl);
		} else {
			point.setFrontLeftElevation(point.getBackLeftElevation());
		}

		if (elevation_fr >= elevationMin && elevation_fr <= elevationMax && elevation_fr != DemConstants.ELEV_NO_DATA) {
			point.setFrontRightElevation(elevation_fr);
		} else {
			point.setFrontRightElevation(point.getBackLeftElevation());
		}
		
		
		if (point.getBackLeftElevation() == 0 
			&& point.getBackRightElevation() == 0 
			&& point.getFrontLeftElevation() == 0 
			&& point.getFrontRightElevation() == 0) {
			point.setCondition(DemConstants.STAT_FLAT_SEA_LEVEL);
			return;
		}
		

		point.setMiddleElevation((point.getBackLeftElevation() + point.getBackRightElevation() + point.getFrontLeftElevation() + point.getFrontRightElevation()) / 4.0f);
		
		point.setCondition(DemConstants.STAT_SUCCESSFUL);
		
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

package us.wthr.jdem846.graphics;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelDimensions;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.modelgrid.IModelGrid;
import us.wthr.jdem846.scaling.ElevationScaler;
import us.wthr.jdem846.scripting.ScriptProxy;

public interface View
{

	public double scaleElevation(double elevation);
	
	public void setElevationScaler(ElevationScaler scaler);
	
	public void setModelContext(ModelContext arg);

	public void setGlobalOptionModel(GlobalOptionModel arg);

	public void setModelDimensions(ModelDimensions arg);

	public void setMapProjection(MapProjection arg);

	public void setScript(ScriptProxy arg);

	public void setModelGrid(IModelGrid modelGrid);

	public double radiusTrue();
	public double radius();

	public double horizFieldOfView();

	public double elevationFromSurface();

	public double nearClipDistance();

	public double farClipDistance();

	public double eyeZ();

	public void project(double latitude, double longitude, double elevation, Vector point);
	
	public void getNormal(double latitude, double longitude, Vector normal);
	public void getNormal(double latitude, double longitude, Vector normal, boolean useModelElevation);
	public void getNormal(double latitude, double longitude, double elevation, Vector normal);
	public void getNormal(double latitude, double longitude, double midElev, double nElev, double sElev, double eElev, double wElev, Vector normal);
	public void getNormal(double latitude, double longitude, Vector normal, ElevationFetchCallback elevationFetchCallback);
	
	public boolean getUseFlatNormals();
	public void setUseFlatNormals(boolean useFlatNormals);
}

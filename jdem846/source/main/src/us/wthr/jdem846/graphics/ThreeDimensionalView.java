package us.wthr.jdem846.graphics;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelDimensions;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.ModelGrid;
import us.wthr.jdem846.scripting.ScriptProxy;

public class ThreeDimensionalView extends FlatView implements View
{

	
	public void project(double latitude, double longitude, double elevation, Vector point)
	{
		super.project(latitude, longitude, elevation, point);
		
		double elev = (elevation - ((this.maxElevation + this.minElevation) / 2.0)) / this.resolution;
		point.x = point.x - (this.width / 2.0);
		point.y = point.y - (this.height / 2.0);
		point.z = elev;
	}
	
	
	protected double xWid()
	{
		double dataCols = this.modelDimensions.dataColumns;
		double longitudeResolution = this.modelDimensions.longitudeResolution;
		double modelLongitudeResolution = this.modelDimensions.textureLongitudeResolutionTrue;
		double xWid = dataCols * (longitudeResolution / modelLongitudeResolution);
		return xWid;
	}
	
	protected double yWid()
	{
		double dataRows = this.modelDimensions.dataRows;
		double latitudeResolution = this.modelDimensions.latitudeResolution;
		double modelLatitudeResolution = this.modelDimensions.textureLatitudeResolutionTrue;
		double yWid = dataRows * (latitudeResolution / modelLatitudeResolution);
		return yWid;
	}
	
	
	@Override
	public double radius() 
	{
		double radius = MathExt.sqrt(MathExt.sqr(xWid()) + MathExt.sqr(yWid()));
		return radius;
	}

	@Override
	public double horizFieldOfView() 
	{
		return DemConstants.DEFAULT_HORIZONTAL_FIELD_OF_VIEW;
	}

	@Override
	public double elevationFromSurface() 
	{
		return -1;
	}

	@Override
	public double nearClipDistance() 
	{
		return this.radius();
	}

	@Override
	public double farClipDistance() 
	{
		return -this.radius();
	}

	@Override
	public double eyeZ() 
	{
		double D = (radius() / 2.0) / MathExt.tan(MathExt.radians(horizFieldOfView() / 2.0));
		return D;
	}


}

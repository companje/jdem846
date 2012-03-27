package us.wthr.jdem846.render;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptionNamesEnum;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.render.gfx.Vector;

public class CanvasProjectionGlobe extends CanvasProjection3d
{
	
	private static Log log = Logging.getLog(CanvasProjectionGlobe.class);
	
	
	private double meanRadius; // In meters
	
	public CanvasProjectionGlobe(ModelContext modelContext)
	{
		super(modelContext);
		
		
		Planet planet = PlanetsRegistry.getPlanet(modelContext.getModelOptions().getOption(ModelOptionNamesEnum.PLANET));
		
		
		if (planet != null) {
			meanRadius = planet.getMeanRadius() * 1000;
		} else {
			meanRadius = DemConstants.EARTH_MEAN_RADIUS * 1000;
		}
	}
	
	
	
	@Override
	public void getPoint(double latitude, double longitude, double elevation, MapPoint point) throws MapProjectionException
	{

		double minSideLength = MathExt.min(getWidth(), getHeight()) - 20;
		double radius = (minSideLength / 2.0)  * scaleX;


		//double maxMultiplied = max * elevationMultiple;
		//double ratio = (elevation - min) / (max - min);
		//elevation = min + (maxMultiplied - min) * ratio;

		double radiusAdjusted = (radius / meanRadius) * (meanRadius + elevation);
		

		Spheres.getPoint3D(longitude+180, latitude, radiusAdjusted, pointVector);


		Vector.rotate(0, rotateY, 0, pointVector);
		Vector.rotate(rotateX, 0, 0, pointVector);
		//Vector.translate(shiftX, shiftY, shiftZ, pointVector);
		
		
		double shiftPixelsX = shiftX * radius;
		double shiftPixelsY = shiftY * radius;
		double shiftPixelsZ = shiftZ * radius;
		
		Vector.translate(shiftPixelsX, shiftPixelsY, shiftPixelsZ, pointVector);

		
		projectTo(pointVector);
		
		point.column = -pointVector[0] + (minSideLength/2.0);
		point.row = pointVector[1] + (minSideLength/2.0);
		point.z = pointVector[2];
		
		
	}
	
	public static LatLonResolution calculateOutputResolutions(double outputWidth,
			double outputHeight,
			double dataColumns,
			double dataRows,
			double latitudeResolution,
			double longitudeResolution,
			double scaleFactor)
	{
		double minSideLength = MathExt.min(outputWidth, outputHeight) - 20;
		double radius = (minSideLength / 2.0)  * scaleFactor;
		
		double circumference = 2 * MathExt.PI * radius;
		
		double xdimRatio = (double)circumference / (double)dataColumns;
		double ydimRatio = (double)circumference / (double)dataRows;
		
		
		double outputLongitudeResolution = longitudeResolution / xdimRatio;
		double outputLatitudeResolution = latitudeResolution / ydimRatio;
		
		LatLonResolution latLonRes = new LatLonResolution(outputLatitudeResolution, outputLongitudeResolution);
		return latLonRes;
	}
	
}

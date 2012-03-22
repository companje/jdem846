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
		
		//double radius = MathExt.min(getWidth(), getHeight()) - 20;
		
		double minSideLength = MathExt.min(getWidth(), getHeight()) - 20;
		double radius = (minSideLength / 2.0)  * scaleX;
		//double radius = (getWidth() / 3);
		
		double elev = 0;
		//elevation -= ((max + min) / 2.0);
		//elev = (elevation / resolution) * elevationMultiple;
		double maxMultiplied = max * elevationMultiple;
		double ratio = (elevation - min) / (max - min);
		elevation = min + (maxMultiplied - min) * ratio;
		
		elevation -= ((maxMultiplied + min) / 2.0);
		elev = (elevation / resolution);
		
		
		//double earthMeanRadiusMeters = DemConstants.EARTH_MEAN_RADIUS * 1000;
		
		radius = (radius / meanRadius) * (meanRadius + elev);
		
		//double[] points = new double[3];
		Spheres.getPoint3D(longitude+180, latitude, radius, pointVector);

		//double globeLat = points[2];
		//double globeLon = points[0];
		//double globeElev = points[1];

		//super.getPoint(globeLat, globeLon, globeElev, point);
		
		//pointVector[0] = pointVector[0] - (getWidth() / 2.0);
		//pointVector[1] = pointVector[1];
		//pointVector[2] = pointVector[2] - (getHeight() / 2.0);
		
		
		//cameraVector[2] = radius;			// Camera position
		//eyeVector[2] = (radius*3.0);	// Viewer's position relative to the display surface
		
		Vector.rotate(0, rotateY, 0, pointVector);
		Vector.rotate(rotateX, 0, 0, pointVector);
		//Vector.translate(shiftX, shiftY, shiftZ, pointVector);
		
		
		double shiftPixelsX = shiftX * radius;
		double shiftPixelsY = shiftY * radius;
		double shiftPixelsZ = shiftZ * radius;
		
		Vector.translate(shiftPixelsX, shiftPixelsY, shiftPixelsZ, pointVector);
		//Vector.scale(scaleX, scaleY, scaleZ, pointVector);
		
		
		
		
		projectTo(pointVector);
		
		point.column = -pointVector[0] + (minSideLength/2.0);
		point.row = pointVector[1] + (minSideLength/2.0);
		point.z = pointVector[2];
		
		
	}
	
}

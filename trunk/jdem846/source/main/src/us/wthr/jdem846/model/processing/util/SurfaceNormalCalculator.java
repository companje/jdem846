package us.wthr.jdem846.model.processing.util;

import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.graphics.ElevationFetchCallback;
import us.wthr.jdem846.graphics.FlatNormalsCalculator;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.ViewPerspective;
import us.wthr.jdem846.modelgrid.IModelGrid;

public class SurfaceNormalCalculator extends FlatNormalsCalculator
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(SurfaceNormalCalculator.class);

	public SurfaceNormalCalculator(IModelGrid modelGrid, Planet planet, double latitudeResolution, double longitudeResolution)
	{
		this(modelGrid, planet, latitudeResolution, longitudeResolution, null);
	}

	public SurfaceNormalCalculator(Planet planet, double latitudeResolution, double longitudeResolution, ViewPerspective viewPerspective)
	{
		this(null, planet, latitudeResolution, longitudeResolution, viewPerspective);
	}
	
	public SurfaceNormalCalculator(double latitudeResolution, double longitudeResolution, ViewPerspective viewPerspective)
	{
		this(null, null, latitudeResolution, longitudeResolution, viewPerspective);
	}
	
	public SurfaceNormalCalculator(final IModelGrid modelGrid, Planet planet, double latitudeResolution, double longitudeResolution, ViewPerspective viewPerspective)
	{
		super(planet, latitudeResolution, longitudeResolution, new ElevationFetchCallback() {

			@Override
			public double getElevation(double latitude, double longitude)
			{
				return modelGrid.getElevation(latitude, longitude, true);
			}
			
		});

	}


	

	
}

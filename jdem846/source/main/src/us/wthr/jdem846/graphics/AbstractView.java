package us.wthr.jdem846.graphics;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelDimensions;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.math.Ellipsoid;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.modelgrid.IModelGrid;
import us.wthr.jdem846.scripting.ScriptProxy;

public abstract class AbstractView implements View
{
	protected ModelContext modelContext = null;
	protected GlobalOptionModel globalOptionModel = null;
	protected ModelDimensions modelDimensions = null;
	protected MapProjection mapProjection = null;
	protected ScriptProxy scriptProxy = null;
	protected IModelGrid modelGrid = null;
	protected Planet planet = null;
	protected NormalsCalculator normals = null;
	
	protected double north;
	protected double south;
	protected double east;
	protected double west;

	protected double width;
	protected double height;

	protected double maxElevation = 0.0;
	protected double minElevation = 0.0;
	protected double resolution;
	
	protected Ellipsoid ellipse;
	
	
	public Ellipsoid getEllipsoid()
	{
		if (ellipse == null) {
			double flattening = 1.0;
			
			Planet planet = getPlanet();
			if (planet != null) {
				flattening = planet.getFlattening();
			}
			
			ellipse = new Ellipsoid(radius(), flattening);
		}
		return ellipse;
	}
	

	protected NormalsCalculator getNormalsCalculator()
	{
		if (normals == null) {
			normals = new NormalsCalculator(planet, modelDimensions.getModelLatitudeResolution(), modelDimensions.getModelLongitudeResolution(), new ElevationFetchCallback() {
				@Override
				public double getElevation(double latitude, double longitude)
				{
					return modelGrid.getElevation(latitude, longitude, true);
				}
			});
		}
		return normals;
	}
	
	public void getNormal(double latitude, double longitude, Vector normal)
	{
		getNormal(latitude, longitude, normal, true);
	}
	
	public void getNormal(double latitude, double longitude, Vector normal, boolean useModelElevation)
	{
		if (useModelElevation) {
			getNormalsCalculator().calculateNormalSpherical(latitude, longitude, normal);
		} else {
			getNormalsCalculator().calculateNormalSpherical(latitude, longitude, radiusTrue(), normal);
		}
	}
	
	public void getNormal(double latitude, double longitude, double elevation, Vector normal)
	{
		getNormalsCalculator().calculateNormalSpherical(latitude, longitude, elevation, elevation, elevation, elevation, elevation, normal);
	}
	
	public void getNormal(double latitude, double longitude, double midElev, double nElev, double sElev, double eElev, double wElev, Vector normal)
	{
		getNormalsCalculator().calculateNormalSpherical(latitude, longitude, midElev, nElev, sElev, eElev, wElev, normal);
	}
	
	public void getNormal(double latitude, double longitude, Vector normal, ElevationFetchCallback elevationFetchCallback)
	{
		getNormalsCalculator().calculateNormalSpherical(latitude, longitude, normal, elevationFetchCallback);
	}
	
	
	public void setModelContext(ModelContext arg)
	{
		modelContext = arg;

		if (arg != null) {
			this.maxElevation = arg.getRasterDataContext().getDataMaximumValue();
			this.minElevation = arg.getRasterDataContext().getDataMinimumValue();
		}
	}

	public void setGlobalOptionModel(GlobalOptionModel arg)
	{
		globalOptionModel = arg;
		if (globalOptionModel != null) {
			planet = PlanetsRegistry.getPlanet(globalOptionModel.getPlanet());
			this.north = arg.getNorthLimit();
			this.south = arg.getSouthLimit();
			this.east = arg.getEastLimit();
			this.west = arg.getWestLimit();

			this.width = arg.getWidth();
			this.height = arg.getHeight();
		} else {
			planet = null;
		}

	}

	public void setModelDimensions(ModelDimensions arg)
	{
		modelDimensions = arg;

		if (arg != null) {
			double meanRadius = (planet != null) ? planet.getMeanRadius() : DemConstants.ELEV_NO_DATA;
			this.resolution = arg.getMetersTrueModelResolution(meanRadius) / globalOptionModel.getElevationMultiple();
		}
	}

	public void setMapProjection(MapProjection arg)
	{
		mapProjection = arg;
	}

	public void setScript(ScriptProxy arg)
	{
		scriptProxy = arg;
	}

	public void setModelGrid(IModelGrid arg)
	{
		modelGrid = arg;
	}

	public double getMaxElevation()
	{
		return (!Double.isNaN(maxElevation)) ? maxElevation : 0.0;
	}

	public void setMaxElevation(double maxElevation)
	{
		this.maxElevation = (!Double.isNaN(maxElevation)) ? maxElevation : 0.0;
	}

	public double getMinElevation()
	{
		return (!Double.isNaN(minElevation)) ? minElevation : 0.0;
	}

	public void setMinElevation(double minElevation)
	{
		this.minElevation = (!Double.isNaN(minElevation)) ? minElevation : 0.0;
	}
	
	
	protected Planet getPlanet()
	{
		if (planet == null) {
			planet = PlanetsRegistry.getPlanet("earth");
		}
		return planet;
	}
	

}

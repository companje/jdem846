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
import us.wthr.jdem846.scaling.ElevationScaler;
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
	
	protected ElevationScaler scaler = null;
	protected FlatNormalsCalculator flatNormals = null;
	protected SphericalNormalsCalculator sphericalNormals = null;
	
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
	
	protected double elevScaler = -1;
	
	protected boolean useFlatNormals = false;
	
	@Override
	public void setElevationScaler(ElevationScaler scaler)
	{
		this.scaler = scaler;
	}
	
	protected double getElevationScaler()
	{
		if (elevScaler == -1) {
			elevScaler = radius() / radiusTrue();
		}
		return elevScaler;
	}
	
	public double scaleElevation(double elevation)
	{
		return (elevation * getElevationScaler());
	}
	
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
	

	protected INormalsCalculator getNormalsCalculator()
	{
		
		INormalsCalculator normals = null;
		
		if (useFlatNormals) {
			if (flatNormals == null) {
				flatNormals = new FlatNormalsCalculator(planet
														, modelDimensions.getModelLatitudeResolution()
														, modelDimensions.getModelLongitudeResolution()
														, new ScaledElevationFetchCallback(modelGrid, scaler));
			}
			normals = flatNormals;
		} else {
			if (sphericalNormals == null) {
				sphericalNormals = new SphericalNormalsCalculator(planet
																, modelDimensions.getModelLatitudeResolution()
																, modelDimensions.getModelLongitudeResolution()
																, new ScaledElevationFetchCallback(modelGrid, scaler));
			}
			normals = sphericalNormals;
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
			getNormalsCalculator().calculateNormal(latitude, longitude, normal);
		} else {
			getNormalsCalculator().calculateNormal(latitude, longitude, radius(), normal);
		}
	}
	
	public void getNormal(double latitude, double longitude, double elevation, Vector normal)
	{
		elevation = scaleElevation(elevation);
		getNormalsCalculator().calculateNormal(latitude, longitude, elevation, elevation, elevation, elevation, elevation, normal);
	}
	
	public void getNormal(double latitude, double longitude, double midElev, double nElev, double sElev, double eElev, double wElev, Vector normal)
	{
		getNormalsCalculator().calculateNormal(latitude, longitude, scaleElevation(midElev), scaleElevation(nElev), scaleElevation(sElev), scaleElevation(eElev), scaleElevation(wElev), normal);
	}
	
	public void getNormal(double latitude, double longitude, Vector normal, final ElevationFetchCallback elevationFetchCallback)
	{
		getNormalsCalculator().calculateNormal(latitude, longitude, normal, new ElevationFetchCallback() {

			@Override
			public double getElevation(double latitude, double longitude) {
				return elevationFetchCallback.getElevation(latitude, longitude);
			}
			
		});
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

	@Override
	public boolean getUseFlatNormals()
	{
		return useFlatNormals;
	}

	@Override
	public void setUseFlatNormals(boolean useFlatNormals)
	{
		this.useFlatNormals = useFlatNormals;
	}
	

}

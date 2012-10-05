package us.wthr.jdem846.graphics;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelDimensions;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.ModelGrid;
import us.wthr.jdem846.model.ModelPointGrid;
import us.wthr.jdem846.scripting.ScriptProxy;

public abstract class AbstractView implements View
{
	protected ModelContext modelContext = null;
	protected GlobalOptionModel globalOptionModel = null;
	protected ModelDimensions modelDimensions = null;
	protected MapProjection mapProjection = null;
	protected ScriptProxy scriptProxy = null;
	protected ModelPointGrid modelGrid = null;
	protected Planet planet = null;
	
	protected double north;
	protected double south;
	protected double east;
	protected double west;
	
	protected double width;
	protected double height;
	
	protected double maxElevation;
	protected double minElevation;
	protected double resolution;
	
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
	
	public void setModelGrid(ModelPointGrid arg)
	{
		modelGrid = arg;
	}
	
}

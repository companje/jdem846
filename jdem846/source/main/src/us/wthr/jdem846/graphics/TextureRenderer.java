package us.wthr.jdem846.graphics;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.model.GlobalOptionModel;

public class TextureRenderer
{
	private static Log log = Logging.getLog(TextureRenderer.class);
	
	private Vector pointVector = new Vector();
	private double lastElevation = 0x0;
	
	protected Texture texture;
	protected IRenderer renderer;
	protected View view;
	
	protected double modelLatitudeResolution;
	protected double modelLongitudeResolution;
	
	protected GlobalOptionModel globalOptionModel;
	
	protected ElevationFetchCallback elevationFetchCallback;

	protected Vector normal = new Vector();
	
	public TextureRenderer(Texture texture, IRenderer renderer, View view, double modelLatitudeResolution, double modelLongitudeResolution, GlobalOptionModel globalOptionModel, ElevationFetchCallback elevationFetchCallback)
	{
		this.texture = texture;
		this.renderer = renderer;
		this.view = view;
		this.modelLatitudeResolution = modelLatitudeResolution;
		this.modelLongitudeResolution = modelLongitudeResolution;
		this.globalOptionModel = globalOptionModel;
		
		if (elevationFetchCallback != null) {
			this.elevationFetchCallback = elevationFetchCallback;
		} else {
			this.elevationFetchCallback = new ElevationFetchCallback() {

				@Override
				public double getElevation(double latitude, double longitude) {
					return 0;
				}
				
			};
		}
		Planet planet = PlanetsRegistry.getPlanet(globalOptionModel.getPlanet());
		if (planet == null) {
			planet = PlanetsRegistry.getPlanet("earth");
		}
	}
	
	
	public void render()
	{
		renderer.unbindTexture();
		
		if (mainTextureHeightPixels() > maxRegionHeightPixels() || mainTextureWidthPixels() > maxRegionWidthPixels()) {

			double useNorth = texture.getNorth();
			double useWest = texture.getWest();
			
			double useSouth = useNorth - maxHeightDegrees();
			double useEast = useWest + maxWidthDegrees();
			
			
			while (useSouth >= texture.getSouth() - maxHeightDegrees()) {
				
				while(useEast <= texture.getEast() + maxWidthDegrees()) {
					
					
					renderSubRegion(useNorth, useSouth, useEast, useWest);
					
					useWest = useEast;
					useEast = useWest + maxWidthDegrees();
				}
				
				useWest = texture.getWest();
				useEast = useWest + maxWidthDegrees();
				
				useNorth = useSouth;
				useSouth = useNorth - maxHeightDegrees();
			}


		} else {
			renderSubRegion(texture.getNorth(), texture.getSouth(), texture.getEast(), texture.getWest());
		}
	}
	
	
	
	protected void renderSubRegion(double north, double south, double east, double west)
	{
		
		if (north > globalOptionModel.getNorthLimit()) {
			north = globalOptionModel.getNorthLimit();
		}
		
		if (south < globalOptionModel.getSouthLimit()) {
			south = globalOptionModel.getSouthLimit();
		}
		
		if (east > globalOptionModel.getEastLimit()) {
			east = globalOptionModel.getEastLimit();
		}
		
		if (west < globalOptionModel.getWestLimit()) {
			west = globalOptionModel.getWestLimit();
		}
		
		if (south >= north || east <= west) {
			return;
		}
		
		Texture subTexture = texture.getSubTexture(north + modelLatitudeResolution
												, south - modelLatitudeResolution
												, east + modelLongitudeResolution
												, west - modelLongitudeResolution);
		//north = subTexture.getNorth();
		//south = subTexture.getSouth();
		//east = subTexture.getEast();
		//west = subTexture.getWest();
		
		log.info("Rendering sub region N/S/E/W: " + north + "/" + south + "/" + east + "/" + west);
		
		int subTextureWidth = subTexture.getWidth();
		int subTextureHeight = subTexture.getHeight();
		
		log.info("Subtexture height/width: " + subTextureHeight + "/" + subTextureWidth);
		
		renderer.bindTexture(subTexture);
		

		for (double latitude = north; latitude > south; latitude -= modelLatitudeResolution) {

			this.renderer.begin(PrimitiveModeEnum.TRIANGLE_STRIP);

			for (double longitude = west; longitude < east; longitude += modelLongitudeResolution) {
				renderPointVertex(latitude, longitude, subTexture);
				renderPointVertex(latitude - modelLatitudeResolution, longitude, subTexture);
			}

			this.renderer.end();

		}

		this.renderer.unbindTexture();
	}
	
	
	
	

	protected void renderPointVertex(double latitude, double longitude, Texture subTexture)
	{
		//double elevation = 0;//this.modelGrid.getElevation(latitude, longitude, true);
		
		double elevation = elevationFetchCallback.getElevation(latitude, longitude);
		
		if (elevation == DemConstants.ELEV_NO_DATA) {
			// elevation = this.lastElevation;
			return;
		} else {
			this.lastElevation = elevation;
		}

		view.project(latitude, longitude, elevation, pointVector);

		
		double north = subTexture.getNorth();
		double south = subTexture.getSouth();
		double east = subTexture.getEast();
		double west = subTexture.getWest();
		
		
		double left = (longitude - west) / (east - west);
		double front = (north - latitude) / (north - south);

		if (left < 0.0) {
			left = 0.0;
		}
		
		if (left > 1.0) {
			left = 1.0;
		}
		
		if (front < 0.0) {
			front = 0.0;
		}
		
		if (front > 1.0) {
			front = 1.0;
		}
		
		view.getNormal(latitude, longitude, normal);
		//normalCalculator.calculateNormalSpherical(latitude, longitude, normal);
		//ViewPerspective view = this.globalOptionModel.getViewAngle();
		//Vectors.rotate(view.getRotateX(), view.getRotateY(), view.getRotateZ(), normal, Vectors.ZYX);
		this.renderer.normal(normal);
		
		this.renderer.texCoord(left, front);
		this.renderer.vertex(pointVector);
	}
	
	
	
	
	
	protected int maxSizeShrinkPixelsByAmount()
	{
		return 100;
	}
	
	protected int maxRegionWidthPixels()
	{
		return renderer.getMaximumTextureWidth() - maxSizeShrinkPixelsByAmount();
	}
	
	protected int maxRegionHeightPixels()
	{
		return renderer.getMaximumTextureHeight() - maxSizeShrinkPixelsByAmount();
	}
	
	
	protected int mainTextureWidthPixels()
	{
		return texture.getWidth();
	}
	protected int mainTextureHeightPixels()
	{
		return texture.getHeight();
	}
	
	protected double mainTextureHeightDegrees()
	{
		return texture.getNorth() - texture.getSouth();
	}
	
	protected double mainTextureWidthDegrees()
	{
		return texture.getEast() - texture.getWest();
	}
	
	protected double maxRegionWidthDegrees()
	{
		return ((double)maxRegionWidthPixels() / (double)mainTextureWidthPixels()) * mainTextureWidthDegrees();
	}
	
	protected double maxRegionHeightDegrees()
	{
		return ((double)maxRegionHeightPixels() / (double)mainTextureHeightPixels()) * mainTextureHeightDegrees();
	}
	
	
	protected double maxWidthDegrees()
	{
		double maxWidthDegrees = (mainTextureWidthDegrees() < maxRegionWidthDegrees()) ? mainTextureWidthDegrees() : maxRegionWidthDegrees();
		return MathExt.floor((maxWidthDegrees / modelLongitudeResolution)) * modelLongitudeResolution;
	}
	
	protected double maxHeightDegrees()
	{
		double maxHeightDegrees = (mainTextureHeightDegrees() < maxRegionHeightDegrees()) ? mainTextureHeightDegrees() : maxRegionHeightDegrees();
		return MathExt.floor((maxHeightDegrees / modelLatitudeResolution)) * modelLatitudeResolution;
	}
	
	
	
}

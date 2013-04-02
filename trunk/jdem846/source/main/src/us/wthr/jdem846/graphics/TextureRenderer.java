package us.wthr.jdem846.graphics;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.exception.ScriptingException;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.graphics.TextureMapConfiguration.InterpolationTypeEnum;
import us.wthr.jdem846.graphics.TextureMapConfiguration.TextureWrapTypeEnum;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.scripting.ScriptProxy;

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
	protected ScriptProxy scriptProxy = null;

	protected Vector normal = new Vector();
	
	protected TextureMapConfiguration textureMapConfig = null;
	
	public TextureRenderer(Texture texture, IRenderer renderer, View view, double modelLatitudeResolution, double modelLongitudeResolution, GlobalOptionModel globalOptionModel)
	{
		this(texture, renderer, view, modelLatitudeResolution, modelLongitudeResolution, globalOptionModel, null, null, null);
	}
	
	public TextureRenderer(Texture texture, IRenderer renderer, View view, double modelLatitudeResolution, double modelLongitudeResolution, GlobalOptionModel globalOptionModel, TextureMapConfiguration textureMapConfig)
	{
		this(texture, renderer, view, modelLatitudeResolution, modelLongitudeResolution, globalOptionModel, null, textureMapConfig, null);
	}
	
	public TextureRenderer(Texture texture, IRenderer renderer, View view, double modelLatitudeResolution, double modelLongitudeResolution, GlobalOptionModel globalOptionModel, ElevationFetchCallback elevationFetchCallback)
	{
		this(texture, renderer, view, modelLatitudeResolution, modelLongitudeResolution, globalOptionModel, null, null, elevationFetchCallback);
	}
	
	public TextureRenderer(Texture texture, IRenderer renderer, View view, double modelLatitudeResolution, double modelLongitudeResolution, GlobalOptionModel globalOptionModel, TextureMapConfiguration textureMapConfig, ElevationFetchCallback elevationFetchCallback)
	{
		this(texture, renderer, view, modelLatitudeResolution, modelLongitudeResolution, globalOptionModel, null, textureMapConfig, elevationFetchCallback);
	}
	
	public TextureRenderer(Texture texture, IRenderer renderer, View view, double modelLatitudeResolution, double modelLongitudeResolution, GlobalOptionModel globalOptionModel, ScriptProxy scriptProxy, ElevationFetchCallback elevationFetchCallback)
	{
		this(texture, renderer, view, modelLatitudeResolution, modelLongitudeResolution, globalOptionModel, scriptProxy, null, elevationFetchCallback);
	}
	
	public TextureRenderer(Texture texture, IRenderer renderer, View view, double modelLatitudeResolution, double modelLongitudeResolution, GlobalOptionModel globalOptionModel, ScriptProxy scriptProxy, TextureMapConfiguration textureMapConfig, ElevationFetchCallback elevationFetchCallback)
	{
		this.texture = texture;
		this.renderer = renderer;
		this.view = view;
		this.modelLatitudeResolution = modelLatitudeResolution;
		this.modelLongitudeResolution = modelLongitudeResolution;
		this.globalOptionModel = globalOptionModel;
		this.scriptProxy = scriptProxy;
		
		this.textureMapConfig = (textureMapConfig != null) ? textureMapConfig : new TextureMapConfiguration(false, InterpolationTypeEnum.LINEAR, TextureWrapTypeEnum.REPEAT);
		
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

			double startNorth = (texture.getNorth() <= globalOptionModel.getNorthLimit()) ? texture.getNorth() : globalOptionModel.getNorthLimit();
			double startWest = (texture.getWest() >= globalOptionModel.getWestLimit()) ? texture.getWest() : globalOptionModel.getWestLimit();
			double useNorth = startNorth;
			double useWest = startWest;
			
			double useSouth = useNorth - maxHeightDegrees();
			double useEast = useWest + maxWidthDegrees();
			
			double southLimit = (texture.getSouth() > globalOptionModel.getSouthLimit()) ? texture.getSouth() : globalOptionModel.getSouthLimit();
			double eastLimit = (texture.getEast() < globalOptionModel.getEastLimit()) ? texture.getEast() : globalOptionModel.getEastLimit();
			
			
			while (useSouth >= southLimit - maxHeightDegrees()) {
				
				while(useEast <= eastLimit + maxWidthDegrees()) {
					
					double regionEast = renderSubRegion(useNorth, useSouth, useEast + modelLongitudeResolution, useWest - modelLongitudeResolution);
					
					if (regionEast == DemConstants.ELEV_NO_DATA || regionEast >= texture.getEast() || regionEast >= globalOptionModel.getEastLimit()) {
						break;
					}
					useWest = regionEast;
					useEast = useWest + maxWidthDegrees();
				}
				
				useWest = startWest;
				useEast = useWest + maxWidthDegrees();
				
				useNorth = useSouth;
				useSouth = useNorth - maxHeightDegrees();
			}


		} else {
			renderTexture(texture, globalOptionModel.getNorthLimit(), globalOptionModel.getSouthLimit(), globalOptionModel.getEastLimit(), globalOptionModel.getWestLimit());
			//renderTexture(texture, texture.getNorth(), texture.getSouth(), texture.getEast(), texture.getWest());
			//renderSubRegion(texture.getNorth(), texture.getSouth(), texture.getEast(), texture.getWest());
		}
	}
	
	
	
	protected double renderSubRegion(double north, double south, double east, double west)
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
			return DemConstants.ELEV_NO_DATA;
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
		
		if (subTextureHeight <= 0 || subTextureWidth <= 0) {
			log.warn("Cannot render subtexture with height/width of " + subTextureHeight + "/" + subTextureWidth);
			return DemConstants.ELEV_NO_DATA;
		}
		
		log.info("Subtexture height/width: " + subTextureHeight + "/" + subTextureWidth);
		
		
		renderTexture(subTexture, north, south, east, west);
		
		
		return east;
	}
	
	
	
	protected void renderTexture(Texture texture, double north, double south, double east, double west)
	{
		renderer.bindTexture(texture, textureMapConfig);
		
		try {
			for (double latitude = north; latitude > south; latitude -= modelLatitudeResolution) {
	
				this.renderer.begin(PrimitiveModeEnum.TRIANGLE_STRIP);
	
				for (double longitude = west; longitude <= east; longitude += modelLongitudeResolution) {
	
					renderPointVertex(latitude, longitude, texture);
					renderPointVertex(latitude - modelLatitudeResolution, longitude, texture);
				}

				this.renderer.end();
	
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		this.renderer.unbindTexture();
	}
	
	

	protected void renderPointVertex(double latitude, double longitude, Texture subTexture)
	{
		double elevation = elevationFetchCallback.getElevation(latitude, longitude);
		
		if (elevation == DemConstants.ELEV_NO_DATA) {
			// elevation = this.lastElevation;
			return;
		} else {
			this.lastElevation = elevation;
		}
		
		
		if (scriptProxy != null) {
			try {
				scriptProxy.onBeforeVertex(latitude, longitude, elevation, renderer, view);
			} catch (ScriptingException ex) {
				// TODO Throw!!
				ex.printStackTrace();
			}
		}

		view.project(latitude, longitude, elevation, pointVector);

		
		double north = subTexture.getNorth();
		double south = subTexture.getSouth();
		double east = subTexture.getEast();
		double west = subTexture.getWest();
		
		
		double left = (longitude - west) / (east - west);
		double front = (north - latitude) / (north - south);

		if (left < 0.0) {
			//left = left + 1.0;
			//return;
		}
		
		if (left > 1.0) {
			//left = left - 1.0;
			//return;
		}
		
		if (front < 0.0) {
			//front = front + 1.0;
			//return;
		}
		
		if (front > 1.0) {
			//front = front - 1.0;
			//return;
		}
		
		view.getNormal(latitude, longitude, normal, elevationFetchCallback);
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

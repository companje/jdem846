package us.wthr.jdem846.graphics;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelDimensions;
import us.wthr.jdem846.exception.GraphicsRenderException;
import us.wthr.jdem846.exception.ScriptingException;
import us.wthr.jdem846.geom.Vertex;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.graphics.framebuffer.FrameBufferModeEnum;
import us.wthr.jdem846.graphics.opengl.OpenGlRenderer;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.RgbaColor;
import us.wthr.jdem846.model.ViewPerspective;
import us.wthr.jdem846.modelgrid.IModelGrid;
import us.wthr.jdem846.scripting.ScriptProxy;
import us.wthr.jdem846.util.ColorUtil;

public class RenderProcess
{
	private static Log log = Logging.getLog(RenderProcess.class);

	protected ModelContext modelContext = null;
	protected GlobalOptionModel globalOptionModel = null;
	protected ModelDimensions modelDimensions = null;
	protected MapProjection mapProjection = null;
	protected ScriptProxy scriptProxy = null;
	protected IModelGrid modelGrid = null;
	protected Planet planet = null;

	protected ImageCapture image = null;

	protected Vertex vtx = new Vertex();
	protected MapPoint point;
	protected double lastElevation = 0x0;

	protected View modelView = null;
	protected IRenderer renderer = null;

	protected Boolean renderCompleted = false;
	
	protected Texture modelTexture = null;
	protected Texture boundTexture = null;
	
	public RenderProcess(View modelView)
	{
		this.modelView = modelView;
	}

	public void prepare()
	{
		this.lastElevation = this.modelContext.getRasterDataContext().getDataMaximumValue();
		
		String renderEngine = JDem846Properties.getProperty("us.wthr.jdem846.rendering.renderEngine"); 
		if (renderEngine == null) {
			renderEngine = "software";
		}
		
		if (renderEngine.equalsIgnoreCase("software")) {
			log.info("Initializing software renderer");
			this.renderer = new GraphicsRenderer();
		} else if (renderEngine.equalsIgnoreCase("opengl")) {
			log.info("Initializing OpenGL renderer");
			this.renderer = new OpenGlRenderer();
		} else {
			// Throw!!
		}
		
		//
		//this.frameBufferController = new ManagedConcurrentFrameBufferController(globalOptionModel.getWidth(), globalOptionModel.getHeight(), numberOfThreads);
		
		//if (this.frameBuffer != null) {
		//	this.renderer.setFrameBuffer(frameBuffer);
		//}
		this.renderer.initialize(globalOptionModel.getWidth(), globalOptionModel.getHeight());
		
		int width = this.modelGrid.getWidth();
		int height = this.modelGrid.getHeight();
		
		double north = this.modelGrid.getNorth();
		double south = this.modelGrid.getSouth();
		double east = this.modelGrid.getEast();
		double west = this.modelGrid.getWest();

		int[] modelTextureBuffer = this.modelGrid.getModelTexture();
		modelTexture = new Texture(width, height, north, south, east, west, modelTextureBuffer);
	}

	public void dispose()
	{
		this.image = null;
		this.renderer = null;
	}

	protected void setRenderCompleted(boolean b)
	{
		synchronized (renderCompleted) {
			renderCompleted = b;
		}
	}

	protected boolean isRenderCompleted()
	{
		synchronized (renderCompleted) {
			return renderCompleted;
		}
	}

	public void run() throws GraphicsRenderException
	{
		try {
			render();
		} catch (GraphicsRenderException ex) {
			throw new GraphicsRenderException("Error rendering model: " + ex.getMessage(), ex, ex.getRenderCode());
		}

		capture();
		setRenderCompleted(true);
	}

	protected void bindTexture(Texture texture)
	{
		this.renderer.unbindTexture();
		this.renderer.bindTexture(texture);
		this.boundTexture = texture;
	}

	protected void setPerspective() throws GraphicsRenderException
	{
		int width = this.globalOptionModel.getWidth();
		int height = this.globalOptionModel.getHeight();

		double horizFieldOfView = this.modelView.horizFieldOfView();

		FrameBufferModeEnum bufferMode = FrameBufferModeEnum.CONCURRENT_PARTIAL_FRAME_BUFFER;// FrameBufferModeEnum.getBufferModeFromIdentifier(this.globalOptionModel.getFrameBufferMode());
		this.renderer.viewPort(0, 0, width, height, bufferMode);

		this.renderer.matrixMode(MatrixModeEnum.MODELVIEW);
		this.renderer.loadIdentity();

		this.renderer.matrixMode(MatrixModeEnum.PROJECTION);
		this.renderer.loadIdentity();

		double aspect = (double) width / (double) height;
		double near = this.modelView.nearClipDistance();
		double far = this.modelView.farClipDistance();
		double eyeZ = this.modelView.eyeZ();

		double radius = this.modelView.radius();// +
												// this.modelContext.getRasterDataContext().getDataMaximumValue();

		if (PerspectiveTypeEnum.getPerspectiveTypeFromIdentifier(this.globalOptionModel.getPerspectiveType()) == PerspectiveTypeEnum.ORTHOGRAPHIC) {
			this.renderer.ortho(-radius // Left
					, radius // Right
					, -radius // Bottom
					, radius // Top
					, -radius // Near
					, radius); // Far

			this.renderer.matrixMode(MatrixModeEnum.MODELVIEW);
			this.renderer.loadIdentity();
		} else if (PerspectiveTypeEnum.getPerspectiveTypeFromIdentifier(this.globalOptionModel.getPerspectiveType()) == PerspectiveTypeEnum.PERSPECTIVE) {

			this.renderer.perspective(horizFieldOfView, aspect, near, far);

			this.renderer.matrixMode(MatrixModeEnum.MODELVIEW);

			this.renderer.lookAt(0 // Eye X
					, 0 // Eye Y
					, eyeZ // Eye Z
					, 0 // Center X
					, 0 // Center Y
					, 0 // Center Z
					, 0 // Up X
					, 1 // Up Y
					, 0); // Up Z

			// this.renderer.loadIdentity();
		}

		/*
		
		*/

	}

	protected void render() throws GraphicsRenderException
	{
		this.setPerspective();

		this.renderer.pushMatrix();

		double north = this.globalOptionModel.getNorthLimit();
		double south = this.globalOptionModel.getSouthLimit();
		double east = this.globalOptionModel.getEastLimit();
		double west = this.globalOptionModel.getWestLimit();

		RgbaColor background = this.globalOptionModel.getBackgroundColor();
		if (background != null) {
			this.renderer.clear(background.getRgba());
		} else {
			this.renderer.clear(ColorUtil.BLACK);
		}

		ViewPerspective view = this.globalOptionModel.getViewAngle();

		double radius = this.modelView.radius();

		if (view != null) {
			this.renderer.rotate(view.getRotateX(), AxisEnum.X_AXIS);
			this.renderer.rotate(view.getRotateY(), AxisEnum.Y_AXIS);
			this.renderer.rotate(view.getRotateZ(), AxisEnum.Z_AXIS);

			this.renderer.translate(view.getShiftX() * radius, view.getShiftY() * radius, view.getShiftZ() * radius);


		}

		this.renderer.pushMatrix();
		if (this.globalOptionModel.getUseScripting() && this.scriptProxy != null) {
			try {
				this.scriptProxy.preRender(this.renderer, this.modelView);
			} catch (ScriptingException ex) {
				log.warn("Exception thrown in user script: " + ex.getMessage(), ex);
			}
		}
		this.renderer.popMatrix();

		
		//int[] c = { 0xFF, 0xFF, 0xFF, 0xFF };
		//this.renderer.color(ColorUtil.rgbaToInt(c));
		
		int maxSizeShrinkPixelsByAmount = 100;
		
		int maxRegionWidthPixels = 500;//renderer.getMaximumTextureWidth() - maxSizeShrinkPixelsByAmount;
		int maxRegionHeightPixels = 500;//renderer.getMaximumTextureHeight() - maxSizeShrinkPixelsByAmount;
		
		
		
		int mainTextureWidthPixels = modelTexture.getWidth();
		int mainTextureHeightPixels = modelTexture.getHeight();
		
		double mainTextureHeightDegrees = modelTexture.getNorth() - modelTexture.getSouth();
		double mainTextureWidthDegrees = modelTexture.getEast() - modelTexture.getWest();
		
		double maxRegionWidthDegrees = ((double)maxRegionWidthPixels / (double)mainTextureWidthPixels) * mainTextureWidthDegrees;
		double maxRegionHeightDegrees = ((double)maxRegionHeightPixels / (double)mainTextureHeightPixels) * mainTextureHeightDegrees;
		
		log.info("Maximum Region Height/Width (Pixels): " + maxRegionHeightPixels + "/" + maxRegionWidthPixels);
		log.info("Main Texture Height/Width (Pixels): " + mainTextureHeightPixels + "/" + mainTextureWidthPixels);
		
		double latitudeResolution = this.modelDimensions.modelLatitudeResolution;
		double longitudeResolution = this.modelDimensions.modelLongitudeResolution;
		
		if (mainTextureHeightPixels > maxRegionHeightPixels || mainTextureWidthPixels > maxRegionWidthPixels) {
			
			double maxWidthDegrees = (mainTextureWidthDegrees < maxRegionWidthDegrees) ? mainTextureWidthDegrees : maxRegionWidthDegrees;
			double maxHeightDegrees = (mainTextureHeightDegrees < maxRegionHeightDegrees) ? mainTextureHeightDegrees : maxRegionHeightDegrees;
			
			
			maxWidthDegrees = MathExt.floor((maxWidthDegrees / longitudeResolution)) * longitudeResolution;
			maxHeightDegrees = MathExt.floor((maxHeightDegrees / latitudeResolution)) * latitudeResolution;
			
			double useNorth = north;
			double useWest = west;
			
			double useSouth = useNorth - maxHeightDegrees;
			double useEast = useWest + maxWidthDegrees;
			
			
			while (useSouth > south - maxHeightDegrees) {
				
				while(useEast < east + maxWidthDegrees) {
					
					log.info("Rendering sub region N/S/E/W: " + useNorth + "/" + useSouth + "/" + useEast + "/" + useWest);
					renderSubRegion(useNorth + latitudeResolution, useSouth - latitudeResolution, useEast + longitudeResolution, useWest - longitudeResolution);
					
					useWest = useEast;
					useEast = useWest + maxWidthDegrees;
				}
				
				useWest = west;
				useEast = useWest + maxWidthDegrees;
				
				useNorth = useSouth;
				useSouth = useNorth - maxHeightDegrees;
			}

			
			
			
			//renderSubRegion(north, useSouth, useEast, west);
			//renderSubRegion(north, south, east, west);
		} else {
			renderSubRegion(north, south, east, west);
		}

		this.renderer.pushMatrix();
		if (this.globalOptionModel.getUseScripting() && this.scriptProxy != null) {
			try {
				this.scriptProxy.postRender(this.renderer, this.modelView);
			} catch (ScriptingException ex) {
				log.warn("Exception thrown in user script: " + ex.getMessage(), ex);
			}
		}
		this.renderer.popMatrix();

		this.renderer.popMatrix();
		
		this.renderer.finish();

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
		
		Texture subTexture = modelTexture.getSubTexture(north, south, east, west);

		int subTextureWidth = subTexture.getWidth();
		int subTextureHeight = subTexture.getHeight();
		
		log.info("Subtexture height/width: " + subTextureHeight + "/" + subTextureWidth);
		
		this.bindTexture(subTexture);
		
		double latitudeResolution = this.modelDimensions.modelLatitudeResolution;
		double longitudeResolution = this.modelDimensions.modelLongitudeResolution;
		
		for (double latitude = north; latitude > south; latitude -= latitudeResolution) {

			this.renderer.begin(PrimitiveModeEnum.TRIANGLE_STRIP);

			for (double longitude = west; longitude <= east; longitude += longitudeResolution) {
				renderPointVertex(latitude, longitude);//, north, south, east, west);
				renderPointVertex(latitude - latitudeResolution, longitude);//, north, south, east, west);
			}

			this.renderer.end();

		}

		this.renderer.unbindTexture();
	}
	
	private Vector pointVector = new Vector();

	protected void renderPointVertex(double latitude, double longitude)//, double north, double south, double east, double west)
	{
		double elevation = this.modelGrid.getElevation(latitude, longitude, true);
		if (elevation == DemConstants.ELEV_NO_DATA) {
			// elevation = this.lastElevation;
			return;
		} else {
			this.lastElevation = elevation;
		}

		this.modelView.project(latitude, longitude, elevation, pointVector);

		//double north = this.globalOptionModel.getNorthLimit();
		//double south = this.globalOptionModel.getSouthLimit();
		//double east = this.globalOptionModel.getEastLimit();
		//double west = this.globalOptionModel.getWestLimit();

		double north = boundTexture.getNorth();
		double south = boundTexture.getSouth();
		double east = boundTexture.getEast();
		double west = boundTexture.getWest();
		
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
		
		
		this.renderer.texCoord(left, front);

		// pointVector.z = -pointVector.z;
		this.renderer.vertex(pointVector);

	}

	public ImageCapture capture()
	{
		if (this.image == null) {
			this.image = this.renderer.getFrameBuffer().captureImage();
		}

		return this.image;
	}

	public void setModelContext(ModelContext arg)
	{
		modelContext = arg;
	}

	public void setGlobalOptionModel(GlobalOptionModel arg)
	{
		globalOptionModel = arg;
		if (globalOptionModel != null) {
			planet = PlanetsRegistry.getPlanet(globalOptionModel.getPlanet());
		} else {
			planet = null;
		}
	}

	public void setModelDimensions(ModelDimensions arg)
	{
		modelDimensions = arg;
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
}

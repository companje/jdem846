package us.wthr.jdem846.graphics;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelDimensions;
import us.wthr.jdem846.exception.GraphicsRenderException;
import us.wthr.jdem846.exception.ScriptingException;
import us.wthr.jdem846.geom.Vertex;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.graphics.TextureMapConfiguration.InterpolationTypeEnum;
import us.wthr.jdem846.graphics.TextureMapConfiguration.TextureWrapTypeEnum;
import us.wthr.jdem846.graphics.framebuffer.FrameBufferModeEnum;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.RgbaColor;
import us.wthr.jdem846.model.ViewerPosition;
import us.wthr.jdem846.model.processing.shading.RenderLightingOptionModel;
import us.wthr.jdem846.model.processing.util.SunlightPositioning;
import us.wthr.jdem846.modelgrid.IModelGrid;
import us.wthr.jdem846.scaling.ElevationScaler;
import us.wthr.jdem846.scripting.ScriptProxy;

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
	protected ElevationScaler scaler = null;
	
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
		
		this.renderer = RenderEngineFactory.createRenderer(modelContext.isPreview());
		this.renderer.initialize(globalOptionModel.getWidth(), globalOptionModel.getHeight());
		
		
		
		
		this.modelTexture = TextureFactory.createTexture(modelGrid);
		
	}

	public void dispose()
	{
		this.image = null;
		
		this.renderer.dispose();
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
		
		ViewerPosition viewer = globalOptionModel.getViewerPosition();
		
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
					, near // Near
					, far); // Far

			this.renderer.matrixMode(MatrixModeEnum.MODELVIEW);
			this.renderer.loadIdentity();
		} else if (PerspectiveTypeEnum.getPerspectiveTypeFromIdentifier(this.globalOptionModel.getPerspectiveType()) == PerspectiveTypeEnum.PERSPECTIVE) {
			//this.renderer.perspective(horizFieldOfView, aspect, 0.1, far);
			this.renderer.perspective(horizFieldOfView, aspect, 0.1, 100000.0);
			this.renderer.matrixMode(MatrixModeEnum.MODELVIEW);
			
			this.renderer.lookAt(viewer.getPosition().x * eyeZ
								, viewer.getPosition().y * eyeZ
								, viewer.getPosition().z * eyeZ
								, viewer.getFocalPoint().x  * eyeZ
								, viewer.getFocalPoint().y * eyeZ
								, viewer.getFocalPoint().z  * eyeZ
								, 0
								, 1
								, 0);
			
			/*
			this.renderer.lookAt(0 // Eye X
					, 0 // Eye Y
					, eyeZ // Eye Z
					, 0 // Center X
					, 0 // Center Y
					, 0 // Center Z
					, 0 // Up X
					, 1 // Up Y
					, 0); // Up Z
			*/
			// this.renderer.loadIdentity();
		}

		/*
		
		*/

	}

	
	protected Vector getEyePosition()
	{
		Vector v = new Vector(0, 0, 1);
		
		return v;
	}
	
	protected void render() throws GraphicsRenderException
	{
		
		
		this.setPerspective();

		this.renderer.pushMatrix();

		RgbaColor background = this.globalOptionModel.getBackgroundColor();
		if (background != null) {
			this.renderer.clear(background.toColor());
		} else {
			this.renderer.clear(Colors.BLACK);
		}
		RenderLightingOptionModel lightingOptionModel = (RenderLightingOptionModel) this.modelContext.getModelProcessManifest().getOptionModelByProcessId("us.wthr.jdem846.model.processing.lighting.RenderLightingProcessor");
		this.modelView.setUseFlatNormals(lightingOptionModel.getFlatLighting());
		
		
		if (lightingOptionModel != null && lightingOptionModel.isLightingEnabled()) {
			long lightOnTime = lightingOptionModel.getSunlightTime().getTime();
			long lightOnDate = lightingOptionModel.getSunlightDate().getDate();
			lightOnDate += lightOnTime;

			Vector sunsource = new Vector();
			SunlightPositioning sunlightPosition = new SunlightPositioning(lightOnDate);
			sunlightPosition.getLightPosition(sunsource);

			renderer.setLighting(sunsource
								, lightingOptionModel.getEmmisive()
								, lightingOptionModel.getAmbient()
								, lightingOptionModel.getDiffuse()
								, lightingOptionModel.getSpecular()
								, lightingOptionModel.getSpotExponent());
			
			renderer.setMaterial(lightingOptionModel.getEmmisive()
								, lightingOptionModel.getAmbient()
								, lightingOptionModel.getDiffuse()
								, lightingOptionModel.getSpecular()
								, lightingOptionModel.getSpotExponent());
		} else {
			renderer.disableLighting();
			renderer.disableMaterial();
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

		TextureMapConfiguration textureMapConfig = new TextureMapConfiguration(true, InterpolationTypeEnum.LINEAR, TextureWrapTypeEnum.CLAMP_TO_EDGE);
		TextureRenderer textureRenderer = new TextureRenderer(modelTexture
															, renderer
															, modelView
															, modelDimensions.modelLatitudeResolution
															, modelDimensions.modelLongitudeResolution
															, globalOptionModel
															, (globalOptionModel.getUseScripting()) ? scriptProxy : null
															, textureMapConfig
															, new ScaledElevationFetchCallback(modelGrid, scaler));
		textureRenderer.render();
		
		
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
	
	public ImageCapture capture()
	{
		if (this.image == null) {
			this.image = this.renderer.captureImage();
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

	public ElevationScaler getScaler()
	{
		return scaler;
	}

	public void setScaler(ElevationScaler scaler)
	{
		this.scaler = scaler;
	}
	
	
}

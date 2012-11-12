package us.wthr.jdem846.graphics;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelDimensions;
import us.wthr.jdem846.canvas.util.ColorUtil;
import us.wthr.jdem846.exception.GraphicsRenderException;
import us.wthr.jdem846.exception.ScriptingException;
import us.wthr.jdem846.geom.Vertex;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.graphics.framebuffer.FrameBufferModeEnum;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.ModelPointGrid;
import us.wthr.jdem846.model.RgbaColor;
import us.wthr.jdem846.model.ViewPerspective;
import us.wthr.jdem846.scripting.ScriptProxy;

public class RenderProcess
{
	private static Log log = Logging.getLog(RenderProcess.class);
	
	protected ModelContext modelContext = null;
	protected GlobalOptionModel globalOptionModel = null;
	protected ModelDimensions modelDimensions = null;
	protected MapProjection mapProjection = null;
	protected ScriptProxy scriptProxy = null;
	protected ModelPointGrid modelGrid = null;
	protected Planet planet = null;
	
	protected ImageCapture image = null;
	
	protected Vertex vtx = new Vertex();
	protected MapPoint point;
	protected double lastElevation = 0x0;
	
	protected View modelView = null;
	protected GraphicsRenderer renderer = null;
	
	protected Boolean renderCompleted = false;
	
	public RenderProcess(View modelView)
	{
		this.modelView = modelView;
	}
	
	public void prepare()
	{
		this.lastElevation = this.modelContext.getRasterDataContext().getDataMaximumValue();
		this.renderer = new GraphicsRenderer();
	}
	
	public void dispose()
	{
		this.image = null;
		this.renderer = null;
	}
	
	
	protected void setRenderCompleted(boolean b)
	{
		synchronized(renderCompleted) {
			renderCompleted = b;
		}
	}

	protected boolean isRenderCompleted()
	{
		synchronized(renderCompleted) {
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
	
	protected void bindTexture()
	{
		this.renderer.unbindTexture();
		
		int width = this.modelGrid.getWidth();
		int height = this.modelGrid.getHeight();
		
		int[] modelTexture = this.modelGrid.getModelTexture();
		this.renderer.bindTexture(modelTexture, width, height);
		
		
	}
	

	
	
	protected void setPerspective() throws GraphicsRenderException
	{
		int width = this.globalOptionModel.getWidth();
		int height = this.globalOptionModel.getHeight();
		
		double horizFieldOfView = this.modelView.horizFieldOfView();
		
		
		FrameBufferModeEnum bufferMode = FrameBufferModeEnum.getBufferModeFromIdentifier(this.globalOptionModel.getFrameBufferMode());
		this.renderer.viewPort(0, 0, width, height, bufferMode);
		
		this.renderer.matrixMode(MatrixModeEnum.MODELVIEW);
		this.renderer.loadIdentity();
		
		this.renderer.matrixMode(MatrixModeEnum.PROJECTION);
		this.renderer.loadIdentity();
		
		double aspect = (double)width / (double)height;
		double near = this.modelView.nearClipDistance();
		double far = this.modelView.farClipDistance();
		double eyeZ = this.modelView.eyeZ();
		
		double radius = this.modelView.radius();// + this.modelContext.getRasterDataContext().getDataMaximumValue();
		
		
		
		if (PerspectiveTypeEnum.getPerspectiveTypeFromIdentifier(this.globalOptionModel.getPerspectiveType()) == PerspectiveTypeEnum.ORTHOGRAPHIC) {
			this.renderer.ortho(-radius		// Left
					, radius	// Right
					, -radius	// Bottom
					, radius	// Top
					, -radius	// Near
					, radius);	// Far
			
			this.renderer.matrixMode(MatrixModeEnum.MODELVIEW);
			this.renderer.loadIdentity();
		} else if (PerspectiveTypeEnum.getPerspectiveTypeFromIdentifier(this.globalOptionModel.getPerspectiveType()) == PerspectiveTypeEnum.PERSPECTIVE) {
			
			this.renderer.perspective(horizFieldOfView, aspect, near, far);
			
			
			
		
			this.renderer.matrixMode(MatrixModeEnum.MODELVIEW);
			
			this.renderer.lookAt(0							// Eye X
					, 0							// Eye Y
					, eyeZ						// Eye Z
					, 0							// Center X
					, 0							// Center Y
					, 0							// Center Z
					, 0							// Up X
					, 1							// Up Y
					, 0);						// Up Z
					
			
			
			//this.renderer.loadIdentity();
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
			this.renderer.clear(0x0);
		}
		
		ViewPerspective view = this.globalOptionModel.getViewAngle();
		
		
		double radius = this.modelView.radius();
		
		if (view != null) {
			
			
			
			
			
			//this.renderer.scale(view.getZoom(), view.getZoom(), view.getZoom());
			this.renderer.rotate(view.getRotateX(), AxisEnum.X_AXIS);
			this.renderer.rotate(-view.getRotateY(), AxisEnum.Y_AXIS);
			this.renderer.rotate(view.getRotateZ(), AxisEnum.Z_AXIS);
			
			this.renderer.translate(view.getShiftX() * radius
									, view.getShiftY() * radius
									, view.getShiftZ() * radius);
			
			//double zoom = view.getZoom();
			//double zoomZ = zoom * (DemConstants.DEFAULT_EYE_DISTANCE_FROM_EARTH_CENTER * (DemConstants.DEFAULT_GLOBAL_RADIUS / modelView.radiusTrue()));
			//log.info("Zoom: " + zoom + ", Z: " + zoomZ);
			//this.renderer.translate(0.0, 0.0, zoomZ);
			
		}
		
		
		
		//double eyeZ = this.modelView.eyeZ();
		//this.renderer.translate(0.0, 0.0, -eyeZ);
		//this.renderer.translate(0, 0, (view.getZoom() * radius));

		this.renderer.pushMatrix();
		if (this.globalOptionModel.getUseScripting() && this.scriptProxy != null) {
			try {
				this.scriptProxy.preRender(this.renderer, this.modelView);
			} catch (ScriptingException ex) {
				log.warn("Exception thrown in user script: " + ex.getMessage(), ex);
			}
		}
		this.renderer.popMatrix();
		
		this.bindTexture();
		int[] c = {0, 0, 0, 0xFF};
		this.renderer.color(ColorUtil.rgbaToInt(c));
		
		double latitudeResolution = this.modelDimensions.modelLatitudeResolution;
		double longitudeResolution = this.modelDimensions.modelLongitudeResolution;
		for (double latitude = north; latitude > south; latitude -= latitudeResolution) {
			
			this.renderer.begin(PrimitiveModeEnum.TRIANGLE_STRIP);
			
			for (double longitude = west; longitude <= east; longitude += longitudeResolution) {
				renderPointVertex(latitude, longitude);
				renderPointVertex(latitude - latitudeResolution, longitude);
			}
			
			this.renderer.end();
			
		}
		
		this.renderer.unbindTexture();
		
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
		
		
		
		
	}

	private Vector pointVector = new Vector();
	protected void renderPointVertex(double latitude, double longitude)
	{
		double elevation = this.modelGrid.getElevation(latitude, longitude, true);
		if (elevation == DemConstants.ELEV_NO_DATA) {
			//elevation = this.lastElevation;
			return;
		} else {
			this.lastElevation = elevation;
		}
		
		this.modelView.project(latitude, longitude, elevation, pointVector);
		
		double north = this.globalOptionModel.getNorthLimit();
		double south = this.globalOptionModel.getSouthLimit();
		double east = this.globalOptionModel.getEastLimit();
		double west = this.globalOptionModel.getWestLimit();
		
		double left = (longitude - west) / (east - west);
		double front = (north - latitude) / (north - south);
		
		this.renderer.texCoord(left, front);
		
		//pointVector.z = -pointVector.z;
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
	
	public void setModelGrid(ModelPointGrid arg)
	{
		modelGrid = arg;
	}
}

package us.wthr.jdem846.model.processing.render;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.exception.GraphicsRenderException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.exception.ScriptingException;
import us.wthr.jdem846.geom.Vertex;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.graphics.AxisEnum;
import us.wthr.jdem846.graphics.IRenderer;
import us.wthr.jdem846.graphics.MatrixModeEnum;
import us.wthr.jdem846.graphics.PerspectiveTypeEnum;
import us.wthr.jdem846.graphics.PrimitiveModeEnum;
import us.wthr.jdem846.graphics.View;
import us.wthr.jdem846.graphics.framebuffer.FrameBuffer;
import us.wthr.jdem846.graphics.framebuffer.FrameBufferModeEnum;
import us.wthr.jdem846.graphics.opengl.OpenGlRenderer;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.model.RgbaColor;
import us.wthr.jdem846.model.ViewPerspective;
import us.wthr.jdem846.model.annotations.GridProcessing;
import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846.model.processing.GridProcessor;
import us.wthr.jdem846.model.processing.RenderProcessor;
import us.wthr.jdem846.util.ColorUtil;


@GridProcessing(id="us.wthr.jdem846.model.processing.render.ModelRenderer",
				name="Rendering Process",
				type=GridProcessingTypesEnum.RENDER,
				optionModel=ModelRenderOptionModel.class,
				enabled=true
				)
public class ModelRenderer extends GridProcessor implements RenderProcessor
{
	private static Log log = Logging.getLog(ModelRenderer.class);

	protected FrameBuffer frameBuffer = null;


	protected MapProjection mapProjection = null;


	protected Planet planet = null;

	protected Vertex vtx = new Vertex();
	protected MapPoint point;
	protected double lastElevation = 0x0;
	
	protected View modelView = null;
	protected IRenderer renderer = null;
	
	protected Boolean renderCompleted = false;
	
	protected double latitudeResolution = 1.0;
	protected double longitudeResolution = 1.0;
	
	protected int rowNum = 0;
	
	public ModelRenderer()
	{
		
	}
	

	@Override
	public void dispose()
	{
		this.renderer = null;
	}
	
	@Override
	public void prepare() throws RenderEngineException
	{
		this.lastElevation = this.modelContext.getRasterDataContext().getDataMaximumValue();
		//this.renderer = new GraphicsRenderer();
		this.renderer = new OpenGlRenderer();
		if (this.frameBuffer != null) {
			this.renderer.setFrameBuffer(frameBuffer);
		}
		this.renderer.initialize(globalOptionModel.getWidth(), globalOptionModel.getHeight());
		
		latitudeResolution = this.modelDimensions.modelLatitudeResolution;
		longitudeResolution = this.modelDimensions.modelLongitudeResolution;
	}

	
	@Override
	public void onLatitudeStart(double latitude)
	{
		this.renderer.begin(PrimitiveModeEnum.TRIANGLE_STRIP);
	}

	@Override
	public void onModelPoint(double latitude, double longitude)
	{	
		renderPointVertex(latitude, longitude);
		renderPointVertex(latitude - latitudeResolution, longitude);
	}

	@Override
	public void onLatitudeEnd(double latitude)
	{
		this.renderer.end();
	}
		
	

	
	


	@Override
	public void onProcessBefore() throws RenderEngineException
	{
		try {
			this.setPerspective();
		} catch (GraphicsRenderException ex) {
			throw new RenderEngineException("Failed to set graphics perspective: " + ex.getMessage(), ex);
		}

		
		this.renderer.pushMatrix();
		
		RgbaColor background = this.globalOptionModel.getBackgroundColor();
		if (background != null) {
			this.renderer.clear(background.getRgba());
		} else {
			this.renderer.clear(0x0);
		}
		
		ViewPerspective view = this.globalOptionModel.getViewAngle();
		
		
		double radius = this.modelView.radius();
		
		if (view != null) {

			this.renderer.rotate(view.getRotateX(), AxisEnum.X_AXIS);
			this.renderer.rotate(-view.getRotateY(), AxisEnum.Y_AXIS);
			this.renderer.rotate(view.getRotateZ(), AxisEnum.Z_AXIS);
			
			this.renderer.translate(view.getShiftX() * radius
									, view.getShiftY() * radius
									, view.getShiftZ() * radius);

			
		}
		
		this.renderer.pushMatrix();
		if (this.globalOptionModel.getUseScripting() && this.script != null) {
			try {
				this.script.preRender(this.renderer, this.modelView);
			} catch (ScriptingException ex) {
				log.warn("Exception thrown in user script: " + ex.getMessage(), ex);
			}
		}
		this.renderer.popMatrix();
		
		this.bindTexture();
		int[] c = {0, 0, 0, 0xFF};
		this.renderer.color(ColorUtil.rgbaToInt(c));
	}



	@Override
	public void onProcessAfter() throws RenderEngineException
	{
		this.renderer.unbindTexture();
		
		this.renderer.pushMatrix();
		if (this.globalOptionModel.getUseScripting() && this.script != null) {
			try {
				this.script.postRender(this.renderer, this.modelView);
			} catch (ScriptingException ex) {
				log.warn("Exception thrown in user script: " + ex.getMessage(), ex);
			}
		}
		this.renderer.popMatrix();
		
		
		this.renderer.popMatrix();
		this.renderer.finish();
	}

	@Override
	public void setFrameBuffer(FrameBuffer frameBuffer)
	{
		this.frameBuffer = frameBuffer;
		if (this.renderer != null) {
			this.renderer.setFrameBuffer(frameBuffer);
		}
	}
	
	@Override
	public void setView(View view)
	{
		this.modelView = view;
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
		
		
		FrameBufferModeEnum bufferMode = FrameBufferModeEnum.BINARY_SPACE_PARTITIONING;//FrameBufferModeEnum.getBufferModeFromIdentifier(this.globalOptionModel.getFrameBufferMode());
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



	private Vector pointVector = new Vector();
	protected void renderPointVertex(double latitude, double longitude)
	{
		double elevation = this.modelGrid.getElevation(latitude, longitude, true);
		if (elevation == DemConstants.ELEV_NO_DATA) {
			elevation = this.lastElevation;
			//return;
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
	
	
}

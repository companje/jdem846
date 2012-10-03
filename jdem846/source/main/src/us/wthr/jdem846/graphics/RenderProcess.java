package us.wthr.jdem846.graphics;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelDimensions;
import us.wthr.jdem846.canvas.util.ColorUtil;
import us.wthr.jdem846.geom.Vertex;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.ModelGrid;
import us.wthr.jdem846.model.ModelPointGrid;
import us.wthr.jdem846.model.RgbaColor;
import us.wthr.jdem846.model.ViewPerspective;
import us.wthr.jdem846.scripting.ScriptProxy;

public class RenderProcess
{
	
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
	
	
	public void run()
	{
		render();
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
	
	
	protected void setPerspective()
	{
		int width = this.globalOptionModel.getWidth();
		int height = this.globalOptionModel.getHeight();
		
		double horizFieldOfView = this.modelView.horizFieldOfView();
		
		this.renderer.viewPort(width, height);
		this.renderer.matrixMode(MatrixModeEnum.PROJECTION);
		this.renderer.loadIdentity();
		
		double aspect = (double)width / (double)height;
		double near = this.modelView.nearClipDistance();
		double far = this.modelView.farClipDistance();
		double eyeZ = this.modelView.eyeZ();
		
		double radius = this.modelView.radius() + this.modelContext.getRasterDataContext().getDataMaximumValue();
		
		
		
		this.renderer.ortho(-radius		// Left
							, radius	// Right
							, -radius	// Bottom
							, radius	// Top
							, -radius	// Near
							, radius);	// Far
							
		
		
		/*
		this.renderer.perspective(horizFieldOfView, aspect, near, far);
		this.renderer.lookAt(0							// Eye X
							, 0							// Eye Y
							, eyeZ						// Eye Z
							, 0							// Center X
							, 0							// Center Y
							, 0							// Center Z
							, 0							// Up X
							, 1							// Up Y
							, 0);						// Up Z
		
		*/
	}
	
	protected void render()
	{
		this.setPerspective();
		
		this.renderer.matrixMode(MatrixModeEnum.MODELVIEW);
		this.renderer.loadIdentity();
		
		
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
		
		if (view != null) {
			this.renderer.rotate(view.getRotateY(), AxisEnum.Y_AXIS);
			this.renderer.rotate(view.getRotateX(), AxisEnum.X_AXIS);
			
			this.renderer.rotate(view.getRotateZ(), AxisEnum.Z_AXIS);
			
			
			this.renderer.scale(view.getZoom(), view.getZoom(), view.getZoom());
			
			double meanRadius = (this.planet != null) ? this.planet.getMeanRadius() : DemConstants.EARTH_MEAN_RADIUS;
			this.renderer.translate(view.getShiftX() * meanRadius * 1000
									, view.getShiftY() * meanRadius * 1000
									, view.getShiftZ() * meanRadius * 1000);
			
			
		}
		
		
		this.renderer.pushMatrix();
		if (this.scriptProxy != null) {
			//this.scriptProxy.preRender(this.renderer);
		}
		this.renderer.popMatrix();
		
		this.bindTexture();
		int[] c = {0, 0, 0, 0xFF};
		this.renderer.color(ColorUtil.rgbaToInt(c));
		
		double latitudeResolution = this.modelDimensions.outputLatitudeResolution;
		double longitudeResolution = this.modelDimensions.outputLongitudeResolution;
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
		if (this.scriptProxy != null) {
			//this.scriptProxy.postRender(this.renderer);
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
			int width = this.globalOptionModel.getWidth();
			int height = this.globalOptionModel.getHeight();
			this.image = new ImageCapture(width, height);
			
			FrameBuffer fb = this.renderer.getFrameBuffer();
			
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int c = fb.get(x, y);
					this.image.set(x, height - y - 1, c);
				}
			}
			
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

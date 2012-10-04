package us.wthr.jdem846.model.processing.dataload;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.ModelGrid;
import us.wthr.jdem846.model.ModelPointHandler;
import us.wthr.jdem846.model.annotations.GridProcessing;
import us.wthr.jdem846.model.processing.AbstractGridProcessor;
import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846.model.processing.GridProcessor;
import us.wthr.jdem846.model.processing.util.SurfaceNormalCalculator;

@GridProcessing(id="us.wthr.jdem846.model.processing.dataload.SurfaceNormalsProcessor",
				name="Surface Normals Process",
				type=GridProcessingTypesEnum.DATA_LOAD,
				optionModel=SurfaceNormalsOptionModel.class,
				enabled=false
				)
public class SurfaceNormalsProcessor extends GridProcessor
{
	private static Log log = Logging.getLog(SurfaceNormalsProcessor.class);

	private SurfaceNormalCalculator normalsCalculator;

	private double[] normal = new double[3];
	
	public SurfaceNormalsProcessor()
	{
		
	}

	@Override
	public void prepare() throws RenderEngineException
	{
		SurfaceNormalsOptionModel optionModel = (SurfaceNormalsOptionModel) this.getOptionModel();

		normalsCalculator = new SurfaceNormalCalculator(modelGrid, 
														PlanetsRegistry.getPlanet(getGlobalOptionModel().getPlanet()), 
														getModelDimensions().getTextureLatitudeResolution(), 
														getModelDimensions().getTextureLongitudeResolution());
		
	}



	@Override
	public void onLatitudeStart(double latitude)
			throws RenderEngineException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onModelPoint(double latitude, double longitude)
			throws RenderEngineException
	{
		//normalsCalculator.resetBuffers(latitude, longitude);
		//normalsCalculator.calculateNormal(latitude, longitude, normal);
		//modelGrid.setNormal(latitude, longitude, normal);
	}

	@Override
	public void onLatitudeEnd(double latitude)
			throws RenderEngineException
	{
		
	}

	@Override
	public void onProcessBefore() throws RenderEngineException
	{
		
	}

	@Override
	public void onProcessAfter() throws RenderEngineException
	{
		
	}

	@Override
	public void dispose() throws RenderEngineException
	{
		
	}




	
	
}

package us.wthr.jdem846.model.processing.shading;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.color.ColorAdjustments;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.model.ModelGrid;
import us.wthr.jdem846.model.ModelPoint;
import us.wthr.jdem846.model.ModelPointHandler;
import us.wthr.jdem846.model.annotations.GridProcessing;
import us.wthr.jdem846.model.processing.AbstractGridProcessor;
import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846.model.processing.GridProcessor;
import us.wthr.jdem846.model.processing.util.Aspect;


@GridProcessing(id="us.wthr.jdem846.model.processing.shading.AspectShadingProcessor",
				name="Aspect Shading Process",
				type=GridProcessingTypesEnum.SHADING,
				optionModel=AspectShadingOptionModel.class,
				enabled=true
)
public class AspectShadingProcessor extends AbstractGridProcessor implements GridProcessor, ModelPointHandler
{
	private static Log log = Logging.getLog(AspectShadingProcessor.class);
	
	protected int[] rgbaBuffer = new int[4];
	
	
	public AspectShadingProcessor()
	{
		
	}
	
	public AspectShadingProcessor(ModelContext modelContext, ModelGrid modelGrid)
	{
		super(modelContext, modelGrid);
	}
	
	@Override
	public void prepare() throws RenderEngineException
	{
		
		AspectShadingOptionModel optionModel = (AspectShadingOptionModel) this.getProcessOptionModel();
		
		
	}
	
	@Override
	public void process() throws RenderEngineException
	{
		super.process();
	}


	@Override
	public void onModelPoint(double latitude, double longitude)
			throws RenderEngineException
	{
		
		ModelPoint modelPoint = modelGrid.get(latitude, longitude);
		
		double x = modelPoint.getNormal()[0];
		double z = modelPoint.getNormal()[2];
		
		double degrees = Aspect.aspectInDegrees(x, z);
		
		double shade = 1.0 - (2.0 * (degrees / 360.0));
		
		modelPoint.setDotProduct(shade);
		processPointColor(modelPoint, latitude, longitude);
		
		
	}
	
	protected void processPointColor(ModelPoint modelPoint, double latitude, double longitude) throws RenderEngineException
	{
		double dot = modelPoint.getDotProduct();
		
		modelPoint.getRgba(rgbaBuffer, false);
		ColorAdjustments.adjustBrightness(rgbaBuffer, dot);
		modelPoint.setRgba(rgbaBuffer, true);
	}
	

	
	
}

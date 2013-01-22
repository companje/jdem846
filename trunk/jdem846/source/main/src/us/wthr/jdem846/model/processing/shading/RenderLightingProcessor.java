package us.wthr.jdem846.model.processing.shading;

import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.model.annotations.GridProcessing;
import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846.model.processing.GridProcessor;

@GridProcessing(id = "us.wthr.jdem846.model.processing.lighting.RenderLightingProcessor"
	, name = "Model Lighting"
	, type = GridProcessingTypesEnum.LIGHTING
	, optionModel = RenderLightingOptionModel.class
	, enabled = true)
public class RenderLightingProcessor extends GridProcessor
{

	@Override
	public void onLatitudeStart(double latitude) throws RenderEngineException
	{
		
	}

	@Override
	public void onLatitudeEnd(double latitude) throws RenderEngineException
	{
		
	}

	@Override
	public void prepare() throws RenderEngineException
	{
		
	}

	@Override
	public void onProcessBefore() throws RenderEngineException
	{
		
	}

	@Override
	public void onModelPoint(double latitude, double longitude) throws RenderEngineException
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

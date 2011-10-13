package us.wthr.jdem846.render;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.annotations.DemEngine;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;


@DemEngine(name="us.wthr.jdem846.render.demEngine2dTiledKML.name", 
				identifier="dem2d-tiled-kml-gen", 
				usesProjection=false,
				enabled=true)
public class Dem2dTiledKmlGenerator extends BasicRenderEngine
{
	
	private static Log log = Logging.getLog(Dem2dTiledKmlGenerator.class);
	
	public Dem2dTiledKmlGenerator(ModelContext modelContext)
	{
		super(modelContext);
	}
	
	//public Dem2dTiledKmlGenerator(DataPackage dataPackage, ModelOptions modelOptions)
	//{
	//	super(dataPackage, modelOptions);
	//}
	
	
	@Override
	public OutputProduct<DemCanvas> generate() throws RenderEngineException
	{
		try {
			return generate(false);
		} catch (OutOfMemoryError err) {
			log.error("Out of memory error when generating model", err);
			throw new RenderEngineException("Out of memory error when generating model", err);
		} catch (Exception ex) {
			log.error("Error occured generating model", ex);
			throw new RenderEngineException("Error occured generating model", ex);
		}
	}
	
	
	@Override
	public OutputProduct<DemCanvas> generate(boolean skipElevation) throws RenderEngineException
	{
		Dem2dGenerator dem2d = new Dem2dGenerator(getModelContext());
		
		return null;
	}
	
}

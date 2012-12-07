package us.wthr.jdem846.model;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class ModelBuildController implements IModelBuildController
{
	private static Log log = Logging.getLog(ModelBuildController.class);

	private IModelBuilder modelBuilder;
	private ProgressTracker progressTracker;

	public ModelBuildController(IModelBuilder modelBuilder, ProgressTracker progressTracker)
	{
		this.modelBuilder = modelBuilder;
		this.progressTracker = progressTracker;
	}

	@Override
	public void dispose()
	{

	}

	@Override
	public void prepare(ModelContext modelContext, ModelProcessManifest modelProcessManifest) throws RenderEngineException
	{

	}

}

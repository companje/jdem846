package us.wthr.jdem846.model;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.RenderEngineException;

public interface IModelBuildController
{
	public void dispose();

	public void prepare(ModelContext modelContext, ModelProcessManifest modelProcessManifest) throws RenderEngineException;

}

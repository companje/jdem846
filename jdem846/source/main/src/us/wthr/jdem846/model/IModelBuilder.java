package us.wthr.jdem846.model;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.RenderEngineException;

public interface IModelBuilder
{

	public void dispose();

	public void prepare(ModelContext modelContext, ModelProcessManifest modelProcessManifest) throws RenderEngineException;

	public ElevationModel process() throws RenderEngineException;

	public boolean isPrepared();

	public void onProcessBefore() throws RenderEngineException;

	public void processModelData() throws RenderEngineException;

	public void processModelRender() throws RenderEngineException;

	public ElevationModel createElevationModel() throws RenderEngineException;

	public void onProcessAfter() throws RenderEngineException;

	public void onDestroy() throws RenderEngineException;

}

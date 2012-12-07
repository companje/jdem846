package us.wthr.jdem846.graphics;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelDimensions;
import us.wthr.jdem846.canvas.CanvasProjectionTypeEnum;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.modelgrid.IModelGrid;
import us.wthr.jdem846.scripting.ScriptProxy;

public class ViewFactory
{

	public static View getViewInstance(ModelContext modelContext, GlobalOptionModel globalOptionModel, ModelDimensions modelDimensions, MapProjection mapProjection, ScriptProxy scriptProxy,
			IModelGrid modelGrid)
	{

		View view = null;

		CanvasProjectionTypeEnum viewProjectionType = CanvasProjectionTypeEnum.getCanvasProjectionEnumFromIdentifier(globalOptionModel.getRenderProjection());

		if (viewProjectionType == CanvasProjectionTypeEnum.PROJECT_FLAT) {
			view = new FlatView();
		} else if (viewProjectionType == CanvasProjectionTypeEnum.PROJECT_3D) {
			view = new ThreeDimensionalView();
		} else if (viewProjectionType == CanvasProjectionTypeEnum.PROJECT_SPHERE) {
			view = new GlobalView();
		}

		view.setModelContext(modelContext);
		view.setGlobalOptionModel(globalOptionModel);
		view.setModelDimensions(modelDimensions);
		view.setMapProjection(mapProjection);
		view.setScript(scriptProxy);
		view.setModelGrid(modelGrid);

		return view;
	}

}

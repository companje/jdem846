package us.wthr.jdem846.modelgrid;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.ModelGridDimensions;
import us.wthr.jdem846.model.ModelProcessManifest;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.scripting.ScriptProxy;

public class ModelGridFactory
{

	public static IModelGrid createBufferedModelGrid(ModelContext modelContext)
	{
		ModelProcessManifest modelProcessManifest = modelContext.getModelProcessManifest();
		GlobalOptionModel globalOptionModel = modelProcessManifest.getGlobalOptionModel();
		ModelGridDimensions modelDimensions = ModelGridDimensions.getModelDimensions(modelContext);

		return ModelGridFactory.createBufferedModelGrid(globalOptionModel.getNorthLimit(), globalOptionModel.getSouthLimit(), globalOptionModel.getEastLimit(), globalOptionModel.getWestLimit(), modelDimensions.getTextureLatitudeResolution(),
				modelDimensions.getTextureLongitudeResolution(), modelContext.getRasterDataContext().getDataMinimumValue(), modelContext.getRasterDataContext().getDataMaximumValue());
	}

	public static IModelGrid createBufferedModelGrid(double north, double south, double east, double west, double latitudeResolution, double longitudeResolution, double minimum, double maximum)
	{
		IModelGrid modelGrid = new BufferedModelGrid(north, south, east, west, latitudeResolution, longitudeResolution, minimum, maximum);

		modelGrid.reset();
		return modelGrid;
	}

	public static IFillControlledModelGrid createFillControlledModelGrid(ModelContext modelContext)
	{
		return ModelGridFactory.createFillControlledModelGrid(modelContext, modelContext.getModelGridContext().getModelGrid());
	}

	public static IFillControlledModelGrid createFillControlledModelGrid(ModelContext modelContext, IModelGrid modelGrid)
	{
		ModelProcessManifest modelProcessManifest = modelContext.getModelProcessManifest();
		GlobalOptionModel globalOptionModel = modelProcessManifest.getGlobalOptionModel();
		ModelGridDimensions modelDimensions = ModelGridDimensions.getModelDimensions(modelContext);

		IFillControlledModelGrid fillControlledModelGrid = ModelGridFactory.createFillControlledModelGrid(globalOptionModel.getNorthLimit(), globalOptionModel.getSouthLimit(), globalOptionModel.getEastLimit(), globalOptionModel.getWestLimit(),
				modelDimensions.getTextureLatitudeResolution(), modelDimensions.getTextureLongitudeResolution(), modelContext.getRasterDataContext().getDataMinimumValue(), modelContext.getRasterDataContext().getDataMaximumValue(), (modelContext
						.getImageDataContext().getImageListSize() > 0), modelContext.getRasterDataContext(), modelGrid, (globalOptionModel.getUseScripting() ? modelContext.getScriptingContext().getScriptProxy() : null));

		return fillControlledModelGrid;
	}

	public static IFillControlledModelGrid createFillControlledModelGrid(double north, double south, double east, double west, double latitudeResolution, double longitudeResolution, double minimum, double maximum, boolean zeroInCaseOfNoRaster,
			RasterDataContext rasterDataContext, IModelGrid modelGrid, ScriptProxy scriptProxy)
	{
		IFillControlledModelGrid fillControlledModelGrid = new FillControlledModelGrid(north, south, east, west, latitudeResolution, longitudeResolution, minimum, maximum, zeroInCaseOfNoRaster, rasterDataContext, modelGrid, scriptProxy);

		return fillControlledModelGrid;
	}

}

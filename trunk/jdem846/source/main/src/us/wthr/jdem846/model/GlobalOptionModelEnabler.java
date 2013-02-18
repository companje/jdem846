package us.wthr.jdem846.model;

import us.wthr.jdem846.ModelContext;

public class GlobalOptionModelEnabler implements IOptionEnabler
{

	@Override
	public boolean isOptionEnabled(ModelContext modelContext, OptionModel optionModel, String propertyId, Object value)
	{

		boolean enable = true;

		GlobalOptionModel globalOptionModel = (GlobalOptionModel) optionModel;

		if (propertyId == null) {
			return enable;
		}

		if ((propertyId.equals("us.wthr.jdem846.model.GlobalOptionModel.perspectiveType")
				|| propertyId.equals("us.wthr.jdem846.model.GlobalOptionModel.viewAngle") 
				|| propertyId.equals("us.wthr.jdem846.model.GlobalOptionModel.fieldOfView"))
				&& globalOptionModel.getRenderProjection().equals("us.wthr.jdem846.render.canvasProjection.flat")) {
			enable = false;
		} else if (propertyId.equals("us.wthr.jdem846.model.GlobalOptionModel.eyeDistance") && (globalOptionModel.getRenderProjection().equals("us.wthr.jdem846.render.canvasProjection.flat") || globalOptionModel.getRenderProjection().equals("us.wthr.jdem846.render.canvasProjection.3d"))) {
			enable = false;
		} else if ((propertyId.equals("us.wthr.jdem846.model.GlobalOptionModel.northLimit") || propertyId.equals("us.wthr.jdem846.model.GlobalOptionModel.southLimit")
				|| propertyId.equals("us.wthr.jdem846.model.GlobalOptionModel.eastLimit") || propertyId.equals("us.wthr.jdem846.model.GlobalOptionModel.westLimit"))
				&& !globalOptionModel.getLimitCoordinates()) {
			enable = false;
		} else if (propertyId.equals("us.wthr.jdem846.model.GlobalOptionModel.mapProjection") && !globalOptionModel.getRenderProjection().equals("us.wthr.jdem846.render.canvasProjection.flat")) {
			enable = false;
		} else if (propertyId.equals("us.wthr.jdem846.model.GlobalOptionModel.maintainAspectRatio") && !globalOptionModel.getRenderProjection().equals("us.wthr.jdem846.render.canvasProjection.flat")) {
			enable = false;
		}
		
		
		if (modelContext.getModelGridContext() != null 
				&& modelContext.getModelGridContext().getGridLoadedFrom() != null
				&& (propertyId.equals("us.wthr.jdem846.model.GlobalOptionModel.width")
						|| propertyId.equals("us.wthr.jdem846.model.GlobalOptionModel.height")
						|| propertyId.equals("us.wthr.jdem846.model.GlobalOptionModel.maintainAspectRatio")
						|| propertyId.equals("us.wthr.jdem846.model.GlobalOptionModel.estimateElevationRange")
						|| propertyId.equals("us.wthr.jdem846.model.GlobalOptionModel.elevationMultiple")
						|| propertyId.equals("us.wthr.jdem846.model.GlobalOptionModel.elevationScale")
						|| propertyId.equals("us.wthr.jdem846.model.GlobalOptionModel.textureQuality")
				)) {
			enable = false;
		}
		
		
		return enable;
	}

}

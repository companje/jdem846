package us.wthr.jdem846.model.processing.shading;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.model.IOptionEnabler;
import us.wthr.jdem846.model.OptionModel;

public class RenderLightingOptionPropertyEnabler implements IOptionEnabler
{

	@Override
	public boolean isOptionEnabled(ModelContext modelContext, OptionModel optionModel, String propertyId, Object value)
	{
		boolean enable = true;

		if (propertyId == null) {
			return enable;
		}

		RenderLightingOptionModel renderLightingOptionModel = (RenderLightingOptionModel) optionModel;

		if (!propertyId.equals("us.wthr.jdem846.model.RenderLightingOptionModel.lightingEnabled") && !renderLightingOptionModel.isLightingEnabled()) {
			enable = false;
			return enable;
		}

		return enable;
	}
}

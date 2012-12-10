package us.wthr.jdem846.model.processing.shading;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.model.IOptionEnabler;
import us.wthr.jdem846.model.OptionModel;

public class HillshadingOptionPropertyEnabler implements IOptionEnabler
{

	@Override
	public boolean isOptionEnabled(ModelContext modelContext, OptionModel optionModel, String propertyId, Object value)
	{
		boolean enable = true;

		if (propertyId == null) {
			return enable;
		}

		HillshadingOptionModel hillshadingOptionModel = (HillshadingOptionModel) optionModel;

		if (!propertyId.equals("us.wthr.jdem846.model.HillshadingOptionModel.lightingEnabled") && !hillshadingOptionModel.isLightingEnabled()) {
			enable = false;
			return enable;
		}

		if (!hillshadingOptionModel.getAdvancedLightingControl()
				&& (propertyId.equals("us.wthr.jdem846.model.HillshadingOptionModel.emmisive") || propertyId.equals("us.wthr.jdem846.model.HillshadingOptionModel.ambient")
						|| propertyId.equals("us.wthr.jdem846.model.HillshadingOptionModel.diffuse") || propertyId.equals("us.wthr.jdem846.model.HillshadingOptionModel.specular"))) {
			enable = false;
		}

		if (hillshadingOptionModel.getAdvancedLightingControl()
				&& (propertyId.equals("us.wthr.jdem846.model.HillshadingOptionModel.lightIntensity") || propertyId.equals("us.wthr.jdem846.model.HillshadingOptionModel.darkIntensity"))) {
			enable = false;
		}

		if (!hillshadingOptionModel.isRayTraceShadows() && propertyId.equals("us.wthr.jdem846.model.HillshadingOptionModel.shadowIntensity")) {
			enable = false;
		}

		return enable;
	}

}

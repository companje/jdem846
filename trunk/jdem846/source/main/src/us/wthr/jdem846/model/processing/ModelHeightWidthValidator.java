package us.wthr.jdem846.model.processing;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.OptionValidator;
import us.wthr.jdem846.model.exceptions.OptionValidationException;

public class ModelHeightWidthValidator implements OptionValidator
{
	private static Log log = Logging.getLog(ModelHeightWidthValidator.class);
	
	
	public boolean validate(ModelContext modelContext, OptionModel optionModel, String propertyId, Object value) throws OptionValidationException
	{
		GlobalOptionModel globalOptionModel = (GlobalOptionModel) optionModel;
		
		if (!(value instanceof Integer)) {
			throw new OptionValidationException("Wrong data type '" + value.getClass().getName() + "'. Property should be an integer.", propertyId, value);
		}
		
		int v = (Integer) value;
		
		if (v <= 0) {
			throw new OptionValidationException("Value cannot be zero or less", propertyId, value);
		}
		
		log.info("### Checking aspect ratio for " + propertyId);
		
		if (globalOptionModel.getMaintainAspectRatio() 
				&& modelContext != null
				&& modelContext.getRasterDataContext() != null 
				&& modelContext.getRasterDataContext().getRasterDataListSize() > 0) {
			
			
			
			double aspectRatio = (double)modelContext.getRasterDataContext().getDataColumns() / (double)modelContext.getRasterDataContext().getDataRows();
			
			if (propertyId.equals("us.wthr.jdem846.model.GlobalOptionModel.width")) {
				
				int height = (int) Math.round((double)v / aspectRatio);
				
				log.info("*** Width modified, setting height to " + height + " (" + aspectRatio + ")");
				
				if (height != globalOptionModel.getHeight()) {
					globalOptionModel.setHeight(height);
					return true;
				}
				
				
			} else if (propertyId.equals("us.wthr.jdem846.model.GlobalOptionModel.height")) {
				
				int width = (int) Math.round((double)v * aspectRatio);
				
				log.info("*** Height modified, setting width to " + width + " (" + aspectRatio + ")");
				
				if (width != globalOptionModel.getWidth()) {
					globalOptionModel.setWidth(width);
					return true;
				}
				
			}
			

		} 
		
		return false;
		

	}
	
	
}

package us.wthr.jdem846.render;

import us.wthr.jdem846.ModelContext;

public class CanvasProjectionFactory
{
	
	
	public static CanvasProjection create(ModelContext modelContext)
	{
		/*
		 * Kinda simplistic for now since there's only two canvas projections
		 * and one option switching between the two...
		 */
		
		CanvasProjection canvasProjection = null;
		
		
		if (modelContext.getModelOptions().getModelProjection() == CanvasProjectionTypeEnum.PROJECT_FLAT) {
			canvasProjection = new CanvasProjection(modelContext);
		} else if (modelContext.getModelOptions().getModelProjection() == CanvasProjectionTypeEnum.PROJECT_3D) {
			canvasProjection = new CanvasProjection3d(modelContext);
		} else if (modelContext.getModelOptions().getModelProjection() == CanvasProjectionTypeEnum.PROJECT_SPHERE) {
			canvasProjection = new CanvasProjectionGlobe(modelContext);
		}
		
		
		return canvasProjection;
	}
	
	
}

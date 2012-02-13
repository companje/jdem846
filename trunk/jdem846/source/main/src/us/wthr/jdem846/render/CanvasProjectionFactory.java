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
		
		if (modelContext.getModelOptions().getProject3d()) {
			//canvasProjection = new CanvasProjection3d(modelContext);
			canvasProjection = new CanvasProjectionGlobe(modelContext);
		} else {
			canvasProjection = new CanvasProjection(modelContext);
		}
		
		return canvasProjection;
	}
	
	
}

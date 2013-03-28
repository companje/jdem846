package us.wthr.jdem846.project;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.ProjectMarshalException;
import us.wthr.jdem846.image.SimpleGeoImage;
import us.wthr.jdem846.model.ModelProcessContainer;
import us.wthr.jdem846.model.exceptions.ModelContainerException;
import us.wthr.jdem846.modelgrid.UserProvidedModelGrid;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataSource;
import us.wthr.jdem846.shapefile.ShapeFileRequest;

public class ProjectMarshaller
{
	
	
	public static ProjectMarshall marshallProject(ModelContext modelContext) throws ProjectMarshalException
	{
		ProjectMarshall pm = new ProjectMarshall();
		
		if (modelContext != null) {
			try {
				pm.setGlobalOptions(modelContext.getModelProcessManifest().getGlobalOptionModelContainer().getPropertyMapById());
			} catch (ModelContainerException ex) {
				throw new ProjectMarshalException("Error marshalling global option model: " + ex.getMessage(), ex);
			}
			
			for (ModelProcessContainer processContainer : modelContext.getModelProcessManifest().getWorkerList()) {
				
				ProcessMarshall processMarshall = ProjectMarshaller.marshalProcess(processContainer);
				pm.getProcesses().add(processMarshall);
			}
	
	
			for (RasterData rasterData : modelContext.getRasterDataContext().getRasterDataList()) {
				pm.getRasterFiles().add(new RasterDataSource(rasterData.getFilePath(), rasterData.getRasterDefinition()));
			}
			
			for (ShapeFileRequest shapeFileRequest : modelContext.getShapeDataContext().getShapeFiles()) {
				pm.getShapeFiles().add(shapeFileRequest);
			}
			
			for (SimpleGeoImage simpleGeoImage : modelContext.getImageDataContext().getImageList()) {
				pm.getImageFiles().add(simpleGeoImage);
			}
			
			if (modelContext.getModelGridContext().getModelGrid() instanceof UserProvidedModelGrid) {
				pm.setModelGrid(((UserProvidedModelGrid)modelContext.getModelGridContext().getModelGrid()).getFilePath());
			}
			
			pm.setUserScript(modelContext.getScriptingContext().getUserScript());
			pm.setScriptLanguage(modelContext.getScriptingContext().getScriptLanguage());
			
		}
		
		return pm;
	}
	
	
	public static ProcessMarshall marshalProcess(ModelProcessContainer processContainer) throws ProjectMarshalException
	{
		ProcessMarshall pm = new ProcessMarshall();
		
		pm.setId(processContainer.getProcessId());
		
		
		try {
			pm.setOptions(processContainer.getOptionModelContainer().getPropertyMapById());
		} catch (ModelContainerException ex) {
			throw new ProjectMarshalException("Error fetching process options: " + ex.getMessage(), ex);
		}
		
		return pm;
	}
}

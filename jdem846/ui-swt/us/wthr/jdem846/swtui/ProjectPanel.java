package us.wthr.jdem846.swtui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.project.ProjectModel;
import us.wthr.jdem846.shapefile.ShapeFileRequest;

public class ProjectPanel extends TabPanel
{
	private static Log log = Logging.getLog(ProjectPanel.class);
	
	private DataPackage dataPackage;
	private ModelOptions modelOptions;
	
	private String projectLoadedFrom = null;
	
	public ProjectPanel(Composite parent, ProjectModel projectModel)
	{
		super(parent);
		
		// Create data
		dataPackage = new DataPackage(null);
		modelOptions = new ModelOptions();
		
		// Apply model options
		if (projectModel != null) {
			modelOptions.syncFromProjectModel(projectModel);
			
			for (String filePath : projectModel.getInputFiles()) {
				//addElevationDataset(filePath, false);
			}
			
			for (ShapeFileRequest shapeFile : projectModel.getShapeFiles()) {
				//addShapeDataset(shapeFile.getPath(), shapeFile.getShapeDataDefinitionId(), false);
			}

			
			projectLoadedFrom = projectModel.getLoadedFrom();
		}
		
		
		
		
		
	}
	
	
}

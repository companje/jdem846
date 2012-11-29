package us.wthr.jdem846.ui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.project.ProjectFiles;
import us.wthr.jdem846.project.ProjectMarshall;
import us.wthr.jdem846.project.ProjectMarshaller;
import us.wthr.jdem846.project.ProjectTypeEnum;
import us.wthr.jdem846.ui.base.FileChooser;

@SuppressWarnings("serial")
public class DemImageProjectPane extends JdemPanel implements Savable
{
	private static Log log = Logging.getLog(DemImageProjectPane.class);
	
	private ModelContext modelContext;
	
	private RenderPane renderPane;
	
	private String projectLoadedFrom = null;
	
	public DemImageProjectPane()
	{
		this(null);
	}
	
	public DemImageProjectPane(ProjectMarshall projectMarshall)
	{
		initialize(projectMarshall);
	}
	
	protected void initialize(ProjectMarshall projectMarshall)
	{
		// Create Components
		renderPane = new RenderPane(false);
		
		
		// Set layout
		setLayout(new BorderLayout());
		add(renderPane, BorderLayout.CENTER);
		
		
		if (projectMarshall == null || projectMarshall.getElevationModels().size() == 0) {
			return;
		}
		
		for (ElevationModel jdemElevationModel : projectMarshall.getElevationModels()) {
			renderPane.display(jdemElevationModel);
		}
		
	}
	
	
	
	public void dispose() throws ComponentException
	{
		log.info("Closing image project pane.");
		

		super.dispose();
		

	}

	@Override
	public void save()
	{
		if (projectLoadedFrom == null) {
			saveAs();
		} else {
			saveTo(projectLoadedFrom);
		}
		
	}

	@Override
	public void saveAs()
	{
		log.info("Save");
		
		FileChooser chooser = new FileChooser();
		FileNameExtensionFilter filter = null;
		
		
		

		filter = new FileNameExtensionFilter(I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.saveImage.jdemimg"), "jdemimg");
		chooser.addChoosableFileFilter(filter);
		chooser.setFileFilter(filter);
		

	    int returnVal = chooser.showSaveDialog(this);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	String filePath = chooser.getSelectedFile().getAbsolutePath();
	    	

	    	log.info("Saving to: " + filePath);
	    	saveTo(filePath);
	    	log.info("Done Save");
	    }
	}
	
	protected void saveTo(String path) 
	{
		String extension = path.substring(path.lastIndexOf("."));
		if (extension == null) {
			extension = ".jdemimg";
			path += extension;
		}

		try {
			ProjectMarshall projectMarshall = ProjectMarshaller.marshallProject(null);
			
			projectMarshall.setProjectType(ProjectTypeEnum.DEM_IMAGE);
			
			List<ElevationModel> modelList = this.renderPane.getJdemElevationModels();
			projectMarshall.getElevationModels().addAll(modelList);
			
			
			ProjectFiles.write(projectMarshall, path);
			
			projectLoadedFrom = path;
			
			RecentProjectTracker.addProject(path);
			
			log.info("Project file saved to " + path);
			SharedStatusBar.setStatus("Project file saved to " + path);
			
			
			
		} catch (Exception ex) {
			log.warn("Error trying to write project to disk: " + ex.getMessage(), ex);
			JOptionPane.showMessageDialog(getRootPane(),
				    I18N.get("us.wthr.jdem846.ui.jdemFrame.saveError.writeError.message"),
				    I18N.get("us.wthr.jdem846.ui.jdemFrame.saveError.writeError.title"),
				    JOptionPane.ERROR_MESSAGE);
			return;
		}

	}
	
	
	public String getSavedPath()
	{
		return projectLoadedFrom;
	}

	public void setSavedPath(String projectLoadedFrom)
	{
		this.projectLoadedFrom = projectLoadedFrom;
	}
	
	
	
	
	
	
}

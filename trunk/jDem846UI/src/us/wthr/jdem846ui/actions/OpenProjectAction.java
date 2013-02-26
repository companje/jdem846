package us.wthr.jdem846ui.actions;

import org.eclipse.ui.IWorkbenchWindow;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.project.context.ProjectContext;
import us.wthr.jdem846.project.context.ProjectException;
import us.wthr.jdem846ui.ICommandIds;

public class OpenProjectAction extends DirectoryPromptingAction
{
	private static Log log = Logging.getLog(OpenProjectAction.class);

	public OpenProjectAction(IWorkbenchWindow window, String label, String viewId)
	{
		super(window, ICommandIds.CMD_OPEN, viewId, label, "/icons/eclipse/project_open.gif");
	}

	@Override
	public void run()
	{
		super.run();

		String filePath = promptForProjectPath(null);
		if (filePath != null) {
			try {
				ProjectContext.initialize(filePath);
			} catch (ProjectException e) {
				e.printStackTrace();
			}
		
			
			
		    /*		    
		    IWorkspace workspace = ResourcesPlugin.getWorkspace();
		    IWorkspaceRoot root = workspace.getRoot();
		    IProject project  = root.getProject("dem");
		    IFile file = project.getFile("script.js");
		    
		    try {
				if (!project.exists())
					project.create(null);
				if (!project.isOpen())
					project.open(null);
				//if (!folder.exists())
				//	folder.create(IResource.NONE, true, null);
				if (!file.exists()) {
					byte[] bytes = "File contents".getBytes();
					InputStream source = new ByteArrayInputStream(bytes);
					file.create(source, IResource.NONE, null);

				}
				
				
				IWorkbenchPage page = getWindow().getActivePage();
				   HashMap map = new HashMap();
				   map.put(IMarker.LINE_NUMBER, new Integer(5));
				   map.put(IWorkbenchPage.EDITOR_ID_ATTR, 
				      "org.eclipse.ui.DefaultTextEditor");
				   IMarker marker = file.createMarker(IMarker.TEXT);
				   marker.setAttributes(map);
				   //page.openEditor(marker); //2.1 API
				   IDE.openEditor(page, marker); //3.0 API
				   marker.delete();
		    } catch (Exception ex) {
		    	ex.printStackTrace();
		    }*/
		}
		
		//IWorkspace workspace = ResourcesPlugin.getWorkspace();
		
	}
	
	
	/*
	protected String promptForFilePath(String previousFile)
	{
		FileDialog dialog = new FileDialog(this.getWindow().getShell(), SWT.OPEN);
		String[] filterNames = StandardFileTypes.PROJECT_PROJECT_FILE_TYPES;
		String[] filterExtensions = StandardFileTypes.PROJECT_PROJECT_FILE_EXTENSIONS;
		String filterPath = "/";
		String platform = SWT.getPlatform();
		if (platform.equals("win32") || platform.equals("wpf")) {
			filterNames = StandardFileTypes.PROJECT_PROJECT_FILE_TYPES_WIN;
			filterExtensions = StandardFileTypes.PROJECT_PROJECT_FILE_EXTENSIONS_WIN;
			filterPath = "c:\\";
		}
		dialog.setFilterNames(filterNames);
		dialog.setFilterExtensions(filterExtensions);

		if (previousFile != null) {
			File f = new File(previousFile);
			dialog.setFileName(f.getName());
			dialog.setFilterPath(f.getAbsolutePath());
		} else {
			dialog.setFilterPath(filterPath);
		}

		return dialog.open();
	}*/
}

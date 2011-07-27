package us.wthr.jdem846.swtui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.i18n.I18N;
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
	private MenuItem projectMenu;
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
		
		projectMenu = new MenuItem (JDemShell.getMenuInstance(), SWT.CASCADE);
		projectMenu.setText (I18N.get("us.wthr.jdem846.ui.projectPane.menu.project"));
		
		Menu submenu = new Menu (JDemShell.getShellInstance(), SWT.DROP_DOWN);
		projectMenu.setMenu (submenu);
		
		MenuItemFactory.createMenuItem(submenu, 
				ImageFactory.loadImageResource("/us/wthr/jdem846/ui/icons/list-add.png"), 
				I18N.get("us.wthr.jdem846.swtui.projectPane.menu.project.add"), 
				new Listener () {
			public void handleEvent (Event e) {
				
			}
		}, SWT.MOD1 + 'A');
		
		MenuItemFactory.createMenuItem(submenu, 
				ImageFactory.loadImageResource("/us/wthr/jdem846/ui/icons/list-remove.png"), 
				I18N.get("us.wthr.jdem846.swtui.projectPane.menu.project.remove"), 
				new Listener () {
			public void handleEvent (Event e) {
				
			}
		}, SWT.MOD1 + 'R');
		
		MenuItemFactory.createMenuItem(submenu, 
				ImageFactory.loadImageResource("/us/wthr/jdem846/ui/icons/stock_update-data.png"), 
				I18N.get("us.wthr.jdem846.swtui.projectPane.menu.project.create"), 
				new Listener () {
			public void handleEvent (Event e) {
				
			}
		}, SWT.MOD1 + 'C');
		
		
	}
	
	public void onPanelVisible()
	{
		
	}
	
	public void onPanelHidden()
	{
		
	}
	
}

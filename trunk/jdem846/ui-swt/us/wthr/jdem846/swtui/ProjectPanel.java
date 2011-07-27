package us.wthr.jdem846.swtui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

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
	private CoolItem coolItem;
	private String projectLoadedFrom = null;
	
	private ModelOptionsPanel modelOptionsPanel;
	
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
		
		setLayout(new GridLayout(1, true));
		
		modelOptionsPanel = new ModelOptionsPanel(this, modelOptions);
		modelOptionsPanel.addChangeListener(new Listener() {
			public void handleEvent(Event event)
			{
				log.info("Model options changed");
			}
		});
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		modelOptionsPanel.setLayoutData(gridData);
		
		
	}
	
	public void createMenuBar()
	{
		if (projectMenu != null)
			return;
		
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
	
	public void createToolBar()
	{
		// CoolBar addition
		if (coolItem != null)
			return;
		
		ToolBar toolBar = new ToolBar(JDemShell.getCoolBarInstance(), SWT.FLAT);
		
		ToolItemFactory.createToolItem(toolBar, 
				"/us/wthr/jdem846/ui/icons/list-add.png", 
				I18N.get("us.wthr.jdem846.ui.projectButtonBar.addButton"), 
				I18N.get("us.wthr.jdem846.ui.projectButtonBar.addTooltip"), 
				new Listener() {
					public void handleEvent(Event arg0)
					{
						onAddAction();
					}
			});
		
		ToolItemFactory.createToolItem(toolBar, 
				"/us/wthr/jdem846/ui/icons/list-remove.png", 
				I18N.get("us.wthr.jdem846.ui.projectButtonBar.removeButton"), 
				I18N.get("us.wthr.jdem846.ui.projectButtonBar.removeTooltip"), 
				new Listener() {
					public void handleEvent(Event arg0)
					{
						onRemoveAction();
					}
			});
		
		ToolItemFactory.createToolItem(toolBar, 
				"/us/wthr/jdem846/ui/icons/stock_update-data.png", 
				I18N.get("us.wthr.jdem846.ui.projectButtonBar.createButton"), 
				I18N.get("us.wthr.jdem846.ui.projectButtonBar.createTooltip"), 
				new Listener() {
					public void handleEvent(Event arg0)
					{
						onCreateAction();
					}
			});
		toolBar.pack();
		
		Point size = toolBar.getSize();
		coolItem = new CoolItem(JDemShell.getCoolBarInstance(), SWT.NONE);
		coolItem.setControl(toolBar);
	    Point preferred = coolItem.computeSize(size.x, size.y);
	    coolItem.setPreferredSize(preferred);
	}
	
	
	public void onAddAction()
	{
		log.info("onAddAction()");
	}
	
	public void onRemoveAction()
	{
		log.info("onRemoveAction()");
	}
	
	public void onCreateAction()
	{
		log.info("onCreateAction()");
	}
	
	public void onPanelVisible()
	{
		log.info("ProjectPnale.onPanelVisible()");
		createMenuBar();
		createToolBar();
	}
	
	public void onPanelHidden()
	{
		log.info("ProjectPnale.onPanelHidden()");
		projectMenu.dispose();
		projectMenu = null;
		coolItem.dispose();
		coolItem = null;
	}
	
	public void onPanelClosed()
	{
		log.info("ProjectPnale.onPanelClosed()");
		if (projectMenu != null)
			projectMenu.dispose();
		if (coolItem != null)
			coolItem.dispose();
	}
	
	@Override
	public void dispose()
	{
		log.info("dispose()");
		super.dispose();
	}
}

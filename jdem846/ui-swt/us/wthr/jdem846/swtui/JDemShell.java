package us.wthr.jdem846.swtui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.exception.ProjectParseException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.project.ProjectFileReader;
import us.wthr.jdem846.project.ProjectModel;

public class JDemShell
{
	private static Log log = Logging.getLog(JDemShell.class);
	private static JDem846Properties properties = new JDem846Properties(JDem846Properties.UI_PROPERTIES);
	
	private static Display display;
	private static Shell shell;
	
	private static Menu menu;
	private static CoolBar coolBar;
	private static CTabFolder tabFolder;
	
	private TabPanel selectedPanel = null;
	
	public JDemShell()
	{
		display = new Display();
		shell = new Shell(display);
		
		shell.setLayout(new GridLayout(1, true));
		
		shell.setImage(ImageFactory.loadImageResource("/us/wthr/jdem846/images/jdem846-icon.png"));
		
		//shell.setText(properties.getProperty("us.wthr.jdem846.ui.windowTitle"));
		setTitle(null);
		int shellWidth = properties.getIntProperty("us.wthr.jdem846.ui.windowWidth");
		int shellHeight = properties.getIntProperty("us.wthr.jdem846.ui.windowHeight");
		shell.setSize(shellWidth, shellHeight);
		Monitor primary = display.getPrimaryMonitor ();
		Rectangle bounds = primary.getBounds ();
		Rectangle rect = shell.getBounds ();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation (x, y);
		
		menu = ShellMenu.createShellMenu(shell);
		initMainMenu(menu);
		shell.setMenuBar(menu);
		
		coolBar = new CoolBar(shell, SWT.NONE);
		coolBar.addListener(SWT.Resize, new Listener() {
	        public void handleEvent(Event event) {
	            shell.layout();
	        }
	    });
		initMainToolbar(coolBar);
		
		tabFolder = new CTabFolder(shell, SWT.BORDER);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		tabFolder.setSimple(false);
		tabFolder.setUnselectedImageVisible(false);
		tabFolder.setUnselectedCloseVisible(false);
		
		tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
			public void close(CTabFolderEvent arg0)
			{
				CTabItem tabItem = tabFolder.getSelection();
				if (tabItem.getControl() instanceof TabPanel) {
					TabPanel panel = (TabPanel) tabItem.getControl();
					panel.onPanelClosed();
				}
				onTabSelectionChanged();
			}
		});
		tabFolder.addSelectionListener(new SelectionListener() {
		
			public void widgetDefaultSelected(SelectionEvent event)
			{
				log.info("widgetDefaultSelected()");
				onTabSelectionChanged();
			}
			public void widgetSelected(SelectionEvent event)
			{
				onTabSelectionChanged();
				
			}
		});
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		coolBar.setLayoutData(gridData);
		
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		tabFolder.setLayoutData(gridData);
		
		
		
		
		
		
		
		//shell.pack();
	}
	
	protected void onTabSelectionChanged()
	{
		CTabItem tabItem = tabFolder.getSelection();
		TabPanel panel = (TabPanel) tabItem.getControl();
		setTitle(panel.getTitle());
		panel.onPanelVisible();
		
		// If selectedPanel is not null and it's not the same as the new one
		if (selectedPanel != null && selectedPanel != panel) {
			selectedPanel.onPanelHidden();
		}
		selectedPanel = panel;
	}
	
	protected void initMainToolbar(CoolBar coolBar)
	{
		ToolBar toolBar = new ToolBar(coolBar, SWT.FLAT);
		
		
		ToolItem toolItem = null;
		
		toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setImage(ImageFactory.loadImageResource("/us/wthr/jdem846/ui/icons/document-new.png"));
		
		if (properties.getBooleanProperty("us.wthr.jdem846.ui.mainToolBar.displayText"))
			toolItem.setText(I18N.get("us.wthr.jdem846.ui.topToolbar.newProjectButton"));
		toolItem.setToolTipText(I18N.get("us.wthr.jdem846.ui.topToolbar.newProjectTooltip"));
		toolItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event)
			{
				onNew();
			}
		});
		
		toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setImage(ImageFactory.loadImageResource("/us/wthr/jdem846/ui/icons/document-open.png"));
		if (properties.getBooleanProperty("us.wthr.jdem846.ui.mainToolBar.displayText"))
			toolItem.setText(I18N.get("us.wthr.jdem846.ui.topToolbar.openProjectButton"));
		toolItem.setToolTipText(I18N.get("us.wthr.jdem846.ui.topToolbar.openProjectTooltip"));
		toolItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event)
			{
				onOpen();
			}
		});
		
		toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setImage(ImageFactory.loadImageResource("/us/wthr/jdem846/ui/icons/document-save.png"));
		if (properties.getBooleanProperty("us.wthr.jdem846.ui.mainToolBar.displayText"))
			toolItem.setText(I18N.get("us.wthr.jdem846.ui.topToolbar.saveProjectButton"));
		toolItem.setToolTipText(I18N.get("us.wthr.jdem846.ui.topToolbar.saveProjectTooltip"));
		toolItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event)
			{
				onSave();
			}
		});
		
		toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setImage(ImageFactory.loadImageResource("/us/wthr/jdem846/ui/icons/application-exit.png"));
		if (properties.getBooleanProperty("us.wthr.jdem846.ui.mainToolBar.displayText"))
			toolItem.setText(I18N.get("us.wthr.jdem846.ui.topToolbar.exitButton"));
		toolItem.setToolTipText(I18N.get("us.wthr.jdem846.ui.topToolbar.exitTooltip"));
		toolItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event)
			{
				onExitApplication();
			}
		});
		
		
		toolBar.pack();
		
		Point size = toolBar.getSize();
		CoolItem item = new CoolItem(coolBar, SWT.NONE);
	    item.setControl(toolBar);
	    Point preferred = item.computeSize(size.x, size.y);
	    item.setPreferredSize(preferred);

	}
	
	protected void initMainMenu(Menu menu)
	{
		MenuItem fileItem = new MenuItem (menu, SWT.CASCADE);
		fileItem.setText (I18N.get("us.wthr.jdem846.ui.menu.file"));
		
		
		Menu submenu = new Menu (shell, SWT.DROP_DOWN);
		
		fileItem.setMenu (submenu);
		
		MenuItemFactory.createMenuItem(submenu, ImageFactory.loadImageResource("/us/wthr/jdem846/ui/icons/document-new.png"), I18N.get("us.wthr.jdem846.swtui.menu.file.new"), new Listener () {
			public void handleEvent (Event e) {
				onNew();
			}
		}, SWT.MOD1 + 'N');
		
		MenuItemFactory.createMenuItem(submenu, ImageFactory.loadImageResource("/us/wthr/jdem846/ui/icons/document-open.png"), I18N.get("us.wthr.jdem846.swtui.menu.file.open"), new Listener () {
			public void handleEvent (Event e) {
				onOpen();
			}
		}, SWT.MOD1 + 'O');
		
		MenuItemFactory.createMenuItem(submenu, ImageFactory.loadImageResource("/us/wthr/jdem846/ui/icons/document-save.png"), I18N.get("us.wthr.jdem846.swtui.menu.file.save"), new Listener () {
			public void handleEvent (Event e) {
				onSave();
			}
		}, SWT.MOD1 + 'S');
		
		
		MenuItemFactory.createMenuItem(submenu, ImageFactory.loadImageResource("/us/wthr/jdem846/ui/icons/application-exit.png"), I18N.get("us.wthr.jdem846.swtui.menu.file.exit"), new Listener () {
			public void handleEvent (Event e) {
				onExitApplication();
			}
		}, SWT.MOD1 + 'Q');
	}
	
	protected void onNew()
	{
		log.info("On New Action");
		createNewProject(null);
	}
	
	protected void onOpen()
	{
		log.info("On Open Action");
		
		FileDialog dialog = new FileDialog (shell, SWT.OPEN | SWT.MULTI);
		
		String [] filterNames = new String [] {I18N.get("us.wthr.jdem846.ui.projectFormatName") + " (*.xdem)"};
		String [] filterExtensions = new String [] {"*.xdem"};
		
		dialog.setFilterNames (filterNames);
		dialog.setFilterExtensions (filterExtensions);
		
		dialog.open();
		
		String[] fileNames = dialog.getFileNames();
		
		for (String fileName : fileNames) {
			createNewProject(dialog.getFilterPath() + "/" + fileName);
		}
		
	}
	
	protected void onSave()
	{
		log.info("On Save Action");
	}
	
	protected void onExitApplication()
	{
		log.info("Exiting application UI");
		shell.close();
	}
	
	
	
	public void createNewProject(String filePath)
	{
		
		try {
			ProjectModel projectModel = null;
			if (filePath != null) {
				log.info("Opening file: " + filePath);
				projectModel = ProjectFileReader.readProject(filePath);
			}
			
			String title = I18N.get("us.wthr.jdem846.ui.defaultProjectTitle");
			if (projectModel != null && projectModel.getLoadedFrom() != null) {
				File f = new File(projectModel.getLoadedFrom());
				title = f.getName();
			}
			
			ProjectPanel panel = new ProjectPanel(tabFolder, projectModel);
			panel.setTitle(title);
			CTabItem tabItem = new CTabItem(tabFolder, SWT.CLOSE);
			
			
			tabItem.setText(title);
			tabItem.setControl(panel);
			tabFolder.setSelection(tabFolder.getItemCount()-1);
			setTitle(title);
			
			onTabSelectionChanged();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ProjectParseException ex) {
			ex.printStackTrace();
		}
	}
	
	
	
	
	
	
	public boolean isDisposed()
	{
		return shell.isDisposed();
	}
	
	
	public void openAndWait()
	{
		open();
		while (!isDisposed()) {
			if (!readAndDispatch())
				sleep();
		}
		dispose();
	}
	
	public void open()
	{
		shell.open();
	}
	
	public boolean readAndDispatch()
	{
		return display.readAndDispatch();
	}
	
	public boolean sleep()
	{
		return display.sleep();
	}
	
	public void dispose()
	{
		display.dispose();
	}
	
	
	public void setTitle(String title)
	{
		String appTitle = properties.getProperty("us.wthr.jdem846.ui.windowTitle");
		String wndTitle = "";
		
		if (title != null)
			wndTitle = title + " | " + appTitle;
		else
			wndTitle = appTitle;
		
		shell.setText(wndTitle);
	}
	
	public static Display getDisplayInstance()
	{
		return display;
	}
	
	public static Shell getShellInstance()
	{
		return shell;
	}
	
	public static Menu getMenuInstance()
	{
		return menu;
	}
	
	public static CoolBar getCoolBarInstance()
	{
		return coolBar;
	}
}

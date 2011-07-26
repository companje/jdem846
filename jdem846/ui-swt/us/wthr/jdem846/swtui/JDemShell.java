package us.wthr.jdem846.swtui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class JDemShell
{
	private static Log log = Logging.getLog(JDemShell.class);
	private static JDem846Properties properties = new JDem846Properties(JDem846Properties.UI_PROPERTIES);
	
	private static Display display;
	private static Shell shell;
	
	private static CoolBar coolBar;
	private static CTabFolder tabFolder;
	
	
	public JDemShell()
	{
		display = new Display();
		shell = new Shell(display);
		
		shell.setLayout(new GridLayout(1, true));
		
		shell.setImage(ImageFactory.loadImageResource("/us/wthr/jdem846/images/jdem846-icon.png"));
		
		shell.setText(properties.getProperty("us.wthr.jdem846.ui.windowTitle"));
		shell.setSize(properties.getIntProperty("us.wthr.jdem846.ui.windowWidth"), properties.getIntProperty("us.wthr.jdem846.ui.windowHeight"));
		
		Menu menu = ShellMenu.createShellMenu(shell);
		initMainMenu(menu);
		shell.setMenuBar(menu);
		
		coolBar = new CoolBar(shell, SWT.NONE);
		initMainToolbar(coolBar);
		
		tabFolder = new CTabFolder(shell, SWT.BORDER);
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		tabFolder.setLayoutData(gridData);
		
		
		//shell.pack();
	}
	
	protected void initMainToolbar(CoolBar coolBar)
	{
		ToolBar toolBar = new ToolBar(coolBar, SWT.FLAT);
		
		ToolItem toolItem = null;
		
		toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setImage(ImageFactory.loadImageResource("/us/wthr/jdem846/ui/icons/dim24x24/document-new.png"));
		toolItem.setText(I18N.get("us.wthr.jdem846.ui.topToolbar.newProjectButton"));
		toolItem.setToolTipText(I18N.get("us.wthr.jdem846.ui.topToolbar.newProjectTooltip"));
		toolItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event)
			{
				onNew();
			}
		});
		
		toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setImage(ImageFactory.loadImageResource("/us/wthr/jdem846/ui/icons/dim24x24/document-open.png"));
		toolItem.setText(I18N.get("us.wthr.jdem846.ui.topToolbar.openProjectButton"));
		toolItem.setToolTipText(I18N.get("us.wthr.jdem846.ui.topToolbar.openProjectTooltip"));
		toolItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event)
			{
				onOpen();
			}
		});
		
		toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setImage(ImageFactory.loadImageResource("/us/wthr/jdem846/ui/icons/dim24x24/document-save.png"));
		toolItem.setText(I18N.get("us.wthr.jdem846.ui.topToolbar.saveProjectButton"));
		toolItem.setToolTipText(I18N.get("us.wthr.jdem846.ui.topToolbar.saveProjectTooltip"));
		toolItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event)
			{
				onSave();
			}
		});
		
		toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setImage(ImageFactory.loadImageResource("/us/wthr/jdem846/ui/icons/dim24x24/application-exit.png"));
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
		
		MenuItemFactory.createMenuItem(submenu, ImageFactory.loadImageResource("/us/wthr/jdem846/ui/icons/document-new.png"), I18N.get("us.wthr.jdem846.ui.menu.file.new"), new Listener () {
			public void handleEvent (Event e) {
				onNew();
			}
		}, SWT.MOD1 + 'N');
		
		MenuItemFactory.createMenuItem(submenu, ImageFactory.loadImageResource("/us/wthr/jdem846/ui/icons/document-open.png"), I18N.get("us.wthr.jdem846.ui.menu.file.open"), new Listener () {
			public void handleEvent (Event e) {
				onOpen();
			}
		}, SWT.MOD1 + 'O');
		
		MenuItemFactory.createMenuItem(submenu, ImageFactory.loadImageResource("/us/wthr/jdem846/ui/icons/document-save.png"), I18N.get("us.wthr.jdem846.ui.menu.file.save"), new Listener () {
			public void handleEvent (Event e) {
				onSave();
			}
		}, SWT.MOD1 + 'S');
		
		
		MenuItemFactory.createMenuItem(submenu, ImageFactory.loadImageResource("/us/wthr/jdem846/ui/icons/application-exit.png"), I18N.get("us.wthr.jdem846.ui.menu.file.exit"), new Listener () {
			public void handleEvent (Event e) {
				onExitApplication();
			}
		}, SWT.MOD1 + 'Q');
	}
	
	protected void onNew()
	{
		log.info("On New Action");
	}
	
	protected void onOpen()
	{
		log.info("On Open Action");
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
	
	
	
	public static Display getDisplayInstance()
	{
		return display;
	}
	
	public static Shell getShellInstance()
	{
		return shell;
	}
	
	public static CoolBar getCoolBarInstance()
	{
		return coolBar;
	}
}

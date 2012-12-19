package us.wthr.jdem846ui;


import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import us.wthr.jdem846ui.actions.AddDataAction;
import us.wthr.jdem846ui.actions.ExportDataAction;
import us.wthr.jdem846ui.actions.NewProjectAction;
import us.wthr.jdem846ui.actions.OpenProjectAction;
import us.wthr.jdem846ui.actions.RemoveDataAction;
import us.wthr.jdem846ui.actions.RenderAction;
import us.wthr.jdem846ui.actions.SaveProjectAction;
import us.wthr.jdem846ui.actions.SaveProjectAsAction;
import us.wthr.jdem846ui.actions.ZoomActualAction;
import us.wthr.jdem846ui.actions.ZoomFitAction;
import us.wthr.jdem846ui.actions.ZoomInAction;
import us.wthr.jdem846ui.actions.ZoomOutAction;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Actions - important to allocate these only in makeActions, and then use
	// them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.


    // Actions - important to allocate these only in makeActions, and then use them
    // in the fill methods.  This ensures that the actions aren't recreated
    // when fillActionBars is called with FILL_PROXY.
    private IWorkbenchAction exitAction;
    private IWorkbenchAction prefsAction;
    private IWorkbenchAction aboutAction;
    private NewProjectAction newProjectAction;
    private OpenProjectAction openProjectAction;
    private SaveProjectAction saveProjectAction;
    private SaveProjectAsAction saveProjectAsAction;
    
    private ZoomInAction zoomInAction;
    private ZoomOutAction zoomOutAction;
    private ZoomActualAction zoomActualAction;
    private ZoomFitAction zoomFitAction;
    
    private AddDataAction addDataAction;
    private RemoveDataAction removeDataAction;
    private ExportDataAction exportDataAction;
    private RenderAction renderAction;
    
    private IWorkbenchAction cutAction;
    private IWorkbenchAction copyAction;
    private IWorkbenchAction pasteAction;
    private IWorkbenchAction undoAction;
    private IWorkbenchAction redoAction;
    
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }
    
    protected void makeActions(final IWorkbenchWindow window) {
        // Creates the actions and registers them.
        // Registering is needed to ensure that key bindings work.
        // The corresponding commands keybindings are defined in the plugin.xml file.
        // Registering also provides automatic disposal of the actions when
        // the window is closed.
    	
        exitAction = ActionFactory.QUIT.create(window);
        register(exitAction);
        
        
        cutAction = ActionFactory.CUT.create(window);
        register(cutAction);
        
        copyAction = ActionFactory.COPY.create(window);
        register(copyAction);
        
        pasteAction = ActionFactory.PASTE.create(window);
        register(pasteAction);
        
        undoAction = ActionFactory.UNDO.create(window);
        register(undoAction);
        
        redoAction = ActionFactory.REDO.create(window);
        register(redoAction);
        
        prefsAction = ActionFactory.PREFERENCES.create(window);
        register(prefsAction);
        
        aboutAction = ActionFactory.ABOUT.create(window);
        register(aboutAction);
        
        newProjectAction = new NewProjectAction(window, "New Project", View.ID);
        openProjectAction = new OpenProjectAction(window, "Open Project", View.ID);
        saveProjectAction = new SaveProjectAction(window, "Save Project", View.ID);
        saveProjectAsAction = new SaveProjectAsAction(window, "Save Project As", View.ID);
        
        addDataAction = new AddDataAction(window, "Add", View.ID);
        removeDataAction = new RemoveDataAction(window, "Remove", View.ID);
        exportDataAction = new ExportDataAction(window, "Export", View.ID);
        renderAction = new RenderAction(window, "Render", View.ID);
        
        zoomInAction = new ZoomInAction(window, "Zoom In", View.ID);
        zoomOutAction = new ZoomOutAction(window, "Zoom Out", View.ID);
        zoomActualAction = new ZoomActualAction(window, "Zoom Actual", View.ID);
        zoomFitAction = new ZoomFitAction(window, "Zoom Fit", View.ID);
        
        
        register(newProjectAction);
        register(openProjectAction);
        register(saveProjectAction);
        register(saveProjectAsAction);
        register(addDataAction);
        register(removeDataAction);
        register(exportDataAction);
        register(renderAction);
        
        register(zoomInAction);
        register(zoomOutAction);
        register(zoomActualAction);
        register(zoomFitAction);
        
        //newWindowAction = ActionFactory.OPEN_NEW_WINDOW.create(window);
        //register(newWindowAction);
        
      //  openViewAction = new OpenViewAction(window, "Open Another Message View", View.ID);
       // register(openViewAction);
        
      //  messagePopupAction = new MessagePopupAction("Open Message", window);
       // register(messagePopupAction);
    }
    
    protected void fillMenuBar(IMenuManager menuBar) {
        MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
        MenuManager editMenu = new MenuManager("&Edit", IWorkbenchActionConstants.M_EDIT);
        MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);
        
        MenuManager windowMenu = new MenuManager("&Window", IWorkbenchActionConstants.M_WINDOW);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        // Add a group marker indicating where action set menus will appear.
        menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        menuBar.add(windowMenu);
        menuBar.add(helpMenu);
        
        // File
        //fileMenu.add(newWindowAction);
        fileMenu.add(newProjectAction);
        fileMenu.add(openProjectAction);
        fileMenu.add(new Separator());
        fileMenu.add(saveProjectAction);
        fileMenu.add(saveProjectAsAction);
       // fileMenu.add(new Separator());
       // fileMenu.add(messagePopupAction);
       // fileMenu.add(openViewAction);
        fileMenu.add(new Separator());
        fileMenu.add(exitAction);
        
        editMenu.add(undoAction);
        editMenu.add(redoAction);
        editMenu.add(new Separator());
        editMenu.add(cutAction);
        editMenu.add(copyAction);
        editMenu.add(pasteAction);
        
        
        
        windowMenu.add(prefsAction);
        
        // Help
        helpMenu.add(aboutAction);
    }
    
    protected void fillCoolBar(ICoolBarManager coolBar) {
        IToolBarManager mainToolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
        coolBar.add(new ToolBarContributionItem(mainToolbar, "main"));   
        mainToolbar.add(newProjectAction);
        mainToolbar.add(openProjectAction);
        mainToolbar.add(saveProjectAction);
        mainToolbar.add(saveProjectAsAction);
        
        IToolBarManager editToolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
        coolBar.add(new ToolBarContributionItem(editToolbar, "edit"));   
        editToolbar.add(cutAction);
        editToolbar.add(copyAction);
        editToolbar.add(pasteAction);
        editToolbar.add(undoAction);
        editToolbar.add(redoAction);
        
        IToolBarManager dataToolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
        coolBar.add(new ToolBarContributionItem(dataToolbar, "data"));
        dataToolbar.add(addDataAction);
        dataToolbar.add(removeDataAction);
        dataToolbar.add(exportDataAction);
        dataToolbar.add(renderAction);

        IToolBarManager modelToolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
        coolBar.add(new ToolBarContributionItem(modelToolbar, "model"));
        modelToolbar.add(zoomInAction);
        modelToolbar.add(zoomOutAction);
        modelToolbar.add(zoomActualAction);
        modelToolbar.add(zoomFitAction);
        //toolbar.add(openViewAction);
       // toolbar.add(messagePopupAction);
    }
}

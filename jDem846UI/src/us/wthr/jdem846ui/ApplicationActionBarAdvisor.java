package us.wthr.jdem846ui;


import org.eclipse.jface.action.Action;
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
import org.eclipse.ui.internal.about.AboutAction;

import us.wthr.jdem846ui.actions.AddDataAction;
import us.wthr.jdem846ui.actions.ExportDataAction;
import us.wthr.jdem846ui.actions.NewProjectAction;
import us.wthr.jdem846ui.actions.OpenProjectAction;
import us.wthr.jdem846ui.actions.RemoveDataAction;
import us.wthr.jdem846ui.actions.RenderAction;

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
    private IWorkbenchAction aboutAction;
    private NewProjectAction newProjectAction;
    private OpenProjectAction openProjectAction;
    
    private AddDataAction addDataAction;
    private RemoveDataAction removeDataAction;
    private ExportDataAction exportDataAction;
    private RenderAction renderAction;

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
        
        aboutAction = ActionFactory.ABOUT.create(window);
        register(aboutAction);
        
        newProjectAction = new NewProjectAction(window, "New Project", View.ID);
        openProjectAction = new OpenProjectAction(window, "Open Project", View.ID);
        addDataAction = new AddDataAction(window, "Add", View.ID);
        removeDataAction = new RemoveDataAction(window, "Remove", View.ID);
        exportDataAction = new ExportDataAction(window, "Export", View.ID);
        renderAction = new RenderAction(window, "Render", View.ID);
        
        register(newProjectAction);
        register(openProjectAction);
        register(addDataAction);
        register(removeDataAction);
        register(exportDataAction);
        register(renderAction);
        
        //newWindowAction = ActionFactory.OPEN_NEW_WINDOW.create(window);
        //register(newWindowAction);
        
      //  openViewAction = new OpenViewAction(window, "Open Another Message View", View.ID);
       // register(openViewAction);
        
      //  messagePopupAction = new MessagePopupAction("Open Message", window);
       // register(messagePopupAction);
    }
    
    protected void fillMenuBar(IMenuManager menuBar) {
        MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
        MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);
        
        menuBar.add(fileMenu);
        // Add a group marker indicating where action set menus will appear.
        menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        menuBar.add(helpMenu);
        
        // File
        fileMenu.add(newProjectAction);
        fileMenu.add(openProjectAction);
        fileMenu.add(new Separator());
       // fileMenu.add(messagePopupAction);
       // fileMenu.add(openViewAction);
        fileMenu.add(new Separator());
        fileMenu.add(exitAction);
        
        // Help
        helpMenu.add(aboutAction);
    }
    
    protected void fillCoolBar(ICoolBarManager coolBar) {
        IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
        coolBar.add(new ToolBarContributionItem(toolbar, "main"));   
        toolbar.add(newProjectAction);
        toolbar.add(openProjectAction);
        //toolbar.add(openViewAction);
       // toolbar.add(messagePopupAction);
    }
}

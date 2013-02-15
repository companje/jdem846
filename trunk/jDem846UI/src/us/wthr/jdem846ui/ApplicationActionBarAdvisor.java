package us.wthr.jdem846ui;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import us.wthr.jdem846ui.actions.AddDataAction;
import us.wthr.jdem846ui.actions.DeleteRenderedModelAction;
import us.wthr.jdem846ui.actions.ExportDataAction;
import us.wthr.jdem846ui.actions.NewProjectAction;
import us.wthr.jdem846ui.actions.OpenProjectAction;
import us.wthr.jdem846ui.actions.RemoveDataAction;
import us.wthr.jdem846ui.actions.RenderAction;
import us.wthr.jdem846ui.actions.SaveProjectAction;
import us.wthr.jdem846ui.actions.SaveProjectAsAction;
import us.wthr.jdem846ui.actions.UpdatePreviewAction;
import us.wthr.jdem846ui.actions.UpdatePreviewWithDataRefreshAction;
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

	private Map<String, IAction> actionMap = new HashMap<String, IAction>();

	private static ApplicationActionBarAdvisor INSTANCE = null;
	
	
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
        ApplicationActionBarAdvisor.INSTANCE = this;
    }
    
    public static ApplicationActionBarAdvisor getInstance()
    {
    	return ApplicationActionBarAdvisor.INSTANCE;
    }
    
    protected void putAction(IAction action, String id)
    {
    	register(action);
    	actionMap.put(id,  action);
    }
    
    public IAction getAction(String id)
    {
    	return actionMap.get(id);
    }
    
    protected void makeActions(final IWorkbenchWindow window) {

    	putAction(ActionFactory.QUIT.create(window), ActionFactory.QUIT.getId());
    	putAction(ActionFactory.CUT.create(window), ActionFactory.CUT.getId());
    	putAction(ActionFactory.COPY.create(window), ActionFactory.COPY.getId());
    	putAction(ActionFactory.PASTE.create(window), ActionFactory.PASTE.getId());
    	putAction(ActionFactory.UNDO.create(window), ActionFactory.UNDO.getId());
    	putAction(ActionFactory.REDO.create(window), ActionFactory.REDO.getId());
    	putAction(ActionFactory.PREFERENCES.create(window), ActionFactory.PREFERENCES.getId());
    	putAction(ActionFactory.ABOUT.create(window), ActionFactory.ABOUT.getId());

        
    	putAction(new NewProjectAction(window, "New Project", View.ID), ICommandIds.CMD_NEW);
    	
    	
    	
        
    	putAction(new NewProjectAction(window, "New Project", View.ID), ICommandIds.CMD_NEW);
    	putAction(new OpenProjectAction(window, "Open Project", View.ID), ICommandIds.CMD_OPEN);
    	putAction(new SaveProjectAction(window, "Save Project", View.ID), ICommandIds.CMD_SAVE);
    	putAction(new SaveProjectAsAction(window, "Save Project As", View.ID), ICommandIds.CMD_SAVE_AS);
        
    	putAction(new AddDataAction(window, "Add", View.ID), ICommandIds.CMD_ADD_DATA);
    	putAction(new RemoveDataAction(window, "Remove", View.ID), ICommandIds.CMD_REMOVE_DATA);
    	
    	putAction(new UpdatePreviewAction(window, "Update Preview", View.ID), ICommandIds.CMD_UPDATE_PREVIEW);
    	putAction(new UpdatePreviewWithDataRefreshAction(window, "Update Preview", View.ID), ICommandIds.CMD_UPDATE_PREVIEW);
    	
    	putAction(new ExportDataAction(window, "Export", View.ID), ICommandIds.CMD_EXPORT_DATA);
    	putAction(new RenderAction(window, "Render", View.ID), ICommandIds.CMD_RENDER);
        
    	putAction(new ZoomInAction(window, "Zoom In", View.ID), ICommandIds.CMD_ZOOM_IN);
    	putAction(new ZoomOutAction(window, "Zoom Out", View.ID), ICommandIds.CMD_ZOOM_OUT);
    	putAction(new ZoomActualAction(window, "Zoom Actual", View.ID), ICommandIds.CMD_ZOOM_ACTUAL);
    	putAction(new ZoomFitAction(window, "Zoom Fit", View.ID), ICommandIds.CMD_ZOOM_FIT);
    	putAction(new DeleteRenderedModelAction(window, "Delete Model", View.ID), ICommandIds.CMD_DELETE_MODEL);

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
        fileMenu.add(getAction(ICommandIds.CMD_NEW));
        fileMenu.add(getAction(ICommandIds.CMD_OPEN));
        fileMenu.add(new Separator());
        fileMenu.add(getAction(ICommandIds.CMD_SAVE));
        fileMenu.add(getAction(ICommandIds.CMD_SAVE_AS));
        fileMenu.add(new Separator());
        fileMenu.add(getAction(ActionFactory.QUIT.getId()));
        
        editMenu.add(getAction(ActionFactory.UNDO.getId()));
        editMenu.add(getAction(ActionFactory.REDO.getId()));
        editMenu.add(new Separator());
        editMenu.add(getAction(ActionFactory.CUT.getId()));
        editMenu.add(getAction(ActionFactory.COPY.getId()));
        editMenu.add(getAction(ActionFactory.PASTE.getId()));
        
        
        
        windowMenu.add(getAction(ActionFactory.PREFERENCES.getId()));
        
        // Help
        helpMenu.add(getAction(ActionFactory.ABOUT.getId()));
    }
    
    protected void fillCoolBar(ICoolBarManager coolBar) {
    }
}

package us.wthr.jdem846ui.views.data;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.image.ImageDataContext;
import us.wthr.jdem846.image.SimpleGeoImage;
import us.wthr.jdem846.input.InputSourceData;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.modelgrid.ModelGridContext;
import us.wthr.jdem846.modelgrid.ModelGridHeader;
import us.wthr.jdem846.project.context.ProjectChangeAdapter;
import us.wthr.jdem846.project.context.ProjectContext;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.shapedata.ShapeDataContext;
import us.wthr.jdem846.shapefile.ShapeFileRequest;
import us.wthr.jdem846ui.ApplicationActionBarAdvisor;
import us.wthr.jdem846ui.ICommandIds;
import us.wthr.jdem846ui.observers.RenderedModelSelectionObserver;
import us.wthr.jdem846ui.project.IconEnum;
import us.wthr.jdem846ui.views.tree.TreeObject;
import us.wthr.jdem846ui.views.tree.ViewContentProvider;

public class DataView extends ViewPart
{
	private static Log log = Logging.getLog(DataView.class);

	public static final String ID = "jdem846ui.dataView";

	private TreeViewer viewer;
	private static List<TreeSelectionListener> treeSelectionListeners = new LinkedList<TreeSelectionListener>();

	private static DataView INSTANCE;

	public DataView()
	{
		DataView.INSTANCE = this;
	}

	public static DataView getInstance()
	{
		return DataView.INSTANCE;
	}

	public static void addTreeSelectionListener(TreeSelectionListener l)
	{
		synchronized (treeSelectionListeners) {
			DataView.treeSelectionListeners.add(l);
		}
	}

	public static boolean removeTreeSelectionListener(TreeSelectionListener l)
	{
		synchronized (treeSelectionListeners) {
			return DataView.treeSelectionListeners.remove(l);
		}
	}

	protected void fireOnSourceDataSelectionChanged(InputSourceData selectedData)
	{
		List<TreeSelectionListener> treeSelectionListenersList = getSelectionListenersListCopy();
		for (TreeSelectionListener listener : treeSelectionListenersList) {
			listener.onSourceDataSelectionChanged(selectedData);
		}

	}

	protected void fireOnRenderedModelSelectionChanged(ElevationModel elevationModel)
	{
		List<TreeSelectionListener> treeSelectionListenersList = getSelectionListenersListCopy();
		for (TreeSelectionListener listener : treeSelectionListenersList) {
			listener.onRenderedModelSelectionChanged(elevationModel);
		}

	}

	protected List<TreeSelectionListener> getSelectionListenersListCopy()
	{
		List<TreeSelectionListener> copy = new LinkedList<TreeSelectionListener>();
		synchronized (treeSelectionListeners) {
			copy.addAll(treeSelectionListeners);
		}
		return copy;
	}

	private TreeObject<?> createTreeModel()
	{
		DataTreeParent dataSources = new DataTreeParent("Data Sources", IconEnum.DATA_SOURCE_CATEGORY);
		DataTreeParent renderedModels = new DataTreeParent("Completed Models", IconEnum.RENDERED_MODEL_CATEGORY);

		DataTreeParent root = new DataTreeParent("", IconEnum.NONE);
		root.addChild(dataSources);
		root.addChild(renderedModels);

		createDataSourceTreeModel(dataSources);
		createRenderedModelTreeModel(renderedModels);

		return root;
	}

	protected void createRenderedModelTreeModel(DataTreeParent parent)
	{
		for (ElevationModel elevationModel : ProjectContext.getInstance().getElevationModelList()) {
			String modelSubject = elevationModel.getProperty("subject");
			String renderDate = elevationModel.getProperty("render-date");

			String name = renderDate;
			if (modelSubject != null && modelSubject.length() > 0) {
				name = modelSubject + " - " + renderDate;
			}

			DataTreeObject obj = new DataTreeObject(name, elevationModel, IconEnum.RENDERED_MODEL_OBJECT);
			parent.addChild(obj);
		}
	}

	protected void createDataSourceTreeModel(DataTreeParent parent)
	{

		DataTreeParent p0 = new DataTreeParent("Elevation Data", IconEnum.RASTER_CATEGORY);
		DataTreeParent p1 = new DataTreeParent("Shape Data", IconEnum.SHAPE_CATEGORY);
		DataTreeParent p2 = new DataTreeParent("Image Data", IconEnum.IMAGE_CATEGORY);
		DataTreeParent p3 = new DataTreeParent("ModelGrid Data", IconEnum.MODELGRID_CATEGORY);

		parent.addChild(p0);
		RasterDataContext rasterDataContext = ProjectContext.getInstance().getRasterDataContext();
		for (RasterData rasterData : rasterDataContext.getRasterDataList()) {
			File f = new File(rasterData.getFilePath());
			DataTreeObject obj = new DataTreeObject(f.getName(), rasterData, IconEnum.RASTER_DATA);
			p0.addChild(obj);
		}

		ShapeDataContext shapeDataContext = ProjectContext.getInstance().getShapeDataContext();
		if (shapeDataContext.getShapeDataListSize() > 0) {
			parent.addChild(p1);
			for (ShapeFileRequest shapeFile : shapeDataContext.getShapeFiles()) {
				File f = new File(shapeFile.getPath());
				DataTreeObject obj = new DataTreeObject(f.getName(), shapeFile, IconEnum.SHAPE_DATA);
				p1.addChild(obj);
			}
		}

		ImageDataContext imageDataContext = ProjectContext.getInstance().getImageDataContext();
		if (imageDataContext.getImageListSize() > 0) {
			parent.addChild(p2);
			for (SimpleGeoImage image : imageDataContext.getImageList()) {
				File f = new File(image.getImageFile());
				DataTreeObject obj = new DataTreeObject(f.getName(), image, IconEnum.IMAGE_DATA);
				p2.addChild(obj);
			}
		}

		ModelGridContext modelGridContext = ProjectContext.getInstance().getModelGridContext();
		ModelGridHeader modelGridHeader = (modelGridContext != null) ? modelGridContext.getUserProvidedModelGridHeader() : null;
		String gridLoadedFrom = (modelGridContext != null) ? modelGridContext.getGridLoadedFrom() : null;
		if (modelGridHeader != null && gridLoadedFrom != null) {
			parent.addChild(p3);
			File f = new File(gridLoadedFrom);
			DataTreeObject obj = new DataTreeObject(f.getName(), modelGridHeader, IconEnum.MODELGRID_DATA);
			p3.addChild(obj);
		}

	}

	protected void resetAndUpdateModelAsync()
	{
		Display.getDefault().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{
				resetAndUpdateModel();
			}

		});
	}

	private void resetAndUpdateModel()
	{
		viewer.setInput(createTreeModel());
		viewer.expandAll();
	}

	
	
	public void fillContextMenu(IMenuManager manager)
	{
		List<TreeObject<?>> selections = getSelectedTreeNode();
		
		boolean inputDataSelected = false;
		boolean elevationModelSelected = false;
		
		for (TreeObject<?> treeObject : selections) {
			if (treeObject instanceof DataTreeObject && ((DataTreeObject) treeObject).getData() instanceof InputSourceData) {
				inputDataSelected = true;
			}
			if (treeObject instanceof DataTreeObject && ((DataTreeObject) treeObject).getData() instanceof ElevationModel) {
				elevationModelSelected = true;
			}
		}
		
		
		
		manager.add(ApplicationActionBarAdvisor.getInstance().getAction(ICommandIds.CMD_ADD_DATA));
		
		if (inputDataSelected) {
			manager.add(ApplicationActionBarAdvisor.getInstance().getAction(ICommandIds.CMD_REMOVE_DATA));
		}
		
		if (elevationModelSelected) {
			manager.add(ApplicationActionBarAdvisor.getInstance().getAction(ICommandIds.CMD_DELETE_MODEL));
		}
	}
	
	
	protected List<TreeObject<?>> getSelectedTreeNode()
	{
		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		return getSelectedTreeNode(selection);
	}
	
	
	protected List<TreeObject<?>> getSelectedTreeNode(IStructuredSelection selection)
	{
		List<TreeObject<?>> selections = new LinkedList<TreeObject<?>>();
		
		if (!selection.isEmpty()) {
			for (Iterator<TreeObject<?>> iter = (Iterator<TreeObject<?>>) selection.iterator(); iter.hasNext();) {
				TreeObject<?> treeObject = iter.next();
				selections.add(treeObject);
			}
		}
		
		return selections;
	}
	
	
	@Override
	public void createPartControl(Composite parent)
	{

		viewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		
		
		MenuManager menuMgr = new MenuManager();
	    menuMgr.setRemoveAllWhenShown(true);
	    menuMgr.addMenuListener(new IMenuListener() {
	        public void menuAboutToShow(IMenuManager manager) {
	        	DataView.this.fillContextMenu(manager);
	        }
	    });
	    
	    Menu menu = menuMgr.createContextMenu(viewer.getControl());
	    viewer.getControl().setMenu(menu);
	    getSite().registerContextMenu(menuMgr, viewer);
		
		viewer.addPostSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{

				boolean selectedSourceData = false;
				boolean selectedRenderedModel = false;
				

				List<TreeObject<?>> selections = getSelectedTreeNode((IStructuredSelection)event.getSelection());
				for (TreeObject<?> treeObject : selections) {
					if (treeObject instanceof DataTreeObject && ((DataTreeObject) treeObject).getData() instanceof InputSourceData) {
						fireOnSourceDataSelectionChanged((InputSourceData) ((DataTreeObject) treeObject).getData());
						selectedSourceData = true;
					}

					if (treeObject instanceof DataTreeObject && ((DataTreeObject) treeObject).getData() instanceof ElevationModel) {

						fireOnRenderedModelSelectionChanged((ElevationModel) ((DataTreeObject) treeObject).getData());
						selectedRenderedModel = true;

					}
				}
				
				
				
				if (!selectedSourceData && !selectedRenderedModel) {
					fireOnSourceDataSelectionChanged(null);
					fireOnRenderedModelSelectionChanged(null);
				}
			}
		});
		
		viewer.getControl().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent arg0)
			{
				List<TreeObject<?>> selections = getSelectedTreeNode();
				for (TreeObject<?> treeObject : selections) {

					if (treeObject instanceof DataTreeObject && ((DataTreeObject) treeObject).getData() instanceof ElevationModel) {

						RenderedModelSelectionObserver.getInstance().openElevationModel((ElevationModel) ((DataTreeObject) treeObject).getData());

					}
				}
			}

		});
		
		
		ProjectContext.getInstance().addProjectChangeListener(new ProjectChangeAdapter()
		{

			@Override
			public void onDataAdded()
			{
				resetAndUpdateModelAsync();
			}

			@Override
			public void onDataRemoved()
			{
				resetAndUpdateModelAsync();
			}

			@Override
			public void onElevationModelAdded(ElevationModel elevationModel)
			{
				log.info("Model added: Rebuilding rendered model list");
				resetAndUpdateModelAsync();
			}

			@Override
			public void onElevationModelRemoved(ElevationModel elevationModel)
			{
				log.info("Model removed: Rebuilding rendered model list");
				resetAndUpdateModelAsync();
			}

			@Override
			public void onProjectLoaded(String projectPath)
			{
				resetAndUpdateModelAsync();
			}

		});

		resetAndUpdateModel();
		
		
		
		
	}

	@Override
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}

}

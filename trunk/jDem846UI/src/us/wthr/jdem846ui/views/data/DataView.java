package us.wthr.jdem846ui.views.data;

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
import us.wthr.jdem846.shapefile.ShapeBase;
import us.wthr.jdem846ui.ApplicationActionBarAdvisor;
import us.wthr.jdem846ui.ICommandIds;
import us.wthr.jdem846ui.project.IconEnum;
import us.wthr.jdem846ui.views.geoimage.GeoImageTreeObject;
import us.wthr.jdem846ui.views.modelgrid.ModelGridTreeObject;
import us.wthr.jdem846ui.views.models.ElevationModelTreeObject;
import us.wthr.jdem846ui.views.raster.RasterTreeObject;
import us.wthr.jdem846ui.views.shape.ShapeTreeObject;
import us.wthr.jdem846ui.views.tree.TreeObject;
import us.wthr.jdem846ui.views.tree.ViewContentProvider;

public class DataView extends ViewPart
{
	private static Log log = Logging.getLog(DataView.class);

	public static final String ID = "jdem846ui.dataView";

	private TreeViewer viewer;
	private static List<TreeSelectionListener> treeSelectionListeners = new LinkedList<TreeSelectionListener>();

	private static DataView INSTANCE;

	private TreeSelectionListener treeSelectionListener;
	
	public DataView()
	{
		DataView.INSTANCE = this;
		
		treeSelectionListener = new TreeSelectionListener()
		{

			@Override
			public void onSourceDataSelectionChanged(InputSourceData selectedData)
			{
				DataView.INSTANCE.fireOnSourceDataSelectionChanged(selectedData);
			}

			@Override
			public void onRenderedModelSelectionChanged(ElevationModel elevationModel)
			{
				DataView.INSTANCE.fireOnRenderedModelSelectionChanged(elevationModel);
			}
			
		};
		
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
		DataTreeParent dataSources = new DataTreeParent("Data Sources", IconEnum.DATA_SOURCE_CATEGORY, treeSelectionListener);
		DataTreeParent renderedModels = new DataTreeParent("Completed Models", IconEnum.RENDERED_MODEL_CATEGORY, treeSelectionListener);

		DataTreeParent root = new DataTreeParent("", IconEnum.NONE, treeSelectionListener);
		root.addChild(dataSources);
		root.addChild(renderedModels);

		createDataSourceTreeModel(dataSources);
		createRenderedModelTreeModel(renderedModels);

		return root;
	}

	protected void createRenderedModelTreeModel(DataTreeParent parent)
	{
		for (ElevationModel elevationModel : ProjectContext.getInstance().getElevationModelList()) {
			parent.addChild(new ElevationModelTreeObject(elevationModel, treeSelectionListener));
		}
	}

	protected void createDataSourceTreeModel(DataTreeParent parent)
	{

		DataTreeParent p0 = new DataTreeParent("Elevation Data", IconEnum.RASTER_CATEGORY, treeSelectionListener);
		DataTreeParent p1 = new DataTreeParent("Shape Data", IconEnum.SHAPE_CATEGORY, treeSelectionListener);
		DataTreeParent p2 = new DataTreeParent("Image Data", IconEnum.IMAGE_CATEGORY, treeSelectionListener);
		DataTreeParent p3 = new DataTreeParent("ModelGrid Data", IconEnum.MODELGRID_CATEGORY, treeSelectionListener);

		parent.addChild(p0);
		RasterDataContext rasterDataContext = ProjectContext.getInstance().getRasterDataContext();
		for (RasterData rasterData : rasterDataContext.getRasterDataList()) {
			p0.addChild(new RasterTreeObject(rasterData, treeSelectionListener));
		}

		ShapeDataContext shapeDataContext = ProjectContext.getInstance().getShapeDataContext();
		if (shapeDataContext.getShapeDataListSize() > 0) {
			parent.addChild(p1);
			for (ShapeBase shapeBase : shapeDataContext.getShapeFiles()) {
				p1.addChild(new ShapeTreeObject(shapeBase, treeSelectionListener));
			}
		}

		ImageDataContext imageDataContext = ProjectContext.getInstance().getImageDataContext();
		if (imageDataContext.getImageListSize() > 0) {
			parent.addChild(p2);
			for (SimpleGeoImage image : imageDataContext.getImageList()) {
				p2.addChild(new GeoImageTreeObject(image, treeSelectionListener));
			}
		}

		ModelGridContext modelGridContext = ProjectContext.getInstance().getModelGridContext();
		ModelGridHeader modelGridHeader = (modelGridContext != null) ? modelGridContext.getUserProvidedModelGridHeader() : null;
		String gridLoadedFrom = (modelGridContext != null) ? modelGridContext.getGridLoadedFrom() : null;
		if (modelGridHeader != null && gridLoadedFrom != null) {
			parent.addChild(p3);
			p3.addChild(new ModelGridTreeObject(gridLoadedFrom, modelGridHeader, treeSelectionListener));
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
			if (treeObject instanceof DataTreeObject && ((DataTreeObject<?>) treeObject).getData() instanceof InputSourceData) {
				inputDataSelected = true;
			}
			if (treeObject instanceof DataTreeObject && ((DataTreeObject<?>) treeObject).getData() instanceof ElevationModel) {
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
					treeObject.onSelected();
					
					/*
					if (treeObject instanceof DataTreeObject && ((DataTreeObject) treeObject).getData() instanceof InputSourceData) {
						fireOnSourceDataSelectionChanged((InputSourceData) ((DataTreeObject) treeObject).getData());
						selectedSourceData = true;
					}

					if (treeObject instanceof DataTreeObject && ((DataTreeObject) treeObject).getData() instanceof ElevationModel) {

						fireOnRenderedModelSelectionChanged((ElevationModel) ((DataTreeObject) treeObject).getData());
						selectedRenderedModel = true;

					}
					*/
				}
				
				
				
				//if (!selectedSourceData && !selectedRenderedModel) {
				//	fireOnSourceDataSelectionChanged(null);
				//	fireOnRenderedModelSelectionChanged(null);
				//}
			}
		});
		
		viewer.getControl().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent arg0)
			{
				List<TreeObject<?>> selections = getSelectedTreeNode();
				for (TreeObject<?> treeObject : selections) {
					treeObject.onDoubleClick();
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

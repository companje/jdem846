package us.wthr.jdem846ui.views.data;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import us.wthr.jdem846.image.ImageDataContext;
import us.wthr.jdem846.image.SimpleGeoImage;
import us.wthr.jdem846.input.InputSourceData;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.shapedata.ShapeDataContext;
import us.wthr.jdem846.shapefile.ShapeFileRequest;
import us.wthr.jdem846ui.project.IconEnum;
import us.wthr.jdem846ui.project.ProjectChangeAdapter;
import us.wthr.jdem846ui.project.ProjectContext;
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
		DataView.treeSelectionListeners.add(l);
	}
	
	public static boolean removeTreeSelectionListener(TreeSelectionListener l)
	{
		return DataView.treeSelectionListeners.remove(l);
	}
	
	protected void fireOnSelectionChanged(InputSourceData selectedData)
	{
		for (TreeSelectionListener listener : DataView.treeSelectionListeners) {
			listener.onSelectionChanged(selectedData);
		}
	}
	

	private TreeObject createTreeModel() {

        DataTreeParent p0 = new DataTreeParent("Elevation Data", IconEnum.RASTER_CATEGORY);
        DataTreeParent p1 = new DataTreeParent("Shape Data", IconEnum.SHAPE_CATEGORY);
        DataTreeParent p2 = new DataTreeParent("Image Data", IconEnum.IMAGE_CATEGORY);
        
        DataTreeParent root = new DataTreeParent("", IconEnum.NONE);
        
        root.addChild(p0);
        RasterDataContext rasterDataContext = ProjectContext.getInstance().getRasterDataContext();
        for(RasterData rasterData : rasterDataContext.getRasterDataList()) {
        	File f = new File(rasterData.getFilePath());
        	DataTreeObject obj = new DataTreeObject(f.getName(), rasterData, IconEnum.RASTER_DATA);
        	p0.addChild(obj);
        }
        
        root.addChild(p1);
        ShapeDataContext shapeDataContext = ProjectContext.getInstance().getShapeDataContext();
        for (ShapeFileRequest shapeFile : shapeDataContext.getShapeFiles()) {
        	File f = new File(shapeFile.getPath());
        	DataTreeObject obj = new DataTreeObject(f.getName(), shapeFile, IconEnum.SHAPE_DATA);
        	p1.addChild(obj);
        }
        
        root.addChild(p2);
        ImageDataContext imageDataContext = ProjectContext.getInstance().getImageDataContext();
        for (SimpleGeoImage image : imageDataContext.getImageList()) {
        	File f = new File(image.getImageFile());
        	DataTreeObject obj = new DataTreeObject(f.getName(), image, IconEnum.IMAGE_DATA);
        	p2.addChild(obj);
        }
        
        return root;
    }
	
	protected void resetAndUpdateModel()
	{
		viewer.setInput(createTreeModel());
		viewer.expandAll();
	}
	
	@Override
	public void createPartControl(Composite parent) {
		

		
		
		viewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		
		
		
		viewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				
				if (event.getSelection().isEmpty()) {
					fireOnSelectionChanged(null);
					return;
				}
				
				if(event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
					for (Iterator<TreeObject> iter = selection.iterator(); iter.hasNext(); ) {
						TreeObject treeObject = iter.next();
						
						if (treeObject instanceof DataTreeObject) {
							fireOnSelectionChanged(((DataTreeObject)treeObject).getData());
						} else {
							fireOnSelectionChanged(null);
						}
						
						
					}
					
				}
				
			}
		});
		
		ProjectContext.getInstance().addProjectChangeListener(new ProjectChangeAdapter() {

			@Override
			public void onDataAdded() {
				resetAndUpdateModel();
			}

			@Override
			public void onDataRemoved() {
				resetAndUpdateModel();
			}
			
		});
		
		
		resetAndUpdateModel();
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	
}

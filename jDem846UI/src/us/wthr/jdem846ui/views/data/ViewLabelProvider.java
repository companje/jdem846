package us.wthr.jdem846ui.views.data;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import us.wthr.jdem846ui.Activator;
import us.wthr.jdem846ui.project.IconEnum;

public class ViewLabelProvider extends LabelProvider
{

	private Image rasterDataIcon;
	private Image shapeDataIcon;
	private Image imageDataIcon;
	private Image modelGridDataIcon;

	private Image dataSourceCategoryIcon;
	private Image renderedModelCategoryIcon;
	
	private Image renderedModelIcon;
	
	private Image rasterCategoryIcon;
	private Image shapeCategoryIcon;
	private Image imageCategoryIcon;
	private Image modelGridCategoryIcon;
	
	private Image folderIcon;

	public ViewLabelProvider()
	{
		folderIcon = Activator.getImageDescriptor("icons/eclipse/fldr_obj.gif").createImage();
		
		rasterDataIcon = Activator.getImageDescriptor("icons/node-icon-elevation.png").createImage();
		shapeDataIcon = Activator.getImageDescriptor("icons/node-icon-shape-polygon.png").createImage();
		imageDataIcon = Activator.getImageDescriptor("icons/node-icon-orthoimagery.png").createImage();
		modelGridDataIcon = Activator.getImageDescriptor("icons/node-icon-modelgrid.gif").createImage();

		dataSourceCategoryIcon = folderIcon;
		renderedModelCategoryIcon = folderIcon;
		
		renderedModelIcon = Activator.getImageDescriptor("icons/eclipse/map.gif").createImage();
		
		rasterCategoryIcon = folderIcon;
		shapeCategoryIcon = folderIcon;
		imageCategoryIcon = folderIcon;
		modelGridCategoryIcon = folderIcon;
	}

	public String getText(Object obj)
	{
		return obj.toString();
	}

	public Image getImage(Object obj)
	{

		if (obj instanceof DataTreeObject) {
			DataTreeObject treeObject = (DataTreeObject) obj;
			return getIcon(treeObject.getIcon());
		} else if (obj instanceof DataTreeParent) {
			DataTreeParent treeParent = (DataTreeParent) obj;
			return getIcon(treeParent.getIcon());
		} else {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}

	}

	protected Image getIcon(IconEnum icon)
	{
		if (icon == IconEnum.RASTER_DATA) {
			return rasterDataIcon;
		} else if (icon == IconEnum.RASTER_CATEGORY) {
			return rasterCategoryIcon;
		} else if (icon == IconEnum.SHAPE_DATA) {
			return shapeDataIcon;
		} else if (icon == IconEnum.SHAPE_CATEGORY) {
			return shapeCategoryIcon;
		} else if (icon == IconEnum.IMAGE_DATA) {
			return imageDataIcon;
		} else if (icon == IconEnum.IMAGE_CATEGORY) {
			return imageCategoryIcon;
		} else if (icon == IconEnum.MODELGRID_DATA) {
			return modelGridDataIcon;
		} else if (icon == IconEnum.MODELGRID_CATEGORY) {
			return modelGridCategoryIcon;
		} else if (icon == IconEnum.DATA_SOURCE_CATEGORY) {
			return dataSourceCategoryIcon;
		} else if (icon == IconEnum.RENDERED_MODEL_CATEGORY) {
			return renderedModelCategoryIcon;
		} else if (icon == IconEnum.RENDERED_MODEL_OBJECT) {
			return renderedModelIcon;
		} else {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

}

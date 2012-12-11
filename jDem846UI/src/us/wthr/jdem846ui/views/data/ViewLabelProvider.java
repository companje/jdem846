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

	private Image rasterCategoryIcon;
	private Image shapeCategoryIcon;
	private Image imageCategoryIcon;
	private Image modelGridCategoryIcon;

	public ViewLabelProvider()
	{
		rasterDataIcon = Activator.getImageDescriptor("icons/node-icon-elevation.png").createImage();
		shapeDataIcon = Activator.getImageDescriptor("icons/node-icon-shape-polygon.png").createImage();
		imageDataIcon = Activator.getImageDescriptor("icons/node-icon-orthoimagery.png").createImage();
		modelGridDataIcon = Activator.getImageDescriptor("icons/node-icon-modelgrid.gif").createImage();

		rasterCategoryIcon = Activator.getImageDescriptor("icons/node-icon-category-elevation.png").createImage();
		shapeCategoryIcon = Activator.getImageDescriptor("icons/node-icon-category-shapes.png").createImage();
		imageCategoryIcon = Activator.getImageDescriptor("icons/node-icon-category-imagery.png").createImage();
		modelGridCategoryIcon = Activator.getImageDescriptor("icons/node-icon-category-modelgrid.gif").createImage();
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
		} else {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

}

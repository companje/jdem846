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
	private Image rasterCategoryIcon;
	private Image shapeCategoryIcon;
	private Image imageCategoryIcon;
	
	public ViewLabelProvider()
	{
		rasterDataIcon = Activator.getImageDescriptor("icons/node-icon-elevation.png").createImage();
		shapeDataIcon = Activator.getImageDescriptor("icons/node-icon-shape-polygon.png").createImage();
		imageDataIcon = Activator.getImageDescriptor("icons/node-icon-orthoimagery.png").createImage();
		rasterCategoryIcon = Activator.getImageDescriptor("icons/node-icon-category-elevation.png").createImage();
		shapeCategoryIcon = Activator.getImageDescriptor("icons/node-icon-category-shapes.png").createImage();
		imageCategoryIcon = Activator.getImageDescriptor("icons/node-icon-category-imagery.png").createImage();
	}
	
	public String getText(Object obj) {
		return obj.toString();
	}

	public Image getImage(Object obj) {

		if (obj instanceof TreeObject) {
			TreeObject treeObject = (TreeObject) obj;

			if (treeObject.getIcon() == IconEnum.RASTER_DATA) {
				return rasterDataIcon;
			} else if (treeObject.getIcon() == IconEnum.RASTER_CATEGORY) {
				return rasterCategoryIcon;
			} else if (treeObject.getIcon() == IconEnum.SHAPE_DATA) {
				return shapeDataIcon;
			} else if (treeObject.getIcon() == IconEnum.SHAPE_CATEGORY) {
				return shapeCategoryIcon;
			} else if (treeObject.getIcon() == IconEnum.IMAGE_DATA) {
				return imageDataIcon;
			} else if (treeObject.getIcon() == IconEnum.IMAGE_CATEGORY) {
				return imageCategoryIcon;
			} else {
				return PlatformUI.getWorkbench().getSharedImages()
						.getImage(ISharedImages.IMG_OBJ_ELEMENT);
			}
		} else {
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}

	}
}

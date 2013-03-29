package us.wthr.jdem846ui.views.shape;

import java.io.File;

import us.wthr.jdem846.shapefile.ShapeBase;
import us.wthr.jdem846ui.observers.ShapeDataSelectionObserver;
import us.wthr.jdem846ui.project.IconEnum;
import us.wthr.jdem846ui.views.data.DataTreeObject;
import us.wthr.jdem846ui.views.data.TreeSelectionListener;

public class ShapeTreeObject extends DataTreeObject<ShapeBase>
{

	public ShapeTreeObject(ShapeBase shapeBase, TreeSelectionListener selectionListener)
	{
		super((new File(shapeBase.getShapeFileReference().getPath()).getName()), shapeBase, IconEnum.SHAPE_DATA, selectionListener);
	}

	@Override
	public void onDoubleClick()
	{
		ShapeDataSelectionObserver.getInstance().openShapeData(getData());
	}
	
	@Override
	public void onSelected()
	{
		super.selectionListener.onSourceDataSelectionChanged(getData());
	}
	
}

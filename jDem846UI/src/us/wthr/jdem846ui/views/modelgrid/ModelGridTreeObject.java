package us.wthr.jdem846ui.views.modelgrid;

import java.io.File;

import us.wthr.jdem846.modelgrid.ModelGridHeader;
import us.wthr.jdem846ui.project.IconEnum;
import us.wthr.jdem846ui.views.data.DataTreeObject;
import us.wthr.jdem846ui.views.data.TreeSelectionListener;

public class ModelGridTreeObject extends DataTreeObject<ModelGridHeader>
{
	public ModelGridTreeObject(String fileLoadedFrom, ModelGridHeader data, TreeSelectionListener selectionListener)
	{
		super((new File(fileLoadedFrom).getName()), data, IconEnum.MODELGRID_DATA, selectionListener);
	}
}

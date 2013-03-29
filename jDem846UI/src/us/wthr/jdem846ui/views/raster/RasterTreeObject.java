package us.wthr.jdem846ui.views.raster;

import java.io.File;

import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846ui.project.IconEnum;
import us.wthr.jdem846ui.views.data.DataTreeObject;
import us.wthr.jdem846ui.views.data.TreeSelectionListener;

public class RasterTreeObject extends DataTreeObject<RasterData>
{
	public RasterTreeObject(RasterData data, TreeSelectionListener selectionListener)
	{
		super((new File(data.getFilePath()).getName()), data, IconEnum.RASTER_DATA, selectionListener);
	}

	@Override
	public void onSelected()
	{
		super.selectionListener.onSourceDataSelectionChanged(getData());
	}
	
	
	
}

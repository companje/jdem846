package us.wthr.jdem846ui.views.geoimage;

import java.io.File;

import us.wthr.jdem846.image.SimpleGeoImage;
import us.wthr.jdem846ui.project.IconEnum;
import us.wthr.jdem846ui.views.data.DataTreeObject;
import us.wthr.jdem846ui.views.data.TreeSelectionListener;

public class GeoImageTreeObject extends DataTreeObject<SimpleGeoImage>
{
	public GeoImageTreeObject(SimpleGeoImage data, TreeSelectionListener selectionListener)
	{
		super((new File(data.getImageFile()).getName()), data, IconEnum.IMAGE_DATA, selectionListener);
	}
}

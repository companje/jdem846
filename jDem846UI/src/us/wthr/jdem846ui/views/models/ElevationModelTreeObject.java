package us.wthr.jdem846ui.views.models;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846ui.observers.RenderedModelSelectionObserver;
import us.wthr.jdem846ui.project.IconEnum;
import us.wthr.jdem846ui.views.data.DataTreeObject;
import us.wthr.jdem846ui.views.data.TreeSelectionListener;

public class ElevationModelTreeObject extends DataTreeObject<ElevationModel>
{
	public ElevationModelTreeObject(ElevationModel data, TreeSelectionListener selectionListener)
	{
		super(createName(data), data, IconEnum.RENDERED_MODEL_OBJECT, selectionListener);
	}
	
	
	
	@Override
	public void onDoubleClick()
	{
		RenderedModelSelectionObserver.getInstance().openElevationModel(getData());
	}

	@Override
	public void onSelected()
	{
		this.selectionListener.onRenderedModelSelectionChanged(getData());
	}

	protected static String createName(ElevationModel elevationModel)
	{
		String modelSubject = elevationModel.getProperty("subject");
		String renderDate = elevationModel.getProperty("render-date");

		String name = renderDate;
		if (modelSubject != null && modelSubject.length() > 0) {
			name = modelSubject + " - " + renderDate;
		}
		return name;
	}
	
	
}

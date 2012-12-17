package us.wthr.jdem846ui.views.data;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.input.InputSourceData;

public interface TreeSelectionListener {
	public void onSourceDataSelectionChanged(InputSourceData selectedData);
	public void onRenderedModelSelectionChanged(ElevationModel elevationModel);
}

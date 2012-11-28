package us.wthr.jdem846ui.views.models;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846ui.views.tree.TreeParent;

public class ModelTreeParent extends TreeParent<ElevationModel>
{

	public ModelTreeParent(String name) {
		super(name, ElevationModel.class);
	}


}

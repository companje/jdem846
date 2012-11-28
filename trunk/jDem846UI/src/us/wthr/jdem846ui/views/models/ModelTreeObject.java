package us.wthr.jdem846ui.views.models;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846ui.views.tree.TreeObject;

public class ModelTreeObject extends TreeObject<ElevationModel> {

	private ElevationModel elevationModel;
	
	public ModelTreeObject(String name, ElevationModel elevationModel)
	{
		super(name, ElevationModel.class);
		this.elevationModel = elevationModel;
	}
	
	public ElevationModel getElevationModel()
	{
		return this.elevationModel;
	}

	
	public String toString()
	{
		String modelSubject = elevationModel.getProperty("subject");
		String renderDate = elevationModel.getProperty("render-date");
		return modelSubject + " - " + renderDate;
	}
	
	
	
}

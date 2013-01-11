package us.wthr.jdem846.ui.optionModels;

import us.wthr.jdem846.ui.base.JComboBoxModel;

public class RenderEngineListModel extends JComboBoxModel<String>
{
	
	public RenderEngineListModel()
	{
		addItem("OpenGL", "opengl");
		addItem("Software", "software");
	}
	
}

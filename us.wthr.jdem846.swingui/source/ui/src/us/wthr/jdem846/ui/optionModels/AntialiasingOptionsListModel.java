package us.wthr.jdem846.ui.optionModels;

import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.ui.base.JComboBoxModel;

public class AntialiasingOptionsListModel extends JComboBoxModel<Boolean>
{
	
	public AntialiasingOptionsListModel()
	{
		addItem(I18N.get("us.wthr.jdem846.ui.yes"), true);
		addItem(I18N.get("us.wthr.jdem846.ui.no"), false);
	}
	
}

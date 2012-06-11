package us.wthr.jdem846.ui.optionModels;

import java.awt.Image;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.ui.base.JComboBoxModel;

public class ImageQualityListModel extends JComboBoxModel<Integer>
{
	public ImageQualityListModel()
	{
		addItem(I18N.get("us.wthr.jdem846.ui.outputImageViewButtonBar.quality.smooth"), Image.SCALE_SMOOTH);
		addItem(I18N.get("us.wthr.jdem846.ui.outputImageViewButtonBar.quality.default"), Image.SCALE_DEFAULT);
		addItem(I18N.get("us.wthr.jdem846.ui.outputImageViewButtonBar.quality.fast"), Image.SCALE_FAST);
		
		this.setSelectedItemByValue(JDem846Properties.getIntProperty("us.wthr.jdem846.general.view.imageScaling.quality"));
		
		//this.setSelectedItemByValue(Image.SCALE_DEFAULT);
	}
}

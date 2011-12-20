package us.wthr.jdem846.ui.optionModels;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.ui.base.JComboBoxModel;

public class PrecacheStrategyOptionsListModel extends JComboBoxModel<String>
{
	
	public PrecacheStrategyOptionsListModel()
	{
		addItem(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.precacheStrategyOptions.tiled"), DemConstants.PRECACHE_STRATEGY_TILED);
		addItem(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.precacheStrategyOptions.none"), DemConstants.PRECACHE_STRATEGY_NONE);
		addItem(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.precacheStrategyOptions.full"), DemConstants.PRECACHE_STRATEGY_FULL);
		
	}
	
}
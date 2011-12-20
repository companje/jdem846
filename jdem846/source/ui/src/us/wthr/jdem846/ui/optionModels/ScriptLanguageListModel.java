package us.wthr.jdem846.ui.optionModels;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.scripting.ScriptLanguageEnum;
import us.wthr.jdem846.ui.base.JComboBoxModel;

public class ScriptLanguageListModel extends JComboBoxModel<String>
{
	
	public ScriptLanguageListModel()
	{
		ScriptLanguageEnum defaultSelected = null;
		if (JDem846Properties.getBooleanProperty("us.wthr.jdem846.scripting.groovy.enabled")) {
			addItem(I18N.get("us.wthr.jdem846.programmingLanguage.groovy"), ScriptLanguageEnum.GROOVY.text());
			defaultSelected = ScriptLanguageEnum.GROOVY;
		}
		if (JDem846Properties.getBooleanProperty("us.wthr.jdem846.scripting.jython.enabled")) {
			addItem(I18N.get("us.wthr.jdem846.programmingLanguage.jython"), ScriptLanguageEnum.JYTHON.text());
			if (defaultSelected == null) 
				defaultSelected = ScriptLanguageEnum.JYTHON;
		}
		
		if (defaultSelected != null) {
			setSelectedItemByValue(ScriptLanguageEnum.GROOVY.text());
		}
	}
}

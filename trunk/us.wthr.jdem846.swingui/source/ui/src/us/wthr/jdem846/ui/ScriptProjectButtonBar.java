package us.wthr.jdem846.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.i18n.I18N;

@SuppressWarnings("serial")
public class ScriptProjectButtonBar extends ComponentButtonBar
{
	
	public static final int BTN_EXECUTE = 0;
	
	private List<ButtonClickedListener> buttonClickedListeners = new LinkedList<ButtonClickedListener>();
	
	private ToolbarButton jbtnExecute;
	
	public ScriptProjectButtonBar(Component owner)
	{
		super(owner);
		
		jbtnExecute = new ToolbarButton(I18N.get("us.wthr.jdem846.ui.scriptProjectPane.menu.execute.label"), JDem846Properties.getProperty("us.wthr.jdem846.ui.project.createModel") , new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(BTN_EXECUTE);
			}
		});
		
		jbtnExecute.setToolTipText(I18N.get("us.wthr.jdem846.ui.scriptProjectPane.menu.execute.tooltip"));
		boolean displayText = JDem846Properties.getBooleanProperty("us.wthr.jdem846.ui.projectToolBar.displayText");
		jbtnExecute.setTextDisplayed(displayText);
		
		add(jbtnExecute);
		
	}
	
	public void setButtonEnabled(int button, boolean enabled)
	{
		switch(button) {
		case BTN_EXECUTE:
			jbtnExecute.setEnabled(enabled);
			break;
		}
	}
	
	public void addButtonClickedListener(ButtonClickedListener listener)
	{
		buttonClickedListeners.add(listener);
	}
	
	public boolean removeButtonClickedListener(ButtonClickedListener listener)
	{
		return buttonClickedListeners.remove(listener);
	}
	
	protected void fireButtonClickedListeners(int button)
	{
		for (ButtonClickedListener listener : buttonClickedListeners) {
			switch(button) {
			case BTN_EXECUTE:
				listener.onExecuteClicked();
				break;
			}
		}
	}
	
	
	public interface ButtonClickedListener
	{
		public void onExecuteClicked();

	}
}

package us.wthr.jdem846.ui.scripting;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.ui.ComponentButtonBar;
import us.wthr.jdem846.ui.ToolbarButton;
import us.wthr.jdem846.ui.base.ToolBar;

@SuppressWarnings("serial")
public class ScriptEditorButtonBar extends ComponentButtonBar
{
	
	public enum ScriptEditButtons {
		COPY,
		PASTE,
		CUT,
		UNDO,
		REDO
	};
	
	
	private ToolbarButton jbtnCopy;
	private ToolbarButton jbtnCut;
	private ToolbarButton jbtnPaste;
	private ToolbarButton jbtnUndo;
	private ToolbarButton jbtnRedo;
	
	private List<ScriptEditorButtonClickedListener> scriptEditorButtonClickedListeners = new LinkedList<ScriptEditorButtonClickedListener>();
	
	public ScriptEditorButtonBar(Component owner)
	{
		// Set Properties
		super(owner);

		jbtnUndo = new ToolbarButton(I18N.get("us.wthr.jdem846.ui.scriptEditorPane.menu.undo.label"), JDem846Properties.getProperty("us.wthr.jdem846.ui.scriptEditorPane.menu.undo.icon"), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(ScriptEditButtons.UNDO);
			}
		});
		
		jbtnRedo = new ToolbarButton(I18N.get("us.wthr.jdem846.ui.scriptEditorPane.menu.redo.label"), JDem846Properties.getProperty("us.wthr.jdem846.ui.scriptEditorPane.menu.redo.icon"), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(ScriptEditButtons.REDO);
			}
		});
		
		jbtnCut = new ToolbarButton(I18N.get("us.wthr.jdem846.ui.scriptEditorPane.menu.cut.label"), JDem846Properties.getProperty("us.wthr.jdem846.ui.scriptEditorPane.menu.cut.icon"), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(ScriptEditButtons.CUT);
			}
		});
		
		jbtnCopy = new ToolbarButton(I18N.get("us.wthr.jdem846.ui.scriptEditorPane.menu.copy.label"), JDem846Properties.getProperty("us.wthr.jdem846.ui.scriptEditorPane.menu.copy.icon"), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(ScriptEditButtons.COPY);
			}
		});
		
		jbtnPaste = new ToolbarButton(I18N.get("us.wthr.jdem846.ui.scriptEditorPane.menu.paste.label"), JDem846Properties.getProperty("us.wthr.jdem846.ui.scriptEditorPane.menu.paste.icon"), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(ScriptEditButtons.PASTE);
			}
		});
		
		
		jbtnUndo.setToolTipText(I18N.get("us.wthr.jdem846.ui.scriptEditorPane.menu.undo.tooltip"));
		jbtnRedo.setToolTipText(I18N.get("us.wthr.jdem846.ui.scriptEditorPane.menu.redo.tooltip"));
		jbtnCut.setToolTipText(I18N.get("us.wthr.jdem846.ui.scriptEditorPane.menu.cut.tooltip"));
		jbtnCopy.setToolTipText(I18N.get("us.wthr.jdem846.ui.scriptEditorPane.menu.copy.tooltip"));
		jbtnPaste.setToolTipText(I18N.get("us.wthr.jdem846.ui.scriptEditorPane.menu.paste.tooltip"));
		
		boolean displayText = JDem846Properties.getBooleanProperty("us.wthr.jdem846.ui.outputImageButtonBar.displayText");
		jbtnUndo.setTextDisplayed(displayText);
		jbtnRedo.setTextDisplayed(displayText);
		jbtnCut.setTextDisplayed(displayText);
		jbtnCopy.setTextDisplayed(displayText);
		jbtnPaste.setTextDisplayed(displayText);
		

		add(jbtnCut);
		add(jbtnCopy);
		add(jbtnPaste);
		addSeparator();
		add(jbtnUndo);
		add(jbtnRedo);
		
		
	}
	
	
	public void addScriptEditorButtonClickedListener(ScriptEditorButtonClickedListener listener)
	{
		scriptEditorButtonClickedListeners.add(listener);
	}
	
	public boolean removeScriptEditorButtonClickedListener(ScriptEditorButtonClickedListener listener)
	{
		return scriptEditorButtonClickedListeners.remove(listener);
	}
	
	protected void fireButtonClickedListeners(ScriptEditButtons button)
	{
		for (ScriptEditorButtonClickedListener listener : scriptEditorButtonClickedListeners) {
			listener.onButtonClicked(button);
		}
	}
	
	
	public interface ScriptEditorButtonClickedListener
	{
		public void onButtonClicked(ScriptEditButtons button);
	}
}

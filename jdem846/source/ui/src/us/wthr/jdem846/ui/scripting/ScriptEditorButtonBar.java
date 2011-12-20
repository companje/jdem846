package us.wthr.jdem846.ui.scripting;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.scripting.ScriptLanguageEnum;
import us.wthr.jdem846.ui.ComponentButtonBar;
import us.wthr.jdem846.ui.ToolbarButton;
import us.wthr.jdem846.ui.base.ComboBox;
import us.wthr.jdem846.ui.base.JComboBoxModel;
import us.wthr.jdem846.ui.base.Label;
import us.wthr.jdem846.ui.base.ToolBar;
import us.wthr.jdem846.ui.optionModels.ScriptLanguageListModel;

@SuppressWarnings("serial")
public class ScriptEditorButtonBar extends ComponentButtonBar
{
	
	public enum ScriptEditButtons {
		COPY,
		PASTE,
		CUT,
		UNDO,
		REDO,
		LANGUAGE
	};
	
	
	private ToolbarButton jbtnCopy;
	private ToolbarButton jbtnCut;
	private ToolbarButton jbtnPaste;
	private ToolbarButton jbtnUndo;
	private ToolbarButton jbtnRedo;
	
	private ComboBox cmbLanguage;
	private ScriptLanguageListModel languageModel;
	
	private List<ScriptEditorButtonClickedListener> scriptEditorButtonClickedListeners = new LinkedList<ScriptEditorButtonClickedListener>();
	
	public ScriptEditorButtonBar(Component owner)
	{
		// Set Properties
		super(null);

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
		
		
		languageModel = new ScriptLanguageListModel();
		Label lblLanguage = new Label(I18N.get("us.wthr.jdem846.ui.scriptEditorPane.language.label") + ":");
		cmbLanguage = new ComboBox(languageModel);
		
		
		jbtnUndo.setToolTipText(I18N.get("us.wthr.jdem846.ui.scriptEditorPane.menu.undo.tooltip"));
		jbtnRedo.setToolTipText(I18N.get("us.wthr.jdem846.ui.scriptEditorPane.menu.redo.tooltip"));
		jbtnCut.setToolTipText(I18N.get("us.wthr.jdem846.ui.scriptEditorPane.menu.cut.tooltip"));
		jbtnCopy.setToolTipText(I18N.get("us.wthr.jdem846.ui.scriptEditorPane.menu.copy.tooltip"));
		jbtnPaste.setToolTipText(I18N.get("us.wthr.jdem846.ui.scriptEditorPane.menu.paste.tooltip"));
		cmbLanguage.setToolTipText(I18N.get("us.wthr.jdem846.ui.scriptEditorPane.language.tooltip"));
		
		boolean displayText = JDem846Properties.getBooleanProperty("us.wthr.jdem846.ui.outputImageButtonBar.displayText");
		jbtnUndo.setTextDisplayed(displayText);
		jbtnRedo.setTextDisplayed(displayText);
		jbtnCut.setTextDisplayed(displayText);
		jbtnCopy.setTextDisplayed(displayText);
		jbtnPaste.setTextDisplayed(displayText);
		lblLanguage.setVisible(displayText);
		
		
		// Add listeners
		
		cmbLanguage.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					fireButtonClickedListeners(ScriptEditButtons.LANGUAGE);
				}	
			}
		});
		
		// Set Layout
		add(jbtnCut);
		add(jbtnCopy);
		add(jbtnPaste);
		addSeparator();
		add(jbtnUndo);
		add(jbtnRedo);
		addSeparator();
		add(lblLanguage);
		add(cmbLanguage);
		
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

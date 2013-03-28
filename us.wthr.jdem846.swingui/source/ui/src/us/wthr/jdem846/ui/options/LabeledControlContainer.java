package us.wthr.jdem846.ui.options;

import java.awt.Color;

import us.wthr.jdem846.ui.base.Label;

public class LabeledControlContainer
{
	private Label label;
	private OptionModelUIControl control;
	private String id;
	
	private String labelTooltip;
	private Color textColor;
	
	public LabeledControlContainer(String id, Label label, OptionModelUIControl control)
	{
		this.id = id;
		this.label = label;
		this.control = control;
		
		textColor = label.getForeground();
		labelTooltip = label.getToolTipText();
	}

	public Label getLabel()
	{
		return label;
	}

	public OptionModelUIControl getControl()
	{
		return control;
	}

	public String getId()
	{
		return id;
	}
	
	
	public void setDisplayError(boolean display, String message)
	{
		if (display) {
			label.setForeground(Color.RED);
			label.setToolTipText(message);
		} else {
			label.setForeground(textColor);
			label.setToolTipText(labelTooltip);
		}
	}
}

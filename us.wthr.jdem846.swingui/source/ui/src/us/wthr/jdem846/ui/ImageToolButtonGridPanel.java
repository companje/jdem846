package us.wthr.jdem846.ui;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.image.ImageIcons;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Button;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.panels.FlexGridPanel;

@SuppressWarnings("serial")
public class ImageToolButtonGridPanel extends FlexGridPanel
{
	private static Log log = Logging.getLog(ImageToolButtonGridPanel.class);
	
	
	private List<ClickListener> clickListeners = new LinkedList<ClickListener>();
	
	public ImageToolButtonGridPanel()
	{
		super(1);
		
		//this.setFloatable(false);
		//this.setAlignmentY(TOP_ALIGNMENT);
		
		ToolbarButton btnCrop = new ToolbarButton("Crop", 
												  JDem846Properties.getProperty("us.wthr.jdem846.ui.renderViewPane.tools.crop"), 
												  "Crop", 
												  new ButtonClickActionListener("crop"));

		ToolbarButton btnAutoCrop = new ToolbarButton("Autocrop", 
												  JDem846Properties.getProperty("us.wthr.jdem846.ui.renderViewPane.tools.autoCrop"), 
												  "Autocrop Model Image", 
												  new ButtonClickActionListener("auto-crop"));
		
		ToolbarButton btnInvert = new ToolbarButton("Invert", 
												  JDem846Properties.getProperty("us.wthr.jdem846.ui.renderViewPane.tools.invert"), 
												  "Invert",
												  new ButtonClickActionListener("invert"));
		
		ToolbarButton btnValueInvert = new ToolbarButton("Value Invert", 
												  JDem846Properties.getProperty("us.wthr.jdem846.ui.renderViewPane.tools.valueInvert"), 
												  "Value Invert",
												  new ButtonClickActionListener("value-invert"));
		
		ToolbarButton btnDraw = new ToolbarButton("Draw", 
												  JDem846Properties.getProperty("us.wthr.jdem846.ui.renderViewPane.tools.draw"), 
												  "Freehand Draw",
												  new ButtonClickActionListener("draw"));
		
		ToolbarButton btnAnnotate = new ToolbarButton("Annotate", 
												  JDem846Properties.getProperty("us.wthr.jdem846.ui.renderViewPane.tools.annotate"), 
												  "Annotate", 
												  new ButtonClickActionListener("annotate"));
		/*
		btnCrop.setTextDisplayed(false);
		btnAutoCrop.setTextDisplayed(false);
		btnInvert.setTextDisplayed(false);
		btnValueInvert.setTextDisplayed(false);
		btnDraw.setTextDisplayed(false);
		btnAnnotate.setTextDisplayed(false);
		*/
		
		
		btnCrop.setHorizontalAlignment(Button.LEFT);
		btnAutoCrop.setHorizontalAlignment(Button.LEFT);
		btnInvert.setHorizontalAlignment(Button.LEFT);
		btnValueInvert.setHorizontalAlignment(Button.LEFT);
		btnDraw.setHorizontalAlignment(Button.LEFT);
		btnAnnotate.setHorizontalAlignment(Button.LEFT);
		
		
		add(btnCrop);
		add(btnAutoCrop);
		add(btnInvert);
		add(btnValueInvert);
		add(btnDraw);
		add(btnAnnotate);
		
		this.closeGrid();
	}
	
	
	public void addClickListener(ClickListener listener)
	{
		clickListeners.add(listener);
	}
	
	public boolean removeClickListener(ClickListener listener)
	{
		return clickListeners.remove(listener);
	}
	
	protected void fireClickListeners(String action)
	{
		for (ClickListener listener : this.clickListeners) {
			listener.onButtonClicked(action);
		}
	}
	
	
	public interface ClickListener
	{
		public void onButtonClicked(String action);
	}
	
	
	private class ButtonClickActionListener implements ActionListener
	{
		
		private String action;

		public ButtonClickActionListener(String action)
		{
			this.action = action;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			fireClickListeners(action);
		}
		
	}
}

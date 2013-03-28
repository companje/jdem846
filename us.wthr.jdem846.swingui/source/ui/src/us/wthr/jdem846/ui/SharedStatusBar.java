package us.wthr.jdem846.ui;

import java.awt.Component;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Label;
import us.wthr.jdem846.ui.base.Panel;

@SuppressWarnings("serial")
public class SharedStatusBar extends Panel
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(SharedStatusBar.class);
	
	private static SharedStatusBar instance = null;
	
	private List<StatusBarComponentContainer> componentContainers = new LinkedList<StatusBarComponentContainer>();
	private Label lblStatus;
	
	public SharedStatusBar()
	{
		if (SharedStatusBar.instance == null) {
			SharedStatusBar.instance = this;
		}
		
		lblStatus = new Label();

		BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
		setLayout(layout);
		add(lblStatus);
		add(Box.createHorizontalGlue());
		

	}
	
	
	public static void removeControl(Component comp)
	{
		if (SharedStatusBar.instance != null) {
			SharedStatusBar.instance.remove(comp);
		}
	}
	
	public static void addControl(Component comp)
	{
		if (SharedStatusBar.instance != null) {
			SharedStatusBar.instance.add(comp);
		}
	}
	
	public static void addControl(Component comp, Component parent)
	{
		SharedStatusBar.addControl(comp);
		SharedStatusBar.instance.componentContainers.add(new StatusBarComponentContainer(comp, parent));
	}
	
	public static void setStatus(String text)
	{
		if (SharedStatusBar.instance != null) {
			SharedStatusBar.instance.lblStatus.setText(text);
		}
	}
	

	
}

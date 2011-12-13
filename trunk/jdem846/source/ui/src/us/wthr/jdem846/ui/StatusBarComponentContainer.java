package us.wthr.jdem846.ui;

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class StatusBarComponentContainer
{
	private Component component;
	private Component parent;
	
	public StatusBarComponentContainer(Component component, Component parent)
	{
		this.component = component;
		this.parent = parent;
		
		if (parent != null) {
			parent.addComponentListener(new ComponentListener() {
				public void componentResized(ComponentEvent e) {
					
				}
				public void componentMoved(ComponentEvent e) {
					
				}
				public void componentShown(ComponentEvent e) {
					getComponent().setVisible(true);
				}
				public void componentHidden(ComponentEvent e) {
					getComponent().setVisible(false);
				}
			});
		}
		
	}
	
	public Component getComponent()
	{
		return component;
	}
}

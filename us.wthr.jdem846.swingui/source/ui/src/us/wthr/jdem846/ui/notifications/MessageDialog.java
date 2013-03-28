package us.wthr.jdem846.ui.notifications;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.ImageIcon;

import us.wthr.jdem846.image.ImageIcons;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Dialog;
import us.wthr.jdem846.ui.base.Label;

@SuppressWarnings("serial")
public class MessageDialog extends Dialog
{
	private static Log log = Logging.getLog(MessageDialog.class);
	
	public MessageDialog(String title, String iconPath, String message, Throwable thrown)
	{
		this.setTitle(title);
		this.setModal(true);
		this.setSize(300, 150);
		this.setLocationRelativeTo(null);
		
		this.setLayout(new BorderLayout());
		
		if (iconPath != null) {
			try {
				ImageIcon icon = ImageIcons.loadImageIcon(iconPath);
				Label lblIcon = new Label(icon);
				
				this.add(lblIcon, BorderLayout.WEST);
			} catch (IOException ex) {
				log.error("Error loading the icon from " + iconPath + " for the message dialog: " + ex.getMessage(), ex);
			}
		}
		
		Label lblMessage = new Label(message);
		this.add(lblMessage, BorderLayout.CENTER);
		
	}
	
	

}

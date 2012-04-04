package us.wthr.jdem846.ui;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.image.ImageIcons;
import us.wthr.jdem846.ui.base.MenuItem;

/**
 * http://www.jroller.com/santhosh/entry/dropdownbutton_for_swing
 * @author Kevin M. Gill
 * 
 */
@SuppressWarnings("serial")
public class DropDownButton extends ToolbarButton implements ChangeListener, PopupMenuListener, ActionListener, PropertyChangeListener
{
	private final JButton mainButton = this;
	private final JButton arrowButton = new JButton();
	
	private boolean popupVisible = false; 
	
	private JPopupMenu popupMenu = new JPopupMenu();
	
	public DropDownButton(String text, String iconPath, ActionListener actionListener)
	{
		super(text, iconPath, actionListener);

		try {
			arrowButton.setIcon(ImageIcons.loadImageIcon(JDem846Properties.getProperty("us.wthr.jdem846.ui.dropDownButtonArrow")));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		

		mainButton.getModel().addChangeListener(this);
		arrowButton.getModel().addChangeListener(this);
		arrowButton.addActionListener(this);
		arrowButton.setMargin(new Insets(7, 0, 7, 0));
		mainButton.addPropertyChangeListener("enabled", this);
		
	}
	
	public void propertyChange(PropertyChangeEvent evt)
	{
		arrowButton.setEnabled(mainButton.isEnabled());
	}
	
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() == mainButton.getModel()) {
			if (popupVisible && !mainButton.getModel().isRollover()) {
				mainButton.getModel().setRollover(true);
				return;
			}
			arrowButton.getModel().setRollover(mainButton.getModel().isRollover());
			arrowButton.setSelected(mainButton.getModel().isArmed() && mainButton.getModel().isPressed());
		} else {
			if (popupVisible && !arrowButton.getModel().isSelected()) {
				arrowButton.getModel().setSelected(true);
				return;
			}
			mainButton.getModel().setRollover(arrowButton.getModel().isRollover());
		}
	}
	
	public void actionPerformed(ActionEvent ae){ 
         JPopupMenu popup = getPopupMenu(); 
         popup.addPopupMenuListener(this); 
         popup.show(mainButton, 0, mainButton.getHeight()); 
     } 
 

	public void popupMenuWillBecomeVisible(PopupMenuEvent e)
	{
		popupVisible = true;
		mainButton.getModel().setRollover(true);
		arrowButton.getModel().setSelected(true);
	}

	public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
	{
		popupVisible = false;

		mainButton.getModel().setRollover(false);
		arrowButton.getModel().setSelected(false);
		((JPopupMenu) e.getSource()).removePopupMenuListener(this);
	}

	public void popupMenuCanceled(PopupMenuEvent e)
	{
		popupVisible = false;
	}
	
	public JPopupMenu getPopupMenu()
	{
		return popupMenu;
	}
	
	public void addMenuItem(MenuItem menuItem)
	{
		popupMenu.add(menuItem);
	}
	
	public JButton addToToolBar(JToolBar toolbar)
	{
		toolbar.add(mainButton);
		toolbar.add(arrowButton);
		return mainButton;
	}
}

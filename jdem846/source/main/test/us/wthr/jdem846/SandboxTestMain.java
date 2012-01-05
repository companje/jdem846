package us.wthr.jdem846;

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import us.wthr.jdem846.image.ImageIcons;
import us.wthr.jdem846.ui.ComponentButtonBar;
import us.wthr.jdem846.ui.MainButtonBar;
import us.wthr.jdem846.ui.ToolbarButton;

public class SandboxTestMain extends JFrame
{

	public static void main(String[] args)
	{
		try {
			AbstractTestMain.initialize(false);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		SandboxTestMain frame = new SandboxTestMain();
		frame.setVisible(true);
	}
	
	//constructor:
	public SandboxTestMain() 
	{
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(150, 100);
		
		MainButtonBar mainButtonBar = MainButtonBar.getInstance();
		add(mainButtonBar);
		
		ComponentButtonBar toolbar = new ComponentButtonBar(this);
		mainButtonBar.addToolBar(toolbar);
		
		//BoxLayout boxLayout = (BoxLayout)toolbar.getLayout();
		BoxLayout layout = new BoxLayout(toolbar, BoxLayout.X_AXIS);
		toolbar.setLayout(layout);
		
		DropDownButton btnDropDown = new DropDownButton("Blah", JDem846Properties.getProperty("us.wthr.jdem846.ui.project.new"), new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{ }
		});
		
		//btnDropDown.setText("Blah");
		btnDropDown.addToToolBar(toolbar);
		
		
		//add(btnDropDown);
		//pack();
	}
	
	
	class DropDownButton extends ToolbarButton implements ChangeListener, PopupMenuListener, ActionListener, PropertyChangeListener
	{
		private JButton mainButton = this;
		private JButton arrowButton;
		
		private boolean popupVisible = false; 
		
		private JPopupMenu popupMenu = new JPopupMenu();
		
		public DropDownButton(String text, String iconPath, ActionListener actionListener)
		{
			super(text, iconPath, actionListener);
			arrowButton = new JButton();
			try {
				arrowButton.setIcon(ImageIcons.loadImageIcon(JDem846Properties.getProperty("us.wthr.jdem846.ui.dropDownButtonArrow")));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			
			popupMenu.add(new JMenuItem("Hello"));
			
			mainButton.addChangeListener(this);
			arrowButton.addChangeListener(this);
			arrowButton.addActionListener(this);
			//arrowButton.setBorderPainted(false);
			arrowButton.setMargin(new Insets(9, 0, 9, 0));
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
		
		protected JPopupMenu getPopupMenu()
		{
			return popupMenu;
		}
		
		public JButton addToToolBar(JToolBar toolbar)
		{
			toolbar.add(mainButton);
			toolbar.add(arrowButton);
			return mainButton;
		}
		
	}
}

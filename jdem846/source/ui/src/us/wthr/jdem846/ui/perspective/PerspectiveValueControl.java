package us.wthr.jdem846.ui.perspective;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Button;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.base.TextField;

@SuppressWarnings("serial")
public class PerspectiveValueControl extends Panel implements PerspectiveSelectionListener
{
	private static Log log = Logging.getLog(PerspectiveValueControl.class);
	
	private TextField txtValue;
	private Button btnChange;
	
	private double rotateX = 30;
	private double rotateY = 0;
	
	private List<ChangeListener> changeListeners = new LinkedList<ChangeListener>();
	
	public PerspectiveValueControl()
	{
		// create controls
		txtValue = new TextField("");
		txtValue.setEditable(false);
		btnChange = new Button(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.perspectiveValueControl.change.label"));
		btnChange.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.perspectiveValueControl.change.tooltip"));
		
		
		// Add Listeners
		btnChange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				log.info("Perspective Change Button Clicked");
				onShowPerspectiveSelect();
			}
		});
		
		// Set layout
		
		GridBagLayout gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 0.75;
		//constraints.weighty = 1.0;
		
		
		gridbag.setConstraints(txtValue, constraints);
		add(txtValue);
		
		constraints.weightx = 0.25;
		constraints.gridwidth  = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(btnChange, constraints);
		add(btnChange);
		
		
		updateValueText();
	}
	
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		txtValue.setEnabled(enabled);
		btnChange.setEnabled(enabled);
	}
	
	protected void onShowPerspectiveSelect()
	{
		PerspectiveSelectDialog dialog = new PerspectiveSelectDialog();
		dialog.setRotation(rotateX, rotateY);
		dialog.showDialog(this);
	}
	
	
	public void onPerspectiveSelected(double rotateX, double rotateY)
	{
		log.info("Rotate X/Y: " + rotateX + "/" + rotateY);
		
		setRotateX(rotateX);
		setRotateY(rotateY);
		
		fireChangeListeners();
	}
	
	
	
	public void setToolTipText(String toolTipText)
	{
		txtValue.setToolTipText(toolTipText);
	}
	
	protected void updateValueText()
	{
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		String text = nf.format(rotateX) + "\u00B0, " + nf.format(rotateY) + "\u00B0";
		txtValue.setText(text);
	}
	
	
	
	
	
	public double getRotateX()
	{
		return rotateX;
	}


	public void setRotateX(double rotateX)
	{
		this.rotateX = rotateX;
		updateValueText();
	}


	public double getRotateY()
	{
		return rotateY;
	}


	public void setRotateY(double rotateY)
	{
		this.rotateY = rotateY;
		updateValueText();
	}


	protected void fireChangeListeners()
	{
		ChangeEvent e = new ChangeEvent(this);
		for (ChangeListener listener : changeListeners)	{
			listener.stateChanged(e);
		}
	}
	
	public void addChangeListener(ChangeListener listener)
	{
		changeListeners.add(listener);
	}
	
	public boolean removeChangeListener(ChangeListener listener)
	{
		return changeListeners.remove(listener);
	}
}

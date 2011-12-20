package us.wthr.jdem846.ui.lighting;

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
public class LightingValueControl extends Panel 
{
	private static Log log = Logging.getLog(LightingValueControl.class);
	
	private TextField txtValue;
	private Button btnChange;
	
	private double solarAzimuth = 183.0;
	private double solarElevation = 71.0;
	
	private List<ChangeListener> changeListeners = new LinkedList<ChangeListener>();
	
	public LightingValueControl()
	{
		// create controls
		txtValue = new TextField("");
		txtValue.setEditable(false);
		btnChange = new Button(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.lightingConfig.change.label"));
		btnChange.setToolTipText(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.lightingConfig.change.tooltip"));
		
		
		// Add Listeners
		btnChange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				log.info("Lighting Change Button Clicked");
				onShowLightSourceDialog();
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
	
	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		txtValue.setEnabled(enabled);
		btnChange.setEnabled(enabled);
	}
	
	protected void onShowLightSourceDialog()
	{
		LightingSelectDialog dialog = new LightingSelectDialog();
		dialog.setSolarAzimuth(solarAzimuth);
		dialog.setSolarElevation(solarElevation);
		dialog.showDialog(new LightPositionSelectionListener() {
			public void onLightPositionSelected(double solarAzimuth, double solarElevation) {
				setSolarAzimuth(solarAzimuth);
				setSolarElevation(solarElevation);
				fireChangeListeners();
			}
		});
	}
	
	protected void updateValueText()
	{
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		String text = nf.format(solarAzimuth) + "\u00B0, " + nf.format(solarElevation) + "\u00B0";
		txtValue.setText(text);
	}
	
	
	
	public double getSolarAzimuth()
	{
		return solarAzimuth;
	}

	public void setSolarAzimuth(double solarAzimuth)
	{
		this.solarAzimuth = solarAzimuth;
		updateValueText();
	}

	public double getSolarElevation()
	{
		return solarElevation;
	}
	
	public void setSolarElevation(double solarElevation)
	{
		this.solarElevation = solarElevation;
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

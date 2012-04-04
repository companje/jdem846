package us.wthr.jdem846.ui.lighting;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.LightPositionConfigPanel;
import us.wthr.jdem846.ui.base.Button;
import us.wthr.jdem846.ui.base.Dialog;
import us.wthr.jdem846.ui.base.Panel;

@SuppressWarnings("serial")
public class LightingSelectDialog extends Dialog
{
	private static Log log = Logging.getLog(LightingSelectDialog.class);
	
	private LightPositionConfigPanel lightPositionConfigPanel;
	private Button btnOk;
	private Button btnCancel;
	
	private boolean okClicked = false;
	
	public LightingSelectDialog()
	{
		this.setTitle(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.lightingConfig.positionDialog.title"));
		this.setModal(true);
		this.setSize(500, 600);
		this.setLocationRelativeTo(null);
		
		lightPositionConfigPanel = new LightPositionConfigPanel();
		
		btnOk = new Button(I18N.get("us.wthr.jdem846.ui.ok"));
		btnCancel = new Button(I18N.get("us.wthr.jdem846.ui.cancel"));
		
		
		// Add listeners
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onOkClicked();
			}
		});
		
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onCancelClicked();
			}
		});
		
		// Set Layout
		setLayout(new BorderLayout());
		add(lightPositionConfigPanel, BorderLayout.CENTER);
		
		
		Panel pnlButtons = new Panel();
		pnlButtons.add(btnOk);
		pnlButtons.add(btnCancel);
		add(pnlButtons, BorderLayout.SOUTH);
		
		this.pack();
		
	}
	
	public void disposeData()
	{
		try {
			lightPositionConfigPanel.dispose();
		} catch (ComponentException ex) {
			log.error("Failed to dispose of light source preview data: " + ex.getMessage(), ex);
		}
	}
	
	public void setSolarAzimuth(double solarAzimuth)
	{
		lightPositionConfigPanel.setSolarAzimuth(solarAzimuth);
	}
	
	public double getSolarAzimuth()
	{
		return lightPositionConfigPanel.getSolarAzimuth();
	}
	
	public void setSolarElevation(double solarElevation)
	{
		lightPositionConfigPanel.setSolarElevation(solarElevation);
	}
	
	public double getSolarElevation()
	{
		return lightPositionConfigPanel.getSolarElevation();
	}
	
	protected void onOkClicked()
	{
		this.okClicked = true;
		this.setVisible(false);
	}
	
	protected void onCancelClicked()
	{
		this.okClicked = false;
		this.setVisible(false);
	}
	
	
	public void showDialog(LightPositionSelectionListener listener)
	{
		lightPositionConfigPanel.updatePreview(true);
		this.setVisible(true);
		if (okClicked) {
			listener.onLightPositionSelected(lightPositionConfigPanel.getSolarAzimuth(), lightPositionConfigPanel.getSolarElevation());
		}
		//disposeData();
	}
}

package us.wthr.jdem846.ui.coloring;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.GradientConfigPanel;
import us.wthr.jdem846.ui.base.Button;
import us.wthr.jdem846.ui.base.Dialog;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.perspective.PerspectiveSelectionListener;

@SuppressWarnings("serial")
public class ColoringEditDialog extends Dialog
{
	private static Log log = Logging.getLog(ColoringEditDialog.class);
	
	private GradientConfigPanel gradientConfigPanel;
	private Button btnOk;
	private Button btnCancel;
	
	private boolean okClicked = false;
	
	public ColoringEditDialog()
	{
		this.setTitle(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.coloringEditDialog.title"));
		this.setModal(true);
		this.setSize(500, 600);
		this.setLocationRelativeTo(null);
		
		
		gradientConfigPanel = new GradientConfigPanel();
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
		add(gradientConfigPanel, BorderLayout.CENTER);
				
				
		Panel pnlButtons = new Panel();
		pnlButtons.add(btnOk);
		pnlButtons.add(btnCancel);
		add(pnlButtons, BorderLayout.SOUTH);
				
		this.pack();		
	}
	
	
	public void setGradientIdentifier(String gradientIdentifier)
	{
		gradientConfigPanel.setGradientIdentifier(gradientIdentifier);
	}
	
	public String getGradientIdentifier()
	{
		return gradientConfigPanel.getGradientIdentifier();
	}
	
	public void setConfigString(String configString)
	{
		gradientConfigPanel.setConfigString(configString);
	}
	
	public String getConfigString()
	{
		return gradientConfigPanel.getConfigString();
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
	
	
	public void showDialog(ColoringEditListener listener)
	{
		this.setVisible(true);
		if (okClicked) {
			listener.onColoringEdited(getGradientIdentifier(), getConfigString());
		}
	}
	
	
	
}

package us.wthr.jdem846.ui.perspective;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Button;
import us.wthr.jdem846.ui.base.Dialog;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.projectionconfig.ProjectionConfigPanel;

@SuppressWarnings("serial")
public class PerspectiveSelectDialog extends Dialog
{
	private static Log log = Logging.getLog(PerspectiveSelectDialog.class);
	
	private ProjectionConfigPanel projectionConfigPanel;
	private Button btnOk;
	private Button btnCancel;
	
	private boolean okClicked = false;
	
	public PerspectiveSelectDialog()
	{
		this.setTitle(I18N.get("us.wthr.jdem846.ui.modelOptionsPanel.perspectiveSelectDialog.title"));
		this.setModal(true);
		this.setSize(500, 600);
		this.setLocationRelativeTo(null);
		
		// Create Components
		projectionConfigPanel = new ProjectionConfigPanel();
		
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
		add(projectionConfigPanel, BorderLayout.CENTER);
		
		
		Panel pnlButtons = new Panel();
		pnlButtons.add(btnOk);
		pnlButtons.add(btnCancel);
		add(pnlButtons, BorderLayout.SOUTH);
		
		this.pack();
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
	
	public void showDialog(PerspectiveSelectionListener listener)
	{
		this.setVisible(true);
		if (okClicked) {
			listener.onPerspectiveSelected(projectionConfigPanel.getRotateX(), projectionConfigPanel.getRotateY());
		}
	}
	
	public void setRotation(double x, double y)
	{
		projectionConfigPanel.setRotation(x, y, 0.0);
	}
}

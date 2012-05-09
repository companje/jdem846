package us.wthr.jdem846.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.tasks.RunnableTask;
import us.wthr.jdem846.tasks.TaskControllerService;
import us.wthr.jdem846.tasks.TaskStatusListener;
import us.wthr.jdem846.ui.FileSaveThread.SaveCompletedListener;
import us.wthr.jdem846.ui.OutputImageViewButtonBar.ButtonClickedListener;
import us.wthr.jdem846.ui.OutputImageViewButtonBar.OptionChangeListener;
import us.wthr.jdem846.ui.base.FileChooser;
import us.wthr.jdem846.ui.base.Menu;
import us.wthr.jdem846.ui.base.MenuItem;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.panels.EmbeddedTabbedPane;

@SuppressWarnings("serial")
public class RenderPane extends Panel implements Savable
{
	private static Log log = Logging.getLog(RenderPane.class);
	
	private OutputImageViewButtonBar buttonBar;
	private Menu modelMenu;
	private EmbeddedTabbedPane outputImageTabbedPane;
	
	private int renderCount = 0;
	
	public RenderPane()
	{
		
		buttonBar = new OutputImageViewButtonBar(this);
		this.setButtonBarAllDisabled();
		
		outputImageTabbedPane = new EmbeddedTabbedPane(EmbeddedTabbedPane.BOTTOM);
		
		outputImageTabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				onImageTabChanged();
			}
		});
		
		modelMenu = new ComponentMenu(this, I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.menu"), KeyEvent.VK_M);
		MainMenuBar.insertMenu(modelMenu);
		
		modelMenu.add(new MenuItem(I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.menu.save"), JDem846Properties.getProperty("us.wthr.jdem846.ui.outputImageView.save"), KeyEvent.VK_A, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				save();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.SHIFT_MASK | ActionEvent.CTRL_MASK)));

		modelMenu.add(new MenuItem(I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.menu.zoomIn"), JDem846Properties.getProperty("us.wthr.jdem846.ui.outputImageView.zoomIn"), KeyEvent.VK_I, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onZoomIn();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.SHIFT_MASK | ActionEvent.CTRL_MASK)));
		
		modelMenu.add(new MenuItem(I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.menu.zoomOut"), JDem846Properties.getProperty("us.wthr.jdem846.ui.outputImageView.zoomOut"), KeyEvent.VK_O, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onZoomOut();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.SHIFT_MASK | ActionEvent.CTRL_MASK)));
		
		modelMenu.add(new MenuItem(I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.menu.zoomFit"), JDem846Properties.getProperty("us.wthr.jdem846.ui.outputImageView.zoomFit"), KeyEvent.VK_F, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onZoomFit();
			}
		}));
		
		modelMenu.add(new MenuItem(I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.menu.zoomActual"), JDem846Properties.getProperty("us.wthr.jdem846.ui.outputImageView.zoomActual"), KeyEvent.VK_A, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onZoomActual();
			}
		}));
		
		modelMenu.add(new MenuItem(I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.menu.stop"), JDem846Properties.getProperty("us.wthr.jdem846.ui.outputImageView.stop"), KeyEvent.VK_S, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onStop();
			}
		}));
		
		this.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e)
			{
				modelMenu.setVisible(false);
			}
			public void componentShown(ComponentEvent e)
			{
				modelMenu.setVisible(true);
			}
		});
		
		
		buttonBar.addButtonClickedListener(new ButtonClickedListener() {
			public void onSaveClicked() {
				save();
			}
			public void onZoomActualClicked() {
				onZoomActual();
			}
			public void onZoomFitClicked() {
				onZoomFit();
			}
			public void onZoomInClicked() {
				onZoomIn();
			}
			public void onZoomOutClicked() {
				onZoomOut();
			}
			public void onStopClicked() {
				onStop();
			}
			public void onPauseClicked() {
				onPause();
			}
			public void onResumeClicked() {
				onResume();
			}
		});
		
		buttonBar.addOptionChangeListener(new OptionChangeListener() {
			public void onImageQualityChanged(int quality) {
				RenderViewPane renderViewPane = getActiveRenderViewPane();
				if (renderViewPane != null) {
					renderViewPane.onImageQualityChanged(quality);
				}
			}
		});
		
		
		
		
		
		
		
		
		// Set Layout
		setLayout(new BorderLayout());
		
		add(buttonBar, BorderLayout.NORTH);
		add(outputImageTabbedPane, BorderLayout.CENTER);
	}
	
	
	protected void setButtonBarAllDisabled()
	{
		buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_SAVE, false);
		buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_IN, false);
		buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_OUT, false);
		buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_ACTUAL, false);
		buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_FIT, false);
		buttonBar.setComponentEnabled(OutputImageViewButtonBar.OPTION_QUALITY, false);
		buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_STOP, false);
		buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_PAUSE, false);
		buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_RESUME, false);
	}
	
	public void render(ModelContext modelContext)
	{
		renderCount++;
		
		RenderViewPane renderViewPane = new RenderViewPane(modelContext);
		outputImageTabbedPane.addTab("Image #" + renderCount, renderViewPane, true);
		
		renderViewPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				updateActiveRenderViewState();
			}
		});
		
		outputImageTabbedPane.setSelectedComponent(renderViewPane);
		
		renderViewPane.startWorker();
		
	}
	
	
	protected RenderViewPane getActiveRenderViewPane()
	{
		if (outputImageTabbedPane.getTabCount() == 0) {
			return null;
		}
		
		int index = outputImageTabbedPane.getSelectedIndex();
		Component comp = outputImageTabbedPane.getComponentAt(index);
		
		if (comp instanceof RenderViewPane) {
			RenderViewPane renderViewPane = (RenderViewPane) comp;
			return renderViewPane;
		} else {
			return null;
		}
	}
	
	protected void onImageTabChanged()
	{
		updateActiveRenderViewState();
	}
	
	protected void updateActiveRenderViewState()
	{
		RenderViewPane renderViewPane = getActiveRenderViewPane();
		if (renderViewPane != null) {
			renderViewPane.setButtonBarState(buttonBar);
		} else {
			setButtonBarAllDisabled();
		}
	}
	
	
	public void onStop()
	{
		RenderViewPane renderViewPane = getActiveRenderViewPane();
		if (renderViewPane != null) {
			renderViewPane.onStop();
		}
	}
	
	public void onPause()
	{
		RenderViewPane renderViewPane = getActiveRenderViewPane();
		if (renderViewPane != null) {
			renderViewPane.onPause();
		}
	}
	
	public void onResume()
	{
		RenderViewPane renderViewPane = getActiveRenderViewPane();
		if (renderViewPane != null) {
			renderViewPane.onResume();
		}
	}
	
	public void onZoomActual() 
	{
		RenderViewPane renderViewPane = getActiveRenderViewPane();
		if (renderViewPane != null) {
			renderViewPane.onZoomActual();
		}
	}
	
	public void onZoomFit()
	{
		RenderViewPane renderViewPane = getActiveRenderViewPane();
		if (renderViewPane != null) {
			renderViewPane.onZoomFit();
		}
	}
	
	public void onZoomIn()
	{
		RenderViewPane renderViewPane = getActiveRenderViewPane();
		if (renderViewPane != null) {
			renderViewPane.onZoomIn();
		}
	}
	
	public void onZoomOut() 
	{
		RenderViewPane renderViewPane = getActiveRenderViewPane();
		if (renderViewPane != null) {
			renderViewPane.onZoomOut();
		}
	}
	
	
	

	@Override
	public void save()
	{
		RenderViewPane renderViewPane = getActiveRenderViewPane();
		if (renderViewPane != null) {
			renderViewPane.save();
		}
	}

	@Override
	public void saveAs()
	{
		RenderViewPane renderViewPane = getActiveRenderViewPane();
		if (renderViewPane != null) {
			renderViewPane.saveAs();
		}
	}


}

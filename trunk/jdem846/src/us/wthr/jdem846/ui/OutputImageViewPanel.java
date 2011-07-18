/*
 * Copyright (C) 2011 Kevin M. Gill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.wthr.jdem846.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.DemCanvas;
import us.wthr.jdem846.render.RenderEngine.TileCompletionListener;
import us.wthr.jdem846.render.RenderEngine;
import us.wthr.jdem846.ui.FileSaveThread.SaveCompletedListener;
import us.wthr.jdem846.ui.ImageDisplayPanel.MousePositionListener;
import us.wthr.jdem846.ui.ModelingWorkerThread.ModelCompletionListener;
import us.wthr.jdem846.ui.OutputImageViewButtonBar.ButtonClickedListener;
import us.wthr.jdem846.ui.OutputImageViewButtonBar.OptionChangeListener;

@SuppressWarnings("serial")
public class OutputImageViewPanel extends JdemPanel
{
	private static Log log = Logging.getLog(OutputImageViewPanel.class);
	
	private DemCanvas canvas;
	
	private OutputImageViewButtonBar buttonBar;
	private ImageDisplayPanel imageDisplay;
	private StatusBar statusBar;
	private ProcessWorkingSpinner spinner;
	
	private DataPackage dataPackage;
	//private ModelOptions modelOptions;
	//private RenderEngine engine;
	
	private boolean isWorking = false;
	private ModelingWorkerThread worker;
	
	private ModelCompletionListener modelCompletionListener = null;
	private TileCompletionListener tileCompletionListener = null;
	
	private JMenu modelMenu;
	
	public OutputImageViewPanel(RenderEngine engine)
	{
		// Set Properties
		//this.canvas = canvas;
		this.setLayout(new BorderLayout());
		//this.engine = engine;
		this.dataPackage = engine.getDataPackage();
		//this.modelOptions = engine.getModelOptions();
		
		// Create components
		imageDisplay = new ImageDisplayPanel();
		buttonBar = new OutputImageViewButtonBar();
		buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_SAVE, false);
		buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_IN, false);
		buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_OUT, false);
		buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_ACTUAL, false);
		buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_FIT, false);
		buttonBar.setComponentEnabled(OutputImageViewButtonBar.OPTION_QUALITY, false);
		
		statusBar = new StatusBar();
		statusBar.setProgressVisible(true);
		//bottomStatusLabel = new JLabel(" ");
		//bottomStatusLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		spinner = new ProcessWorkingSpinner();
		spinner.step();
		buttonBar.add( Box.createHorizontalGlue() );
		buttonBar.add(spinner);

		// Set the image to be zoomed all the way out so the user can see the image updating
		// as it is drawn.
		imageDisplay.setScalePercent(0.0);
		
		modelMenu = new ComponentMenu(this, "Model", KeyEvent.VK_M);
		MainMenuBar.insertMenu(modelMenu);
		
		modelMenu.add(new MenuItem("Save Image", "/us/wthr/jdem846/ui/icons/document-save.png", KeyEvent.VK_A, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onSave();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.SHIFT_MASK | ActionEvent.CTRL_MASK)));

		modelMenu.add(new MenuItem("Zoom In", "/us/wthr/jdem846/ui/icons/zoom-in.png", KeyEvent.VK_I, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onZoomIn();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.SHIFT_MASK | ActionEvent.CTRL_MASK)));
		
		modelMenu.add(new MenuItem("Zoom Out", "/us/wthr/jdem846/ui/icons/zoom-out.png", KeyEvent.VK_O, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onZoomOut();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.SHIFT_MASK | ActionEvent.CTRL_MASK)));
		
		modelMenu.add(new MenuItem("Zoom Fit", "/us/wthr/jdem846/ui/icons/zoom-fit-best.png", KeyEvent.VK_F, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onZoomFit();
			}
		}));
		
		modelMenu.add(new MenuItem("Zoom Actual", "/us/wthr/jdem846/ui/icons/zoom-original.png", KeyEvent.VK_A, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onZoomActual();
			}
		}));
		
		
		// Add listeners
		
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
				onSave();
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
		});
		buttonBar.addOptionChangeListener(new OptionChangeListener() {
			public void onImageQualityChanged(int quality) {
				imageDisplay.setScaleQuality(quality);
				imageDisplay.repaint();
			}
		});
		
		imageDisplay.addMousePositionListener(new MousePositionListener() {
			public void onMousePositionChanged(int x, int y, double scaledPercent) {
				
				onMousePosition(x, y, scaledPercent);
			}
		});
		
		
		// Set layout
		this.add(buttonBar, BorderLayout.NORTH);
		this.add(imageDisplay, BorderLayout.CENTER);
		this.add(statusBar, BorderLayout.SOUTH);
		
		// Set up worker thread
		worker = new ModelingWorkerThread(engine);
		
		modelCompletionListener = new ModelCompletionListener() {
			public void onModelComplete(DemCanvas completedCanvas) {
				if (completedCanvas != null) {
					imageDisplay.setImage(completedCanvas.getImage());
				}
				canvas = completedCanvas;
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_SAVE, true);
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_IN, true);
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_OUT, true);
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_ACTUAL, true);
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_FIT, true);
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.OPTION_QUALITY, true);
				setWorking(false);
				statusBar.setProgressVisible(false);
				spinner.stop();
				detachModelListeners(true);
				repaint();
			}
		};
		worker.addModelCompletionListener(modelCompletionListener);
		
		tileCompletionListener = new TileCompletionListener() {
			public void onTileCompleted(DemCanvas tileCanvas, DemCanvas outputCanvas, double pctComplete) {
				statusBar.setProgress((int)(pctComplete * 100));
				imageDisplay.setImage(outputCanvas.getImage());
				imageDisplay.zoomFit();
				canvas = outputCanvas;
				repaint();
			}
		};
		worker.addTileCompletionListener(tileCompletionListener);
	}
	
	
	public void cleanUp()
	{
		log.info("Closing output image pane.");
		detachModelListeners(false);
		MainMenuBar.removeMenu(modelMenu);
	}
	
	public void detachModelListeners(boolean delayed)
	{
		
		if (delayed) {
			Timer timer = new Timer(500, new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					detachModelListeners(false);
				}
			});
			timer.setRepeats(false);
			timer.start();
		} else {
			if (modelCompletionListener != null) {
				worker.removeModelCompletionListener(modelCompletionListener);
				modelCompletionListener = null;
			}
			
			if (tileCompletionListener != null) {
				worker.removeTileCompletionListener(tileCompletionListener);
				tileCompletionListener = null;
			}
		}
	}
	
	public void startWorker()
	{
		if (worker != null) {
			setWorking(true);
			spinner.start();
			worker.start();
		}
	}
	
	public void onSave()
	{
		log.info("Save");
		
		JFileChooser chooser = new BasicFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG", "png");
		chooser.setFileFilter(filter);
		
		filter = new FileNameExtensionFilter("JPEG", "jpg", "jpeg");
		chooser.setFileFilter(filter);
		
		filter = new FileNameExtensionFilter("Supported Types", "png", "jpg", "jpeg");
		chooser.setFileFilter(filter);
		
		
	   // int returnVal = chooser.showOpenDialog(this);
	    int returnVal = chooser.showSaveDialog(this);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	String filePath = chooser.getSelectedFile().getAbsolutePath();
	    	
	    	//if (!filePath.toLowerCase().endsWith(".png")) {
	    	//	filePath = filePath + ".png";
    		//}
	    	
	    	log.info("Saving to: " + filePath);
	    	doSave(filePath);
	    	//canvas.save(filePath);
	    	log.info("Done Save");
	    }
	}
	
	protected void doSave(String path) 
	{
		FileSaveThread saveThread = new FileSaveThread(canvas.getImage(), path);
		saveThread.addSaveCompletedListener(new SaveCompletedListener() {
			public void onSaveSuccessful()
			{
				JOptionPane.showMessageDialog(getRootPane(),
					    "Image exported successfully",
					    "Export Successful",
					    JOptionPane.INFORMATION_MESSAGE);
			}
			public void onSaveFailed(Exception ex)
			{
				JOptionPane.showMessageDialog(getRootPane(),
					    "Error exporting image: " + ex.getMessage(),
					    "Export Error",
					    JOptionPane.ERROR_MESSAGE);
			}
		});
		saveThread.start();
	}
	
	public void onZoomActual() 
	{
		imageDisplay.zoomActual();
	}
	public void onZoomFit()
	{
		imageDisplay.zoomFit();
	}
	public void onZoomIn()
	{
		imageDisplay.zoomIn();
	}
	public void onZoomOut() 
	{
		imageDisplay.zoomOut();
	}
	
	protected void onMousePosition(int x, int y, double scaledPercent)
	{
		if (isWorking()) {
			return;
		}
		
		int trueX = (int) Math.round((double)x / scaledPercent);
		int trueY = (int) Math.round((double)y / scaledPercent);
		
		if (trueX == -1 || trueY == -1) {
			statusBar.setStatus(" ");
			return;
		}
		
		double mouseLatitude = dataPackage.rowToLatitude(trueY);
		double mouseLongitude = dataPackage.columnToLongitude(trueX);
		
		DecimalFormat formatter = new DecimalFormat("#.#####");
		String strMouseLatitude = formatter.format(mouseLatitude);
		String strMouseLongitude = formatter.format(mouseLongitude);;
		
		float elevation = dataPackage.getElevation(trueY, trueX);
		if (elevation == DemConstants.ELEV_NO_DATA || elevation == dataPackage.getNoData())
			elevation = 0;
		
		statusBar.setStatus("X/Y: " + trueX + " / " + trueY + ", Latitude/Longitude: " + strMouseLatitude + " / " + strMouseLongitude + ", Elevation: " + elevation);
		
		
	}


	public boolean isWorking() 
	{
		return isWorking;
	}


	protected void setWorking(boolean isWorking) 
	{
		this.isWorking = isWorking;
	}
	

	
}

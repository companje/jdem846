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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.render.DemCanvas;
import us.wthr.jdem846.render.RenderEngine;
import us.wthr.jdem846.render.RenderEngine.TileCompletionListener;
import us.wthr.jdem846.ui.ModelingWorkerThread.ModelCompletionListener;

@SuppressWarnings("serial")
public class DataGenerationViewPanel extends JdemPanel
{
	private DataPackage dataPackage;
	private ModelOptions modelOptions;
	
	
	private boolean isWorking = false;
	private ModelingWorkerThread worker;
	
	private StatusBar statusBar;
	private ProcessWorkingSpinner spinner;
	private VisualPreviewPanel previewPanel;
	
	public DataGenerationViewPanel(RenderEngine engine)
	{
		// Set Properties
		//this.engine = engine;
		this.dataPackage = engine.getDataPackage();
		this.modelOptions = engine.getModelOptions();
		
		// Create Components
		previewPanel = new VisualPreviewPanel(dataPackage, modelOptions);
		
		statusBar = new StatusBar();
		statusBar.setProgressVisible(true);

		spinner = new ProcessWorkingSpinner();
		spinner.step();
		
		
		// Set Listeners
		worker = new ModelingWorkerThread(engine);
		worker.addModelCompletionListener(new ModelCompletionListener() {
			public void onModelComplete(DemCanvas completedCanvas) {
				statusBar.setProgressVisible(false);
				setWorking(false);
			}
			public void onModelFailed(Exception ex)
			{
				statusBar.setProgressVisible(false);
				setWorking(false);
				
				JOptionPane.showMessageDialog(getRootPane(),
					    I18N.get("us.wthr.jdem846.ui.dataGenerationViewPanel.modelFailed.message") + ": " + ex.getMessage(),
					    I18N.get("us.wthr.jdem846.ui.dataGenerationViewPanel.modelFailed.title"),
					    JOptionPane.ERROR_MESSAGE);
				
			}
		});
		
		worker.addTileCompletionListener(new TileCompletionListener() {
			public void onTileCompleted(DemCanvas tileCanvas, DemCanvas outputCanvas, double pctComplete) {
				statusBar.setProgress((int)(pctComplete * 100));
			}
		});
		
		
		// Set Layout
		GridBagLayout gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		GridBagConstraints constraints = new GridBagConstraints();
		
		
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.gridwidth  = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(previewPanel, constraints);
		add(previewPanel);
		
		
		constraints.weighty = 0.0;
		gridbag.setConstraints(statusBar, constraints);
		add(statusBar);
		
	}
	
	
	public void startWorker()
	{
		if (worker != null) {
			setWorking(true);
			spinner.start();
			worker.start();
		}
	}
	
	public boolean isWorking() 
	{
		return isWorking;
	}


	protected void setWorking(boolean isWorking) 
	{
		statusBar.setStatus(((isWorking) ? I18N.get("us.wthr.jdem846.ui.working") : I18N.get("us.wthr.jdem846.ui.done")));
		this.isWorking = isWorking;
	}


	@Override
	public void cleanUp()
	{
		
		
	}
	
	
}

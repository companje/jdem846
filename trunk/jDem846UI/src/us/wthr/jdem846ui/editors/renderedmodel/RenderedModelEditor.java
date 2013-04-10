package us.wthr.jdem846ui.editors.renderedmodel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846ui.ApplicationActionBarAdvisor;
import us.wthr.jdem846ui.ICommandIds;
import us.wthr.jdem846ui.View;
import us.wthr.jdem846ui.actions.ActionListener;
import us.wthr.jdem846ui.actions.BasicZoomAction;
import us.wthr.jdem846ui.actions.ExportModelAction;
import us.wthr.jdem846ui.controls.ImageDisplay;
import us.wthr.jdem846ui.editors.ElevationModelEditorInput;
import us.wthr.jdem846ui.editors.RenderedModelPropertiesContainer;
import us.wthr.jdem846ui.observers.RenderedModelSelectionObserver;

public class RenderedModelEditor extends EditorPart
{
	public static final String ID = "us.wthr.jdem846ui.editors.renderedmodel.RenderedModelEditor";
	
	private static Log log = Logging.getLog(RenderedModelEditor.class);

	private ElevationModel elevationModel;
	
	
	private Composite parent;
	
	private Long imageMutex = new Long(0);
	private ImageDisplay imageDisplay;
	private ElevationHistogram elevationHistogramDisplay;
	private TonalHistogram tonalHistogramDisplay;
	
	private ExportModelAction exportModelAction;
	
	private TabFolder tabFolder;
	private RenderedModelPropertiesContainer propertiesContainer;
	
	@Override
	public void doSave(IProgressMonitor arg0)
	{
		
	}

	@Override
	public void doSaveAs()
	{
		
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		setSite(site);
		setInput(input);
		
		this.setPartName(input.getName());
	}

	@Override
	public boolean isDirty()
	{
		return false;
	}

	@Override
	public boolean isSaveAsAllowed()
	{

		return false;
	}

	@Override
	public void createPartControl(Composite parent)
	{
		this.parent = parent;
		
		tabFolder = new TabFolder (parent, SWT.TOP);
		
		TabItem displayTabItem = new TabItem(tabFolder, SWT.NONE);
		displayTabItem.setText("Model");
		
		createDisplayControls(tabFolder);
		displayTabItem.setControl(imageDisplay);
		
		TabItem propertiesTabItem = new TabItem(tabFolder, SWT.NONE);
		propertiesTabItem.setText("Properties");
		this.propertiesContainer = new RenderedModelPropertiesContainer(tabFolder, SWT.NONE);
		propertiesTabItem.setControl(propertiesContainer);
		
		
		TabItem histogramTabItem = new TabItem(tabFolder, SWT.NONE);
		histogramTabItem.setText("Elevation Histogram");
		this.elevationHistogramDisplay = new ElevationHistogram(tabFolder, SWT.NONE);
		histogramTabItem.setControl(elevationHistogramDisplay);
		
		TabItem imageHistogramTabItem = new TabItem(tabFolder, SWT.NONE);
		imageHistogramTabItem.setText("Tonal Histogram");
		this.tonalHistogramDisplay = new TonalHistogram(tabFolder, SWT.NONE);
		imageHistogramTabItem.setControl(this.tonalHistogramDisplay);
		
		ElevationModelEditorInput editorInput = (ElevationModelEditorInput) this.getEditorInput();
		if (editorInput.getElevationModel() != null) {
			this.setElevationModel(editorInput.getElevationModel());
		}
	}
	
	
	
	@Override
	public void dispose()
	{
		IActionBars actionBars = getEditorSite().getActionBars();
		IToolBarManager toolBar = actionBars.getToolBarManager();
		toolBar.remove(exportModelAction.getId());
		super.dispose();
	}

	protected void createDisplayControls(Composite parent)
	{
		
		log.info("Creating parts for elevation model display editor");
		
		exportModelAction = new ExportModelAction("Export...", View.ID);
		IActionBars actionBars = getEditorSite().getActionBars();
		IToolBarManager toolBar = actionBars.getToolBarManager();
		toolBar.add(exportModelAction);
		
		
		imageDisplay = new ImageDisplay(parent, SWT.NONE);
		
		
		
		
		((BasicZoomAction)ApplicationActionBarAdvisor.getInstance().getAction(ICommandIds.CMD_ZOOM_IN)).addActionListener(new ActionListener() {
			@Override
			public void onAction() {
				// TODO: If this is the active image display widget
				imageDisplay.zoomIn();
			}
		});
		
		((BasicZoomAction)ApplicationActionBarAdvisor.getInstance().getAction(ICommandIds.CMD_ZOOM_OUT)).addActionListener(new ActionListener() {
			@Override
			public void onAction() {
				imageDisplay.zoomOut();
			}
		});
		
		
		((BasicZoomAction)ApplicationActionBarAdvisor.getInstance().getAction(ICommandIds.CMD_ZOOM_ACTUAL)).addActionListener(new ActionListener() {
			@Override
			public void onAction() {
				imageDisplay.zoomActual();
			}
		});
		
		
		((BasicZoomAction)ApplicationActionBarAdvisor.getInstance().getAction(ICommandIds.CMD_ZOOM_FIT)).addActionListener(new ActionListener() {
			@Override
			public void onAction() {
				if (!imageDisplay.isDisposed()) {
					imageDisplay.zoomFit();
				}
			}
		});
	
	}
	
	


	
	public void setElevationModel(ElevationModel elevationModel)
	{
		this.elevationModel = elevationModel;
		if (elevationModel == null) {
			return;
		}
		
		if (!elevationModel.isLoaded()) {
			try {
				elevationModel.load();
			} catch (Exception ex) {
				log.error("Error loading elevation model: " + ex.getMessage(), ex);
				return;
			} 
		}
		
		this.propertiesContainer.setElevationModel(elevationModel);
		this.elevationHistogramDisplay.setElevationHistogramModel(elevationModel.getElevationHistogramModel());
		this.tonalHistogramDisplay.setElevationModel(elevationModel);
		
		synchronized(imageMutex) {
			
			int width = (elevationModel != null) ? elevationModel.getWidth() : imageDisplay.getClientArea().width;
			int height = (elevationModel != null) ? elevationModel.getHeight() : imageDisplay.getClientArea().height;
			
			if (width <= 0 || height <= 0)
				return;
			
			PaletteData palette = new PaletteData(0xFF000000, 0xFF0000 , 0xFF00);
			ImageData imageData = new ImageData(width, height, 32, palette);
			
			if (elevationModel != null) {

				for (int y = 0; y < elevationModel.getHeight(); y++) {
					for (int x = 0; x < elevationModel.getWidth(); x++) {
						int rgbaInt = elevationModel.getRgba(x, y);
						imageData.setPixel(x, y, rgbaInt);
					}
				}
				
			}
			
			Image image = new Image(imageDisplay.getCanvasDisplay(), imageData);
			imageDisplay.setImage(image);
		}
	}
	
	@Override
	public void setFocus()
	{
		if (imageDisplay != null) {
			imageDisplay.setFocus();
		}
		
		if (this.elevationModel != null) {
			RenderedModelSelectionObserver.getInstance().fireRenderedModelSelected(elevationModel);
		}
		
	}

}

package us.wthr.jdem846ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.part.EditorPart;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846ui.View;
import us.wthr.jdem846ui.actions.ActionListener;
import us.wthr.jdem846ui.actions.UpdatePreviewAction;
import us.wthr.jdem846ui.observers.ModelPreviewChangeObserver;
import us.wthr.jdem846ui.observers.ModelPreviewReadyListener;
import us.wthr.jdem846ui.util.SwtImageUtil;
import us.wthr.jdem846ui.views.preview.ModelMouseMovementTracker;

public class PreviewEditor extends EditorPart
{
	private static Log log = Logging.getLog(PreviewEditor.class);

	public static final String ID = "us.wthr.jdem846ui.editors.PreviewEditor";

	private Canvas canvas;

	private Composite parent;

	private Long imageMutex = new Long(0);
	private Image image;

	private ModelMouseMovementTracker mouseTracker;

	private UpdatePreviewAction updatePreviewAction;

	public void createPartControl(Composite parent)
	{
		mouseTracker = new ModelMouseMovementTracker();

		updatePreviewAction = new UpdatePreviewAction("Update Preview", View.ID);
		IActionBars actionBars = getEditorSite().getActionBars();
		// IMenuManager dropDownMenu = actionBars.getMenuManager();
		IToolBarManager toolBar = actionBars.getToolBarManager();
		// dropDownMenu.add(updatePreviewAction);
		toolBar.add(updatePreviewAction);

		updatePreviewAction.addActionListener(new ActionListener()
		{
			public void onAction()
			{
				ModelPreviewChangeObserver.getInstance().update(true, true);
			}
		});

		ModelPreviewChangeObserver.getInstance().addModelPreviewReadyListener(new ModelPreviewReadyListener()
		{
			public void onPreviewReady(final ElevationModel elevationModel)
			{
				log.info("Model preview is ready");

				Display.getDefault().asyncExec(new Runnable()
				{

					@Override
					public void run()
					{
						updatePreviewCanvas(elevationModel);
					}

				});

			}
		});

		canvas = new Canvas(parent, SWT.NONE);
		canvas.setBackground(new Color(parent.getDisplay(), 0xFF, 0xFF, 0xFF));
		canvas.addPaintListener(new PaintListener()
		{

			@Override
			public void paintControl(PaintEvent e)
			{

				synchronized (imageMutex) {
					if (image != null) {

						double scalePercent = getZoomToFitScalePercentage();

						int scaleToWidth = (int) Math.floor((double) image.getImageData().width * (double) scalePercent);
						int scaleToHeight = (int) Math.floor((double) image.getImageData().height * (double) scalePercent);

						int x = (int) ((canvas.getClientArea().width / 2.0) - (scaleToWidth / 2.0)) + 0;
						int y = (int) ((canvas.getClientArea().height / 2.0) - (scaleToHeight / 2.0)) + 0;

						Image scaledImage = SwtImageUtil.getScaledImage(image, scalePercent);

						e.gc.drawImage(scaledImage, x, y);
					}
				}

			}

		});

		canvas.addControlListener(new ControlListener()
		{

			@Override
			public void controlMoved(ControlEvent arg0)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void controlResized(ControlEvent e)
			{
				if (canvas.isVisible()) {
					int previewHeight = canvas.getClientArea().height;
					int previewWidth = canvas.getClientArea().width;
					ModelPreviewChangeObserver.getInstance().setPreviewHeight(previewHeight);
					ModelPreviewChangeObserver.getInstance().setPreviewWidth(previewWidth);

					if (previewHeight > 0 && previewWidth > 0) {
						ModelPreviewChangeObserver.getInstance().update(false, false);
					}
				}
			}

		});

		canvas.addMouseListener(mouseTracker);
		canvas.addMouseMoveListener(mouseTracker);
		canvas.addMouseWheelListener(mouseTracker);
		canvas.addMouseTrackListener(mouseTracker);
	}

	public void init(IEditorSite site, IEditorInput input)
	{
		setSite(site);
		setInput(input);
	}

	public void setFocus()
	{
		if (canvas != null)
			canvas.setFocus();
	}

	@Override
	public void doSave(IProgressMonitor arg0)
	{

	}

	@Override
	public void doSaveAs()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDirty()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed()
	{
		// TODO Auto-generated method stub
		return false;
	}

	protected void updatePreviewCanvas(ElevationModel elevationModel)
	{
		synchronized (imageMutex) {

			int width = (elevationModel != null) ? elevationModel.getWidth() : canvas.getClientArea().width;
			int height = (elevationModel != null) ? elevationModel.getHeight() : canvas.getClientArea().height;

			if (width <= 0 || height <= 0)
				return;

			PaletteData palette = new PaletteData(0xFF000000, 0xFF0000, 0xFF00);
			ImageData imageData = new ImageData(width, height, 32, palette);

			if (elevationModel != null) {

				for (int y = 0; y < elevationModel.getHeight(); y++) {
					for (int x = 0; x < elevationModel.getWidth(); x++) {
						int rgbaInt = elevationModel.getRgba(x, y);
						imageData.setPixel(x, y, rgbaInt);
					}
				}

			}
			if (!canvas.isDisposed()) {
				image = new Image(canvas.getDisplay(), imageData);
				canvas.redraw();
			}
		}
	}

	protected double getZoomToFitScalePercentage()
	{
		if (image == null) {
			return 0.0;
		}

		double imageWidth = image.getImageData().width;
		double imageHeight = image.getImageData().height;

		double panelWidth = canvas.getClientArea().width;
		double panelHeight = canvas.getClientArea().height;

		double scaleWidth = 0;
		double scaleHeight = 0;

		double scale = Math.max(panelHeight / imageHeight, panelWidth / imageWidth);
		scaleHeight = imageHeight * scale;
		scaleWidth = imageWidth * scale;

		if (scaleHeight > panelHeight) {
			scale = panelHeight / scaleHeight;
			scaleHeight = scaleHeight * scale;
			scaleWidth = scaleWidth * scale;
		}
		if (scaleWidth > panelWidth) {
			scale = panelWidth / scaleWidth;
			scaleHeight = scaleHeight * scale;
			scaleWidth = scaleWidth * scale;
		}

		return (scaleWidth / imageWidth);
	}

	
}

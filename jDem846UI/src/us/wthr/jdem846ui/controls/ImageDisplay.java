package us.wthr.jdem846ui.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import us.wthr.jdem846ui.util.SwtImageUtil;

public class ImageDisplay extends Composite
{
	private Long imageMutex = new Long(0);
	private Image image;
	private Image scaledImage;
	
	private Canvas canvas;
	
	private int imageTrueWidth = -1;
	private int imageTrueHeight = -1;
	private double scalePercent = 1.0;
	
	private int lastDragMouseX = -1;
	private int lastDragMouseY = -1;
	
	private int translateX = 0;
	private int translateY = 0;
	
	private boolean isBestFit = false;
	
	private double minScalePercent;
	
	private boolean allowPanning = true;
	private boolean allowZooming = true;
	private boolean imageInitialized = false;
	
	private boolean mouseIsDown = false;
	
	public ImageDisplay(Composite parent, int style)
	{
		this(parent, style, true, true);
	}
	
	public ImageDisplay(Composite parent, int style, boolean panning, boolean zooming)
	{
		super(parent, style);
		this.setLayout(new FillLayout());
		this.setAllowPanning(panning);
		this.setAllowZooming(zooming);
		
		canvas = new Canvas(this, SWT.NONE);
		canvas.setBackground(new Color(this.getDisplay(), 0xFF, 0xFF, 0xFF));
		canvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				
				synchronized(imageMutex) {
					Image paintImage = getDisplayImage();
					if (paintImage != null) {

						int scaleToWidth = (int) Math.floor((double)image.getImageData().width * (double) scalePercent);
						int scaleToHeight = (int) Math.floor((double)image.getImageData().height * (double) scalePercent);
						
						int x = (int) ((canvas.getClientArea().width / 2.0) - (scaleToWidth / 2.0)) + translateX;
						int y = (int) ((canvas.getClientArea().height / 2.0) - (scaleToHeight / 2.0)) + translateY;
						

						e.gc.drawImage(paintImage, x, y);
					}
				}
				
			}
			
		});
		
		
		
		canvas.addControlListener(new ControlListener() {

			@Override
			public void controlMoved(ControlEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void controlResized(ControlEvent e) {
				minScalePercent = getZoomToFitScalePercentage();
				if (isBestFit) {
					zoomFit();
				}

			}
			
		});
		
		
		canvas.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e)
			{
				
			}

			@Override
			public void mouseDown(MouseEvent e)
			{
				mouseIsDown = true;
				
				if (allowPanning) {
					//setCursor(CursorFactory.PREDEFINED_CLOSED_HAND);
				}
				
				if (allowZooming) {
					lastDragMouseX = e.x;
					lastDragMouseY = e.y;
				}
			}

			@Override
			public void mouseUp(MouseEvent e)
			{
				mouseIsDown = false;
				
				if (allowPanning) {
					//setCursor(CursorFactory.PREDEFINED_OPEN_HAND);
				}
				
				if (allowZooming) {
					lastDragMouseX = -1;
					lastDragMouseY = -1;
				}
			}
			
		});
		canvas.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent e)
			{
				if (mouseIsDown) {
					int horizStep = -1 * (e.x - lastDragMouseX);
					int vertStep = -1 * (e.y - lastDragMouseY);
					

					lastDragMouseX = e.x;// + horizStep;
					lastDragMouseY = e.y;// + vertStep;
					
					
					
					translateX -= horizStep;
					translateY -= vertStep;
					
					validateImagePosition();
					canvas.redraw();
				}
			}
			
		});
		canvas.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseScrolled(MouseEvent e)
			{
				if (allowZooming) {
					//onMouseWheelMoved(e.getUnitsToScroll(), e.getScrollAmount(), e.getScrollType(), e.x, e.y);
				}
			}
			
		});
		canvas.addMouseTrackListener(new MouseTrackListener() {

			@Override
			public void mouseEnter(MouseEvent arg0)
			{
				
			}

			@Override
			public void mouseExit(MouseEvent arg0)
			{
				
			}

			@Override
			public void mouseHover(MouseEvent arg0)
			{
				
			}
			
		});
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
		
		double scale = Math.max(panelHeight/imageHeight, panelWidth/imageWidth);
		scaleHeight = imageHeight * scale;
		scaleWidth = imageWidth * scale;
		
		
		if (scaleHeight > panelHeight) {
			scale = panelHeight/scaleHeight;
		    scaleHeight = scaleHeight * scale;
			scaleWidth = scaleWidth * scale;
		}
		if (scaleWidth > panelWidth) {
		    scale = panelWidth/scaleWidth;
		    scaleHeight = scaleHeight * scale;
			scaleWidth = scaleWidth * scale;
		}
		
		
		return (scaleWidth / imageWidth);
	}
	

	public double getScalePercent() 
	{
		return scalePercent;
	}



	public void setScalePercent(double scalePercent)
	{
		if (scalePercent >= 1.0)
			scalePercent = 1.0;
		if (scalePercent <= minScalePercent)
			scalePercent = minScalePercent;
		
		this.scalePercent = scalePercent;
		
		if (this.getClientArea().width > 0 && this.getClientArea().height > 0) {
			
			createScaledImage();
			validateImagePosition();
			
			canvas.redraw();
			
			
			this.imageInitialized = true;
		}
	}
	
	protected void validateImagePosition()
	{
		Image displayImage = getDisplayImage();
		if (displayImage == null)
			return;
		
		// ....

	}
	
	protected Image getDisplayImage()
	{
		if (scaledImage != null)
			return scaledImage;
		else
			return image;
	}
	
	public void createScaledImage()
	{
		scaledImage = SwtImageUtil.getScaledImage(image, scalePercent);
	}
	
	

	
	public void zoom(double units)
	{
		isBestFit = false;
		setScalePercent(scalePercent + ((units / 100.0) * -1));
	}
	
	public void zoomIn()
	{
		zoom(-3);
	}
	
	public void zoomOut()
	{
		zoom(3);
	}
	
	public void zoomFit()
	{
		isBestFit = true;
		translateX = 0;
		translateY = 0;
		
		this.setScalePercent(getZoomToFitScalePercentage());

	}
	
	public void zoomActual()
	{
		setScalePercent(1.0);
	}
	
	
	
	public Display getCanvasDisplay()
	{
		return canvas.getDisplay();
	}
	
	public void setImage(Image image)
	{
		this.image = image;
		if (image == null) {
			return;
		}
		
		zoomFit();
		canvas.redraw();
	}

	public boolean isAllowPanning()
	{
		return allowPanning;
	}

	public void setAllowPanning(boolean allowPanning)
	{
		this.allowPanning = allowPanning;
	}

	public boolean isAllowZooming()
	{
		return allowZooming;
	}

	public void setAllowZooming(boolean allowZooming)
	{
		this.allowZooming = allowZooming;
	}
	
	
	
}

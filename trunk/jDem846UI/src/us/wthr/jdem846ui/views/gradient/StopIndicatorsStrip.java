package us.wthr.jdem846ui.views.gradient;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import us.wthr.jdem846.color.GradientColorStop;
import us.wthr.jdem846.color.GradientLoader;
import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.math.MathExt;

public class StopIndicatorsStrip extends Composite
{
	private Canvas canvas;
	private ModelColoring gradient;
	private double minStopValue;
	private double maxStopValue;

	private Color background;
	private Color arrowLines;
	private Color inactiveArrowFill;
	private Color activeArrowFill;
	
	private StopIndicatorConfig indicatorConfig = new StopIndicatorConfig();

	private Cursor cursorSizeNS;
	private Cursor cursorArrow;
	
	private GradientColorStop mouseOverColorStop = null;

	public StopIndicatorsStrip(Composite parent, int style, ModelColoring gradient, StopIndicatorConfig indicatorConfig)
	{
		super(parent, style);
		setLayout(new FillLayout());
		this.indicatorConfig = indicatorConfig;
		
		cursorSizeNS = new Cursor(this.getDisplay(), SWT.CURSOR_SIZENS);
		cursorArrow = new Cursor(this.getDisplay(), SWT.CURSOR_ARROW);

		this.background = new Color(parent.getDisplay(), 0xFF, 0xFF, 0xFF);
		this.arrowLines = new Color(parent.getDisplay(), 0x00, 0x00, 0x00);
		this.inactiveArrowFill = new Color(parent.getDisplay(), 0xFF, 0xFF, 0xFF);
		this.activeArrowFill = new Color(parent.getDisplay(), 0xFF, 0x00, 0x00);
		
		
		this.canvas = new Canvas(this, SWT.NO_BACKGROUND);
		canvas.addPaintListener(new PaintListener()
		{
			@Override
			public void paintControl(PaintEvent e)
			{
				onPaint(e);
			}
		});
		canvas.addMouseMoveListener(new MouseMoveListener()
		{
			@Override
			public void mouseMove(MouseEvent e)
			{
				checkMouseCursorForPosition(e.x, e.y);
			}
		});

		this.setSize(15, 50);

		setGradient(gradient);

	}

	@Override
	public void dispose()
	{
		canvas.dispose();
		cursorSizeNS.dispose();
		cursorArrow.dispose();
	}

	protected void onPaint(PaintEvent e)
	{
		if (this.gradient == null) {
			return;
		}
		GC gc = e.gc;
		Image bufferImage = new Image(getDisplay(),canvas.getClientArea().width,canvas.getClientArea().height); 
		GC bufferGC = new GC(bufferImage); 
		paint(bufferGC);
		
		gc.drawImage(bufferImage, 0, 0);
		
		bufferImage.dispose();
		bufferGC.dispose();
	}
	
	protected void paint(GC gc)
	{
		
		gc.setAntialias(SWT.ON);
		
		gc.setBackground(background);
		gc.fillRectangle(0, 0, canvas.getClientArea().width, canvas.getClientArea().height);
		
		GradientLoader loader = gradient.getGradientLoader();
		for (GradientColorStop stop : loader.getColorStops()) {
			
			int y = getStopYPosition(stop);

			int yTopRight = y - indicatorConfig.getHalfHeight();
			int yBottomRight = y + indicatorConfig.getHalfHeight();

			int xLeft = indicatorConfig.getLeftX();
			int xRight = indicatorConfig.getLeftX() + indicatorConfig.getWidth();
			
			Color fill = (this.mouseOverColorStop == stop) ? this.activeArrowFill : this.inactiveArrowFill;
			gc.setForeground(fill);
			gc.setBackground(fill);
			Path path = new Path(this.getDisplay());
			path.moveTo(xLeft, y);
			path.lineTo(xRight, yTopRight);
			path.lineTo(xRight, yBottomRight);
			path.lineTo(xLeft, y);
			path.close();
			gc.fillPath(path);
			
			gc.setBackground(background);
			gc.setForeground(arrowLines);
			gc.drawPath(path);
		}

	}

	protected void checkMouseCursorForPosition(int x, int y)
	{
		mouseOverColorStop = getColorStopUnderPosition(y);
		if (mouseOverColorStop != null) {
			canvas.setCursor(cursorSizeNS);
		} else {
			canvas.setCursor(cursorArrow);
		}
		canvas.redraw();
	}

	public boolean isPositionOverColorStop(int y)
	{
		return (getColorStopUnderPosition(y) != null);
	}

	public GradientColorStop getColorStopUnderPosition(int y)
	{
		GradientLoader loader = gradient.getGradientLoader();
		for (GradientColorStop stop : loader.getColorStops()) {

			int stopYPosition = getStopYPosition(stop);

			if (y >= stopYPosition - indicatorConfig.getHalfHeight() && y <= stopYPosition + indicatorConfig.getHalfHeight()) {
				return stop;
			}
		}

		return null;
	}

	protected int getStopYPosition(GradientColorStop stop)
	{
		int height = canvas.getClientArea().height;

		int y = (int) MathExt.round((double) height * (1.0 - ((stop.getPosition() - minStopValue) / (maxStopValue - minStopValue))));
		return y;
	}

	public void setGradient(ModelColoring gradient)
	{
		this.gradient = gradient;
		minStopValue = getMinimumStopValue();
		maxStopValue = getMaximumStopValue();
		canvas.redraw();
	}

	protected double getMinimumStopValue()
	{
		GradientLoader loader = gradient.getGradientLoader();
		GradientColorStop stop = loader.getColorStops().get(0);
		if (stop != null) {
			return stop.getPosition();
		} else {
			return 0;
		}
	}

	protected double getMaximumStopValue()
	{
		GradientLoader loader = gradient.getGradientLoader();
		GradientColorStop stop = loader.getColorStops().get(loader.getColorStops().size() - 1);
		if (stop != null) {
			return stop.getPosition();
		} else {
			return 0;
		}
	}
}

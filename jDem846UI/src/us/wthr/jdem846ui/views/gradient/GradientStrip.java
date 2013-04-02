package us.wthr.jdem846ui.views.gradient;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.graphics.IColor;

public class GradientStrip extends Composite
{
	
	private Canvas canvas;
	private ModelColoring gradient;
	
	public GradientStrip(Composite parent, int style, ModelColoring gradient)
	{
		super(parent, style);
		setLayout(new FillLayout());
		
		this.gradient = gradient;
		
		this.canvas = new Canvas(this, SWT.NONE);
		canvas.setBackground(new Color(parent.getDisplay(), 0xFF, 0xFF, 0xFF));
		canvas.addPaintListener(new PaintListener()
		{
			@Override
			public void paintControl(PaintEvent e)
			{
				onPaint(e);
			}
		});
		
		
		
	}
	
	
	protected void onPaint(PaintEvent e)
	{
		if (this.gradient == null) {
			return;
		}
		
		GC gc = e.gc;
		gc.setAntialias(SWT.ON);
		
		int height = canvas.getClientArea().height;
		int width = canvas.getClientArea().width;
		
		GradientColorFetcher fetcher = new GradientColorFetcher(gradient, height);
		
		for (int y = 0; y < height; y++) {
			
			IColor color = fetcher.getColor(y);
			if (color != null) {
				gc.setForeground(new Color(this.getDisplay(), color.getRed(), color.getGreen(), color.getBlue()));
			} else {
				gc.setForeground(new Color(this.getDisplay(), 0x0, 0x0, 0x0));
			}
			
			gc.drawLine(0, y, width, y);
		}
		
		
		
	}
	
	
	public void setGradient(ModelColoring gradient)
	{
		this.gradient = gradient;
		canvas.redraw();
	}
	
}

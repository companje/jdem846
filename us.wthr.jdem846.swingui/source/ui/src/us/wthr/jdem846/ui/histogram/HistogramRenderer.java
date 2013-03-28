package us.wthr.jdem846.ui.histogram;

import java.awt.Color;
import java.awt.Paint;

import org.jfree.chart.renderer.category.AreaRenderer;

public class HistogramRenderer extends AreaRenderer
{
	private Paint[] colors;
	
	public HistogramRenderer() 
	{
		colors = new Paint[] {new Color(255, 0, 0, 85),
				new Color(0, 255, 0, 85),
				new Color(0, 0, 255, 85)
		};
	}
	
	public Paint getItemPaint(final int row, final int column) 
	{ 
		return (this.colors[row % this.colors.length]); 
	} 
}

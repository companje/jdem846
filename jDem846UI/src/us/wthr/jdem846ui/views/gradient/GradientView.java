package us.wthr.jdem846ui.views.gradient;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class GradientView extends ViewPart
{
	private static Log log = Logging.getLog(GradientView.class);
	
	public static final String ID = "jdem846ui.gradientView";
	
	private GradientStrip gradientStrip;
	private StopIndicatorsStrip indicatorsStrip;
	
	public GradientView()
	{
		
	}
	
	@Override
	public void createPartControl(Composite parent)
	{
		ModelColoring gradient = null;
		
		try { 
			gradient = getGradient("hypsometric-tint-global");
		} catch (Exception ex) {
			ex.printStackTrace(); // TODO Handle this better
			return;
		}
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.horizontalSpacing = 0;
		parent.setLayout(gridLayout);
		
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gradientStrip = new GradientStrip(parent, SWT.NONE, gradient);
		gradientStrip.setLayoutData(gridData);
		
		gridData = new GridData(GridData.END, GridData.FILL, false, true);
		indicatorsStrip = new StopIndicatorsStrip(parent, SWT.NONE, gradient, new StopIndicatorConfig(4, 10, 1));
		indicatorsStrip.setLayoutData(gridData);
	}
	
	protected ModelColoring getGradient(String id) throws Exception
	{
		ModelColoring gradient = ColoringRegistry.getInstance(id).getImpl().copy();
		return gradient;
	}
	
	@Override
	public void setFocus()
	{
		gradientStrip.redraw();
		indicatorsStrip.setFocus();
		indicatorsStrip.redraw();
	}
}

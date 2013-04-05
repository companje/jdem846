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
import us.wthr.jdem846.model.OptionModelChangeEvent;
import us.wthr.jdem846.project.context.ProjectChangeAdapter;
import us.wthr.jdem846.project.context.ProjectContext;

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

		Composite composite = new Composite(parent, SWT.SINGLE| SWT.BORDER | SWT.BORDER_SOLID);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.horizontalSpacing = 0;
		composite.setLayout(gridLayout);
		
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gradientStrip = new GradientStrip(composite, SWT.NONE, gradient);
		gradientStrip.setLayoutData(gridData);
		
		gridData = new GridData(GridData.END, GridData.FILL, false, true);
		indicatorsStrip = new StopIndicatorsStrip(composite, SWT.NONE, gradient, new StopIndicatorConfig(4, 10, 1));
		indicatorsStrip.setLayoutData(gridData);
		
		ProjectContext.getInstance().addProjectChangeListener(new ProjectChangeAdapter() {
			@Override
			public void onOptionChanged(OptionModelChangeEvent e)
			{
				if (e.getPropertyId().equals("us.wthr.jdem846.model.HypsometricColorOptionModel.colorTint")) {
					
					setGradient((String)e.getNewValue());
					
				}
			}
		});
		
	}
	
	protected void setGradient(String id)
	{
		try {
			ModelColoring gradient = getGradient(id);
			gradientStrip.setGradient(gradient);
			indicatorsStrip.setGradient(gradient);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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

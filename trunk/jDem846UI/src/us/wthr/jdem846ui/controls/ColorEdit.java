package us.wthr.jdem846ui.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import us.wthr.jdem846.canvas.util.ColorUtil;
import us.wthr.jdem846ui.Activator;

public class ColorEdit extends Composite
{

	private int selection = 0x0;
	private Label colorDisplay;
	
	public ColorEdit(Composite parent, int style) {
		super(parent, style);

		final ColorEdit thisControl = this;
		
		TableWrapLayout layout = new TableWrapLayout();
		this.setLayout(layout);
		layout.numColumns = 2;
		layout.topMargin = 0;
		layout.bottomMargin = 0;
		layout.leftMargin = 0;
		layout.rightMargin = 0;
		
		colorDisplay = new Label(this, SWT.BORDER);
		colorDisplay.setText("");
		TableWrapData wrapData = new TableWrapData(TableWrapData.FILL_GRAB);
		wrapData.valign = TableWrapData.MIDDLE;
		wrapData.grabHorizontal = true;
		wrapData.grabVertical = true;
		colorDisplay.setLayoutData(wrapData);
		
		Button btnChange = new Button(this, SWT.PUSH);
		btnChange.setImage(new Image(this.getDisplay(), Activator.getImageDescriptor("/icons/eclipse/colors.gif").getImageData()));
		btnChange.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				
				ColorDialog colorDialog = new ColorDialog(getShell(), SWT.DIALOG_TRIM);
				//colorDialog.getParent().setLocation(thisControl.toDisplay(thisControl.getLocation()));
				
				colorDialog.setRGB(getRGB());
				RGB userChoice = colorDialog.open();
				
				if (userChoice != null) {
					setColor(getIntFromRGB(userChoice));
					
					fireSelectionListeners();
				}
			}
		});
		
		
		updateColorDisplay();
	}

	
	protected int getIntFromRGB(RGB rgb)
	{
		int[] rgba = {rgb.red, rgb.green, rgb.blue, 0xFF};
		return ColorUtil.rgbaToInt(rgba);
	}
	
	
	protected RGB getRGB()
	{
		int[] rgba = new int[4];
		ColorUtil.intToRGBA(this.selection, rgba);
		return new RGB(rgba[0], rgba[1], rgba[2]);
	}
	
	protected void updateColorDisplay()
	{
		colorDisplay.setBackground(new Color(this.getDisplay(), getRGB()));
	}
	
	public void setColor(int color)
	{
		this.selection = color;
		updateColorDisplay();
	}
	
	public int getColor()
	{
		return this.selection;
	}
	
	public void addSelectionListener(SelectionListener l)
	{
		this.addListener(SWT.Selection, new TypedListener(l));
	}
	
	public void removeSelectionListener(SelectionListener l)
	{
		this.removeListener(SWT.Selection, l);
	}
	
	protected void fireSelectionListeners()
	{
		this.notifyListeners(SWT.Selection, new Event());

	}
	
}

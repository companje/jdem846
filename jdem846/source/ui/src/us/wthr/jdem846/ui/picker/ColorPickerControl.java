package us.wthr.jdem846.ui.picker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Panel;

@SuppressWarnings("serial")
public class ColorPickerControl extends Panel
{
	private static Log log = Logging.getLog(ColorPickerControl.class);
	
	private ColorSwatch selectedColorSwatch;
	private SpectrumPicker picker;
	
	public ColorPickerControl() throws IOException
	{
		picker = new SpectrumPicker();
		
		selectedColorSwatch = new ColorSwatch();
		
		
		MouseAdapter pickerMouseAdapter = new MouseAdapter() {
			public void mouseClicked(MouseEvent e)
			{
				onSelectionChanged(picker.getSelectedColor());
			}
			public void mouseEntered(MouseEvent e)
			{
				
			}
			public void mouseExited(MouseEvent e)
			{
				
			}
			public void mouseMoved(MouseEvent e)
			{
				onSelectionChanged(picker.getMouseOverColor());
			}
			
		};
		picker.addMouseListener(pickerMouseAdapter);
		picker.addMouseMotionListener(pickerMouseAdapter);
		
		setLayout(new BorderLayout());
		add(selectedColorSwatch, BorderLayout.NORTH);
		add(picker, BorderLayout.CENTER);
	}
	
	public void onSelectionChanged(Color selection)
	{
		selectedColorSwatch.setDisplayColor(selection);
	}
	
}

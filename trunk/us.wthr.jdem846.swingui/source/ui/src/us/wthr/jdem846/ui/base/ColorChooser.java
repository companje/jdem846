package us.wthr.jdem846.ui.base;

import java.awt.Color;

import javax.swing.JColorChooser;
import javax.swing.colorchooser.ColorSelectionModel;

@SuppressWarnings("serial")
public class ColorChooser extends JColorChooser
{

	public ColorChooser()
	{
		super();
	}

	public ColorChooser(Color initialColor)
	{
		super(initialColor);
	}

	public ColorChooser(ColorSelectionModel model)
	{
		super(model);
	}

}

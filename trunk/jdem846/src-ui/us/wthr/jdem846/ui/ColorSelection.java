package us.wthr.jdem846.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Dialog;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.util.ColorSerializationUtil;

@SuppressWarnings("serial")
public class ColorSelection extends Panel
{
	private static Log log = Logging.getLog(ColorSelection.class);
	
	private Color value = Color.BLUE;
	private ColorPickerDialog picker = null;
	
	private List<ChangeListener> changeListeners = new LinkedList<ChangeListener>();
	
	public ColorSelection()
	{
		
		
		setBorder(BorderFactory.createEtchedBorder());
		this.setOpaque(false);
		
		picker = new ColorPickerDialog(value);
		picker.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				onColorSelected();
			}
		});
		
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e)
			{
				onColorClicked();
			}
		});
		
	}

	protected void onColorClicked()
	{
		picker.setVisible(true);
	}
	
	protected void onColorSelected()
	{
		picker.setVisible(false);
		
		setValue(picker.getSelectedColor());
		repaint();
		fireChangeListeners();
	}
	
	public Color getValue()
	{
		return value;
	}

	public String getValueString()
	{
		return ColorSerializationUtil.colorToString(getValue());
	}
	
	public void setValue(Color value)
	{
		this.value = value;
		repaint();
	}
	
	public void setValueString(String valueString)
	{
		Color color = ColorSerializationUtil.stringToColor(valueString);
		
		if (color != null) {
			setValue(color);
		}
		
	}
	
	
	@Override
	public void paint(Graphics g)
	{
		g.setColor(value);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		super.paint(g);
	}
	
	public void fireChangeListeners()
	{
		ChangeEvent event = new ChangeEvent(this);
		
		for (ChangeListener listener : changeListeners)
		{
			listener.stateChanged(event);
		}
	}
	
	public void addChangeListener(ChangeListener listener)
	{
		changeListeners.add(listener);
	}
	
	public boolean removeChangeListener(ChangeListener listener)
	{
		return changeListeners.remove(listener);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	class ColorPickerDialog extends Dialog
	{
		
		private JColorChooser colorChooser;
		
		public ColorPickerDialog(Color selection)
		{
			
			colorChooser = new JColorChooser(selection);
			
			BorderLayout layout = new BorderLayout();
			setLayout(layout);
			
			add(colorChooser, BorderLayout.CENTER);
			doLayout();
			pack();
			this.setLocationRelativeTo(null);
		}
		
		public Color getSelectedColor()
		{
			return colorChooser.getSelectionModel().getSelectedColor();
		}
		
		public void addChangeListener(ChangeListener listener)
		{
			colorChooser.getSelectionModel().addChangeListener(listener);
		}
		
	}
}

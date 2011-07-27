package us.wthr.jdem846.swtui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class TextFactory
{
	
	public static Text createText(Composite parent)
	{
		Text text = new Text (parent, SWT.BORDER);
		Point point = text.computeSize(150, 20);
		point.x = 150;
		text.setSize(point);
		return text;
	}
	
	public static Text createNumberText(Composite parent)
	{
		final Text text = TextFactory.createText(parent);
		text.addListener (SWT.Verify, new Listener () {
			
			public void handleEvent (Event e) {
				Text text = (Text) e.widget;
				String string = text.getText() + e.text;
				
				if (!isNumber(string)) {
					e.doit = false;
				}
				
			}
			
			protected boolean isNumber(String s)
			{
				return s.matches("[-+]?[0-9]*\\.?[0-9]*");
			}
		});
		
		
		return text;
	}
	
}

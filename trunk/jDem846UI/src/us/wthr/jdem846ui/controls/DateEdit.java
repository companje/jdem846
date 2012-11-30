package us.wthr.jdem846ui.controls;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846ui.Activator;

public class DateEdit extends Composite
{
	private static Log log = Logging.getLog(DateEdit.class);
	
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
	
	private int year = 0;
	private int month = 0;
	private int day = 0;
	
	private Text txtDate;
	

	public DateEdit(Composite parent, int style) 
	{
		super(parent, style);
		
		Calendar now = Calendar.getInstance();
		this.year = now.get(Calendar.YEAR);
		this.month = now.get(Calendar.MONTH);
		this.day = now.get(Calendar.DAY_OF_MONTH);
		
		
		final DateEdit thisComposite = this;
		
		TableWrapLayout layout = new TableWrapLayout();
		this.setLayout(layout);
		layout.numColumns = 2;
		layout.topMargin = 0;
		layout.bottomMargin = 0;
		layout.leftMargin = 0;
		layout.rightMargin = 0;
		
		txtDate = new Text(this, SWT.BORDER);
		txtDate.setEditable(false);
		
		TableWrapData wrapData = new TableWrapData(TableWrapData.FILL_GRAB);
		wrapData.valign = TableWrapData.MIDDLE;
		wrapData.grabHorizontal = true;
		wrapData.grabVertical = true;
		txtDate.setLayoutData(wrapData);
		
		Button btnChange = new Button(this, SWT.PUSH);
		btnChange.setImage(new Image(this.getDisplay(), Activator.getImageDescriptor("/icons/eclipse/dates.gif").getImageData()));
		btnChange.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				final Shell dialog = new Shell (thisComposite.getShell(), SWT.DIALOG_TRIM);
				dialog.setLayout (new GridLayout (1, false));

				dialog.setLocation(thisComposite.toDisplay(thisComposite.getLocation()));
				
				final DateTime calendar = new DateTime (dialog, SWT.CALENDAR | SWT.BORDER);
				
				calendar.setYear(thisComposite.year);
				calendar.setMonth(thisComposite.month);
				calendar.setDay(thisComposite.day);
				
				Button ok = new Button (dialog, SWT.PUSH);
				ok.setText ("OK");
				ok.setLayoutData(new GridData (SWT.RIGHT, SWT.CENTER, false, false));
				
				ok.addSelectionListener (new SelectionAdapter () {
					public void widgetSelected (SelectionEvent e) {
						
						thisComposite.year = calendar.getYear();
						thisComposite.month = calendar.getMonth();
						thisComposite.day = calendar.getDay();
						log.info("Calendar date selected (MM/DD/YYYY) = " + (calendar.getMonth () + 1) + "/" + calendar.getDay () + "/" + calendar.getYear ());
						
						updateDateText();
						dialog.close ();
						
						fireSelectionListeners();
					}
				});
				dialog.setDefaultButton (ok);
				dialog.pack ();
				dialog.open ();
			}
		});
		
		
		updateDateText();
	}
	
	
	
	protected void updateDateText()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, this.year);
		cal.set(Calendar.MONTH, this.month);
		cal.set(Calendar.DAY_OF_MONTH, this.day);
		
		txtDate.setText(dateFormatter.format(cal.getTime()));
	}



	public int getYear() {
		return year;
	}



	public void setYear(int year) {
		this.year = year;
		updateDateText();
	}



	public int getMonth() {
		return month;
	}



	public void setMonth(int month) {
		this.month = month;
		updateDateText();
	}



	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
		updateDateText();
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

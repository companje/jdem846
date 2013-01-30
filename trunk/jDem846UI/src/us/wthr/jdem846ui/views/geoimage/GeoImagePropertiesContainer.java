package us.wthr.jdem846ui.views.geoimage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import us.wthr.jdem846.image.ISimpleGeoImage;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846ui.Activator;
import us.wthr.jdem846ui.controls.LabeledSpinner;

public class GeoImagePropertiesContainer extends Composite
{
	private static final int RESOLUTION_DIGITS = 6;
	private static final int RESOLUTION_MULTIPLE = (int) MathExt.pow(10, RESOLUTION_DIGITS);

	private static final int NO_DATA_DIGITS = 2;
	private static final int NO_DATA_MULTIPLE = (int) MathExt.pow(10, NO_DATA_DIGITS);
	
	private LabeledSpinner spnNorth;
	private LabeledSpinner spnSouth;
	private LabeledSpinner spnEast;
	private LabeledSpinner spnWest;
	private LabeledSpinner spnLatitudeResolution;
	private LabeledSpinner spnLongitudeResolution;
	private LabeledSpinner spnImageWidth;
	private LabeledSpinner spnImageHeight;

	private Button btnApply;
	private Button btnReset;
	
	private Button btnDetermineNorth;
	private Button btnDetermineSouth;
	private Button btnDetermineEast;
	private Button btnDetermineWest;

	private Button btnDetermineLatitudeResolution;
	private Button btnDetermineLongitudeResolution;
	
	private ISimpleGeoImage simpleGeoImage;
	
	public GeoImagePropertiesContainer(Composite parent, int style)
	{
		super(parent, style);
		this.setLayout(new FillLayout());
		
		
		
		TableWrapLayout layout;

		TableWrapData td;

		Image variableIcon = Activator.getImageDescriptor("icons/eclipse/variable_view.gif").createImage();

		FormToolkit toolkit = new FormToolkit(this.getDisplay());
		final ScrolledForm form = toolkit.createScrolledForm(this);

		layout = new TableWrapLayout();
		form.getBody().setLayout(layout);

		SelectionListener selectionListener = new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				updateImageDefinition(simpleGeoImage);
			}
		};

		Section geoLocationSection = toolkit.createSection(form.getBody(), Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
		td = new TableWrapData(TableWrapData.FILL_GRAB);

		geoLocationSection.setLayoutData(td);
		geoLocationSection.addExpansionListener(new ExpansionAdapter()
		{
			public void expansionStateChanged(ExpansionEvent e)
			{
				form.reflow(true);
			}
		});
		geoLocationSection.setText("Geolocation");

		Composite geoLocationComposite = toolkit.createComposite(geoLocationSection);
		layout = new TableWrapLayout();
		geoLocationComposite.setLayout(layout);
		layout.numColumns = 3;

		
		spnImageHeight = LabeledSpinner.create(geoLocationComposite, "Height:", 1, 1000000, 0, 1);
		spnImageHeight.getControl().addSelectionListener(selectionListener);
		new Label(geoLocationComposite, SWT.NONE);
		
		spnImageWidth = LabeledSpinner.create(geoLocationComposite, "Width:", 1, 1000000, 0, 1);
		spnImageWidth.getControl().addSelectionListener(selectionListener);
		new Label(geoLocationComposite, SWT.NONE);
		
		spnImageWidth.getControl().setEnabled(false);
		spnImageHeight.getControl().setEnabled(false);
		
		spnNorth = LabeledSpinner.create(geoLocationComposite, "North:", -18000, 18000, 2, 100);
		spnNorth.getControl().addSelectionListener(selectionListener);
		btnDetermineNorth = new Button(geoLocationComposite, SWT.PUSH);
		btnDetermineNorth.setImage(variableIcon);
		btnDetermineNorth.setToolTipText("Autocalculate This Field");
		btnDetermineNorth.setLayoutData(new TableWrapData(TableWrapData.RIGHT, TableWrapData.MIDDLE));
		btnDetermineNorth.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (simpleGeoImage != null) {
					simpleGeoImage.getImageDefinition().determineNorth();
					initializeFromImage(simpleGeoImage);
				}
			}
		});
		
		
		spnSouth = LabeledSpinner.create(geoLocationComposite, "South:", -18000, 18000, 2, 100);
		spnSouth.getControl().addSelectionListener(selectionListener);
		btnDetermineSouth = new Button(geoLocationComposite, SWT.PUSH);
		btnDetermineSouth.setImage(variableIcon);
		btnDetermineSouth.setToolTipText("Autocalculate This Field");
		btnDetermineSouth.setLayoutData(new TableWrapData(TableWrapData.RIGHT, TableWrapData.MIDDLE));
		btnDetermineSouth.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (simpleGeoImage != null) {
					simpleGeoImage.getImageDefinition().determineSouth();
					initializeFromImage(simpleGeoImage);
				}
			}
		});
		
		spnEast = LabeledSpinner.create(geoLocationComposite, "East:", -36000, 36000, 2, 100);
		spnEast.getControl().addSelectionListener(selectionListener);
		btnDetermineEast = new Button(geoLocationComposite, SWT.PUSH);
		btnDetermineEast.setImage(variableIcon);
		btnDetermineEast.setToolTipText("Autocalculate This Field");
		btnDetermineEast.setLayoutData(new TableWrapData(TableWrapData.RIGHT, TableWrapData.MIDDLE));
		btnDetermineEast.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (simpleGeoImage != null) {
					simpleGeoImage.getImageDefinition().determineEast();
					initializeFromImage(simpleGeoImage);
				}
			}
		});
		
		
		spnWest = LabeledSpinner.create(geoLocationComposite, "West:", -36000, 36000, 2, 100);
		spnWest.getControl().addSelectionListener(selectionListener);
		btnDetermineWest = new Button(geoLocationComposite, SWT.PUSH);
		btnDetermineWest.setImage(variableIcon);
		btnDetermineWest.setToolTipText("Autocalculate This Field");
		btnDetermineWest.setLayoutData(new TableWrapData(TableWrapData.RIGHT, TableWrapData.MIDDLE));
		btnDetermineWest.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (simpleGeoImage != null) {
					simpleGeoImage.getImageDefinition().determineWest();
					initializeFromImage(simpleGeoImage);
				}
			}
		});
		
		
		spnLatitudeResolution = LabeledSpinner.create(geoLocationComposite, "Latitude Resolution:", 0, 360 * RESOLUTION_MULTIPLE, RESOLUTION_DIGITS, RESOLUTION_MULTIPLE);
		spnLatitudeResolution.getControl().addSelectionListener(selectionListener);
		btnDetermineLatitudeResolution = new Button(geoLocationComposite, SWT.PUSH);
		btnDetermineLatitudeResolution.setImage(variableIcon);
		btnDetermineLatitudeResolution.setToolTipText("Autocalculate This Field");
		btnDetermineLatitudeResolution.setLayoutData(new TableWrapData(TableWrapData.RIGHT, TableWrapData.MIDDLE));
		btnDetermineLatitudeResolution.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (simpleGeoImage != null) {
					simpleGeoImage.getImageDefinition().determineLatitudeResolution();
					initializeFromImage(simpleGeoImage);
				}
			}
		});
		
		
		spnLongitudeResolution = LabeledSpinner.create(geoLocationComposite, "Longitude Resolution:", 0, 360 * RESOLUTION_MULTIPLE, RESOLUTION_DIGITS, RESOLUTION_MULTIPLE);
		spnLongitudeResolution.getControl().addSelectionListener(selectionListener);
		btnDetermineLongitudeResolution = new Button(geoLocationComposite, SWT.PUSH);
		btnDetermineLongitudeResolution.setImage(variableIcon);
		btnDetermineLongitudeResolution.setToolTipText("Autocalculate This Field");
		btnDetermineLongitudeResolution.setLayoutData(new TableWrapData(TableWrapData.RIGHT, TableWrapData.MIDDLE));
		btnDetermineLongitudeResolution.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (simpleGeoImage != null) {
					simpleGeoImage.getImageDefinition().determineLongitudeResolution();
					initializeFromImage(simpleGeoImage);
				}
			}
		});
		
		
		geoLocationSection.setClient(geoLocationComposite);
		
		
		
		Composite buttonComposite = toolkit.createComposite(form.getBody());
		layout = new TableWrapLayout();
		layout.numColumns = 2;
		buttonComposite.setLayout(layout);

		btnApply = new Button(buttonComposite, SWT.PUSH);
		btnApply.setText("Apply");
		btnApply.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0)
			{
				
			}
		});
		
		btnReset = new Button(buttonComposite, SWT.PUSH);
		btnReset.setText("Reset");
		btnReset.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event)
			{
				initializeFromImage(simpleGeoImage);
			}
		});
		
		setImageDefinition(null);
		
		this.pack();
	}
	
	public void setImageDefinition(ISimpleGeoImage simpleGeoImage)
	{
		this.simpleGeoImage = simpleGeoImage;
		this.initializeFromImage(simpleGeoImage);
	}

	public void reset()
	{
		spnNorth.getControl().setSelection(0);
		spnSouth.getControl().setSelection(0);
		spnEast.getControl().setSelection(0);
		spnWest.getControl().setSelection(0);
		spnLatitudeResolution.getControl().setSelection(0);
		spnLongitudeResolution.getControl().setSelection(0);
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		spnNorth.getControl().setEnabled(enabled);
		spnSouth.getControl().setEnabled(enabled);
		spnEast.getControl().setEnabled(enabled);
		spnWest.getControl().setEnabled(enabled);
		spnLatitudeResolution.getControl().setEnabled(enabled);
		spnLongitudeResolution.getControl().setEnabled(enabled);
	}

	protected void initializeFromImage(ISimpleGeoImage img)
	{

		reset();
		if (img == null) {
			this.setEnabled(false);
			return;
		} else {
			this.setEnabled(true);
		}
		spnImageWidth.getControl().setSelection(img.getWidth());
		spnImageHeight.getControl().setSelection(img.getHeight());
		spnNorth.getControl().setSelection((int) MathExt.round(img.getNorth() * 100));
		spnSouth.getControl().setSelection((int) MathExt.round(img.getSouth() * 100));
		spnEast.getControl().setSelection((int) MathExt.round(img.getEast() * 100));
		spnWest.getControl().setSelection((int) MathExt.round(img.getWest() * 100));
		spnLatitudeResolution.getControl().setSelection((int) MathExt.round(img.getLatitudeResolution() * RESOLUTION_MULTIPLE));
		spnLongitudeResolution.getControl().setSelection((int) MathExt.round(img.getLongitudeResolution() * RESOLUTION_MULTIPLE));

		
	}
	protected void updateImageDefinition(ISimpleGeoImage img)
	{
		if (img == null) {
			return;
		}

		img.setNorth(spnNorth.getControl().getSelection() / 100.0);
		img.setSouth(spnSouth.getControl().getSelection() / 100.0);
		img.setEast(spnEast.getControl().getSelection() / 100.0);
		img.setWest(spnWest.getControl().getSelection() / 100.0);
		img.getImageDefinition().setLatitudeResolution(spnLatitudeResolution.getControl().getSelection() / (double) RESOLUTION_MULTIPLE);
		img.getImageDefinition().setLongitudeResolution(spnLongitudeResolution.getControl().getSelection() / (double) RESOLUTION_MULTIPLE);
	}
	
}

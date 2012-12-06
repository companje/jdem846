package us.wthr.jdem846ui.views.raster;

import java.text.NumberFormat;

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

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.DataTypeEnum;
import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.InterleavingTypeEnum;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.rasterdata.generic.IRasterDefinition;
import us.wthr.jdem846ui.Activator;
import us.wthr.jdem846ui.controls.LabeledCombo;
import us.wthr.jdem846ui.controls.LabeledSpinner;
import us.wthr.jdem846ui.controls.LabeledText;

public class RasterPropertiesContainer extends Composite
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
	private LabeledSpinner spnNumberOfBands;
	private LabeledSpinner spnImageHeaderSize;
	private LabeledSpinner spnNoData;
	private LabeledText txtFileSize;
	private LabeledCombo cmbDataType;
	private LabeledCombo cmbByteOrder;
	private LabeledCombo cmbInterleavingType;

	private IRasterDefinition rasterDefinition;

	private Button btnApply;
	private Button btnReset;

	private Button btnDetermineNorth;
	private Button btnDetermineSouth;
	private Button btnDetermineEast;
	private Button btnDetermineWest;

	private Button btnDetermineLatitudeResolution;
	private Button btnDetermineLongitudeResolution;

	public RasterPropertiesContainer(Composite parent, int style)
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
				updateRasterDefinition(rasterDefinition);
				updateFileSizeControl();
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
		geoLocationSection.setText("Geolocation && Dimensions");

		Composite geoLocationComposite = toolkit.createComposite(geoLocationSection);
		layout = new TableWrapLayout();
		geoLocationComposite.setLayout(layout);
		layout.numColumns = 3;

		// icons/eclipse/variable_view.gif
		spnImageHeight = LabeledSpinner.create(geoLocationComposite, "Height:", 1, 1000000, 0, 1);
		spnImageHeight.getControl().addSelectionListener(selectionListener);
		new Label(geoLocationComposite, SWT.NONE);

		spnImageWidth = LabeledSpinner.create(geoLocationComposite, "Width:", 1, 1000000, 0, 1);
		spnImageWidth.getControl().addSelectionListener(selectionListener);
		new Label(geoLocationComposite, SWT.NONE);

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
				if (rasterDefinition != null) {
					rasterDefinition.determineNorth();
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
				if (rasterDefinition != null) {
					rasterDefinition.determineSouth();
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
				if (rasterDefinition != null) {
					rasterDefinition.determineEast();
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
				if (rasterDefinition != null) {
					rasterDefinition.determineWest();
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
				if (rasterDefinition != null) {
					rasterDefinition.determineLatitudeResolution();
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
				if (rasterDefinition != null) {
					rasterDefinition.determineLongitudeResolution();
				}
			}
		});

		geoLocationSection.setClient(geoLocationComposite);

		Section rasterSpecsSection = toolkit.createSection(form.getBody(), Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
		td = new TableWrapData(TableWrapData.FILL_GRAB);

		rasterSpecsSection.setLayoutData(td);
		rasterSpecsSection.addExpansionListener(new ExpansionAdapter()
		{
			public void expansionStateChanged(ExpansionEvent e)
			{
				form.reflow(true);
			}
		});
		rasterSpecsSection.setText("Raster Specifications");

		Composite rasterSpecsComposite = toolkit.createComposite(rasterSpecsSection);
		layout = new TableWrapLayout();
		rasterSpecsComposite.setLayout(layout);
		layout.numColumns = 2;

		spnNumberOfBands = LabeledSpinner.create(rasterSpecsComposite, "Number of Bands:", 1, 100, 0, 1);
		spnNumberOfBands.getControl().addSelectionListener(selectionListener);

		spnImageHeaderSize = LabeledSpinner.create(rasterSpecsComposite, "Header Size:", 0, 999999999, 0, 1);
		spnImageHeaderSize.getControl().addSelectionListener(selectionListener);

		txtFileSize = LabeledText.create(rasterSpecsComposite, "File Size:");
		txtFileSize.getControl().setEditable(false);

		cmbDataType = LabeledCombo.create(rasterSpecsComposite, "Data Type:");
		cmbDataType.getControl().addSelectionListener(selectionListener);

		cmbByteOrder = LabeledCombo.create(rasterSpecsComposite, "Byte Order:");
		cmbByteOrder.getControl().addSelectionListener(selectionListener);

		cmbInterleavingType = LabeledCombo.create(rasterSpecsComposite, "Interleaving Type:");
		cmbInterleavingType.getControl().addSelectionListener(selectionListener);

		spnNoData = LabeledSpinner.create(rasterSpecsComposite, "No Data Value:", -100000000 * NO_DATA_MULTIPLE, 100000000 * NO_DATA_MULTIPLE, NO_DATA_DIGITS, NO_DATA_MULTIPLE);
		spnNoData.getControl().addSelectionListener(selectionListener);

		rasterSpecsSection.setClient(rasterSpecsComposite);

		for (DataTypeEnum type : DataTypeEnum.values()) {
			cmbDataType.getControl().add(type.name());
		}

		for (ByteOrder order : ByteOrder.values()) {
			cmbByteOrder.getControl().add(order.name());
		}

		for (InterleavingTypeEnum type : InterleavingTypeEnum.values()) {
			cmbInterleavingType.getControl().add(type.name());
		}

		Composite buttonComposite = toolkit.createComposite(form.getBody());
		layout = new TableWrapLayout();
		layout.numColumns = 2;
		buttonComposite.setLayout(layout);

		btnApply = new Button(buttonComposite, SWT.PUSH);
		btnApply.setText("Apply");

		btnReset = new Button(buttonComposite, SWT.PUSH);
		btnReset.setText("Reset");

		updateFileSizeControl();

		this.pack();

	}

	public void setRasterDefinition(IRasterDefinition rasterDefinition)
	{
		this.rasterDefinition = rasterDefinition;
		this.initializeFromRasterDefinition(rasterDefinition);
	}

	public void reset()
	{
		spnNorth.getControl().setSelection(0);
		spnSouth.getControl().setSelection(0);
		spnEast.getControl().setSelection(0);
		spnWest.getControl().setSelection(0);

		spnLatitudeResolution.getControl().setSelection(1 * RESOLUTION_DIGITS);
		spnLongitudeResolution.getControl().setSelection(1 * RESOLUTION_DIGITS);

		spnImageWidth.getControl().setSelection(0);
		spnImageHeight.getControl().setSelection(0);
		spnNumberOfBands.getControl().setSelection(1);
		spnImageHeaderSize.getControl().setSelection(1);

		cmbDataType.getControl().select(0);
		cmbByteOrder.getControl().select(0);
		cmbInterleavingType.getControl().select(0);

		spnNoData.getControl().setSelection((int) MathExt.round(DemConstants.ELEV_NO_DATA * NO_DATA_MULTIPLE));

		updateLockedState(null);

		updateFileSizeControl();
	}

	protected void initializeFromRasterDefinition(IRasterDefinition rd)
	{

		reset();
		if (rd == null) {
			return;
		}

		spnNorth.getControl().setSelection((int) MathExt.round(rd.getNorth() * 100));
		spnSouth.getControl().setSelection((int) MathExt.round(rd.getSouth() * 100));
		spnEast.getControl().setSelection((int) MathExt.round(rd.getEast() * 100));
		spnWest.getControl().setSelection((int) MathExt.round(rd.getWest() * 100));

		spnLatitudeResolution.getControl().setSelection((int) MathExt.round(rd.getLatitudeResolution() * RESOLUTION_MULTIPLE));
		spnLongitudeResolution.getControl().setSelection((int) MathExt.round(rd.getLongitudeResolution() * RESOLUTION_MULTIPLE));

		spnImageWidth.getControl().setSelection(rd.getImageWidth());
		spnImageHeight.getControl().setSelection(rd.getImageHeight());
		spnNumberOfBands.getControl().setSelection(rd.getNumBands());
		spnImageHeaderSize.getControl().setSelection(rd.getHeaderSize());

		int index = getIndexOfString(cmbDataType, rd.getDataType().name());
		if (index >= 0)
			cmbDataType.getControl().select(index);

		index = getIndexOfString(cmbByteOrder, rd.getByteOrder().name());
		if (index >= 0)
			cmbByteOrder.getControl().select(index);

		index = getIndexOfString(cmbInterleavingType, rd.getInterleavingType().name());
		if (index >= 0)
			cmbInterleavingType.getControl().select(index);

		spnNoData.getControl().setSelection((int) MathExt.round(rd.getNoData() * NO_DATA_MULTIPLE));

		updateLockedState(rd);
		updateFileSizeControl();
	}

	protected void updateLockedState(IRasterDefinition rd)
	{
		boolean enableControls = false;
		if (rd != null) {
			enableControls = !rd.isLocked();
		}

		spnNorth.getControl().setEnabled(enableControls);
		spnSouth.getControl().setEnabled(enableControls);
		spnEast.getControl().setEnabled(enableControls);
		spnWest.getControl().setEnabled(enableControls);
		spnLatitudeResolution.getControl().setEnabled(enableControls);
		spnLongitudeResolution.getControl().setEnabled(enableControls);
		spnImageWidth.getControl().setEnabled(enableControls);
		spnImageHeight.getControl().setEnabled(enableControls);
		spnNumberOfBands.getControl().setEnabled(enableControls);
		spnImageHeaderSize.getControl().setEnabled(enableControls);
		txtFileSize.getControl().setEnabled(enableControls);
		cmbDataType.getControl().setEnabled(enableControls);
		cmbByteOrder.getControl().setEnabled(enableControls);
		cmbInterleavingType.getControl().setEnabled(enableControls);
		spnNoData.getControl().setEnabled(enableControls);

		btnApply.setEnabled(enableControls);
		btnReset.setEnabled(enableControls);

		btnDetermineNorth.setEnabled(enableControls);
		btnDetermineSouth.setEnabled(enableControls);
		btnDetermineEast.setEnabled(enableControls);
		btnDetermineWest.setEnabled(enableControls);

		btnDetermineLatitudeResolution.setEnabled(enableControls);
		btnDetermineLongitudeResolution.setEnabled(enableControls);
	}

	protected void updateRasterDefinition(IRasterDefinition rd)
	{
		if (rd == null) {
			return;
		}

		rd.setNorth(spnNorth.getControl().getSelection() / 100.0);
		rd.setSouth(spnSouth.getControl().getSelection() / 100.0);
		rd.setEast(spnEast.getControl().getSelection() / 100.0);
		rd.setWest(spnWest.getControl().getSelection() / 100.0);

		rd.setLatitudeResolution((double) spnLatitudeResolution.getControl().getSelection() / (double) RESOLUTION_MULTIPLE);
		rd.setLongitudeResolution((double) spnLongitudeResolution.getControl().getSelection() / (double) RESOLUTION_MULTIPLE);

		rd.setImageWidth(spnImageWidth.getControl().getSelection());
		rd.setImageHeight((Integer) spnImageHeight.getControl().getSelection());
		rd.setNumBands(spnNumberOfBands.getControl().getSelection());
		rd.setHeaderSize(spnImageHeaderSize.getControl().getSelection());

		DataTypeEnum dataType = DataTypeEnum.valueOf(cmbDataType.getControl().getText());
		if (dataType != null) {
			rd.setDataType(dataType);
		}

		ByteOrder byteOrder = ByteOrder.valueOf(cmbByteOrder.getControl().getText());
		if (byteOrder != null) {
			rd.setByteOrder(byteOrder);
		}

		InterleavingTypeEnum interleavingType = InterleavingTypeEnum.valueOf(cmbInterleavingType.getControl().getText());
		if (interleavingType != null) {
			rd.setInterleavingType(interleavingType);
		}

		rd.setNoData((double) spnNoData.getControl().getSelection() / (double) NO_DATA_MULTIPLE);

	}

	protected void updateFileSizeControl()
	{
		NumberFormat formatter = NumberFormat.getIntegerInstance();
		formatter.setGroupingUsed(true);

		if (this.rasterDefinition != null) {
			txtFileSize.getControl().setText(formatter.format(this.rasterDefinition.getFileSize()));
		} else {
			txtFileSize.getControl().setText(formatter.format(0));
		}
	}

	protected int getIndexOfString(LabeledCombo combo, String label)
	{
		for (int i = 0; i < combo.getControl().getItemCount(); i++) {
			if (combo.getControl().getItem(i) != null && combo.getControl().getItem(i).equals(label)) {
				return i;
			}
		}

		return -1;
	}

}

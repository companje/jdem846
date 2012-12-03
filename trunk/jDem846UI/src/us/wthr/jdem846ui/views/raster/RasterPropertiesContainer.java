package us.wthr.jdem846ui.views.raster;

import java.text.NumberFormat;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.DataTypeEnum;
import us.wthr.jdem846.InterleavingTypeEnum;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.rasterdata.generic.RasterDefinition;
import us.wthr.jdem846ui.controls.LabeledCombo;
import us.wthr.jdem846ui.controls.LabeledSpinner;
import us.wthr.jdem846ui.controls.LabeledText;

public class RasterPropertiesContainer extends Composite
{

	private LabeledSpinner spnNorth;
	private LabeledSpinner spnSouth;
	private LabeledSpinner spnEast;
	private LabeledSpinner spnWest;
	private LabeledSpinner spnImageWidth;
	private LabeledSpinner spnImageHeight;
	private LabeledSpinner spnNumberOfBands;
	private LabeledSpinner spnImageHeaderSize;
	private LabeledText txtFileSize;
	private LabeledCombo cmbDataType;
	private LabeledCombo cmbByteOrder;
	private LabeledCombo cmbInterleavingType;
	
	private RasterDefinition rasterDefinition;
	
	public RasterPropertiesContainer(Composite parent, int style)
	{
		super(parent, style);
		
		TableWrapLayout layout = new TableWrapLayout();
		this.setLayout(layout);
		layout.numColumns = 2;
		
		SelectionListener selectionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateRasterDefinition(rasterDefinition);
				
				updateFileSizeControl();
			}
		};
		
		spnNorth = LabeledSpinner.create(this, "North:", -18000, 18000, 2, 100);
		spnNorth.getControl().addSelectionListener(selectionListener);
		
		spnSouth = LabeledSpinner.create(this, "South:", -18000, 18000, 2, 100);
		spnSouth.getControl().addSelectionListener(selectionListener);
		
		spnEast = LabeledSpinner.create(this, "East:", -36000, 36000, 2, 100);
		spnEast.getControl().addSelectionListener(selectionListener);
		
		spnWest = LabeledSpinner.create(this, "West:", -36000, 36000, 2, 100);
		spnWest.getControl().addSelectionListener(selectionListener);
		
		spnImageWidth = LabeledSpinner.create(this, "Width:", 1, 1000000, 0, 1);
		spnImageWidth.getControl().addSelectionListener(selectionListener);
		
		spnImageHeight = LabeledSpinner.create(this, "Height:", 1, 1000000, 0, 1);
		spnImageHeight.getControl().addSelectionListener(selectionListener);
		
		spnNumberOfBands = LabeledSpinner.create(this, "Number of Bands:", 1, 100, 0, 1);
		spnNumberOfBands.getControl().addSelectionListener(selectionListener);
		
		spnImageHeaderSize = LabeledSpinner.create(this, "Header Size:", 0, 999999999, 0, 1);
		spnImageHeaderSize.getControl().addSelectionListener(selectionListener);
		
		txtFileSize = LabeledText.create(this, "File Size:");
		txtFileSize.getControl().setEditable(false);
		
		cmbDataType = LabeledCombo.create(this, "Data Type:");
		cmbDataType.getControl().addSelectionListener(selectionListener);
		
		cmbByteOrder = LabeledCombo.create(this, "Byte Order:");
		cmbByteOrder.getControl().addSelectionListener(selectionListener);
		
		cmbInterleavingType = LabeledCombo.create(this, "Interleaving Type:");
		cmbInterleavingType.getControl().addSelectionListener(selectionListener);
		
		
		for (DataTypeEnum type : DataTypeEnum.values()) {
			cmbDataType.getControl().add(type.name());
		}
		
		for (ByteOrder order : ByteOrder.values()) {
			cmbByteOrder.getControl().add(order.name());
		}
		
		for (InterleavingTypeEnum type : InterleavingTypeEnum.values()) {
			cmbInterleavingType.getControl().add(type.name());
		}
		
		
		updateFileSizeControl();
		
		this.pack();
		
		
	}
	
	public void setRasterDefinition(RasterDefinition rasterDefinition)
	{
		this.rasterDefinition = rasterDefinition;
		this.initializeFromRasterDefinition(rasterDefinition);
	}
	
	protected void reset()
	{
		spnNorth.getControl().setSelection(0);
		spnSouth.getControl().setSelection(0);
		spnEast.getControl().setSelection(0);
		spnWest.getControl().setSelection(0);
		
		spnImageWidth.getControl().setSelection(0);
		spnImageHeight.getControl().setSelection(0);
		spnNumberOfBands.getControl().setSelection(1);
		spnImageHeaderSize.getControl().setSelection(1);
		
		cmbDataType.getControl().select(0);
		cmbByteOrder.getControl().select(0);
		cmbInterleavingType.getControl().select(0);
		
		updateFileSizeControl();
	}
	
	protected void initializeFromRasterDefinition(RasterDefinition rd)
	{
		
		reset();
		if (rd == null) {
			return;
		}
		
		spnNorth.getControl().setSelection((int)MathExt.round(rd.getNorth() * 100));
		spnSouth.getControl().setSelection((int)MathExt.round(rd.getSouth() * 100));
		spnEast.getControl().setSelection((int)MathExt.round(rd.getEast() * 100));
		spnWest.getControl().setSelection((int)MathExt.round(rd.getWest() * 100));
		
		
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
		
		updateFileSizeControl();
	}
	
	
	protected void updateRasterDefinition(RasterDefinition rd)
	{
		if (rd == null) {
			return;
		}
		
		rd.setNorth(spnNorth.getControl().getSelection() / 100.0);
		rd.setSouth(spnSouth.getControl().getSelection() / 100.0);
		rd.setEast(spnEast.getControl().getSelection() / 100.0);
		rd.setWest(spnWest.getControl().getSelection() / 100.0);
		
		rd.setImageWidth(spnImageWidth.getControl().getSelection());
		rd.setImageHeight((Integer)spnImageHeight.getControl().getSelection());
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

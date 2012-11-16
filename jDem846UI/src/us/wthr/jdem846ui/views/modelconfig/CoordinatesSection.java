package us.wthr.jdem846ui.views.modelconfig;

import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import us.wthr.jdem846ui.controls.FormSection;
import us.wthr.jdem846ui.controls.LabeledCheck;
import us.wthr.jdem846ui.controls.LabeledSpinner;
import us.wthr.jdem846ui.controls.LabeledText;
import us.wthr.jdem846ui.controls.SectionContainer;
import us.wthr.jdem846ui.controls.SectionFactory;

public class CoordinatesSection extends FormSection
{

	private LabeledCheck limitCoordinates;
	private LabeledSpinner north;
	private LabeledSpinner south;
	private LabeledSpinner east;
	private LabeledSpinner west;
	
	
	protected CoordinatesSection()
	{
		
	}
	
	
	public static CoordinatesSection create(FormToolkit toolkit, ScrolledForm parent)
	{
		CoordinatesSection section = new CoordinatesSection();
		
		section.sectionContainer = SectionFactory.createSection(toolkit, parent, "Coordinates");
		
		section.limitCoordinates = LabeledCheck.create(toolkit, section.sectionContainer.getSectionClient(), "Limit Coordinates");
		section.north = LabeledSpinner.create(toolkit, section.sectionContainer.getSectionClient(), "North:", -18000, 18000, 2, 100);
		section.south = LabeledSpinner.create(toolkit, section.sectionContainer.getSectionClient(), "South:", -18000, 18000, 2, 100);
		section.east = LabeledSpinner.create(toolkit, section.sectionContainer.getSectionClient(), "East:", -36000, 36000, 2, 100);
		section.west = LabeledSpinner.create(toolkit, section.sectionContainer.getSectionClient(), "West:", -36000, 3600, 2, 100);

		return section;
		
	}
}

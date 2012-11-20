package us.wthr.jdem846ui.views.modelconfig;

import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import us.wthr.jdem846ui.controls.FormSection;
import us.wthr.jdem846ui.controls.LabeledCombo;
import us.wthr.jdem846ui.controls.LabeledSpinner;
import us.wthr.jdem846ui.controls.SectionFactory;

public class ProjectionSection extends FormSection 
{
	private LabeledCombo elevationScale;
	private LabeledCombo mapProjection;
	private LabeledCombo renderProjection;
	private LabeledCombo perspective;
	private LabeledSpinner fieldOfView;
	private LabeledSpinner eyeDistance;
	
	protected ProjectionSection()
	{
		
	}
	
	
	
	public LabeledCombo getElevationScale() {
		return elevationScale;
	}



	public void setElevationScale(LabeledCombo elevationScale) {
		this.elevationScale = elevationScale;
	}



	public LabeledCombo getMapProjection() {
		return mapProjection;
	}



	public void setMapProjection(LabeledCombo mapProjection) {
		this.mapProjection = mapProjection;
	}



	public LabeledCombo getRenderProjection() {
		return renderProjection;
	}



	public void setRenderProjection(LabeledCombo renderProjection) {
		this.renderProjection = renderProjection;
	}



	public LabeledCombo getPerspective() {
		return perspective;
	}



	public void setPerspective(LabeledCombo perspective) {
		this.perspective = perspective;
	}



	public static ProjectionSection create(FormToolkit toolkit, ScrolledForm parent)
	{
		ProjectionSection section = new ProjectionSection();
		
		section.sectionContainer = SectionFactory.createSection(toolkit, parent, "Projections");
		
		section.elevationScale = LabeledCombo.create(section.sectionContainer.getSectionClient(), "Elevation Scale", SWT.READ_ONLY);
		section.mapProjection = LabeledCombo.create(section.sectionContainer.getSectionClient(), "Map Projection", SWT.READ_ONLY);
		section.renderProjection = LabeledCombo.create(section.sectionContainer.getSectionClient(), "Render Projection", SWT.READ_ONLY);
		section.perspective = LabeledCombo.create(section.sectionContainer.getSectionClient(), "Perspective Type", SWT.READ_ONLY);
		section.fieldOfView = LabeledSpinner.create(section.sectionContainer.getSectionClient(), "Field of View", 0, 180, 0, 1);
		section.eyeDistance = LabeledSpinner.create(section.sectionContainer.getSectionClient(), "Eye Distance", 0, 1000000000, 0, 1000);
		
		return section;
		
	}
}

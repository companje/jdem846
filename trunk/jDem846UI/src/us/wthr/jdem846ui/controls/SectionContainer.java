package us.wthr.jdem846ui.controls;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

public class SectionContainer {
	private Section section;
	private Composite sectionClient;
	
	
	public SectionContainer(Section section, Composite sectionClient)
	{
		this.section = section;
		this.sectionClient = sectionClient;
	}


	public Section getSection() {
		return section;
	}


	public Composite getSectionClient() {
		return sectionClient;
	}
	
	
}

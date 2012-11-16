package us.wthr.jdem846ui.controls;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class SectionFactory {

	public static SectionContainer createSection(FormToolkit toolkit,
			final ScrolledForm parent, String title) {
		Section section = toolkit.createSection(parent.getBody(), Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);

		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.colspan = 2;
		section.setLayoutData(td);
		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				parent.reflow(true);
			}
		});
		section.setText(title);
		Composite sectionClient = toolkit.createComposite(section);
		TableWrapLayout layout = new TableWrapLayout();
		sectionClient.setLayout(layout);

		layout.numColumns = 2;

		section.setClient(sectionClient);

		return new SectionContainer(section, sectionClient);
	}
}

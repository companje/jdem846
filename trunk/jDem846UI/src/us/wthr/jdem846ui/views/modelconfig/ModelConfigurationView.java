package us.wthr.jdem846ui.views.modelconfig;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.OptionModelContainer;
import us.wthr.jdem846.model.exceptions.InvalidProcessOptionException;
import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846ui.project.ProjectContext;

public class ModelConfigurationView extends ViewPart {
	private static Log log = Logging.getLog(ModelConfigurationView.class);
	public static final String ID = "jdem846ui.modelConfigurationView";


	private TabFolder tabFolder;
	
	@Override
	public void createPartControl(Composite parent) {

		tabFolder = new TabFolder (parent, SWT.TOP);

		TabItem generalOptionsTabItem = new TabItem(tabFolder, SWT.NONE);
		generalOptionsTabItem.setText("General");

		OptionModelContainer globalOptionModelContainer = null;
		try {
			globalOptionModelContainer = new OptionModelContainer(ProjectContext.getInstance().getModelProcessManifest().getGlobalOptionModel());
		} catch (InvalidProcessOptionException ex) {
			// TODO Display error dialog
			log.error("Error loading global option model container: " + ex.getMessage(), ex);
			return;
		}
		
		ModelOptionPage globalOptionPage = new ModelOptionPage(tabFolder, globalOptionModelContainer);
		generalOptionsTabItem.setControl(globalOptionPage);
		
		
		
		List<OptionModel> optionModelList = new LinkedList<OptionModel>();
		optionModelList.addAll(ProjectContext.getInstance().getDefaultOptionModelList());
		
		String defaultColoringProcessor = JDem846Properties.getProperty("us.wthr.jdem846.ui.options.modelConfiguration.coloringProcessor.default");
		String defaultShadingProcessor = JDem846Properties.getProperty("us.wthr.jdem846.ui.options.modelConfiguration.shadingProcessor.default");
		String defaultRenderProcessor = JDem846Properties.getProperty("us.wthr.jdem846.ui.options.modelConfiguration.renderProcessor.default");
		
		TabItem coloringOptionsTabItem = new TabItem(tabFolder, SWT.NONE);
		coloringOptionsTabItem.setText("Coloring");
		ProcessTypeOptionPageContainer coloringOptionsPageContainer = new ProcessTypeOptionPageContainer(tabFolder, GridProcessingTypesEnum.COLORING, defaultColoringProcessor, optionModelList);
		coloringOptionsTabItem.setControl(coloringOptionsPageContainer);
		
		
		TabItem shadingOptionsTabItem = new TabItem(tabFolder, SWT.NONE);
		shadingOptionsTabItem.setText("Shading");
		ProcessTypeOptionPageContainer shadingOptionsPageContainer = new ProcessTypeOptionPageContainer(tabFolder, GridProcessingTypesEnum.SHADING, defaultShadingProcessor, optionModelList);
		shadingOptionsTabItem.setControl(shadingOptionsPageContainer);

	}

	@Override
	public void setFocus() {
		tabFolder.setFocus();
	}

	public void dispose() {
		tabFolder.dispose();
		super.dispose();
	}
}

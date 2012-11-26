package us.wthr.jdem846ui.views.modelconfig;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.OptionModelContainer;
import us.wthr.jdem846.model.exceptions.ProcessContainerException;
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

		OptionModelContainer globalOptionModelContainer = ProjectContext.getInstance().getOptionModelContainer(GlobalOptionModel.class);
		
		ModelOptionPage globalOptionPage = new ModelOptionPage(tabFolder, globalOptionModelContainer);
		generalOptionsTabItem.setControl(globalOptionPage);
		

		String defaultColoringProcessor = JDem846Properties.getProperty("us.wthr.jdem846.ui.options.modelConfiguration.coloringProcessor.default");
		String defaultShadingProcessor = JDem846Properties.getProperty("us.wthr.jdem846.ui.options.modelConfiguration.shadingProcessor.default");
		String defaultRenderProcessor = JDem846Properties.getProperty("us.wthr.jdem846.ui.options.modelConfiguration.renderProcessor.default");
		
		TabItem coloringOptionsTabItem = new TabItem(tabFolder, SWT.NONE);
		coloringOptionsTabItem.setText("Coloring");
		ProcessTypeOptionPageContainer coloringOptionsPageContainer = new ProcessTypeOptionPageContainer(tabFolder, GridProcessingTypesEnum.COLORING, defaultColoringProcessor);
		coloringOptionsTabItem.setControl(coloringOptionsPageContainer);
		
		
		TabItem shadingOptionsTabItem = new TabItem(tabFolder, SWT.NONE);
		shadingOptionsTabItem.setText("Shading");
		ProcessTypeOptionPageContainer shadingOptionsPageContainer = new ProcessTypeOptionPageContainer(tabFolder, GridProcessingTypesEnum.SHADING, defaultShadingProcessor);
		shadingOptionsTabItem.setControl(shadingOptionsPageContainer);
		
		
		try {
			
			ProjectContext.getInstance().getModelProcessManifest().addWorker(defaultColoringProcessor, ProjectContext.getInstance().getOptionModelContainer(defaultColoringProcessor).getOptionModel());
			ProjectContext.getInstance().getModelProcessManifest().addWorker(defaultShadingProcessor, ProjectContext.getInstance().getOptionModelContainer(defaultShadingProcessor).getOptionModel());
		} catch (ProcessContainerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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

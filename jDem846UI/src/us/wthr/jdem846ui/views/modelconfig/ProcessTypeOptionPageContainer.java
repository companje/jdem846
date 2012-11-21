package us.wthr.jdem846ui.views.modelconfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846.model.processing.ModelProcessRegistry;
import us.wthr.jdem846.model.processing.ProcessInstance;
import us.wthr.jdem846ui.controls.LabeledCombo;

public class ProcessTypeOptionPageContainer extends Composite {
	private static Log log = Logging.getLog(ProcessTypeOptionPageContainer.class);
	
	private Composite parent;
	private GridProcessingTypesEnum processType;
	private List<OptionModel> providedOptionModelList;
	private String initialSelection;
	private String currentProcessId = null;
	
	private LabeledCombo labeledCombo;
	
	private ProcessTypeListModel processTypeList;
	
	private Map<String, ModelOptionPage> modelOptionPages = new HashMap<String, ModelOptionPage>();
	
	private StackLayout stackLayout;
	private Composite optionCards;
	
	public ProcessTypeOptionPageContainer(Composite parent, GridProcessingTypesEnum processType, String initialSelection, List<OptionModel> providedOptionModelList)
	{
		super(parent, SWT.NONE);
		
		//this.setBackground(parent.getBackground());
		
		this.parent = parent;
		this.processType = processType;
		this.providedOptionModelList = providedOptionModelList;
		
		this.processTypeList = new ProcessTypeListModel(processType);
		
		TableWrapLayout layout = new TableWrapLayout();
		this.setLayout(layout);
		layout.numColumns = 2;
		
		
		labeledCombo = LabeledCombo.create(this, "Process:", SWT.READ_ONLY);
		
		
		optionCards = new Composite(this, SWT.NONE);
		TableWrapData layoutData = new TableWrapData();
		layoutData.colspan = 2;
		optionCards.setLayoutData(layoutData);
		
		stackLayout = new StackLayout();
		optionCards.setLayout(stackLayout);
		
		
		
		for (int i = 0; i < processTypeList.getProcessListSize(); i++) {
			String processName = processTypeList.getProcessNameAtIndex(i);
			String processId = processTypeList.getProcessIdAtIndex(i);
			
			labeledCombo.getControl().add(processName, i);
			if (processId.equals(initialSelection)) {
				labeledCombo.getControl().select(i);
				this.currentProcessId = initialSelection;
			}
			
			ModelOptionPage optionPage = new ModelOptionPage(optionCards, getProvidedOptionModel(processId));
			modelOptionPages.put(processId, optionPage);
		}
		
		labeledCombo.getControl().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				onProcessSelectionChange();
			}
		});
		
		onProcessSelectionChange();
		
	}
	
	protected void onProcessSelectionChange()
	{
		String label = labeledCombo.getControl().getText();
		String id = processTypeList.getIdByProcessName(label);
		//OptionModel providedOptionModel = getProvidedOptionModel(id);
		
		
		stackLayout.topControl = modelOptionPages.get(id);
		optionCards.layout();
	}
	
	
	protected OptionModel getProvidedOptionModel(String processId)
	{
		ProcessInstance processInstance = ModelProcessRegistry.getInstance(processId);
		
		if (processInstance != null) {
			log.info("Process Selected: " + processInstance.getId());
			this.currentProcessId = processInstance.getId();
			
			Class<?> clazz = processInstance.getOptionModelClass();
			
			for (OptionModel optionModel : this.providedOptionModelList) {
				if (optionModel.getClass().equals(clazz)) {
					return optionModel;
				}
			}
			
			return null;
			
		} else {
			log.info("Process not found with id " + processId);
			return null;
		}
	}
}

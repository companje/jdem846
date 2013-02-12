package us.wthr.jdem846ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.jsdt.internal.ui.javaeditor.CompilationUnitEditor;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.scripting.ScriptLanguageEnum;
import us.wthr.jdem846.scripting.ScriptingContext;
import us.wthr.jdem846ui.project.ProjectContext;

public class JavascriptEditor extends CompilationUnitEditor
{
	public static final String ID = "us.wthr.jdem846ui.editors.JavascriptEditor";
	private static Log log = Logging.getLog(JavascriptEditor.class);

	public JavascriptEditor()
	{
		super();
	}


	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}
	
	@Override
	protected boolean isLineNumberRulerVisible() { 
		return false; 
	} 


	@Override
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);
		//CompositeRuler compositeRuler = this.createCompositeRuler();
		//IVerticalRulerColumn rulerColumn = this.createLineNumberRulerColumn();
		//this.initializeLineNumberRulerColumn((LineNumberRulerColumn) rulerColumn);
		//showOverviewRuler();
		//this.getVerticalRuler().getControl().setVisible(true);
	}


	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		super.init(site, input);
	}

	@Override
	public void doSave(IProgressMonitor progressMonitor)
	{
		super.doSave(progressMonitor);
		log.info("Intercepting JavaScript save action");

		// Very, VERY, stupidly basic at the moment...
		updateScriptingContext();

	}

	protected void updateScriptingContext()
	{
		ScriptingContext scriptingContext = ProjectContext.getInstance().getScriptingContext();

		String script = this.getViewer().getDocument().get();

		scriptingContext.setUserScript(script);
		scriptingContext.setScriptLanguage(ScriptLanguageEnum.JAVASCRIPT);

		if (ProjectContext.getInstance().getModelProcessManifest().getGlobalOptionModel().getUseScripting()) {
			try {
				scriptingContext.prepare();
			} catch (Exception ex) {
				log.warn("Error compiling script: " + ex.getMessage(), ex);
				// ERROR!!!!!
			}
		}

	}

	@Override
	public void doRevertToSaved()
	{
		// TODO Auto-generated method stub
		super.doRevertToSaved();
	}

	@Override
	public boolean isDirty()
	{
		return super.isDirty();
	}

}

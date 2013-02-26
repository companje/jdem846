package us.wthr.jdem846ui.views.scripteditor;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import us.wthr.jdem846.project.context.ProjectChangeAdapter;
import us.wthr.jdem846.project.context.ProjectContext;

/**
 * http://www.realsolve.co.uk/site/tech/jface-text.php
 * 
 * @author Kevin M. Gill
 * 
 */
public class ScriptEditorView extends ViewPart
{
	public static final String ID = "jdem846ui.scriptEditorView";

	private SourceViewer viewer;
	private IDocument doc; 


	@Override
	public void createPartControl(Composite parent)
	{

		viewer = new SourceViewer(parent, new VerticalRuler(0), SWT.V_SCROLL 
				| SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
		//doc = new Document(); 
		//doc.set(); 
		//viewer.setDocument(doc); 

		//JavaSourceViewerConfiguration javaConf = new 
		//JavaSourceViewerConfiguration 
		//(org.eclipse.jdt.ui.JavaUI.getColorManager(), 
	//	org.eclipse.jdt.ui.PreferenceConstants.getPreferenceStore(), null, 
	//	null); 
		//viewer.configure(javaConf); 

		ProjectContext.getInstance().addProjectChangeListener(new ProjectChangeAdapter()
		{
			public void onProjectLoaded(String filePath)
			{
				loadProjectScript();
			}
		});
		
		loadProjectScript();
	}

	
	protected void loadProjectScript()
	{
		doc = new Document(); 
		doc.set(ProjectContext.getInstance().getScriptingContext().getUserScript());
		viewer.setDocument(doc);
		viewer.getControl().redraw();
	}
	
	@Override
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}

}

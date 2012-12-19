package us.wthr.jdem846ui.views;

import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import us.wthr.jdem846ui.project.ProjectChangeAdapter;
import us.wthr.jdem846ui.project.ProjectContext;

/**
 * http://www.realsolve.co.uk/site/tech/jface-text.php
 * @author Kevin M. Gill
 *
 */
public class ScriptEditorView extends ViewPart
{
	public static final String ID = "jdem846ui.scriptEditorView";
	
	private TextViewer viewer;
	
	@Override
	public void createPartControl(Composite parent)
	{

		String filename = "file:/C:/jdem/temp/unique.js";
		viewer = new TextViewer(parent, SWT.NONE);
		
		ProjectContext.getInstance().addProjectChangeListener(new ProjectChangeAdapter() {
			public void onProjectLoaded(String filePath)
			{
				
			}
		});
	}

	@Override
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}

}

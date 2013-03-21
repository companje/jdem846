package us.wthr.jdem846ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846ui.views.LogConsoleView;
import us.wthr.jdem846ui.views.data.DataView;
import us.wthr.jdem846ui.views.layers.LayerPropertiesView;
import us.wthr.jdem846ui.views.modelconfig.ModelConfigurationView;
import us.wthr.jdem846ui.views.models.ModelStatisticsView;
import us.wthr.jdem846ui.views.preview.MiniPreviewView;
import us.wthr.jdem846ui.views.preview.PreviewView;
import us.wthr.jdem846ui.views.scripteditor.ScriptEditorView;

public class Perspective implements IPerspectiveFactory
{

	/**
	 * The ID of the perspective as specified in the extension.
	 */
	public static final String ID = "jDem846UI.perspective";

	public void createInitialLayout(IPageLayout layout)
	{
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		
		IFolderLayout topLeftFolder = layout.createFolder("topLeft", IPageLayout.LEFT, 0.30f, editorArea);
		topLeftFolder.addView(DataView.ID);

		IFolderLayout bottomLeftFolder = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.40f, "topLeft");
		bottomLeftFolder.addView(LayerPropertiesView.ID);
		
		
		

		IFolderLayout topRightFolder = layout.createFolder("topRight", IPageLayout.RIGHT, 0.60f, editorArea);
		topRightFolder.addView(ModelConfigurationView.ID);
		
		IFolderLayout bottomRightFolder = layout.createFolder("bottomRight", IPageLayout.BOTTOM, 0.40f, "topRight");
		bottomRightFolder.addView(MiniPreviewView.ID);

		IFolderLayout bottomFolder = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.75f, editorArea);
		if (JDem846Properties.getBooleanProperty("us.wthr.jdem846.general.ui.displayLogViewPanel")) {
			bottomFolder.addView(LogConsoleView.ID);
		}
		bottomFolder.addView("org.eclipse.ui.views.ProgressView");
		bottomFolder.addView(ModelStatisticsView.ID);
		//bottomFolder.addView(RenderedModelDisplayView.ID);
		
		layout.getViewLayout(PreviewView.ID).setCloseable(false);
		layout.getViewLayout(ScriptEditorView.ID).setCloseable(false);
		layout.getViewLayout(DataView.ID).setCloseable(false);
		layout.getViewLayout(ModelConfigurationView.ID).setCloseable(false);
		layout.getViewLayout(ModelStatisticsView.ID).setCloseable(false);
		//layout.getViewLayout(RenderedModelPropertiesView.ID).setCloseable(false);
		//layout.getViewLayout(RenderedModelDisplayView.ID).setCloseable(false);
		layout.getViewLayout(LayerPropertiesView.ID).setCloseable(false);
	}

}

package us.wthr.jdem846ui.observers;

public interface PreviewRenderThreadCallback {
	public void render(boolean dataModelChange, boolean optionsChanged, boolean force) throws Exception;
}

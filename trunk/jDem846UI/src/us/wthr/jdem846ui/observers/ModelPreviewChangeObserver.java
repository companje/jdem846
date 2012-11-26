package us.wthr.jdem846ui.observers;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.ModelBuilder;
import us.wthr.jdem846.model.OptionModelChangeEvent;
import us.wthr.jdem846ui.project.ProjectChangeListener;
import us.wthr.jdem846ui.project.ProjectContext;

public class ModelPreviewChangeObserver extends ProjectChangeObserver {
	
	private static Log log = Logging.getLog(ModelPreviewChangeObserver.class);
	
	private static ModelPreviewChangeObserver INSTANCE;
	
	private ModelContext modelContextWorkingCopy = null;
	private double previewTextureQuality = 0.25;
	private double previewModelQuality = 0.25;
	
	private boolean autoUpdate = true;
	private boolean useScripting = false;
	
	private ModelBuilder modelBuilder = null;
	
	private List<ModelPreviewReadyListener> previewReadyListeners = new LinkedList<ModelPreviewReadyListener>();
	
	public ModelPreviewChangeObserver()
	{
		super();
		ModelPreviewChangeObserver.INSTANCE = this;
	}
	
	@Override
	public void onDataAdded() {
		
	}

	@Override
	public void onDataRemoved() {
		
	}

	@Override
	public void onOptionChanged(OptionModelChangeEvent e) {
		
	}
	
	
	
	public void addModelPreviewReadyListener(ModelPreviewReadyListener l)
	{
		this.previewReadyListeners.add(l);
	}
	
	public boolean removeModelPreviewReadyListener(ModelPreviewReadyListener l)
	{
		return this.previewReadyListeners.remove(l);
	}
	
	protected void fireModelPreviewReadyListeners()
	{
		for (ModelPreviewReadyListener l : this.previewReadyListeners) {
			l.onPreviewReady();
		}
	}
	
	public static ModelPreviewChangeObserver getInstance()
	{
		return ModelPreviewChangeObserver.INSTANCE;
	}
}

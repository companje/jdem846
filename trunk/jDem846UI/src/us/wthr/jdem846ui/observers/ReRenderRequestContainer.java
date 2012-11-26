package us.wthr.jdem846ui.observers;

public class ReRenderRequestContainer {
	private boolean reRenderNeeded = false;
	private boolean dataModelChanged = false;
	private boolean optionsChanged = false;
	private boolean force = false;
	private boolean working = false;
	
	public ReRenderRequestContainer()
	{
		
	}
	
	public void setRendererWorking(boolean working)
	{
		synchronized(this) {
			this.working = working;
		}
	}
	
	public boolean isRendererWorking()
	{
		synchronized(this) {
			return this.working;
		}
	}
	
	public boolean isReRenderNeeded()
	{
		synchronized(this) {
			return this.reRenderNeeded;
		}
	}
	
	public boolean isDataModelChanged()
	{
		synchronized(this) {
			return this.dataModelChanged;
		}
	}
	
	public boolean isOptionsChanged()
	{
		synchronized(this) {
			return this.optionsChanged;
		}
	}
	
	public boolean isForce()
	{
		synchronized(this) {
			return this.force;
		}
	}
	
	public void setReRenderNeeded(boolean dataModelChanged, boolean optionsChanged, boolean force)
	{
		synchronized(this) {
			reRenderNeeded = true;
			this.dataModelChanged = (this.dataModelChanged || dataModelChanged);
			this.optionsChanged = (this.optionsChanged || optionsChanged);
			this.force = (this.force || force);
		}
	}
	
	public void setReRenderCaptured()
	{
		synchronized(this) {
			reRenderNeeded = false;
			dataModelChanged = false;
			optionsChanged = false;
			force = false;
		}
	}
	
}

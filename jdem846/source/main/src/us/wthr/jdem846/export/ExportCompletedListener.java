package us.wthr.jdem846.export;

public interface ExportCompletedListener
{
	public void onSaveSuccessful();
	public void onSaveFailed(Exception ex);
}

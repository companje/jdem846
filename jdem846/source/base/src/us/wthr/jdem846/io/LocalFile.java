package us.wthr.jdem846.io;

import java.io.File;

import us.wthr.jdem846.util.TempFiles;

@SuppressWarnings("serial")
public class LocalFile extends File implements IResourceFile
{
	
	private boolean isTemporary = false;
	
	
	public LocalFile(File parent, String child, boolean isTemporary)
	{
		super(parent, child);
		this.isTemporary = isTemporary;
	}
	
	public LocalFile(File parent, String child)
	{
		this(parent, child, false);
	}
	
	public LocalFile(String path, boolean isTemporary)
	{
		super(path);
		this.isTemporary = isTemporary;
	}
	
	public LocalFile(String path)
	{
		this(path, false);
	}
	
	public LocalFile(File file, boolean isTemporary)
	{
		super(file.getAbsolutePath());
	}

	public LocalFile(File file)
	{
		this(file, false);
	}
	
	public boolean isTemporary()
	{
		return isTemporary;
	}

	protected void setTemporary(boolean isTemporary)
	{
		this.isTemporary = isTemporary;
	}
	
	
	public void releaseTemporaryFile()
	{
		if (isTemporary()) {
			TempFiles.releaseFile(this);
		}
	}
	
	
	public File getFile()
	{
		return this;
	}
	
}

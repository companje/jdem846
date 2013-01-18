package us.wthr.jdem846.io;

import java.io.File;

public interface IResourceFile
{
	
	public String getName();
	public boolean delete();
	public void deleteOnExit();
	
	public File getFile();
	
}

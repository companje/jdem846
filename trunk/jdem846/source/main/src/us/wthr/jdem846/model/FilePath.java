package us.wthr.jdem846.model;

import java.io.File;

public class FilePath
{
	private String path;
	
	public FilePath()
	{
		
	}
	
	public FilePath(String path)
	{
		this.path = path;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}
	
	public boolean isValid(boolean checkForWriteAccess)
	{
		File f = new File(path);
		
		if (!f.exists()) {
			File p = f.getParentFile();
			if (checkForWriteAccess) {
				return (p.exists() && p.canRead() && p.canWrite());
			} else {
				return (p.exists() && p.canRead());
			}
		} else {
			return f.canRead();
		}
	}
	
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof FilePath) {
			return this.path.equals(((FilePath)obj).path);
		}
		return false;
	}
	
	public String toString()
	{
		return "file:[" + path + "];";
	}
	
	public static FilePath fromString(String s)
	{
		s = s.replace("file:[", "").replace("]", "");
		return new FilePath(s);
	}
	
	public FilePath copy()
	{
		return new FilePath(path);
	}
}

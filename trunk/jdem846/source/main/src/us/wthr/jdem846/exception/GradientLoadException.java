package us.wthr.jdem846.exception;

@SuppressWarnings("serial")
public class GradientLoadException extends Exception
{
	
	private String filePath;
	
	public GradientLoadException(String message)
	{
		super(message);
	}
	
	public GradientLoadException(String message, Throwable thrown)
	{
		super(message, thrown);
	}
	
	public GradientLoadException(String filePath, String message)
	{
		super(message);
		this.filePath = filePath;
	}
	
	public GradientLoadException(String filePath, String message, Throwable thrown)
	{
		super(message, thrown);
		this.filePath = filePath;
	}
	
	public String getFilePath()
	{
		return filePath;
	}
	

}

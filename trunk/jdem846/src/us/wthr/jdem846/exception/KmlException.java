package us.wthr.jdem846.exception;

import us.wthr.jdem846.kml.KmlDocument;

@SuppressWarnings("serial")
public class KmlException extends Exception
{
	
	private KmlDocument kmlDocument;
	
	public KmlException()
	{
		
	}
	
	public KmlException(String message)
	{
		super(message);
	}
	
	public KmlException(String message, Throwable thrown)
	{
		super(message, thrown);
	}
	
	public KmlException(String message, KmlDocument kmlDocument)
	{
		super(message);
		this.kmlDocument = kmlDocument;
	}
	
	public KmlException(String message, Throwable thrown, KmlDocument kmlDocument)
	{
		super(message, thrown);
		this.kmlDocument = kmlDocument;
	}
	
	public KmlDocument getKmlDocument()
	{
		return kmlDocument;
	}
	
}

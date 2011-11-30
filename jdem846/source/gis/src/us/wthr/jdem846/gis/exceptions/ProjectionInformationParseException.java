package us.wthr.jdem846.gis.exceptions;

import us.wthr.jdem846.exception.WKTParseException;

@SuppressWarnings("serial")
public class ProjectionInformationParseException extends WKTParseException
{
	
	public ProjectionInformationParseException(String message)
	{
		super(message);
	}
	
	public ProjectionInformationParseException(String message, Throwable thrown)
	{
		super(message, thrown);
	}
	
}

package us.wthr.jdem846.nasa.pds;

import java.util.Map;

public class PdsLine
{
	private String field;
	private String value;
	
	public PdsLine(String raw)
	{
		String[] parts = raw.split("=");
		field = parts[0].trim().toLowerCase();
		if (parts.length == 2) {
			value = parts[1].trim();
		}
	}

	public String getField()
	{
		return field;
	}

	public String getValue()
	{
		return value;
	}
	
	
	public void checkValueIdentifier(Map<String, String> quotedStrings)
	{
		if (value != null) {
			if (quotedStrings.containsKey(value)) {
				value = quotedStrings.get(value);
			}
		}
	}
	
	public String toString()
	{
		if (value != null) {
			return field + " = " + value;
		} else {
			return field;
		}
	}
	
}

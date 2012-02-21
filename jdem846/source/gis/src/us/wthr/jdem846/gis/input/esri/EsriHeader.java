package us.wthr.jdem846.gis.input.esri;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.gis.exceptions.ParseException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class EsriHeader 
{
	
	private static Log log = Logging.getLog(EsriHeader.class);
	
	private File file;
	private Map<String, String> valueMap = new HashMap<String, String>();
	
	public EsriHeader()
	{
		
	}
	
	public EsriHeader(String filePath) throws ParseException
	{
		this(new File(filePath));
	}
	
	public EsriHeader(File file) throws ParseException
	{
		this.file = file;
		read();
	}
	
	protected void read() throws ParseException
	{
		if (!file.exists()) {
			throw new ParseException("Header file not found at " + file.getAbsolutePath());
		}
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				readHeaderLine(line);
			}
			reader.close();
		} catch (FileNotFoundException ex) {
			log.error("File Not Found error opening data file: " + ex.getMessage(), ex);
			throw new ParseException("File Not Found error opening data file: " + ex.getMessage(), ex);
		} catch (IOException ex) {
			log.error("IO error when opening data file: " + ex.getMessage(), ex);
			throw new ParseException("IO error when opening data file: " + ex.getMessage(), ex);
		}
	}
	
	
	private void readHeaderLine(String line)
	{
		
		line = line.replaceAll("[ ]+", " ");
		String[] parts = line.split(" ");
		if (parts.length < 2) {
			return;
		}
		String title = parts[0];
		String value = parts[1];
		title = title.trim();
		value = value.trim();
		
		
		valueMap.put(title, value);
		
	}
	
	
	public String getAttribute(String key)
	{
		return getAttribute(key, null);
	}
	
	public String getAttribute(String key, String ifNull)
	{
		String value = valueMap.get(key);
		if (value != null) {
			return value;
		} else {
			return ifNull;
		}
	}
	
	public double getDoubleAttribute(String key)
	{
		return getDoubleAttribute(key, DemConstants.ELEV_NO_DATA);
	}
	
	public double getDoubleAttribute(String key, double ifNull)
	{
		String value = getAttribute(key);
		if (value == null) {
			return ifNull;
		} else {
			return Double.parseDouble(value);
		}
	
	}
	
	public int getIntAttribute(String key)
	{
		return getIntAttribute(key, (int)DemConstants.ELEV_NO_DATA);
	}
	
	public int getIntAttribute(String key, int ifNull)
	{
		String value = getAttribute(key);
		if (value == null) {
			return ifNull;
		} else {
			return Integer.parseInt(value);
		}
	
	}
	
	
	
}

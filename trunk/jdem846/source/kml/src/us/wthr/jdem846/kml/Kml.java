package us.wthr.jdem846.kml;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import us.wthr.jdem846.kml.exception.KmlException;

/** Contains and constructs a KML document
 * 
 * @author Kevin M. Gill
 *
 */
public class Kml
{
	
	private KmlDocument kmlDocument = null;
	private String version = "2.2";
	
	public Kml()
	{
		
	}
	
	public Kml(KmlDocument kmlDocument)
	{
		this.kmlDocument = kmlDocument;
	}

	
	
	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public KmlDocument getDocument()
	{
		return kmlDocument;
	}

	public void setDocument(KmlDocument kmlDocument)
	{
		this.kmlDocument = kmlDocument;
	}
	
	
	protected Document _toXmlDocument()
	{
		Document xmlDocument = DocumentHelper.createDocument();
        Element kml = xmlDocument.addElement("kml");
        kml.addAttribute("xmlns", "http://www.opengis.net/kml/"+version);
        if (kmlDocument != null) {
        	kmlDocument.toKml(kml);
        }
        
        return xmlDocument;
	}
	
	public String toXmlDocument() throws KmlException
	{
		return toXmlDocument(false);
	}
	
	public String toXmlDocument(boolean pretty) throws KmlException
	{
		Document xmlDoc = _toXmlDocument();
		
		OutputFormat format = null;
		
		if (pretty) {
			format = OutputFormat.createPrettyPrint();
		} else {
			format = OutputFormat.createCompactFormat();
		}
		
		StringWriter stringWriter = new StringWriter();
		XMLWriter writer = new XMLWriter( stringWriter, format);
		
		try {
			writer.write(xmlDoc);
			return stringWriter.toString();
		} catch (IOException ex) {
			throw new KmlException("Failed to write KML to XML string: " + ex.getMessage(), ex, kmlDocument);
		}

	}
	
}

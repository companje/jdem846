package us.wthr.jdem846.kml;

public class Icon extends KmlElement
{
	private String href = null;
	private double viewBoundScale = -1;
	
	
	public Icon()
	{
		
	}
	
	public Icon(String href)
	{
		setHref(href);
	}
	
	public Icon(String href, double viewBoundScale)
	{
		setHref(href);
		setViewBoundScale(viewBoundScale);
	}

	public String getHref()
	{
		return href;
	}

	public void setHref(String href)
	{
		this.href = href;
	}

	public double getViewBoundScale()
	{
		return viewBoundScale;
	}

	public void setViewBoundScale(double viewBoundScale)
	{
		this.viewBoundScale = viewBoundScale;
	}
	
	
	@Override
	public String toKml(String id)
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("		<Icon>\r\n");
		buffer.append("			<href>" + href + "</href>\r\n");
		
		if (viewBoundScale != -1) {
			buffer.append("			<viewBoundScale>" + viewBoundScale + "</viewBoundScale>\r\n");
		}
		
		buffer.append("		</Icon>\r\n");
		return buffer.toString();
	}
}

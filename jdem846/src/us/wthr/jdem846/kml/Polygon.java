package us.wthr.jdem846.kml;

public class Polygon extends Geometry
{

	private boolean extrude = false;
	private String altitudeMode = "clampToGround";
	private LinearRing outerBoundary = new LinearRing();
	private LinearRing innerBoundary = new LinearRing();
	
	public Polygon()
	{
		
	}
	
	public boolean extrude()
	{
		return extrude;
	}

	public void setExtrude(boolean extrude)
	{
		this.extrude = extrude;
	}


	public String getAltitudeMode()
	{
		return altitudeMode;
	}

	public void setAltitudeMode(String altitudeMode)
	{
		this.altitudeMode = altitudeMode;
	}

	public LinearRing getOuterBoundary()
	{
		return outerBoundary;
	}

	public void setOuterBoundary(LinearRing outerBoundary)
	{
		this.outerBoundary = outerBoundary;
	}

	public LinearRing getInnerBoundary()
	{
		return innerBoundary;
	}

	public void setInnerBoundary(LinearRing innerBoundary)
	{
		this.innerBoundary = innerBoundary;
	}

	@Override
	public String toKml(String id)
	{
		StringBuffer buffer = new StringBuffer();
		
		if (id != null) {
			buffer.append("<Polygon id=\"" + id + "\">\r\n");
		} else {
			buffer.append("<Polygon>\r\n");
		}
		
		buffer.append("<extrude>" + (extrude ? 1 : 0) + "</extrude>\r\n");
		
		if (altitudeMode.equals("clampToGround") || altitudeMode.equals("clampToSearFloor")) {
			buffer.append("<tessellate>1</tessellate>\r\n");
		}
		
		buffer.append("<altitudeMode>" + altitudeMode + "</altitudeMode>\r\n");
		
		buffer.append("<outerBoundaryIs>\r\n");
		buffer.append(outerBoundary.toKml());
		buffer.append("</outerBoundaryIs>\r\n");
		
		if (innerBoundary.getCoordinates().size() > 0) {
			buffer.append("<innerBoundaryIs>\r\n");
			buffer.append(innerBoundary.toKml());
			buffer.append("</innerBoundaryIs>\r\n");
		}
		
		buffer.append("</Polygon>\r\n");
		return buffer.toString();
	}
	
	
	
}

package us.wthr.jdem846.render;

import us.wthr.jdem846.exception.CanvasException;

public class CanvasRectangleFill
{
	
	private boolean useSimpleCanvasFill;
	
	private int[] color;
	private double lat0;
	private double lon0;
	private double elev0; 
	
	private double lat1;
	private double lon1;
	private double elev1;
	
	private double lat2;
	private double lon2;
	private double elev2;
	
	private double lat3;
	private double lon3;
	private double elev3;
	
	private double width;
	private double height;
	
	public CanvasRectangleFill(int[] color, 
						double latitude, double longitude, 
						double width, double height, 
						double elevation)
	{
		useSimpleCanvasFill = true;
		
		this.color = new int[4];
		this.color[0] = color[0];
		this.color[1] = color[1];
		this.color[2] = color[2];
		this.color[3] = color[3];
		
		this.lat0 = latitude;
		this.lon0 = longitude;
		this.elev0 = elevation;
		
		this.width = width;
		this.height = height;
		/*
		 * modelCanvas.fillRectangle(color, 
						latitude, longitude, 
						latitudeResolution, longitudeResolution,
						point.getElevation());
		 */
	}
	
	public CanvasRectangleFill(int[] color, 
			double lat0, double lon0, double elev0, 
			double lat1, double lon1, double elev1, 
			double lat2, double lon2, double elev2, 
			double lat3, double lon3, double elev3)
	{
		useSimpleCanvasFill = false;
		
		this.color = new int[4];
		this.color[0] = color[0];
		this.color[1] = color[1];
		this.color[2] = color[2];
		this.color[3] = color[3];
		
		this.lat0 = lat0;
		this.lon0 = lon0;
		this.elev0 = elev0;
		
		this.lat1 = lat1;
		this.lon1 = lon1;
		this.elev1 = elev1;
		
		this.lat2 = lat2;
		this.lon2 = lon2;
		this.elev2 = elev2;
		
		this.lat3 = lat3;
		this.lon3 = lon3;
		this.elev3 = elev3;
		/*
		 * modelCanvas.fillRectangle(color,
					latitude, longitude, nw,
					latitude-latitudeResolution, longitude, sw,
					latitude-latitudeResolution, longitude+longitudeResolution, se,
					latitude, longitude+longitudeResolution, ne);
		 */
	}
	
	public void fill(ModelCanvas modelCanvas) throws CanvasException
	{
		if (useSimpleCanvasFill) {
			modelCanvas.fillRectangle(color, 
						lat0, lon0, 
						width, height,
						elev0);
		} else {

			modelCanvas.fillRectangle(color,
					lat0, lon0, elev0,
					lat1, lon1, elev1,
					lat2, lon2, elev2,
					lat3, lon3, elev3);
		}
	}
	
}

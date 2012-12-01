package us.wthr.jdem846.canvas;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelDimensions;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Vectors;
import us.wthr.jdem846.model.ViewPerspective;

public class CanvasProjection3d extends CanvasProjection
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(CanvasProjection3d.class);
	
	protected double[] cameraVector;
	protected double[] eyeVector;
	protected double[] pointVector;
	
	protected double rotateX = 30;
	protected double rotateY = 0;
	
	protected double shiftX = 0;
	protected double shiftY = 0;
	protected double shiftZ = 0;
	
	protected double scaleX = 1;
	protected double scaleY = 1;
	protected double scaleZ = 1;
	
	protected double min = 0;
	protected double max = 0;
	protected double resolution = 0;
	
	protected ModelDimensions modelDimensions;
	protected double elevationMultiple = 1.0;
	protected double minSideLength = 1.0;
	protected double modelRadius;
	
	public CanvasProjection3d(MapProjection mapProjection,
			double north,
			double south,
			double east,
			double west,
			double width,
			double height,
			Planet planet,
			double elevationMultiple,
			double minimumValue,
			double maximumValue,
			ModelDimensions modelDimensions,
			ViewPerspective projection)
	{
		super(mapProjection,
				north, 
				south,
				east,
				west,
				width,
				height);
		
		setUp3d(planet, 
				elevationMultiple, 
				minimumValue, 
				maximumValue, 
				modelDimensions, 
				projection);
			
	}
	
	
	public void setUp3d(Planet planet,
			double elevationMultiple,
			double minimumValue,
			double maximumValue,
			ModelDimensions modelDimensions,
			ViewPerspective projection)
	{
		eyeVector = new double[3];
		cameraVector = new double[3];
		pointVector = new double[3];
		
		
		this.modelDimensions = modelDimensions;
		cameraVector[0] = 0; cameraVector[1] = 0;
		eyeVector[0] = 0; eyeVector[1] = 0;

		this.elevationMultiple = elevationMultiple;
		//elevationMultiple = modelContext.getModelOptions().getElevationMultiple();
		rotateX = projection.getRotateX();
		rotateY = projection.getRotateY();
		
		shiftX = projection.getShiftX();
		shiftY = projection.getShiftY();
		shiftZ = projection.getShiftZ();
		
		scaleX = projection.getZoom();
		scaleY = projection.getZoom();
		scaleZ = projection.getZoom();
		
		min = minimumValue;
		max = maximumValue;

		this.minSideLength = MathExt.min(getWidth(), getHeight()) - 20;

		double meanRadius = DemConstants.EARTH_MEAN_RADIUS;
		
		if (planet != null) {
			meanRadius = planet.getMeanRadius();
		}
		
		

		double yWid = modelDimensions.dataRows * (modelDimensions.latitudeResolution / modelDimensions.textureLatitudeResolutionTrue);
		double xWid = modelDimensions.dataColumns * (modelDimensions.longitudeResolution / modelDimensions.textureLongitudeResolutionTrue);
		
		double fov = 38.0;
		//double fov = 38.0;
		//fov = 38.0;
		double a = (fov / 2.0);
		this.modelRadius = MathExt.sqrt(MathExt.sqr(xWid) + MathExt.sqr(yWid));
		double R = modelRadius / 2.0;

		
		double D = R / MathExt.tan(MathExt.radians(a));
		double d = (minSideLength / 2.0) / MathExt.tan(MathExt.radians(a));
		
		cameraVector[2] = D - d;
		eyeVector[2] = d;
		

		log.info("Zoom: " + projection.getZoom());
		log.info("Camera: " + cameraVector[2]);
		log.info("Eye: " + eyeVector[2]);
		
		
		resolution = modelDimensions.getMetersTrueTextureResolution(meanRadius);

		if (Double.isNaN(resolution) || resolution == 0.0) {
			resolution = 1.0;
		}
	}
	
	@Override
	public void getPoint(double latitude, double longitude, double elevation, MapPoint point) throws MapProjectionException
	{
		super.getPoint(latitude, longitude, elevation, point);
		

		
		double elev = (elevation - ((max + min) / 2.0)) / resolution;
		

		pointVector[0] = point.column - (getWidth() / 2.0);
		pointVector[1] = elev;
		pointVector[2] = point.row - (getHeight() / 2.0);
		

		double shiftPixelsX = shiftX * getWidth();
		double shiftPixelsY = shiftY * getHeight();
		double shiftPixelsZ = (shiftZ * modelRadius) - modelRadius;

		
		//Vectors.rotate(0, rotateY, 0, pointVector);
		//Vectors.rotate(rotateX, 0, 0, pointVector);
		Vectors.rotate(rotateX, rotateY, 0, pointVector, Vectors.YXZ);
		Vectors.translate(shiftPixelsX, shiftPixelsY, shiftPixelsZ, pointVector);
		Vectors.scale(scaleX, scaleY, scaleZ, pointVector);
		
		
		projectTo(pointVector);
		
		point.column = -pointVector[0] + (getWidth()/2.0);
		point.row = pointVector[1] + (getHeight()/2.0);
		point.z = pointVector[2];
		
		
	}
	
	public void projectTo(double[] vector) //Vector eye, Vector near)
	{
		CanvasProjection3d.projectTo(vector, eyeVector, cameraVector);
	}
	

	public static void projectTo(double[] vector, double meanRadius, double zoom)
	{
		double[] cameraVector = {0.0, 0.0, 0.0};
		double[] eyeVector = {0.0, 0.0, 0.0};
		
		cameraVector[2] = meanRadius / zoom;			// Camera position
		eyeVector[2] = ((meanRadius / 2.0) / zoom);	// Viewer's position relative to the display surface
		
		CanvasProjection3d.projectTo(vector, eyeVector, cameraVector);
	}
	
	public static void projectTo(double[] vector, double[] eyeVector, double[] cameraVector)
	{
		double[] a = vector;   // 3D position of points being projected
		double[] e = eyeVector;     // Viewer's position relative to the display surface
		double[] c = cameraVector;      // Camera position
		
		//eyeVector[2] = width;			// Camera position
		//nearVector[2] = (width/2.0f);	// Viewer's position relative to the display surface
		
		//double[] c = {1.0, 1.0, getWidth() * 20};
		
		
		//vector[0] = ((a[0] - c[0]) - e[0]) * (e[2] / (a[2] - c[2]));
		//vector[1] = ((a[1] - c[1]) - e[1]) * (e[2] / (a[2] - c[2]));
		
		
		//double thetaX = 0;//MathExt.radians(15); // Orientation of the camera
		//double thetaY = 0;//MathExt.radians(45);
		//double thetaZ = 0;
		

		double sinTX = 0.0;//MathExt.sin(thetaX);
		double sinTY = 0.0;//MathExt.sin(thetaY);
		double sinTZ = 0.0;//MathExt.sin(thetaZ);
		
		double cosTX = 1.0;//MathExt.cos(thetaX);
		double cosTY = 1.0;//MathExt.cos(thetaY);
		double cosTZ = 1.0;//MathExt.cos(thetaZ);
		
		double dX = cosTY * (sinTZ * (a[1] - c[1]) + cosTZ * (a[0] - c[0])) - sinTY * (a[2] - c[2]);
		double dY = sinTX * (cosTY * (a[2] - c[2]) + sinTY * (sinTZ * (a[1] - c[1]) + cosTZ * (a[0] - c[0]))) + cosTX * (cosTZ * (a[1] - c[1]) - sinTZ * (a[0] - c[0]));
		double dZ = cosTX * (cosTY * (a[2] - c[2]) + sinTY * (sinTZ * (a[1] - c[1]) + cosTZ * (a[0] - c[0]))) - sinTX * (cosTZ * (a[1] - c[1]) - sinTZ * (a[0] - c[0]));
		
		
		vector[0] = (dX - e[0]) * (e[2] / dZ);
		vector[1] = (dY - e[1]) * (e[2] / dZ);
		vector[2] = (dZ - e[2]) * (e[2] / dZ);

	}
	
	public static LatLonResolution calculateOutputResolutions(double outputWidth,
			double outputHeight,
			double dataColumns,
			double dataRows,
			double latitudeResolution,
			double longitudeResolution,
			double scaleFactor)
	{
		// Same as for the flat 2d canvas projection for now...
		double xdimRatio = (double)outputWidth / (double)dataColumns;
		double ydimRatio = (double)outputHeight / (double)dataRows;
		
		double outputLongitudeResolution = longitudeResolution / xdimRatio;
		double outputLatitudeResolution = latitudeResolution / ydimRatio;
		
		LatLonResolution latLonRes = new LatLonResolution(outputLatitudeResolution, outputLongitudeResolution);
		return latLonRes;
	}

}
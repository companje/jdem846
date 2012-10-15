package us.wthr.jdem846.canvas;

import java.util.Arrays;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelDimensions;
import us.wthr.jdem846.ModelOptionNamesEnum;
import us.wthr.jdem846.Projection;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.math.Vectors;
import us.wthr.jdem846.model.ModelGridDimensions;
import us.wthr.jdem846.model.ViewPerspective;

public class CanvasProjectionGlobe extends CanvasProjection3d
{
	
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(CanvasProjectionGlobe.class);
	
	
	private double meanRadius; // In meters
	private double D; // In meters
	private double fov;
	private double aspect;
	private double zNear;
	private double zFar;
	
	public CanvasProjectionGlobe(MapProjection mapProjection,
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
				height,
				planet, 
				elevationMultiple, 
				minimumValue, 
				maximumValue, 
				modelDimensions, 
				projection);
		
		
		//Planet planet = PlanetsRegistry.getPlanet(modelContext.getModelOptions().getOption(ModelOptionNamesEnum.PLANET));
		
		
		//if (planet != null) {
		//	meanRadius = planet.getMeanRadius() * 1000;
		//} else {
		//	meanRadius = DemConstants.EARTH_MEAN_RADIUS * 1000;
		//}
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

		
		cameraVector[0] = 0; cameraVector[1] = 0;
		eyeVector[0] = 0; eyeVector[1] = 0;

		this.elevationMultiple = elevationMultiple;
		
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
		
		double latRes = modelDimensions.getLatitudeResolution();
		double effLatRes = modelDimensions.getTextureLatitudeResolutionTrue();
		
		//Planet planet = PlanetsRegistry.getPlanet(modelContext.getModelOptions().getOption(ModelOptionNamesEnum.PLANET));
		meanRadius = DemConstants.EARTH_MEAN_RADIUS;// * 1000.0;
		if (planet != null) {
			meanRadius = planet.getMeanRadius();// * 1000.0;
		}
		

		resolution = modelDimensions.getMetersResolution(meanRadius);
		resolution = resolution / effLatRes;

		if (Double.isNaN(resolution) || resolution == 0.0) {
			resolution = 1.0;
		}

		aspect = (double)getWidth() / (double)getHeight();
		
		//fov = 18.0;
		fov = 38.0;
		//fov = 38.0;
		double a = (fov / 2.0);
		double R = ((meanRadius * 1000.0));
		
		D = R / MathExt.tan(MathExt.radians(a));
		double d = (minSideLength / 2.0) / MathExt.tan(MathExt.radians(a));

		cameraVector[2] = D - d;
		eyeVector[2] = d;

		log.info("Zoom: " + projection.getZoom());
		log.info("Camera: " + cameraVector[2]);
		log.info("Eye: " + eyeVector[2]);
		log.info("Fov: " + fov);
		log.info("Aspect: " + aspect);
		

		
	}
	
	@Override
	public void getPoint(double latitude, double longitude, double elevation, MapPoint point) throws MapProjectionException
	{

		double minSideLength = MathExt.min(getWidth(), getHeight()) - 20;
		//double radius = (minSideLength)  * scaleX;
		//double radiusAdjusted = (radius / (meanRadius * 1000.0)) * ((meanRadius * 1000.0) + elevation);
		
		double radius = meanRadius * 1000 + elevation;
		//double radiusAdjusted = radius;// * resolution;
		
		Spheres.getPoint3D(longitude, latitude, radius, pointVector);
			
		Vectors.rotate(rotateX, -rotateY, 0, pointVector, Vectors.YXZ);
		
		//Vectors.rotateY(rotateY, pointVector);
		//Vectors.rotateX(rotateX, pointVector);
		
		//Vectors.rotate(0, rotateY, 0, pointVector);
		//Vectors.rotate(rotateX, 0, 0, pointVector);

		Vectors.scale(scaleX, scaleY, scaleZ, pointVector);
		
		double shiftPixelsX = shiftX * radius;
		double shiftPixelsY = shiftY * radius;
		double shiftPixelsZ = shiftZ * radius;
		
		Vectors.translate(shiftPixelsX, shiftPixelsY, shiftPixelsZ, pointVector);

		double z = pointVector[2];
		
		projectTo(pointVector);
		
		point.column = pointVector[0] + (minSideLength/2.0);
		point.row = pointVector[1] + (minSideLength/2.0);
		point.z = z;
		
		
	}
	
	public static LatLonResolution calculateOutputResolutions(double outputWidth,
			double outputHeight,
			double dataColumns,
			double dataRows,
			double latitudeResolution,
			double longitudeResolution,
			double scaleFactor)
	{
		//double minSideLength = MathExt.min(outputWidth, outputHeight) - 20;
		//double radius = (minSideLength / 2.0)  * scaleFactor;
		double meanRadius = DemConstants.EARTH_MEAN_RADIUS * 1000;
		
		double minSideLength = MathExt.min(outputWidth, outputHeight) - 20;
		double radius = minSideLength;// * scaleFactor;
		//double radiusAdjusted = (radius / meanRadius) * (meanRadius + 0);
		
		
		double circumference = 2 * MathExt.PI * radius;
		
		double xdimRatio = (double)circumference / (double)dataColumns;
		double ydimRatio = (double)circumference / (double)dataRows;
		
		
		double outputLongitudeResolution = longitudeResolution / xdimRatio;
		double outputLatitudeResolution = latitudeResolution / ydimRatio;
		
		LatLonResolution latLonRes = new LatLonResolution(outputLatitudeResolution, outputLongitudeResolution);
		return latLonRes;
	}
	
	
	
	protected double[] matrix()
	{
		double[] matrix = new double[16];
		Arrays.fill(matrix, 0);
		return matrix;
	}
	
	protected double[] identityMatrix()
	{
		double[] matrix = matrix();
		matrix[0] = matrix[5] = matrix[10] = matrix[15] = 1.0;
		return matrix;
	}
	
	protected double[] multiplyMatrix(double[] m0, double[] m1)
	{
		double[] matrix = matrix();
		int col = 0;
		
		for (int y = 0; y < 4; y++) {
			col = y * 4;
			
			for (int x = 0; x < 4; x++) {
				for (int i = 0; i < 4; i++) {
					matrix[x+col] += m1[i+col] * m0[x+i*4];
				}
			}
		}
		
		return matrix;
	}
	
	protected double[] multiplyVectorAndMatrix(double[] v, double[] m)
	{
		double[] vec = new double[4];
		
		vec[0] = v[0] * m[0] + v[1] * m[4] + v[2] * m[8] + v[3] * m[12];
		vec[1] = v[0] * m[1] + v[1] * m[5] + v[2] * m[9] + v[3] * m[13];
		vec[2] = v[0] * m[2] + v[1] * m[6] + v[2] * m[10] + v[3] * m[14];
		vec[3] = v[0] * m[3] + v[1] * m[7] + v[2] * m[11] + v[3] * m[15];
		
		return vec;
	}
	
}

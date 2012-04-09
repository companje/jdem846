package us.wthr.jdem846.gis.datetime;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import us.wthr.jdem846.AbstractTestCase;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.Perspectives;
import us.wthr.jdem846.color.ColorAdjustments;
import us.wthr.jdem846.exception.CanvasException;
import us.wthr.jdem846.exception.ModelContextException;
import us.wthr.jdem846.gis.CardinalDirectionEnum;
import us.wthr.jdem846.gis.Coordinate;
import us.wthr.jdem846.gis.CoordinateTypeEnum;
import us.wthr.jdem846.gis.projections.MapProjectionEnum;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.canvas.ModelCanvas;

public class SolarUtilTest extends AbstractTestCase
{
	
	private double sunsource[] = {0.0, 0.0, 0.0};	
	private double normal[] = {0.0, 0.0, 0.0};
	private double p0[] = {0.0, 0.0, 0.0};
	//private double p1[] = {0.0, 0.0, 0.0};
	private int[] color = {0, 0, 0, 0};
	private int[] baseColor = {0, 0, 0, 0};
	private Perspectives perspectives;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
	}
	
	public void testLocationDayCalculations()
	{
		String saveTableTo = System.getProperty("us.wthr.jdem846.testOutputPath") + "/solar-util-test-location-date.csv";
		File tableFile = new File(saveTableTo);
		PrintWriter tableWriter = null;
		try {
			tableWriter = new PrintWriter(new FileWriter(tableFile));
		} catch (IOException ex) {
			log.error("Failed to open table writer: " + ex.getMessage(), ex);
			fail("Failed to open table writer: " + ex.getMessage());
		}
		
		int year = 2011;
		int month = 12;
		int day = 19;
		int timezone = 0;
		
		Coordinate latitude = new Coordinate(42, 45, 27, CardinalDirectionEnum.NORTH, CoordinateTypeEnum.LATITUDE);
		Coordinate longitude = new Coordinate(71, 27, 52, CardinalDirectionEnum.WEST, CoordinateTypeEnum.LONGITUDE);
		
		//Location location = new Location("Boire Field, Nashua NH", latitude, longitude, -5, false);
		SolarPosition position = new SolarPosition();
		
		tableWriter.printf("Time,Hour,Minute,Eq. Of Time,Sol Decl.,App. Sunrise,Solar Noon,App. Sunset,Azimuth,Elevation,Zenith Angle\n");
		
		for (int hour = 0; hour < 24; hour++) {
			for (int minute = 0; minute < 60; minute+=15) {
				EarthDateTime datetime = new EarthDateTime(year, month, day, hour, minute, 0, timezone, false);
				SolarUtil.getSolarPosition(datetime, latitude, longitude, position);
				
				double sunrise = position.getApparentSunrise().toMinutes();
				double sunset = position.getApprentSunset().toMinutes();
				double time = datetime.toMinutes();
				
				String light = "";
				if (time >= sunrise && time <= sunset) {
					light = "Light";
				} else {
					light = "Dark";
				}
				
				double solarZenith = position.getZenithAngle();
				double azimuth = position.getAzimuth();//Math.floor(position.getAzimuth()*100 +0.5)/100.0;
				double elevation = position.getElevation();//Math.floor((90.0-solarZenith)*100+0.5)/100.0;
				
				tableWriter.printf("%s, %d, %d, %.5f, %.5f, %s, %s, %s, %.5f, %.5f, %.5f, %s\n", datetime.toString(), hour, minute, position.getEquationOfTime(), position.getSolarDeclination(), position.getApparentSunrise().toString(), position.getSolarNoon().toString(), position.getApprentSunset().toString(), azimuth, elevation, solarZenith, light);
				
			}
		}
		
		
		tableWriter.close();
		
	}
	
	public void testGlobalCalculations()
	{
		
		String saveImagesTo = System.getProperty("us.wthr.jdem846.testOutputPath") + "/solar-util-test.png";
		String saveTableTo = System.getProperty("us.wthr.jdem846.testOutputPath") + "/solar-util-test-date.csv";
		int width = 800;
		int height = 400;
		
		File tableFile = new File(saveTableTo);
		PrintWriter tableWriter = null;
		try {
			tableWriter = new PrintWriter(new FileWriter(tableFile));
		} catch (IOException ex) {
			log.error("Failed to open table writer: " + ex.getMessage(), ex);
			fail("Failed to open table writer: " + ex.getMessage());
		}
		
		tableWriter.printf("Latitude, Longitude, Lon Time, Solar Elevation, Solar Azimuth, Solar Zenith, Time, Time Minutes, Hour, Minute, Second, Timezone, Time Local, Julian Day\n");
		
		ModelOptions modelOptions = new ModelOptions();
		modelOptions.setWidth((int)width);
		modelOptions.setHeight((int)height);
		modelOptions.setMapProjection(MapProjectionEnum.EQUIRECTANGULAR);
		modelOptions.setBackgroundColor("255;255;255;0");
		modelOptions.setAntialiased(false);
		//modelOptions.setUseSimpleCanvasFill(false);
		RasterDataContext rasterDataContext = new RasterDataContext();
		ModelContext modelContext = null;
		
		try {
			modelContext = ModelContext.createInstance(rasterDataContext, modelOptions);
		} catch (ModelContextException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
		
		ModelCanvas modelCanvas = modelContext.getModelCanvas(true);
		
		this.perspectives = new Perspectives();
		
		SolarPosition position = new SolarPosition();
		EarthDateTime datetime = new EarthDateTime(2011, 5, 2, 0, 0, 0, 0, false);

		//double latStep = (double)height / 180.0;
		//double lonStep = (double)width / 360.0;
		
		double latStep = 0.5;
		double lonStep = 0.5;		
		
		int hour = 0;
		int minute = 0;
		int second = 0;
		int timezone = 0;
		
		Coordinate latitudeCoordinate = new Coordinate(0.0, CoordinateTypeEnum.LATITUDE);
		Coordinate longitudeCoordinate = new Coordinate(0.0, CoordinateTypeEnum.LONGITUDE);
		
		log.info("Latitude Step: " + latStep);
		log.info("Longitude Step: " + lonStep);
		
		baseColor[0] = baseColor[1] = baseColor[2] = 127;
		baseColor[3] = 0xFF;
		
		normal[0] = 0.0;
		normal[1] = 0.0;
		normal[2] = 0.0;


		p0[0] = 0.0;
		p0[1] = 1.0;
		p0[2] = 0.0;
		
		log.info("Position X/Y/Z: " + p0[0] + "/" + p0[1] + "/" + p0[2]);
		
		//double elevation = 0.0;
		
		// Phi
		for (double latitude = 90.0; latitude >= -90.0 - latStep; latitude -= latStep) {
			
			// Theta
			for (double longitude = -180.0; longitude <= 180.0 + lonStep; longitude += lonStep) {
				
				double lonTime = (longitude / 180.0) * 12.0;
				double minutes = (lonTime + 12) * 60;
				datetime.fromMinutes(minutes);
				hour = datetime.getHour();
				minute = datetime.getMinute();
				second = datetime.getSecond();

				timezone = -1 * (12 - hour);

				datetime.setTimezone(lonTime);
				
				latitudeCoordinate.fromDecimal(latitude);
				longitudeCoordinate.fromDecimal(longitude);
					
				SolarUtil.getSolarPosition(datetime, latitudeCoordinate, longitudeCoordinate, position);

				setUpLightSource(position.getElevation(), position.getAzimuth());

				double dot = perspectives.dotProduct(p0, sunsource);
				
				color[0] = baseColor[0];
				color[1] = baseColor[1];
				color[2] = baseColor[2];
				color[3] = baseColor[3];
				
				ColorAdjustments.adjustBrightness(color, dot);
				
				try {
					modelCanvas.fillRectangle(color, 
							latitude, longitude, 0.0,
							latitude-latStep, longitude, 0.0,
							latitude-latStep, longitude+lonStep, 0.0,
							latitude, longitude+lonStep, 0.0);
				} catch (CanvasException ex) {
					log.error("Error applying color to canvas: " + ex.getMessage(), ex);
					fail("Error applying color to canvas: " + ex.getMessage());
				}
				
				
				tableWriter.printf("%.5f, %.5f, %f, %.5f, %.5f, %.5f, %s, %f, %d, %d, %d, %d, %f, %f\n", latitudeCoordinate.toDecimal(), longitudeCoordinate.toDecimal(), lonTime, position.getElevation(), position.getAzimuth(), position.getZenithAngle(), datetime.toString(), datetime.toMinutes(), hour, minute, second, timezone, datetime.timeLocal(), datetime.julianDay());
				//System.out.println("Lat/Lon: " + latitudeCoordinate.toDecimal() + "/" + longitudeCoordinate.toDecimal() + ", Elev/Azimuth: " + position.getElevation() + "/" + position.getAzimuth() + ", " + datetime);
				
			}
			
		}
		
		
		
		try {
			tableWriter.close();
		} catch (Exception ex) {
			log.error("Failed to close table writer: " + ex.getMessage(), ex);
			fail("Failed to close table writer: " + ex.getMessage());
		}
		
		try {
			modelCanvas.save(saveImagesTo);
		} catch (CanvasException ex) {
			log.error("Error saving canvas to disk: " + ex.getMessage(), ex);
			fail("Error saving canvas to disk: " + ex.getMessage());
		}
		
	}
	
	
	protected void setUpLightSource(double solarElevation, double solarAzimuth)
	{
		getSpherePoints(solarAzimuth, solarElevation, 100, sunsource);
		

	}
	
	protected void getSpherePoints(double theta, double phi, double radius, double[] points)
	{
		double _y = sqrt(pow(radius, 2) - pow(radius * cos(phi), 2));
		double r0 = sqrt(pow(radius, 2) - pow(_y, 2));

		double _b = r0 * cos(theta );
        double _z = sqrt(pow(r0, 2) - pow(_b, 2));
        double _x = sqrt(pow(r0, 2) - pow(_z, 2));
        if (theta <= 90.0) {
                _z *= -1.0;
        } else if (theta  <= 180.0) {
                _x *= -1.0;
                _z *= -1.0;
        } else if (theta  <= 270.0) {
                _x *= -1.0;
        }

        if (phi >= 0) { 
                _y = abs(_y);
        } else {
                _y = abs(_y) * -1;
        }


        points[0] = _x;
        points[1] = _y;
        points[2] = _z;
        
        //Vector.rotate(0.0, 90, 0.0, points);

	}
	
	
	protected double asin(double a)
	{
		return Math.asin(a);
	}
	
	protected double atan2(double a, double b)
	{
		return Math.atan2(a, b);
	}
	
	protected double sqr(double a)
	{
		return (a*a);
	}
	
	protected double abs(double a)
	{
		return Math.abs(a);
	}
	
	protected double pow(double a, double b)
	{
		return Math.pow(a, b);
	}
	
	protected double sqrt(double d)
	{
		return Math.sqrt(d);
	}
	
	protected double cos(double d)
	{
		return Math.cos(Math.toRadians(d));
	}
	
	protected double sin(double d)
	{
		return Math.sin(Math.toRadians(d));
	}
}

package us.wthr.jdem846;

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import us.wthr.jdem846.color.ColorAdjustments;
import us.wthr.jdem846.exception.CanvasException;
import us.wthr.jdem846.gis.Coordinate;
import us.wthr.jdem846.gis.CoordinateTypeEnum;
import us.wthr.jdem846.gis.datetime.EarthDateTime;
import us.wthr.jdem846.gis.datetime.SolarPosition;
import us.wthr.jdem846.gis.datetime.SolarUtil;
import us.wthr.jdem846.gis.projections.MapProjectionEnum;
import us.wthr.jdem846.image.ImageIcons;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.render.ModelCanvas;
import us.wthr.jdem846.ui.ComponentButtonBar;
import us.wthr.jdem846.ui.MainButtonBar;
import us.wthr.jdem846.ui.ToolbarButton;

public class SandboxTestMain extends AbstractTestMain
{
	private static Log log = null;
	
	private double sunsource[] = {0.0, 0.0, 0.0};	
	private double normal[] = {0.0, 0.0, 0.0};
	private double p0[] = {0.0, 0.0, 0.0};
	private double p1[] = {0.0, 0.0, 0.0};
	private int[] color = {0, 0, 0, 0};
	private int[] baseColor = {0, 0, 0, 0};
	private Perspectives perspectives;
	
	public static void main(String[] args)
	{
		try {
			AbstractTestMain.initialize(false);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		log = Logging.getLog(SandboxTestMain.class);
		
		SandboxTestMain sandbox = new SandboxTestMain();
		
		try {
			sandbox.doTesting();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public SandboxTestMain() 
	{

	}
	
	
	public void doTesting() throws Exception
	{
		String saveFramesTo = "C:\\srv\\elevation\\sunlight\\frame-{iter}.png";
		int width = 800;
		int height = 400;
		
		ModelOptions modelOptions = new ModelOptions();
		modelOptions.setWidth((int)width);
		modelOptions.setHeight((int)height);
		modelOptions.setMapProjection(MapProjectionEnum.EQUIRECTANGULAR);
		modelOptions.setBackgroundColor("255;255;255;0");
		modelOptions.setAntialiased(false);
		modelOptions.setUseSimpleCanvasFill(false);
		RasterDataContext rasterDataContext = new RasterDataContext();
		ModelContext modelContext = ModelContext.createInstance(rasterDataContext, modelOptions);
		
		
		ModelCanvas modelCanvas = modelContext.getModelCanvas(true);
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2011);
		
		for (int doy = 1; doy <= 365; doy++) {
			cal.set(Calendar.DAY_OF_YEAR, doy);
			log.info("Date: " + cal.getTime());
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			int day = cal.get(Calendar.DAY_OF_MONTH);
			renderFrame(modelCanvas, year, month, day);
			modelCanvas.save(saveFramesTo.replace("{iter}", ""+doy));
		}
		
		
		//renderFrame(modelCanvas, 2011, 5, 2);
		//modelCanvas.save(saveFramesTo.replace("{iter}", ""+0));
	}
	
	public void renderFrame(ModelCanvas modelCanvas, int year, int month, int day)
	{
		Perspectives perspectives = new Perspectives();
		
		
		SolarPosition position = new SolarPosition();
		EarthDateTime datetime = new EarthDateTime(year, month, day, 0, 0, 0, 0, false);

		//double latStep = (double)height / 180.0;
		//double lonStep = (double)width / 360.0;
		
		double latStep = 2;
		double lonStep = 2;		
		
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
		
		double elevation = 0.0;
		
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
					//log.error("Error applying color to canvas: " + ex.getMessage(), ex);
					//fail("Error applying color to canvas: " + ex.getMessage());
				}
				
				
				//tableWriter.printf("%.5f, %.5f, %f, %.5f, %.5f, %.5f, %s, %f, %d, %d, %d, %d, %f, %f\n", latitudeCoordinate.toDecimal(), longitudeCoordinate.toDecimal(), lonTime, position.getElevation(), position.getAzimuth(), position.getZenithAngle(), datetime.toString(), datetime.toMinutes(), hour, minute, second, timezone, datetime.timeLocal(), datetime.julianDay());
				//System.out.println("Lat/Lon: " + latitudeCoordinate.toDecimal() + "/" + longitudeCoordinate.toDecimal() + ", Elev/Azimuth: " + position.getElevation() + "/" + position.getAzimuth() + ", " + datetime);
				
			}
			
		}
		
		
		
	}
	

	protected void setUpLightSource(double solarElevation, double solarAzimuth)
	{
		Spheres.getPoint3D(solarAzimuth, solarElevation, 100, sunsource);
		

	}
}

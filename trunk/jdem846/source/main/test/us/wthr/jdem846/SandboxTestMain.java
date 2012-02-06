package us.wthr.jdem846;

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
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
import us.wthr.jdem846.util.ByteConversions;

public class SandboxTestMain extends AbstractTestMain
{
	private static Log log = null;
	

	
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
		
		File inputFile = new File("F:\\GEBCO_08\\gebco_08.asc");
		File outputFile = new File("F:\\GEBCO_08\\gebco_08.flt");
		
		
		try {
			sandbox.doConversion(inputFile, outputFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public SandboxTestMain() 
	{

	}
	
	
	public void doConversion(File inputFile, File outputFile) throws Exception
	{
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile));
		
		BufferedReader inputReader = new BufferedReader(new FileReader(inputFile));
		for (int i = 0; i < 6; i++)
			inputReader.readLine();
		
		int lines = 0;
		
		String line = null;
		while ((line = inputReader.readLine()) != null) {
			String parts[] = line.split(" ");
			for (int i = 0; i < parts.length; i++) {
				float value = Float.parseFloat(parts[i]);
				byte[] buffer = ByteConversions.floatToBytes(value, ByteOrder.LSBFIRST);
				out.write(buffer);
			}
			
			lines++;
			if (lines % 100 == 0)
				log.info("Wrote line #" + lines);
		}
		out.close();
	}
	

}

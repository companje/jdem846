package us.wthr.jdem846.gis.input.proj4;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class Proj4GridParser
{
	
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(Proj4GridParser.class);
	
	public Object parseFile(String url) throws Exception
	{
		InputStream in = JDemResourceLoader.getAsInputStream(url);
		return parseFile(in);		
	}
	
	public Object parseFile(InputStream in) throws Exception
	{
		
		LineNumberReader reader = new LineNumberReader(new InputStreamReader(in));
		
		String line = null;
		
		while ((line = reader.readLine()) != null) {
			if (isSpecLine(line)) {
				System.out.println(line);
				Proj4GridSpecification spec = parseSpecification(line);
			}
		}
		
		return null;
		
	}
	
	protected Proj4GridSpecification parseSpecification(String line)
	{
		Proj4GridSpecification spec = new Proj4GridSpecification();
		
		
		String[] parts = line.split(" ");
		spec.id = Integer.parseInt(parts[0].replace("<", "").replace(">", ""));
		
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			
			
			if (part.startsWith("+a=")) {
				spec.a = Double.parseDouble(part.substring(3));
			} else if (part.startsWith("+alpha=")) {
				spec.alpha = Double.parseDouble(part.substring(7));
			} else if (part.startsWith("+axis=")) { //+axis=wsu
				spec.axis = part.substring(6);
				//spec.axis = AxisOrientationEnum.getByValue(part.substring(6));
			} else if (part.startsWith("+b=")) {
				spec.b = Double.parseDouble(part.substring(3));
			} else if (part.startsWith("+datum=")) {
				String datumCode = part.substring(7);
				spec.datum = datumCode;
				//spec.datum = DatumsListRegistry.getDatum(datumCode);
			} else if (part.startsWith("+ellps=")) {
				spec.ellps = part.substring(7);
				//spec.ellipse = DatumsListRegistry.getEllipsoid(spec.ellipseCode);
			} else if (part.startsWith("+k=")) {
				spec.k = Double.parseDouble(part.substring(3));
			} else if (part.startsWith("+k_0=")) {
				spec.k_0 = Double.parseDouble(part.substring(5));
			} else if (part.startsWith("+lat_0=")) {
				spec.lat_0 = Double.parseDouble(part.substring(7));
			} else if (part.startsWith("+lat_1=")) {
				spec.lat_1 = Double.parseDouble(part.substring(7));
			} else if (part.startsWith("+lat_2=")) {
				spec.lat_2 = Double.parseDouble(part.substring(7));
			} else if (part.startsWith("+lat_ts=")) {
				spec.lat_ts = Double.parseDouble(part.substring(8));
			} else if (part.startsWith("+lon_0=")) {
				spec.lon_0 = Double.parseDouble(part.substring(7));
			} else if (part.startsWith("+lonc=")) {
				spec.lonc = Double.parseDouble(part.substring(6));
			} else if (part.startsWith("+lon_wrap=")) {
				spec.lon_wrap = Double.parseDouble(part.substring(10));
			} else if (part.startsWith("+nadgrids=")) {
				spec.nadgrids = part.substring(10);
			} else if (part.startsWith("+no_defs")) {
				spec.no_defs = true;
			} else if (part.startsWith("+over")) {
				spec.over = true;
			} else if (part.startsWith("+pm=")) {
				spec.pm = part.substring(4);
			} else if (part.startsWith("+proj=")) {
				spec.proj = part.substring(6);
			} else if (part.equals("+south")) {
				spec.south = true;
			} else if (part.startsWith("+to_meter=")) {
				spec.to_meter = Double.parseDouble(part.substring(10));
			} else if (part.startsWith("+towgs84=")) {
				String[] s_toWGS84 = part.substring(9).split(",");
				spec.toWGS84 = new double[s_toWGS84.length];
				for (int j = 0; j < s_toWGS84.length; j++) {
					spec.toWGS84[j] = Double.parseDouble(s_toWGS84[j]);
				}
			} else if (part.startsWith("+units=")) {
				spec.units = part.substring(7);
			} else if (part.startsWith("+vto_meter=")) {
				spec.vto_meter = Double.parseDouble(part.substring(11));
			} else if (part.startsWith("+vunits=")) {
				spec.vunits = part.substring(8);
			} else if (part.startsWith("+x_0=")) {
				spec.x_0 = Double.parseDouble(part.substring(5));
			} else if (part.startsWith("+y_0=")) {
				spec.y_0 = Double.parseDouble(part.substring(5));
			} else if (part.startsWith("+zone=")) {
				spec.zone = Integer.parseInt(part.substring(6));
			} 

		}
		
		System.out.printf(" %d proj: %s, zone: %d, datum: %s, towgs84: %s, ellps: %s, k: %f\n", spec.id, spec.proj, spec.zone, spec.datum, spec.toWGS84, spec.ellps, spec.k);
		
		return spec;
	}
	
	protected boolean isSpecLine(String line)
	{
		if (line == null)
			return false;
		String[] parts = line.split(" ");
		if (parts[0].charAt(0) == '<')
			return true;
		else
			return false;
	}
	
	
}

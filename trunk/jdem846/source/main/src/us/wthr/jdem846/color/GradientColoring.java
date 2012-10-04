/*
 * Copyright (C) 2011 Kevin M. Gill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.wthr.jdem846.color;

import java.io.File;

import us.wthr.jdem846.exception.GradientLoadException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.scaling.ElevationScaler;

public class GradientColoring implements ModelColoring
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(GradientColoring.class);
	
	
	private static final int UNITS_PERCENT = 0;
	private static final int UNITS_METERS = 10;
	
	private String name;
	private String identifier;
	private boolean needsMinMaxElevation;
	private int units;

	private static DemColor defaultColor = new DemColor(0, 0, 0, 0xFF);
	private GradientLoader gradient;
	private String configFile = null;
	private GradientColorStop[] colorStops = null;
	
	private ElevationScaler elevationScaler;
	
	public GradientColoring(String configFile) throws GradientLoadException
	{
		this.configFile = configFile;
		reset();
	}
	

	public ModelColoring copy() throws Exception
	{
		return new GradientColoring(configFile);
	}
	
	
	@Override
	public void reset() throws GradientLoadException
	{
		//URL url = this.getClass().getResource(configFile);
		try {
			gradient = GradientLoader.loadGradient(new File(configFile));
		} catch (Exception ex) {
			throw new GradientLoadException(configFile, "Invalid gradient file location: " + ex.getMessage(), ex);
		}
		this.name = gradient.getName();
		this.identifier = gradient.getIdentifier();
		this.needsMinMaxElevation = gradient.needsMinMaxElevation();
		if (gradient.getUnits().equalsIgnoreCase("percent")) {
			this.units = UNITS_PERCENT;
		} else if (gradient.getUnits().equalsIgnoreCase("meters")) {
			this.units = UNITS_METERS;
		} else {
			throw new GradientLoadException(configFile, "Unsupported unit of measurement: " + gradient.getUnits());
		}
		
		colorStops = new GradientColorStop[gradient.getColorStops().size()];
		gradient.getColorStops().toArray(colorStops);
	}
	
	@Override
	public GradientLoader getGradientLoader()
	{
		return gradient;
	}
	
	
	
	public String getName()
	{
		return name;
	}


	public String getIdentifier()
	{
		return identifier;
	}


	public boolean needsMinMaxElevation()
	{
		return needsMinMaxElevation;
	}


	public String getUnits()
	{
		return gradient.getUnits();
	}


	@Override
	public void getColorByMeters(double meters, int[] color) 
	{
		GradientColorStop lower = null;
		GradientColorStop upper = null;
		
		
		
		for (GradientColorStop stop : colorStops) {
			if (getAdjustedElevation(stop.getPosition()) <= meters) {
				lower = stop;
			}
			if (getAdjustedElevation(stop.getPosition()) >= meters) {
				upper = stop;
				break;
			}
		}
		
		
		
		if (upper == null && lower == null) {
			defaultColor.toList(color);
			return;
		} else if (upper == null) { // lower != null is implied by the first condition
			upper = lower;
		} else if (lower == null) { // upper != null is implied by the first condition
			lower = upper;
		}
			
		
		double color_ratio = (meters - getAdjustedElevation(lower.getPosition())) / (getAdjustedElevation(upper.getPosition()) - getAdjustedElevation(lower.getPosition()));
		if (Double.isNaN(color_ratio))
			color_ratio = 1.0;


		color[0] = (int)Math.round(((lower.getColor().getRed() * (1.0 - color_ratio)) + (upper.getColor().getRed() * color_ratio)) * 255.0);
		color[1] = (int)Math.round(((lower.getColor().getGreen() * (1.0 - color_ratio)) + (upper.getColor().getGreen() * color_ratio)) * 255.0);
		color[2] = (int)Math.round(((lower.getColor().getBlue() * (1.0 - color_ratio)) + (upper.getColor().getBlue() * color_ratio)) * 255.0);
		color[3] = 0xFF;
		
		
	}
	
	@Override
	public void getColorByPercent(double ratio, int[] color) 
	{
		
		if (ratio < 0 || ratio > 1) {
			defaultColor.toList(color);
			return;
			//return defaultColor.getCopy();
		}
		
		GradientColorStop lower = null;
		GradientColorStop upper = null;
		
		for (GradientColorStop stop : colorStops) {
			if (stop.getPosition() <= ratio) {
				lower = stop;
			}
			if (stop.getPosition() >= ratio) {
				upper = stop;
				break;
			}
		}
		
		if (upper == null && lower == null) {
			defaultColor.toList(color);
			return;
		} else if (upper == null) { // lower != null is implied by the first condition
			upper = lower;
		} else if (lower == null) { // upper != null is implied by the first condition
			lower = upper;
		}
		
		
		if (ratio == 0.0f || (upper.getPosition() - lower.getPosition()) == 0.0f) {
			lower.getColor().toList(color);
			return;
			//return lower.getColor().getCopy();
		}
		
		double color_ratio = (ratio - lower.getPosition()) / (upper.getPosition() - lower.getPosition());
		if (Double.isNaN(color_ratio))
			color_ratio = 1.0;

		
		color[0] = (int)Math.round(((lower.getColor().getRed() * (1.0 - color_ratio)) + (upper.getColor().getRed() * color_ratio)) * 255.0);
		color[1] = (int)Math.round(((lower.getColor().getGreen() * (1.0 - color_ratio)) + (upper.getColor().getGreen() * color_ratio)) * 255.0);
		color[2] = (int)Math.round(((lower.getColor().getBlue() * (1.0 - color_ratio)) + (upper.getColor().getBlue() * color_ratio)) * 255.0);
		color[3] = 0xFF;
		//return new DemColor(red, green, blue, 0xFF);
	}

	protected double getAdjustedElevation(double elevation)
	{
		if (elevationScaler != null) {
			return elevationScaler.scale(elevation);
		} else {
			return elevation;
		}
	}
	
	@Override
	public void getGradientColor(double elevation, double minElevation, double maxElevation, int[] color) 
	{
		if (units == UNITS_PERCENT) {
			double ratio = (elevation - minElevation) / (maxElevation - minElevation);
			
			if (ratio <= 0)
				ratio = .001;
			
			getColorByPercent(ratio, color);
		} else if (units == UNITS_METERS) {
			getColorByMeters(elevation, color);
		}
		
		
		//return getColor(ratio);
	}
	
	
	public double getMinimumSupported()
	{
		if (units == UNITS_PERCENT) {
			return 0.0;
		} else if (units == UNITS_METERS) {
			return colorStops[0].getPosition();
		} else {
			return 0.0;
		}
	}
	
	public double getMaximumSupported()
	{
		if (units == UNITS_PERCENT) {
			return 1.0;
		} else if (units == UNITS_METERS) {
			return colorStops[colorStops.length - 1].getPosition();
		} else {
			return 0;
		}
	}




	@Override
	public void setElevationScaler(ElevationScaler elevationScaler) 
	{
		this.elevationScaler = elevationScaler;
	}


	
}

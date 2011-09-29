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

package us.wthr.jdem846.kml;

public class Coordinate
{
	private double latitude;
	private double longitude;
	private double altitude;
	
	public Coordinate(double latitude, double longitude)
	{
		setLatitude(latitude);
		setLongitude(longitude);
		setAltitude(-9999);
	}
	
	public Coordinate(double latitude, double longitude, double altitude)
	{
		setLatitude(latitude);
		setLongitude(longitude);
		setAltitude(altitude);
	}

	public double getLatitude()
	{
		return latitude;
	}

	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

	public double getLongitude()
	{
		return longitude;
	}

	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}

	public double getAltitude()
	{
		return altitude;
	}

	public void setAltitude(double altitude)
	{
		this.altitude = altitude;
	}
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(""+getLongitude()+","+getLatitude());
		if (altitude != -9999) {
			buffer.append(","+altitude);
		}
		
		return buffer.toString();
	}

}

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

package us.wthr.jdem846.shapefile;

import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.shapefile.modeling.FeatureType;

public abstract class Shape
{
	private static Log log = Logging.getLog(Shape.class);
	
	private int recordNumber;
	private int shapeType;
	private FeatureType featureType;
	
	private List<ShapeAttributes> shapeAttributes = new LinkedList<ShapeAttributes>();
	private Map<String, Object> properties = new HashMap<String, Object>();
	
	public Shape(int recordNumber, int shapeType)
	{
		this.recordNumber = recordNumber;
		this.shapeType = shapeType;
	}
	
	public void setFeatureType(FeatureType featureType)
	{
		this.featureType = featureType;
	}
	
	public FeatureType getFeatureType()
	{
		return this.featureType;
	}
	
	public void setProperty(String name, Object value)
	{
		properties.put(name, value);
	}
	
	public Object getProperty(String name)
	{
		return properties.get(name);
	}
	
	public String[] getPropertyNames()
	{
		return (String[]) properties.keySet().toArray();
	}
	
	public ShapePath getShapePath()
	{
		return null;
	}
	
	public boolean isPartiallyWithin(Rectangle2D field)
	{
		ShapePath path = getShapePath();
		if (path == null)
			return false;
		
		
		//ShapeBounds rec = (ShapeBounds) path.getBounds2D();
		// TODO: Restore isPartiallyWithin
		return false;//field.intersects(rec);
		
	}
	
	public boolean contains(double x, double y)
	{
		/*
		ShapePath path = getShapePath();
		if (path == null)
			return false;
		else
			return path.contains(x, y);
		*/
		return false;// TODO: Restore contains
	}

	public int getRecordNumber()
	{
		return recordNumber;
	}

	public void setRecordNumber(int recordNumber)
	{
		this.recordNumber = recordNumber;
	}


	public int getShapeType()
	{
		return shapeType;
	}

	protected void setShapeType(int shapeType)
	{
		this.shapeType = shapeType;
	}

	public List<ShapeAttributes> getShapeAttributes()
	{
		return shapeAttributes;
	}

	public void setShapeAttributes(List<ShapeAttributes> shapeAttributes)
	{
		this.shapeAttributes = shapeAttributes;
	}
	
	
	
	
}

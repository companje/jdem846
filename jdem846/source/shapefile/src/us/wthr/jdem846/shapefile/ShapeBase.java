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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import us.wthr.jdem846.IDataObject;
import us.wthr.jdem846.dbase.DBaseFieldDescriptor;
import us.wthr.jdem846.dbase.DBaseFile;
import us.wthr.jdem846.dbase.DBaseRecord;
import us.wthr.jdem846.dbase.exception.DBaseException;
import us.wthr.jdem846.input.InputSourceData;
import us.wthr.jdem846.shapefile.exception.ShapeFileException;
import us.wthr.jdem846.shapefile.modeling.FeatureType;
import us.wthr.jdem846.shapefile.modeling.FeatureTypeStrokeLoader;
import us.wthr.jdem846.shapefile.modeling.FeatureTypesDefinition;
import us.wthr.jdem846.shapefile.modeling.FeatureTypesDefinitionLoader;
import us.wthr.jdem846.shapefile.modeling.ShapeDataDefinition;
import us.wthr.jdem846.shapefile.modeling.ShapeDataDefinitionLoader;

public class ShapeBase implements InputSourceData, IDataObject
{
	
	private ShapeFileReference shapeFileReference;
	private ShapeFile shapeFile = null;
	private ShapeDataDefinition shapeDataDefinition;
	private DBaseFile dBase;
	
	private FeatureTypeStrokeLoader featureTypeStrokeLoader;
	private FeatureTypesDefinitionLoader featureTypesDefinitionLoader;
	private FeatureTypesDefinition featureTypesDefinition;
	
	
	public ShapeBase(ShapeFileReference shapeFileReference) throws Exception
	{

		this.shapeFileReference = shapeFileReference;
		shapeFile = new ShapeFile(shapeFileReference.getPath());
		
		dBase = new DBaseFile(shapeFileReference.getPath().replace(".shp", ".dbf"));
		
		featureTypeStrokeLoader = new FeatureTypeStrokeLoader();
		featureTypesDefinitionLoader = new FeatureTypesDefinitionLoader(featureTypeStrokeLoader);
		
		ShapeDataDefinitionLoader shapeDataDefinitionLoader = new ShapeDataDefinitionLoader();
		shapeDataDefinition = shapeDataDefinitionLoader.getShapeDataDefinition(shapeFileReference.getShapeDataDefinitionId());
		if (shapeDataDefinition != null)
			featureTypesDefinition = featureTypesDefinitionLoader.getFeatureTypesDefinition(shapeDataDefinition.getFeatureTypeDefinitionId());
	}
	
	public ShapeBase(String shapeFilePath, String dataDefinitionId) throws Exception
	{
		this(new ShapeFileReference(shapeFilePath, dataDefinitionId));
	}
	
	public DBaseFile getDBaseFile()
	{
		return dBase;
	}
	
	public void close() throws ShapeFileException
	{
		try {
			if (dBase != null) {
				dBase.close();
				dBase = null;
			}
		} catch (Exception ex) {
			throw new ShapeFileException("Error closing dBase file", ex);
		}
		
		if (shapeFile != null) {
			shapeFile.close();
			shapeFile = null;
		}
	}
	
	public Shape getShape(int index) throws ShapeFileException
	{
		Shape shape = shapeFile.getShape(index);
		
		if (shapeDataDefinition != null && shapeDataDefinition.getFeatureTypeColumn() != null) {
			DBaseRecord dbaseRecord = null;
			try {
				dbaseRecord = dBase.getRecord(index);
			} catch (DBaseException ex) {
				throw new ShapeFileException("Error retrieving dBase record", ex);
			}
			String typeCode = dbaseRecord.getString(shapeDataDefinition.getFeatureTypeColumn());
			FeatureType featureType = featureTypesDefinition.getFeatureType(typeCode);
			if (featureType == null) {
				featureType = featureTypesDefinition.getDefaultFeatureType();
			}
			shape.setFeatureType(featureType);
		}
		
		// TODO: Do stuff
		return shape;
	}
	
	public int getShapeType() throws ShapeFileException
	{
		return shapeFile.getShapeType();
	}
	
	public Box getBounds()
	{
		return shapeFile.getBounds();
	}
	
	public long getShapeCount()
	{
		return shapeFile.getShapeCount();
	}
	
	public Set<Shape> getShapes()
	{
		Set<Shape> shapes = shapeFile.getShapes();
		// TODO: Do stuff
		return shapes;
	}
	
	public ShapeDataDefinition getShapeDataDefinition()
	{
		return shapeDataDefinition;
	}
	
	protected Map<String, Object> getDBaseInfoMap(int index) throws DBaseException
	{
		DBaseFieldDescriptor[] fieldDescriptors = dBase.getFieldDescriptors();
		DBaseRecord record = dBase.getRecord(index);
		
		Map<String, Object> infoMap = new HashMap<String, Object>();
		
		for (DBaseFieldDescriptor fieldDescriptor : fieldDescriptors) {
			Object value = record.getValue(fieldDescriptor.getName());
			infoMap.put(fieldDescriptor.getName(), value);
		}
		
		
		return infoMap;
	}

	public ShapeFileReference getShapeFileReference()
	{
		return shapeFileReference;
	}

	@Override
	public int hashCode()
	{
		return shapeFileReference.getPath().hashCode();
	}

	
	
}

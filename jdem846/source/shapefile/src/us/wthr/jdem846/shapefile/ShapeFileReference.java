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

import us.wthr.jdem846.DataSetTypes;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.input.InputSourceData;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.shapefile.exception.ShapeFileException;

public class ShapeFileReference implements InputSourceData
{
	private static Log log = Logging.getLog(ShapeFileReference.class);
	
	private String path;
	private String shapeDataDefinitionId;
	private int shapeType;
	private int datasetType;
	
	private boolean isDisposed = false;
	
	protected ShapeFileReference()
	{
		
	}
	
	public ShapeFileReference(String path, String shapeDataDefinitionId) throws ShapeFileException
	{
		this(path, shapeDataDefinitionId, true);
	}
	
	public ShapeFileReference(String path, String shapeDataDefinitionId, boolean loadTypes) throws ShapeFileException
	{
		this.path = path;
		this.shapeDataDefinitionId = shapeDataDefinitionId;
		
		if (loadTypes) {
			try {
				ShapeBase shapeBase = open();
				shapeType = shapeBase.getShapeType();
				if (shapeType == ShapeConstants.TYPE_POLYGON ||
						shapeType == ShapeConstants.TYPE_POLYGONM ||
						shapeType == ShapeConstants.TYPE_POLYGONZ) {
					
					datasetType = DataSetTypes.SHAPE_POLYGON;
					
				} else if (shapeType == ShapeConstants.TYPE_POLYLINE ||
							shapeType == ShapeConstants.TYPE_POLYLINEM ||
							shapeType == ShapeConstants.TYPE_POLYLINEZ) {
					
					datasetType = DataSetTypes.SHAPE_POLYLINE;
	
				}	
				
				shapeBase.close();
			} catch (Exception ex) {
				//ex.printStackTrace();
				log.warn("Failed to open shapefile to determine type: " + ex.getMessage(), ex);
				throw new ShapeFileException("Failed to open shapefile to determine type", ex);
			}
		}
	}
	
	
	public String getPath()
	{
		return path;
	}
	
	public void setShapeDataDefinitionId(String shapeDataDefinitionId)
	{
		this.shapeDataDefinitionId = shapeDataDefinitionId;
	}
	
	public String getShapeDataDefinitionId()
	{
		return shapeDataDefinitionId;
	}
	
	
	
	public int getShapeType()
	{
		return shapeType;
	}

	public int getDatasetType()
	{
		return datasetType;
	}

	public boolean equals(Object object)
	{
		if (!(object instanceof ShapeFileReference))
			return false;
		
		ShapeFileReference other = (ShapeFileReference) object;
		return this.path.equals(other.path);
	}
	
	public ShapeBase open() throws Exception
	{
		ShapeBase shapeBase = new ShapeBase(this);
		return shapeBase;
	}
	
	public ShapeFileReference copy()
	{
		ShapeFileReference shapeFileRequest = new ShapeFileReference();
		
		shapeFileRequest.path = this.path;
		shapeFileRequest.shapeDataDefinitionId = this.shapeDataDefinitionId;
		shapeFileRequest.shapeType = this.shapeType;
		shapeFileRequest.datasetType = this.datasetType;
		
		return shapeFileRequest;
	}
	
	public void dispose() throws DataSourceException
	{
		if (isDisposed) {
			throw new DataSourceException("Object already disposed of");
		}
		
		isDisposed = true;
	}
}

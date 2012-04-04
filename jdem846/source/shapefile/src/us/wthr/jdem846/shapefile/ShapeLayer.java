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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import us.wthr.jdem846.geom.Edge;
import us.wthr.jdem846.shapefile.modeling.FeatureType;

public class ShapeLayer
{
	private int type;
	private List<ShapePath> shapePaths = new LinkedList<ShapePath>();
	
	public ShapeLayer(int type)
	{
		this.type = type;
	}
	
	public void addShape(Shape shape)
	{
		shapePaths.add(shape.getShapePath());
	}
	
	public void addShapePath(ShapePath shapePath)
	{
		shapePaths.add(shapePath);
	}
	
	public List<ShapePath> getShapePaths()
	{
		return shapePaths;
	}
	
	public int size()
	{
		return shapePaths.size();
	}
	
	public int getType()
	{
		return type;
	}
	
	public void translate(PointTranslateHandler translateHandler, boolean closePath)
	{
		 List<ShapePath> newShapePaths = new LinkedList<ShapePath>();
		 
		 for (ShapePath shapePath : shapePaths) {
			 ShapePath newShapePath = shapePath.translate(translateHandler, closePath);
			 newShapePaths.add(newShapePath);
		 }
		 
		 shapePaths = newShapePaths;
	}
	
	
	public ShapePath getCombinedPath()
	{
		return getCombinedPath(null);
	}
	
	public ShapeLayer getCombinedPathsByTypes()
	{
		ShapeLayer newLayer = new ShapeLayer(this.type);
		List<FeatureType> featureTypes = getFeatureTypes();
		
		for (FeatureType featureType : featureTypes) {
			ShapePath shapePath = getCombinedPath(featureType);
			if (shapePath != null)
				newLayer.addShapePath(shapePath);
		}
		
		return newLayer;
	}
	
	public ShapePath getCombinedPath(FeatureType featureType)
	{
		ShapePath combinedPath = new ShapePath();
		
		boolean atLeastOnePoint = false;
		for (ShapePath shapePath : shapePaths) {
			if (featureType == null || (shapePath.getFeatureType() != null && shapePath.getFeatureType().equals(featureType))) {
				
				Edge[] edgeList = shapePath.getEdges();
				for (Edge edge : edgeList) {
					combinedPath.addEdge(edge);
				}
				
				//combinedPath.append(shapePath, false);
				atLeastOnePoint = true;
			}
		}
		
		if (!atLeastOnePoint)
			return null;
		
		combinedPath.setFeatureType(featureType);
		
		return combinedPath;
	}
	
	protected List<FeatureType> getFeatureTypes()
	{
		List<FeatureType> featureTypes = new LinkedList<FeatureType>();
		
		Map<FeatureType, FeatureType> featureTypeMap = new HashMap<FeatureType, FeatureType>();
		
		for (ShapePath shapePath : shapePaths) {
			featureTypeMap.put(shapePath.getFeatureType(), shapePath.getFeatureType());
		}
		
		featureTypes.addAll(featureTypeMap.keySet());
		return featureTypes;
	}
	
}

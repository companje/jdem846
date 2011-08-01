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

package us.wthr.jdem846.shapefile.modeling;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;


import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;


public class ShapeDataDefinitionLoader
{
	private static Log log = Logging.getLog(ShapeDataDefinitionLoader.class);
	
	private static String DEFAULT_XML_FILE = JDem846Properties.getProperty("us.wthr.jdem846.shapefile") + "/shape_data_definitions.xml";
	
	private List<ShapeDataDefinition> shapeDataDefinitions;
	
	public ShapeDataDefinitionLoader()
	{
		try {
			shapeDataDefinitions = loadShapeDataDefinition();
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("Failed to load shape data definitions: " + ex.getMessage(), ex);
		}
	}
	
	public ShapeDataDefinition getShapeDataDefinition(String id)
	{
		for (ShapeDataDefinition shapeDataDefinition : shapeDataDefinitions) {
			if (shapeDataDefinition.getId().equals(id))
				return shapeDataDefinition;
		}
		return null;
	}
	
	
	public List<ShapeDataDefinition> getShapeDataDefinitions()
	{
		return shapeDataDefinitions;
	}

	public static List<ShapeDataDefinition> loadShapeDataDefinition() throws Exception
	{
		List<ShapeDataDefinition> shapeDataDefinitions = new LinkedList<ShapeDataDefinition>();
		
		log.info("Loading shape data definitions from " + DEFAULT_XML_FILE);
		Document doc = loadDocument(DEFAULT_XML_FILE);
		
		ShapeDataDefinition shapeDataDefinition = null;
		Node node = null;
		
		List shapeDataDefinitionList = doc.selectNodes( "//jdem846/shape-types/shape-type" );
		for (Iterator iter = shapeDataDefinitionList.iterator(); iter.hasNext(); ) {
			Node shapeDataDefinitionNode = (Node) iter.next();
			Node nameAttribute = shapeDataDefinitionNode.selectSingleNode("@name");
			Node idAttribute = shapeDataDefinitionNode.selectSingleNode("@id");
			
			shapeDataDefinition = new ShapeDataDefinition(nameAttribute.getText(), idAttribute.getText());
			
			Node featureTypeColumnNode = shapeDataDefinitionNode.selectSingleNode("featureTypeColumn");
			if (featureTypeColumnNode != null) {
				
				Node featureTypeDefinitionIdNode = featureTypeColumnNode.selectSingleNode("@definition");
				String featureTypeColumn = featureTypeColumnNode.getText();
				
				shapeDataDefinition.setFeatureTypeDefinitionId(featureTypeDefinitionIdNode.getText());
				shapeDataDefinition.setFeatureTypeColumn(featureTypeColumn);
			}
			
			List columnList = shapeDataDefinitionNode.selectNodes("columns/column");
			for (Iterator columnIter = columnList.iterator(); columnIter.hasNext(); ) {
				Node columnNode = (Node) columnIter.next();
				Node columnNameNode = columnNode.selectSingleNode("@name");
				
				shapeDataDefinition.addColumn(columnNameNode.getText(), columnNode.getText());
			}

			shapeDataDefinitions.add(shapeDataDefinition);
			
		}
		
		return shapeDataDefinitions;
	}
	
	
	protected static Document loadDocument(String path) throws DocumentException 
	{
		SAXReader reader = new SAXReader();
        Document document = reader.read(FeatureTypeStrokeLoader.class.getResourceAsStream(path));
        return document;
	}
	
}

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

public class FeatureTypesDefinitionLoader
{
	private static Log log = Logging.getLog(FeatureTypesDefinitionLoader.class);
	
	private static String DEFAULT_XML_FILE = JDem846Properties.getProperty("us.wthr.jdem846.shapefile") + "/feature-type-definitions.xml";
	
	private List<FeatureTypesDefinition> featureTypesDefinitions;
	
	
	public FeatureTypesDefinitionLoader(FeatureTypeStrokeLoader featureTypeStrokeLoader)
	{
		try {
			featureTypesDefinitions = loadFeatureTypesDefinition(featureTypeStrokeLoader);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("Failed to load feature type definitions: " + ex.getMessage(), ex);
		}
	}
	
	public FeatureTypesDefinition getFeatureTypesDefinition(String id)
	{
		for (FeatureTypesDefinition featureTypesDefinition : featureTypesDefinitions) {
			if (featureTypesDefinition.getId().equals(id))
				return featureTypesDefinition;
		}
		return null;
	}
	
	
	
	public List<FeatureTypesDefinition> getFeatureTypesDefinitions()
	{
		return featureTypesDefinitions;
	}



	public static List<FeatureTypesDefinition> loadFeatureTypesDefinition(FeatureTypeStrokeLoader featureTypeStrokeLoader) throws Exception
	{
		List<FeatureTypesDefinition> featureTypesDefinitions = new LinkedList<FeatureTypesDefinition>();
		
		log.info("Loading feature type definitions from " + DEFAULT_XML_FILE);
		Document doc = loadDocument(DEFAULT_XML_FILE);
		
		FeatureTypesDefinition featureTypeDefinition = null;
		Node node = null;
		
		List featureTypeDefinitionList = doc.selectNodes( "//jdem846/feature-types/feature-type-definition" );
		for (Iterator iter = featureTypeDefinitionList.iterator(); iter.hasNext(); ) {
			Node featureTypeDefinitionNode = (Node) iter.next();
			Node nameAttribute = featureTypeDefinitionNode.selectSingleNode("@name");
			Node idAttribute = featureTypeDefinitionNode.selectSingleNode("@id");
			
			featureTypeDefinition = new FeatureTypesDefinition(nameAttribute.getText(), idAttribute.getText());
			
			Node defaultStrokeNode = (Node) featureTypeDefinitionNode.selectSingleNode("default-stroke");
			if (defaultStrokeNode != null) {
				featureTypeDefinition.setDefaultStroke(featureTypeStrokeLoader.getFeatureTypeStroke(defaultStrokeNode.getText()));
			}
			
			
			List featureTypeGroupList = featureTypeDefinitionNode.selectNodes("type-groups/type-group");
			for (Iterator featureTypeGroupIter = featureTypeGroupList.iterator(); featureTypeGroupIter.hasNext(); ) {
				Node featureTypeGroupNode = (Node) featureTypeGroupIter.next();
				
				Node featureTypeGroupNameAttribute = featureTypeGroupNode.selectSingleNode("@name");
				Node featureTypeGroupIdAttribute = featureTypeGroupNode.selectSingleNode("@id");
				Node featureTypeGroupDefaultStrokeAttribute = featureTypeGroupNode.selectSingleNode("@default-stroke");
				
				FeatureTypeStroke featureTypeStroke = featureTypeStrokeLoader.getFeatureTypeStroke(featureTypeGroupDefaultStrokeAttribute.getText());
				
				FeatureTypeGroup featureTypeGroup = new FeatureTypeGroup(featureTypeGroupNameAttribute.getText(), featureTypeGroupIdAttribute.getText(), featureTypeStroke);

				
				featureTypeDefinition.addFeatureTypeGroup(featureTypeGroup);
			}
			
			
			List featureTypeList = featureTypeDefinitionNode.selectNodes("type");
			for (Iterator featureTypeIter = featureTypeList.iterator(); featureTypeIter.hasNext(); ) {
				Node featureTypeNode = (Node) featureTypeIter.next();
				
				Node featureTypeCodeAttribute = featureTypeNode.selectSingleNode("@code");
				Node featureTypeGroupIdAttribute = featureTypeNode.selectSingleNode("@group");
				Node featureTypeStrokeAttribute = featureTypeNode.selectSingleNode("@stroke");
				String definition = featureTypeNode.getText();
				
				FeatureTypeGroup featureTypeGroup = featureTypeDefinition.getFeatureTypeGroup(featureTypeGroupIdAttribute.getText());
				
				
				FeatureTypeStroke featureTypeStroke = null;
				if (featureTypeStrokeAttribute != null) {
					featureTypeStroke = featureTypeStrokeLoader.getFeatureTypeStroke(featureTypeStrokeAttribute.getText());
				} else {
					featureTypeStroke = featureTypeGroup.getDefaultStroke();
				}
				
				
				// TODO: Get FeatureTypeStroke
				
				FeatureType featureType = new FeatureType(featureTypeCodeAttribute.getText(), featureTypeGroup, definition, featureTypeStroke);
				featureTypeDefinition.addFeatureType(featureType);
				
			}
			
			
			
			featureTypesDefinitions.add(featureTypeDefinition);
		}
		
		return featureTypesDefinitions;
	}
	
	
	
	protected static Document loadDocument(String path) throws DocumentException 
	{
		SAXReader reader = new SAXReader();
        Document document = reader.read(FeatureTypeStrokeLoader.class.getResourceAsStream(path));
        return document;
	}
	
}

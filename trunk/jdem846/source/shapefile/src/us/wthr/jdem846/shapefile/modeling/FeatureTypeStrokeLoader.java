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
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class FeatureTypeStrokeLoader
{
	private static Log log = Logging.getLog(FeatureTypeStrokeLoader.class);
	
	private static String DEFAULT_XML_FILE = JDem846Properties.getProperty("us.wthr.jdem846.shapefile") + "/line-strokes.xml";
	
	private List<FeatureTypeStroke> featureTypeStrokes;
	
	public FeatureTypeStrokeLoader()
	{
		try {
			featureTypeStrokes = loadFeatureTypeStrokes();
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("Failed to load feature type strokes: " + ex.getMessage(), ex);
		}
	}
	
	public FeatureTypeStroke getFeatureTypeStroke(String name)
	{
		for (FeatureTypeStroke featureTypeStroke : featureTypeStrokes) {
			if (featureTypeStroke.getName().equals(name))
				return featureTypeStroke;
		}
		return null;
	}
	
	
	
	public List<FeatureTypeStroke> getFeatureTypeStrokes()
	{
		return featureTypeStrokes;
	}


	public static List<FeatureTypeStroke> loadFeatureTypeStrokes() throws Exception
	{
		List<FeatureTypeStroke> featureTypeStrokes = new LinkedList<FeatureTypeStroke>();
		
		log.info("Loading feature type line strokes from " + DEFAULT_XML_FILE);
		Document doc = loadDocument(DEFAULT_XML_FILE);
		
		FeatureTypeStroke featureTypeStroke = null;
		
		
		List<?> lineStrokeList = doc.selectNodes( "//jdem846/line-strokes/line-stroke" );
		for (Iterator<?> iter = lineStrokeList.iterator(); iter.hasNext(); ) {
			Node lineStrokeNode = (Node) iter.next();
			Node nameAttribute = lineStrokeNode.selectSingleNode("@name");
			
			featureTypeStroke = new FeatureTypeStroke(nameAttribute.getText());
			
			List<?> strokeList = lineStrokeNode.selectNodes("stroke");
			
			for (Iterator<?> strokeIter = strokeList.iterator(); strokeIter.hasNext(); ) {
				Node strokeNode = (Node) strokeIter.next();
				
				float width = 1.0f;
				int cap = BasicStroke.CAP_SQUARE;
				int join = BasicStroke.JOIN_ROUND;
				float miterLimit = 1.0f;
				float[] dash = null;
				float dashPhase = 0.0f;
				int red = 0;
				int green = 0;
				int blue = 0;
				int alpha = 255;
				
				Node widthNode = strokeNode.selectSingleNode("@width");
				if (widthNode != null)
					width = Float.parseFloat(widthNode.getText());
				
				Node capNode = strokeNode.selectSingleNode("@cap");
				if (capNode != null)
					cap = getStrokeConstant(capNode.getText());
				
				Node joinNode = strokeNode.selectSingleNode("@join");
				if (joinNode != null)
					join = getStrokeConstant(joinNode.getText());
				
				Node miterLimitNode = strokeNode.selectSingleNode("@miterLimit");
				if (miterLimitNode != null)
					miterLimit = Float.parseFloat(miterLimitNode.getText());
				
				Node dashNode = strokeNode.selectSingleNode("@dash");
				if (dashNode != null) {
					String[] split = dashNode.getText().split(",");
					dash = new float[split.length];
					
					for (int i = 0; i < split.length; i++) {
						dash[i] = Float.parseFloat(split[i]);
					}
				}
				
				Node dashPhaseNode = strokeNode.selectSingleNode("@dashPhase");
				if (dashPhaseNode != null)
					dashPhase = Float.parseFloat(dashPhaseNode.getText());
				
				Node redNode = strokeNode.selectSingleNode("@red");
				if (redNode != null)
					red = Integer.parseInt(redNode.getText());
				
				Node greenNode = strokeNode.selectSingleNode("@green");
				if (greenNode != null)
					green = Integer.parseInt(greenNode.getText());
				
				Node blueNode = strokeNode.selectSingleNode("@blue");
				if (blueNode != null)
					blue = Integer.parseInt(blueNode.getText());
				
				Node alphaNode = strokeNode.selectSingleNode("@alpha");
				if (alphaNode != null)
					alpha = Integer.parseInt(alphaNode.getText());
				
				
				
				Color lineColor = new Color(red, green, blue, alpha);
				LineStroke lineStroke = new LineStroke(width, cap, join, miterLimit, dash, dashPhase, lineColor);
				featureTypeStroke.addLineStroke(lineStroke);
				
			}
			
			featureTypeStrokes.add(featureTypeStroke);
			
		}
		
		
		return featureTypeStrokes;
		
	}
	
	
	protected static Document loadDocument(String path) throws DocumentException, FileNotFoundException 
	{
		SAXReader reader = new SAXReader();
		InputStream in = JDemResourceLoader.getAsInputStream(path);
        Document document = reader.read(in);
        return document;
	}
	
	
	
	protected static int getStrokeConstant(String key)
	{
		if (key == null)
			return -1;
		else if (key.equalsIgnoreCase("CAP_BUTT"))
			return BasicStroke.CAP_BUTT;
		else if (key.equalsIgnoreCase("CAP_ROUND"))
			return BasicStroke.CAP_ROUND;
		else if (key.equalsIgnoreCase("CAP_SQUARE"))
			return BasicStroke.CAP_SQUARE;
		else if (key.equalsIgnoreCase("JOIN_BEVEL"))
			return BasicStroke.JOIN_BEVEL;
		else if (key.equalsIgnoreCase("JOIN_MITER"))
			return BasicStroke.JOIN_MITER;
		else if (key.equalsIgnoreCase("JOIN_ROUND"))
			return BasicStroke.JOIN_ROUND;
		else
			return -1;
	}
	
}

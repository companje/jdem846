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

package us.wthr.jdem846.project;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.shapefile.ShapeFileReference;



@Deprecated
public class XmlProjectFileWriter
{
	
	private static Log log = Logging.getLog(XmlProjectFileWriter.class);
	
	protected XmlProjectFileWriter()
	{
		
	}
	

	public static Document createDocument(ProjectModel projectModel)
	{
		Document document = DocumentHelper.createDocument();
        Element jdem846 = document.addElement("jdem846");
        jdem846.addAttribute("spec-version", DemConstants.APPLICATION_SPEC_VERSION);
        jdem846.addAttribute("generated", ""+(new Date(System.currentTimeMillis())));

        Element project = jdem846.addElement("project");
        
        
        if (projectModel.getProjectType() != null) {
        	Element projectType = project.addElement("type");
        	projectType.addText(projectModel.getProjectType().identifier());
        }
        
        Element options = project.addElement("options");

        
        for (String key : projectModel.getOptionKeys()) {
        	String value = projectModel.getOption(key);
        	if (value != null) {
        		
        		Element element = options.addElement("option");
	        	element.addAttribute("key", key);
        		element.addCDATA(value);

        	}
        }
        
        
        Element scripting = project.addElement("scripting");
        if (projectModel.getScriptLanguage() != null) {
        	scripting.addElement("language").addText(projectModel.getScriptLanguage().text());
        }
        
        if (projectModel.getUserScript() != null) {
        	Element script = scripting.addElement("script");
        	script.addCDATA(projectModel.getUserScript());
        }
        
        Element inputFiles = project.addElement("input-files");
        for (String inputFile : projectModel.getInputFiles()) {
        	inputFiles.addElement("input").addText(inputFile);
        }
        
        for (ShapeFileReference shapeFileRequest : projectModel.getShapeFiles()) {
        	
        	Element shapeFileElement = inputFiles.addElement("shapefile");
        	shapeFileElement.addElement("path").addText(shapeFileRequest.getPath());
        	if (shapeFileRequest.getShapeDataDefinitionId() != null) {
        		shapeFileElement.addElement("data-definition-id").addText(shapeFileRequest.getShapeDataDefinitionId());
        	}

        }

        return document;

	}
	
	/** A simple output implementation
	 * 
	 * @param projectModel
	 * @param path
	 */
	public static void writeProject(ProjectModel projectModel, String path) throws IOException
	{
		log.info("Writing project file to " + path);
		
		Document doc = XmlProjectFileWriter.createDocument(projectModel);
		
		// lets write to a file
		OutputFormat format = OutputFormat.createPrettyPrint();

        XMLWriter writer = new XMLWriter( new FileWriter(path), format);
        writer.write( doc );
        writer.close();

		
	}
	
	
}

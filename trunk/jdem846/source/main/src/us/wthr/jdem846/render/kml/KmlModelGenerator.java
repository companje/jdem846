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

package us.wthr.jdem846.render.kml;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.image.ImageTypeEnum;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.kml.Folder;
import us.wthr.jdem846.kml.Kml;
import us.wthr.jdem846.kml.KmlDocument;
import us.wthr.jdem846.kml.ListItemTypeEnum;
import us.wthr.jdem846.kml.ListStyle;
import us.wthr.jdem846.kml.Lod;
import us.wthr.jdem846.kml.NetworkLink;
import us.wthr.jdem846.kml.Region;
import us.wthr.jdem846.kml.Style;
import us.wthr.jdem846.kml.exception.KmlException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;



public class KmlModelGenerator
{
	private static Log log = Logging.getLog(KmlModelGenerator.class);
	
	
	//private DataPackage dataPackage;
	//private ModelOptions modelOptions;
	private ModelContext modelContext;
	private GriddedModel griddedModel;
	private String outputPath;
	private int overlayTileSize;
	private int layerMultiplier;
	private String name;
	private String description;
	private ImageTypeEnum imageType;
	
	protected KmlModelGenerator(ModelContext modelContext,
								GriddedModel griddedModel,
								String outputPath,
								int overlayTileSize,
								int layerMultiplier,
								String name,
								String description,
								ImageTypeEnum imageType)
	{
		this.modelContext = modelContext;
		this.griddedModel = griddedModel;
		this.outputPath = outputPath;
		this.overlayTileSize = overlayTileSize;
		this.layerMultiplier = layerMultiplier;
		this.name = name;
		this.description = description;
		this.imageType = imageType;
	}
	
	
	public static KmlDocument generate(ModelContext modelContext,
								GriddedModel griddedModel,
								String outputPath,
								int overlayTileSize,
								int layerMultiplier,
								String name,
								String description,
								ImageTypeEnum imageType, 
								boolean write) throws RenderEngineException
	{
		
		KmlModelGenerator generator = new KmlModelGenerator(modelContext, griddedModel, outputPath, overlayTileSize, layerMultiplier, name, description, imageType);
		
		KmlDocument kml = generator.generate(write);
		
		
		
		return kml;
	}
	
	
	protected KmlDocument generate(boolean write) throws RenderEngineException
	{
		KmlDocument kml = new KmlDocument();
		if (name != null) {
			kml.setName(name);
		}
		if (description != null) {
			kml.setDescription(description);
		}
		
		Style style = new Style();
		ListStyle listStyle = new ListStyle(ListItemTypeEnum.CHECK_HIDE_CHILDREN);
		listStyle.setId("ckHdChldrn");
		style.addSubStyle(listStyle);
		//kml.addStyle(style);
		
		
		log.info("North: " + griddedModel.getNorth());
		log.info("South: " + griddedModel.getSouth());
		log.info("East: " + griddedModel.getEast());
		log.info("West: " + griddedModel.getWest());

		double multiple = 1.0;
		while(true) {
			
			int layerNumber =  ((int)multiple / layerMultiplier + 1);
			
			// latRes = tileSize * ydim
			double latRes = griddedModel.getLatitudeResolution() * multiple;
			
			// lonRes = tileSize * xdim
			double lonRes = griddedModel.getLongitudeResolution() * multiple;

			Folder layerFolder = generateLayer(layerNumber, multiple);
			kml.addFeature(layerFolder);


			multiple = multiple * layerMultiplier;
			
			double longitudeWidth = modelContext.getRasterDataContext().getEast() - modelContext.getRasterDataContext().getEast();
			double latitudeHeight = modelContext.getRasterDataContext().getNorth() - modelContext.getRasterDataContext().getSouth();
			
			if (lonRes > (longitudeWidth * 2)) {
				break;
			}
			
			if (latRes > (latitudeHeight * 2)) {
				break;
			}
			
			//if (dataPackage.getLongitudeWidth() / lonRes < 1.0 || dataPackage.getLatitudeHeight() / latRes < 1.0) {
			//	break;
			//}
		}
		
		if (write) {
			try {
				writeKml(kml);
			} catch (IOException ex) {
				throw new RenderEngineException("Error writing KML to disk: " + ex.getMessage(), ex);
			} catch (KmlException ex) {
				throw new RenderEngineException("Error generating KML: " + ex.getMessage(), ex);
			}
		}
		
		return kml;

	}
	
	protected Folder generateLayer(int layerNumber, double multiple) throws RenderEngineException
	{
		double modelNorth = griddedModel.getNorth();
		double modelSouth = griddedModel.getSouth();
		
		Folder layerFolder = new Folder();
		layerFolder.setName("Layer #" + layerNumber);

		// latRes = tileSize * ydim
		double latRes = griddedModel.getLatitudeResolution() * multiple;
		
		// lonRes = tileSize * xdim
		double lonRes = griddedModel.getLongitudeResolution() * multiple;
		
		int regionNum = 1;
		for (double north = modelNorth; north >= modelSouth; north -= latRes) {
			double south = north - latRes;
			
			List<NetworkLink> networkLinks = generateSubRegion(layerNumber, regionNum, multiple, north, south, lonRes);
			layerFolder.getFeaturesList().addAll(networkLinks);

			regionNum++;
			if (north < griddedModel.getSouth())
				break;
		}
		
		
		return layerFolder;
	}
	
	protected List<NetworkLink> generateSubRegion(int layerNumber, int regionNum, double multiple, double north, double south, double lonRes) throws RenderEngineException
	{
		
		double modelEast = griddedModel.getEast();
		double modelWest = griddedModel.getWest();
		
		List<NetworkLink> networkLinks = new LinkedList<NetworkLink>();
		
		int subRegionNum = 1;
		for (double west = modelWest; west <= modelEast; west += lonRes) {
			double east = west + lonRes;
			

			try {
				int scaleTo = (layerNumber == 1) ? -1 : overlayTileSize;
				KmlDocument kmlDoc = KmlLayerGenerator.generate(modelContext, griddedModel, north, south, east, west, layerNumber, regionNum, subRegionNum, scaleTo, imageType, outputPath, true);
				
				if (kmlDoc != null) {
					String linkName = layerNumber + "/" + regionNum + "/" + subRegionNum;
					String href = linkName + ".kml";
					NetworkLink networkLink = new NetworkLink(linkName, href);
					networkLink.getLink().setViewRefreshMode("onRegion");
					Region region = new Region(north, south, east, west);
					region.setLod(new Lod(128, -1));
					networkLink.setRegion(region);
					networkLinks.add(networkLink);
				}

			} catch (IOException ex) {
				throw new RenderEngineException("IO exception generating region: " + ex.getMessage(), ex);
			} catch (KmlException ex) {
				throw new RenderEngineException("KML exception generating region: " + ex.getMessage(), ex);
			}
			
			subRegionNum++;
			if (west > griddedModel.getEast())
				break;
		}
		
		return networkLinks;
	}
	
	protected void writeKml(KmlDocument kmlDocument) throws IOException, KmlException
	{
		String writeTo = outputPath + "/doc.kml";
		File kmlFile = new File(writeTo);
		
		Kml kml = new Kml(kmlDocument);
		
		try {
			BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(kmlFile));
			fos.write(kml.toXmlDocument(true).getBytes());
			fos.flush();
			fos.close();
		} catch (IOException ex) {
			throw new IOException("Failed to write KML document to " + writeTo, ex);
		} catch (KmlException ex) {
			throw new KmlException("Failed to write KML document to " + writeTo, ex);
		}
		
		
	}
	
}

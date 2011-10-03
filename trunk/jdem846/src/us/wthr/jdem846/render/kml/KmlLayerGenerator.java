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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.exception.ImageException;
import us.wthr.jdem846.exception.KmlException;
import us.wthr.jdem846.image.ImageTypeEnum;
import us.wthr.jdem846.image.ImageUtilities;
import us.wthr.jdem846.image.ImageWriter;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.kml.GroundOverlay;
import us.wthr.jdem846.kml.Icon;
import us.wthr.jdem846.kml.Kml;
import us.wthr.jdem846.kml.KmlDocument;
import us.wthr.jdem846.kml.LatLonBox;
import us.wthr.jdem846.kml.ListItemTypeEnum;
import us.wthr.jdem846.kml.ListStyle;
import us.wthr.jdem846.kml.Lod;
import us.wthr.jdem846.kml.Region;
import us.wthr.jdem846.kml.Style;
import us.wthr.jdem846.kml.ViewRefreshModeEnum;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class KmlLayerGenerator
{
	private static Log log = Logging.getLog(KmlLayerGenerator.class);
	
	
	private DataPackage dataPackage;
	private ModelOptions modelOptions;
	private GriddedModel griddedModel;
	private double north;
	private double south;
	private double east;
	private double west;
	private int layerNumber;
	private int regionNumber;
	private int subRegionNumber;
	private int scaleSize;
	private ImageTypeEnum imageType;
	private String outputPath;
	
	
	protected KmlLayerGenerator(DataPackage dataPackage, 
								ModelOptions modelOptions, 
								GriddedModel griddedModel,
								double north,
								double south,
								double east,
								double west,
								int layerNumber,
								int regionNumber,
								int subRegionNumber,
								int scaleSize,
								ImageTypeEnum imageType, 
								String outputPath)
	{
		this.dataPackage = dataPackage;
		this.modelOptions = modelOptions;
		this.griddedModel = griddedModel;
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		this.layerNumber = layerNumber;
		this.regionNumber = regionNumber;
		this.subRegionNumber = subRegionNumber;
		this.scaleSize = scaleSize;
		this.imageType = imageType;
		this.outputPath = outputPath;
		
	}
	
	
	
	

	protected File createImageTile() throws KmlException, IOException
	{
		int tempTileSize = modelOptions.getTileSize();
		
		int fromRow = Math.round(dataPackage.latitudeToRow((float)north));
		int toRow = Math.round(dataPackage.latitudeToRow((float)south));
		int fromCol = Math.round(dataPackage.longitudeToColumn((float)west));
		int toCol = Math.round(dataPackage.longitudeToColumn((float)east));
		
		//getTilesIntersecting(double north, double south, double east, double west)
		List<Tile> tilesIntersecting = griddedModel.getTilesIntersecting(north, south, east, west);
		
		// If there are no tiles in this area, don't bother making this
		if (tilesIntersecting.size() == 0) {
			return null;
		}
		
		
		// If -1, then set scaleTo to the rendered tile size (full size)
		if (scaleSize == -1) {
			scaleSize = tempTileSize;
		}
		
		int tileSize = toRow - fromRow;
		double scalePct = (double)scaleSize / (double)tileSize;
		log.info("Tile " + north + "/" + west + " - " + south + "/" + east);
		log.info("From/To Row: " + fromRow + "/" + toRow + ", From/To Column: " + fromCol + "/" + toCol);
		log.info("Tiles Intersecting: " + tilesIntersecting.size() + " out of " + griddedModel.getTiles().size());
		log.info("Overlay Tile Size: " + tileSize);
		log.info("Scale Percentage: " + scalePct);
		
		BufferedImage image = new BufferedImage(scaleSize, scaleSize, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2d = (Graphics2D) image.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		g2d.setColor(new Color(0, 0, 0, 0));
		g2d.fillRect(0, 0, scaleSize, scaleSize);

		for (Tile tile : tilesIntersecting) {
			BufferedImage subtile = tile.loadImage();

			double _x = tile.getFromColumn() - fromCol;
			double _y = tile.getFromRow() - fromRow;
			
			double _x2 = _x + (tile.getToColumn() - tile.getFromColumn());
			double _y2 = _y + (tile.getToRow() - tile.getFromRow());
			
			int x = (int) Math.round(_x * scalePct) - 1;
			int y = (int) Math.round(_y * scalePct) - 1;
			
			int x2 = (int) Math.round(_x2 * scalePct);
			int y2 = (int) Math.round(_y2 * scalePct);
			
			int width = Math.abs(x2 - x);
			int height = Math.abs(y2 - y);
			
			
			log.info("x/y: " + x + "/" + y + ", width/height: " + width + "/" + height);
			
			BufferedImage scaled = ImageUtilities.getScaledInstance(subtile, width, width, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
			
			g2d.drawImage(scaled, x, y, width, height, new ImageObserver() {
				public boolean imageUpdate(Image img, int infoflags, int x,
						int y, int width, int height)
				{
					return true;
				}
			});
			

		}
		
		
		g2d.dispose();
		
		String fileName = "" + layerNumber + "/" + regionNumber + "/" + subRegionNumber + "." + imageType.extension();

		String path = outputPath + "/" + fileName;
		log.info("Writing image to " + path);
		
		File tileFile = new File(path);
		try {
			ImageWriter.saveImage(image, path, imageType);
		} catch (ImageException ex) {
			throw new KmlException("Failed to save image tile to disk: " + ex.getMessage(), ex);
		}

		return tileFile;
	}
	
	

	protected GroundOverlay createGroundOverlay(File distTileFile)
	{

		String overlayName = distTileFile.getName();

		LatLonBox latLonBox = new LatLonBox(east, west, north, south);
		Icon icon = new Icon(distTileFile.getName());
		icon.setViewFreshMode(ViewRefreshModeEnum.ON_REGION);
		
		GroundOverlay groundOverlay = new GroundOverlay(overlayName, icon, latLonBox);
		return groundOverlay;
		
	}
	
	
	protected void checkPathExists()
	{
		File path = new File(outputPath + "/" + layerNumber + "/" + regionNumber);
		if (!path.exists()) {
			path.mkdirs();
		}
	}
	
	protected void writeKml(KmlDocument kmlDocument) throws IOException, KmlException
	{
		String writeTo = "/" + layerNumber + "/" + regionNumber + "/" + subRegionNumber + ".kml";
		File kmlFile = new File(outputPath + writeTo);
		
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
	
	protected KmlDocument generate(boolean write) throws IOException, KmlException
	{
		KmlDocument kml = new KmlDocument();
		kml.setName("" + layerNumber + "/" + regionNumber + "/" + subRegionNumber);

		Style style = new Style();
		ListStyle listStyle = new ListStyle(ListItemTypeEnum.CHECK_HIDE_CHILDREN);
		listStyle.setId("ckHdChldrn");
		style.addSubStyle(listStyle);
		//kml.addStyle(style);
		
		checkPathExists();
		
		File distTileFile = createImageTile();
		if (distTileFile != null) {
		
			GroundOverlay overlay = createGroundOverlay(distTileFile);
			overlay.setDrawOrder(1000 - layerNumber);
	
			Region region = new Region(north, south, east, west);
			region.setLod(new Lod(128, -1));
			kml.setRegion(region);
			kml.addFeature(overlay);
	
			
			if (write) {
				writeKml(kml);
			}
			
			return kml;
		} else {
			return null;
		}
		
	}
	
	public static KmlDocument generate(DataPackage dataPackage, 
										ModelOptions modelOptions, 
										GriddedModel griddedModel,
										double north,
										double south,
										double east,
										double west,
										int layerNumber,
										int regionNumber,
										int subRegionNumber,
										int scaleSize,
										ImageTypeEnum imageType, 
										String outputPath, 
										boolean write) throws IOException, KmlException
	{
		
		KmlLayerGenerator generator = new KmlLayerGenerator(dataPackage,
				modelOptions,
				griddedModel,
				north,
				south,
				east,
				west,
				layerNumber,
				regionNumber,
				subRegionNumber,
				scaleSize,
				imageType,
				outputPath);
		
		return generator.generate(write);
	}
	
}

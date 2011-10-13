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

package us.wthr.jdem846.kml;

/* Note: This is sandbox code... It's gonna be /really/ fugly, make little to no sense, and
 * be outright incorrect or stupid. Sorry.
 * 
 */

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.RegistryKernel;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.image.ImageTypeEnum;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.input.gridfloat.GridFloat;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.KmlDemGenerator;
import us.wthr.jdem846.render.OutputProduct;
import us.wthr.jdem846.render.kml.GriddedModel;
import us.wthr.jdem846.render.kml.GriddedModelGenerator;
import us.wthr.jdem846.render.kml.KmlLayerGenerator;
import us.wthr.jdem846.render.kml.KmlModelGenerator;

/**
 * http://code.google.com/apis/kml/documentation/kmlreference.html
 * @author Kevin M. Gill
 *
 */
public class KmlTesting
{
	private static Log log = Logging.getLog(KmlTesting.class);
	
	private ModelContext modelContext;
	private DataPackage dataPackage;
	private ModelOptions modelOptions;
	private GriddedModel griddedModel;
	private String outputPath = "C:/srv/kml/dist";
	private String tempPath = "C:/srv/kml/temp";
	private int overlayTileSize = 256;
	private int layerMultiplier = 3;
	private ImageTypeEnum imageType = ImageTypeEnum.JPEG;
	
	public static void main(String[] args)
	{
		KmlTesting testing = new KmlTesting();
		testing.doTesting();
		
	}
	
	public void doTesting()
	{
		List<String> inputDataList = new LinkedList<String>();
		
		//inputDataList.add("C:/srv/elevation/Maui/15749574.flt");
		//inputDataList.add("C:/srv/elevation/Maui/58273983.flt");
		
		//inputDataList.add("C:\\srv\\elevation\\GEBCO_08\\gebco_08\\gebco_08_flt.flt");
		//outputPath = "C:\\srv\\elevation\\kml\\gebco_08/dist";
		//tempPath = "C:\\srv\\elevation\\kml\\gebco_08/temp";
		
		/*
		inputDataList.add("C:\\srv\\elevation\\etopo1_ice_g_f4\\etopo1_ice_g_f4.flt");

		outputPath = "C:/srv/elevation/kml/etopo1/dist.jpg";
		tempPath = "C:/srv/elevation/kml/etopo1/temp";
		*/
		
		inputDataList.add("/elev/etopo1_bed_g_f4/etopo1_bed_g_f4.flt");

		outputPath = "/elev/etopo1_bed_g_f4/kml/dist.png";
		tempPath = "/elev/etopo1_bed_g_f4/kml/temp";
		
		overlayTileSize = 256;
		layerMultiplier = 2;
		imageType = ImageTypeEnum.PNG;
		
		
		try {
			RegistryKernel regKernel = new RegistryKernel();
			regKernel.init();
		} catch (Exception ex) {
			log.error("Failed to initialize registry configuration: " + ex.getMessage(), ex);
			return;
		}
		

		modelOptions = new ModelOptions();
		modelOptions.setColoringType("hypsometric-etopo1-tint");
		modelOptions.setPrecacheStrategy(DemConstants.PRECACHE_STRATEGY_TILED);
		modelOptions.setTileSize(1000);
		
		dataPackage = new DataPackage();
		
		for (String inputDataPath : inputDataList) {
			GridFloat previewData = new GridFloat(inputDataPath);
			dataPackage.addDataSource(previewData);
		}
		
		dataPackage.prepare();

		try {
			dataPackage.calculateElevationMinMax(true);
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		} 
		
		modelContext = ModelContext.createInstance(dataPackage, modelOptions);
		
		try {
			
			KmlDemGenerator generator = new KmlDemGenerator(modelContext);
			generator.setOutputPath(outputPath);
			generator.setTempPath(tempPath);
			generator.setOverlayTileSize(overlayTileSize);
			generator.setLayerMultiplier(layerMultiplier);
			generator.setImageType(imageType);
			OutputProduct<KmlDocument> product = generator.generate();
			
		} catch (RenderEngineException ex) {
			ex.printStackTrace();
		}
		
		log.info("COMPLETE");

	}
	
	
	
	
	
	


}

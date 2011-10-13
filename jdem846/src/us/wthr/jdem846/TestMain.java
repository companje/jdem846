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

package us.wthr.jdem846;


import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;


import us.wthr.jdem846.dbase.DBaseFieldDescriptor;
import us.wthr.jdem846.dbase.DBaseFile;
import us.wthr.jdem846.dbase.DBaseLastUpdate;
import us.wthr.jdem846.dbase.DBaseRecord;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.InvalidFileFormatException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.input.DataSource;
import us.wthr.jdem846.input.DataSourceFactory;
import us.wthr.jdem846.input.edef.ElevationDatasetExchangeHeader;
import us.wthr.jdem846.input.edef.ElevationDatasetExchangeWriter;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.Dem2dGenerator;
import us.wthr.jdem846.render.DemCanvas;
import us.wthr.jdem846.render.ElevationDsFormatGenerator;
import us.wthr.jdem846.render.OutputProduct;
import us.wthr.jdem846.scaling.FloatRaster;
import us.wthr.jdem846.scaling.RasterScale;
import us.wthr.jdem846.shapefile.Shape;
import us.wthr.jdem846.shapefile.ShapeFile;
import us.wthr.jdem846.shapefile.ShapeIndexFile;
import us.wthr.jdem846.shapefile.ShapeIndexRecord;

/** Testing entry point.
 * 
 * @author Kevin M. Gill
 *
 */
@Deprecated
public class TestMain 
{
	private static Log log = Logging.getLog(TestMain.class);
	
	public static void convertToEdef(DataPackage dataPackage, String outputPath, int toWidth, int toHeight)
	{
		ElevationDatasetExchangeHeader header = new ElevationDatasetExchangeHeader();
		header.setCellSize(dataPackage.getAvgXDim());
		//header.setColumns((int)Math.ceil(dataPackage.getColumns()));
		//header.setRows((int)dataPackage.getRows());
		header.setColumns(toWidth);
		header.setRows(toHeight);
		header.setMaxElevation(dataPackage.getMaxElevation());
		header.setMinElevation(dataPackage.getMinElevation());
		header.setxCellSize(dataPackage.getAvgXDim());
		header.setyCellSize(dataPackage.getAvgYDim());
		header.setxLowerLeft(dataPackage.getMinLongitude());
		header.setyLowerLeft(dataPackage.getMinLatitude());
		
		int width = (int)Math.ceil(dataPackage.getColumns());
		int height = (int)dataPackage.getRows();
		
		ElevationDatasetExchangeWriter writer = new ElevationDatasetExchangeWriter(outputPath, header);
		
		try {
			
			
			FloatRaster raster = new FloatRaster(width, height);
			for (int row = 0; row < height; row++) {
				for (int column = 0; column < width; column++) {
					float elevation = dataPackage.getElevation(row, column);
					raster.set(column, row, elevation);
					//writer.write(elevation);
					
				}
			}
			
			FloatRaster scaled = RasterScale.scale(raster, toWidth, toHeight);
			
			toWidth = scaled.getWidth();
			toHeight = scaled.getHeight();
			
			System.out.println("Data width/height: " + toWidth + "/" + toHeight);
			
			header.setColumns(toWidth);
			header.setRows(toHeight);
			writer.open();
			writer.writeHeader();
			
			for (int row = 0; row < toHeight; row++) {
				for (int column = 0; column < toWidth; column++) {
					writer.write(scaled.get(column, row));
				}
			}
			
			writer.flush();
			writer.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void convertToEdef(DataSource dataSource, String outputPath)
	{
		
		
		ElevationDatasetExchangeHeader header = new ElevationDatasetExchangeHeader();
		header.setCellSize(dataSource.getHeader().getCellSize());
		header.setColumns(dataSource.getHeader().getColumns());
		header.setRows(dataSource.getHeader().getRows());
		header.setMaxElevation(dataSource.getMaxElevation());
		header.setMinElevation(dataSource.getMinElevation());
		header.setxCellSize(dataSource.getHeader().getCellSize());
		header.setyCellSize(dataSource.getHeader().getCellSize());
		header.setxLowerLeft(dataSource.getHeader().getxLowerLeft());
		header.setyLowerLeft(dataSource.getHeader().getyLowerLeft());
		
		
		ElevationDatasetExchangeWriter writer = new ElevationDatasetExchangeWriter(outputPath, header);
		
		try {
			writer.open();
			writer.writeHeader();
			
			for (int row = 0; row < dataSource.getHeader().getRows(); row++) {
				for (int column = 0; column < dataSource.getHeader().getColumns(); column++) {
					float elevation = dataSource.getElevation(row, column);
					
					writer.write(elevation);
					
				}
			}
			
			writer.flush();
			writer.close();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	public static void doTest(List<String> inputList, String writeTo)
	{
		DataPackage dataPackage = new DataPackage(null);
		for (String inputPath : inputList) {
			
			DataSource input;
			try {
				input = DataSourceFactory.loadDataSource(inputPath);
			} catch (InvalidFileFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			
			input.calculateDataStats();
			//input.calculateElevationMinMax();

			//String edefFile = inputPath.substring(0, inputPath.length() - 4) + ".edef";
			//System.out.println("Converting to EDEF: " + edefFile);
			
			System.out.println("=====================================================");
			//convertToEdef(input, edefFile);
			
			dataPackage.addDataSource(input);
		}
		
		dataPackage.prepare();
		//dataPackage.calculateElevationMinMax();
		//convertToEdef(dataPackage, "/home/oracle/testfile.edef");
		
		System.out.println("Creating EDEF...");
		
		long start = 0;
		long end = 0;
		
		ModelOptions modelOptions = new ModelOptions();
		modelOptions.setWriteTo(writeTo);
		modelOptions.setWidth(1000);
		modelOptions.setHeight(1000);
		modelOptions.setTileSize(2000);
		
		ModelContext modelContext = ModelContext.createInstance(dataPackage, modelOptions);
		
		ElevationDsFormatGenerator engine = new ElevationDsFormatGenerator(modelContext);
		//engine.setDataPackage(dataPackage);
		//engine.setModelOptions(modelOptions);
		
		start = System.currentTimeMillis();
		try {
			dataPackage.calculateElevationMinMax(true);
		} catch (DataSourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		end = System.currentTimeMillis();
		System.out.println("Completed elevation min/max in " + (end - start) + " milliseconds");
		
	
		start = System.currentTimeMillis();
		try {
			engine.generate();
		} catch (RenderEngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		end = System.currentTimeMillis();
		System.out.println("Completed Generate in " + (end - start) + " milliseconds");
		
		//convertToEdef(dataPackage, "C:/srv/elevation/testfile.edef", 1000, 1000);
		System.out.println("Done.");
	}
	
	public static void main(String[] args)
	{
		
		
		//byte[] bytes = ByteConversions.floatToBytes(365.567f);
		
		//float test = ByteConversions.bytesToFloat(bytes);
		//System.out.println("Test: " + test);
		
		
		Properties props = System.getProperties();
		for (Object o_name : props.keySet()) {
			String name = (String) o_name;
			String value = props.getProperty(name);
			System.out.println(name + ": " + value);
		}
		
		
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			log.info("LaF: " + info.getClassName());
		}
		
		
		//List<String> inputList = new LinkedList<String>();
		//inputList.add("C:\\srv\\DEM\\Nashua NH 1-3 Arc Second\\78522096.flt");
		//inputList.add("/elev/BilInt16 Testing/NED_55017568.bil");
		//inputList.add("/elev/mars_dem_usgs_64deg_cub/mars_dem_usgs_64deg_cub.bil");
		
		//inputList.add("/elev/Pawtuckaway/74339812.flt");
		//inputList.add("/elev/Maui/15749574.flt");
		//inputList.add("/elev/Maui/58273983.flt");
		
		//inputList.add("/elev/Carrizo Plain/83819993.flt");
		//inputList.add("/elev/Carrizo Plain/92538336.flt");
		
		//inputList.add("C:/srv/elevation/Maui/15749574.flt");
		//inputList.add("C:/srv/elevation/Maui/58273983.flt");
		//doTest(inputList, "C:/srv/elevation/testfile.edef");
		
		//inputList.clear();
		
		//inputList.add("C:/srv/elevation/Maui/58273983.flt");
		//doTest(inputList, "C:/srv/elevation/testfile-2.edef");
		//String gridFloatPath = "C:\\srv\\elevation\\Nashua NH\\51109002.flt";
		//String gridFloatPath = "C:\\srv\\elevation\\Presidential Ridge With Bretton Woods\\70358325.flt";
		//String gridFloatPath = "C:\\srv\\elevation\\Grand Canyon 1-3 Arc Second\\45024532.flt";
		//inputList.add("C:\\srv\\elevation\\Nashua NH\\51109002.flt");
		//inputList.add("/media/geomodeling/Mount Saint Helens - OrthoImagery/18231408.flt");
		//inputList.add("/elev/Maui/15749574.flt");
		//inputList.add("/elev/Maui/58273983.flt");
		
		//System.out.println("Loading GridFloat " + gridFloatPath1);
		//GridFloat gridFloat1 = new GridFloat(gridFloatPath1);
		//gridFloat1.calculateDataStats();
		//
		//System.out.println("Loading GridFloat " + gridFloatPath2);
		//GridFloat gridFloat2 = new GridFloat(gridFloatPath2);
		//gridFloat2.calculateDataStats();
		
		
		/*
		
		*/
		
		/*
		System.out.println("Creating data package...");
		DataPackage dataPackage = new DataPackage(null);
		
		for (String inputPath : inputList) {
			
			System.out.println("=====================================================");
			System.out.println("Loading GridFloat data: " + inputPath);
			GridFloat gridFloat = new GridFloat(inputPath);
			gridFloat.calculateDataStats();
			dataPackage.addDataSource(gridFloat);
			
			
			System.out.println("Max Difference: " + gridFloat.getMaxDifference());
			System.out.println("Max Elevation: " + gridFloat.getMaxElevation());
			System.out.println("Min Elevation: " + gridFloat.getMinElevation());
			System.out.println("Max Columns: " + gridFloat.getMaxCol());
			System.out.println("Max Rows: " + gridFloat.getMaxRow());
			System.out.println("X Lower Left: " + gridFloat.getHeader().getxLowerLeft());
			System.out.println("Y Lower Left: " + gridFloat.getHeader().getyLowerLeft());
			System.out.println("Cellsize: " + gridFloat.getHeader().getCellSize());
			System.out.println("=====================================================");
			
		}

		dataPackage.prepare();
		
		
		//System.out.println("Testing 2D Generator...");
		
		ModelOptions modelOptions = new ModelOptions();
		Dem2dGenerator dem2d = new Dem2dGenerator(dataPackage, modelOptions);
		dataPackage.calculateElevationMinMax();
		
		long duration = 0;
		for (int i = 0; i < 10; i++) {
			long start = System.currentTimeMillis();
			DemCanvas canvas = dem2d.generate(0, 200, 0, 200);
			//DemCanvas canvas = dem2d.generate(1000, 1000, 3000);
			long finish = System.currentTimeMillis();
			System.err.println(""+(finish - start));
			duration += (finish - start);
		}
		double avgDuration = (double)duration / 10.0;
		System.err.println("Average: " + avgDuration);
		
		*/
		
		/*
		try {
			URL url = ClasspathUrlFinder.findClassBase(TestMain.class);
			AnnotationDB db = new AnnotationDB();
			db.scanArchives(url);
			db.crossReferenceImplementedInterfaces();
			 	
			Map<String, Set<String>> annotationIndex = db.getAnnotationIndex();
			Set<String> colorClasses = annotationIndex.get(DemColoring.class.getName());
			
			for (String annot : annotationIndex.keySet()) {
				System.out.println(annot);
				Set<String> annotlist = annotationIndex.get(annot);
				for (String annotClass : annotlist) {
					System.out.println("	" + annotClass);
				}
			}
			
			if (colorClasses != null) {
				for (String clazzName : colorClasses) {
					System.out.println("Coloring Class: " + clazzName);
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		*/
		//DemCanvas output = dem2d.generate(3000, 3000, 5000);
		//output.save("C:\\srv\\DEM\\Nashua NH 1-3 Arc Second\\output.png");
		
		
		/*
		String testProjectPath = "C:\\srv\\elevation\\test-project.xml";
		ProjectModel modelToSave = new ProjectModel();
		modelToSave.setWidth(4000);
		modelToSave.setHeight(3000);
		modelToSave.setColoringType(DemConstants.COLORING_TYPE_HYPSOMETRIC);
		modelToSave.setHillShading(true);
		modelToSave.setHillShadingType(DemConstants.HILLSHADING_DARKEN);
		modelToSave.setTileSize(3000);
		modelToSave.setLightingMultiple(0.75);
		modelToSave.getInputFiles().add("C:\\srv\\elevation\\Nashua NH\\51109002.flt");
		
		Date date = new Date(System.currentTimeMillis());
		System.out.println("Test Date: " + date);
		
		try {
			ProjectFileWriter.writeProject(modelToSave, testProjectPath);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			ProjectModel projectModel = ProjectFileReader.readProject(testProjectPath);
			System.out.println("Width: " + projectModel.getWidth());
			System.out.println("Height: " + projectModel.getHeight());
			System.out.println("Coloring Type: " + projectModel.getColoringType());
			System.out.println("Hill Shading: " + projectModel.isHillShading());
			System.out.println("Hill Shading Type: " + projectModel.getHillShadingType());
			System.out.println("Tile Size: " + projectModel.getTileSize());
			System.out.println("Lighting Multiple: " + projectModel.getLightingMultiple());
			
			for (String file : projectModel.getInputFiles()) {
				System.out.println("Input File: " + file);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ProjectParseException e) {
			e.printStackTrace();
		}
		*/
		
		/*
		try {
			DBaseFile dbase = new DBaseFile("C:\\srv\\elevation\\Shapefile Testing\\hydrography\\NHDArea.dbf");
			DBaseLastUpdate lastUpdate = dbase.getLastUpdate();
			System.out.println("Last Update: " + lastUpdate.getMonth() + "/" + lastUpdate.getDay() + "/" + lastUpdate.getYear());
			System.out.println("Number of Records: " + dbase.getNumRecords());
			System.out.println("Number of Header Bytes: " + dbase.getNumHeaderBytes());
			System.out.println("Number of Record Bytes: " + dbase.getNumRecordBytes());
			System.out.println("Number of Field Descriptors: " + dbase.getFieldDescriptorCount());
			
			for (int i = 0; i < dbase.getFieldDescriptorCount(); i++) {
				DBaseFieldDescriptor fieldDescriptor = dbase.getFieldDescriptor(i);
				System.out.println("Field: " + fieldDescriptor.getName());
			}
			
			for (int i = 0; i < dbase.getNumRecords(); i++ ) {
				DBaseRecord record = dbase.getRecord(i);
				int comId = record.getInteger("ComID");
				String fType = record.getString("FTYPE");
				Date fDate = record.getDate("FDate");
				String resolution = record.getString("RESOLUTION");
				String gnisName = record.getString("GNIS_Name");
				float areaSqKm = record.getFloat("AreaSqKm");
				System.out.println("FType: " + fType + ", FDate: " + fDate + ", Resolution: " + resolution + ", ComID: " + comId + ", AreaSqKm: " + areaSqKm + ", GNIS Name: " + gnisName);
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		/*
		List<String> inputList = new LinkedList<String>();
		inputList.add("C:\\srv\\elevation\\Shapefile Testing\\ned_64087130.flt");
		
		DataPackage dataPackage = new DataPackage(null);
		for (String inputPath : inputList) {
			log.info("Adding elevation data: " + inputPath);
			DataSource input;
			try {
				input = DataSourceFactory.loadDataSource(inputPath);
			} catch (InvalidFileFormatException e) {
				e.printStackTrace();
				continue;
			}
			
			input.calculateDataStats();
			dataPackage.addDataSource(input);
		}
		
		dataPackage.prepare();
		
		ModelOptions modelOptions = new ModelOptions();
		Dem2dGenerator dem2d = new Dem2dGenerator(dataPackage, modelOptions);
		dataPackage.calculateElevationMinMax(true);
		
		OutputProduct<DemCanvas> output = dem2d.generate();
		
		try {
			
			ShapeFile shapeFile = new ShapeFile("C:\\srv\\elevation\\Shapefile Testing\\hydrography\\NHDArea.shp");
			
			
			log.info("Shape Count: " + shapeFile.getShapeCount());
			log.info("File Length: " + shapeFile.getFileLength());
			log.info("Version: " + shapeFile.getVersion());
			log.info("Shape Type: " + shapeFile.getShapeType());
			log.info("Bounds: " + shapeFile.getBounds());
			
			ShapeIndexFile indexFile = shapeFile.getIndexFile();
			for (int i = 0; i < shapeFile.getShapeCount(); i++) {
				ShapeIndexRecord indexRecord = indexFile.getIndexRecord(i);
				
				log.info("Record: Offset: " + indexRecord.getOffset() + ", Length: " + indexRecord.getLength());
				Shape shape = shapeFile.getShape(i);
			}
			
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		*/
	}
	
}

package us.wthr.jdem846.render;


import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelDimensions;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.canvas.CanvasProjection;
import us.wthr.jdem846.canvas.CanvasProjection3d;
import us.wthr.jdem846.canvas.CanvasProjectionGlobe;
import us.wthr.jdem846.canvas.CanvasProjectionTypeEnum;
import us.wthr.jdem846.canvas.LatLonResolution;
import us.wthr.jdem846.image.ImageDataContext;
import us.wthr.jdem846.image.SimpleGeoImage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataContext;

public class ModelDimensions2D extends ModelDimensions
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(ModelDimensions2D.class);
	
	protected ModelDimensions2D()
	{
		
	}

	
	public ModelDimensions2D(ModelContext modelContext)
	{
		init(modelContext);
	}
	
	
	public void init(ModelContext modelContext)
	{
		RasterDataContext rasterDataContext = modelContext.getRasterDataContext();
		ImageDataContext imageDataContext = modelContext.getImageDataContext();
		ModelOptions modelOptions = modelContext.getModelOptions();
		
		/*
		north = modelContext.getNorth();
		south = modelContext.getSouth();
		east = modelContext.getEast();
		west = modelContext.getWest();
		*/
		north = -90;
		south = 90;
		east = -180;
		west = 180;
		
		latitudeResolution = Double.MAX_VALUE;
		longitudeResolution = Double.MAX_VALUE;
		
		if (rasterDataContext != null) {
			for (RasterData rasterData : rasterDataContext.getRasterDataList()) {
				latitudeResolution = MathExt.min(latitudeResolution, rasterData.getLatitudeResolution());
				longitudeResolution = MathExt.min(longitudeResolution, rasterData.getLongitudeResolution());
				
				north = MathExt.max(north, rasterData.getNorth());
				south = MathExt.min(south, rasterData.getSouth());
				east = MathExt.max(east, rasterData.getEast());
				west = MathExt.min(west, rasterData.getWest());
			}
		}
		
		if (imageDataContext != null) {
			for (SimpleGeoImage image : imageDataContext.getImageList()) {
				latitudeResolution = MathExt.min(latitudeResolution, image.getLatitudeResolution());
				longitudeResolution = MathExt.min(longitudeResolution, image.getLongitudeResolution());
			
				north = MathExt.max(north, image.getNorth());
				south = MathExt.min(south, image.getSouth());
				east = MathExt.max(east, image.getEast());
				west = MathExt.min(west, image.getWest());
			}
		}
		
		if (north > 90) {
			north = 90;
		}
		
		if (south < -90) {
			south = -90;
		}
		
		if (east > 180) {
			east = 180;
		}
		
		if (west < -180) {
			west = -180;
		}
		
		
		//double scale = modelContext.getModelOptions().getProjection().getZoom();
		
		dataRows = (int) MathExt.round((north - south) / latitudeResolution);
		dataColumns = (int) MathExt.round((east - west) / longitudeResolution);
		
		outputHeight = modelOptions.getHeight();
		outputWidth = modelOptions.getWidth();
		
		//double xdimRatio = (double)outputWidth / (double)dataColumns;
		//double ydimRatio = (double)outputHeight / (double)dataRows;
		
		double scaleX = modelContext.getModelOptions().getProjection().getZoom();
		
		/*
		double minSideLength = MathExt.min(outputWidth, outputHeight) - 20;
		double radius = (minSideLength / 2.0)  * scaleX;
		
		double circumference = 2 * MathExt.PI * radius;
		
		double xdimRatio = (double)circumference / (double)dataColumns;
		double ydimRatio = (double)circumference / (double)dataRows;
		
		
		outputLongitudeResolution = longitudeResolution / xdimRatio;
		outputLatitudeResolution = latitudeResolution / ydimRatio;
		*/
		
		LatLonResolution latLonOutputRes = null;
		CanvasProjectionTypeEnum canvasProjectionType = modelOptions.getModelProjection();
		if (canvasProjectionType == CanvasProjectionTypeEnum.PROJECT_3D) {
			latLonOutputRes = CanvasProjection3d.calculateOutputResolutions(outputWidth,
															outputHeight,
															dataColumns,
															dataRows,
															latitudeResolution,
															longitudeResolution,
															scaleX);
		} else if (canvasProjectionType == CanvasProjectionTypeEnum.PROJECT_SPHERE) {
			latLonOutputRes = CanvasProjectionGlobe.calculateOutputResolutions(outputWidth,
															outputHeight,
															dataColumns,
															dataRows,
															latitudeResolution,
															longitudeResolution,
															scaleX);
		} else  {
			latLonOutputRes = CanvasProjection.calculateOutputResolutions(outputWidth,
															outputHeight,
															dataColumns,
															dataRows,
															latitudeResolution,
															longitudeResolution,
															scaleX);
		}
		
		textureLatitudeResolution = latLonOutputRes.latitudeResolution;
		textureLongitudeResolution = latLonOutputRes.longitudeResolution;
		
		if (textureLongitudeResolution < longitudeResolution)
			textureLongitudeResolution = longitudeResolution;
		
		if (textureLatitudeResolution < latitudeResolution)
			textureLatitudeResolution = latitudeResolution;
		
	}
	
	public static ModelDimensions2D getModelDimensions(ModelContext modelContext)
	{
		
		ModelDimensions2D modelDimensions = new ModelDimensions2D(modelContext);
		return modelDimensions;
	}
	
	
	public ModelDimensions2D copy()
	{
		ModelDimensions2D copy = new ModelDimensions2D();
		copy.dataColumns = this.dataColumns;
		copy.dataRows = this.dataRows;
		copy.north = this.north;
		copy.south = this.south;
		copy.east = this.east;
		copy.west = this.west;
		copy.latitudeResolution = this.latitudeResolution;
		copy.longitudeResolution = this.longitudeResolution;
		copy.outputHeight = this.outputHeight;
		copy.outputWidth = this.outputWidth;
		copy.textureLatitudeResolution = this.textureLatitudeResolution;
		copy.textureLongitudeResolution = this.textureLongitudeResolution;

		return copy;
	}
	
}

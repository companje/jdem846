package us.wthr.jdem846.model.processing.coloring;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.color.ColorAdjustments;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.model.ModelGrid;
import us.wthr.jdem846.model.ModelPoint;
import us.wthr.jdem846.model.ModelPointHandler;
import us.wthr.jdem846.model.annotations.GridProcessing;
import us.wthr.jdem846.model.processing.AbstractGridProcessor;
import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846.model.processing.GridProcessor;


@GridProcessing(id="us.wthr.jdem846.model.processing.coloring.RoughnessColoringProcessor",
	name="Roughness Color Process",
	type=GridProcessingTypesEnum.COLORING,
	optionModel=RoughnessColoringOptionModel.class,
	enabled=true
)
/** Calculates a roughness ratio based on the largest difference between a
 * central elevation value and each surrounding point. If a surrounding
 * elevation value is not valid, it is not checked. If no valid elevation
 * values are found surrounding the central point, then the result will be
 * zero (0).
 * 
 * @author Kevin M. Gill
 *
 */
public class RoughnessColoringProcessor extends AbstractGridProcessor implements GridProcessor, ModelPointHandler
{
	private static Log log = Logging.getLog(RoughnessColoringProcessor.class);
	
	private double latitudeResolution;
	private double longitudeResolution;
	
	private double[] gridElevationBuffer = new double[8];
	
	// TODO: Make these configurable
	private int[] minColor = {0, 255, 0, 255};
	private int[] maxColor = {255, 0, 0, 255};
	private int[] colorBuffer = new int[4];
	
	private double min = 10000000;
	private double max = -10000000;
	
	private double pass = 0;
	
	private int band = 1;
	private int bandHalf = 1;
	private ModelColoring modelColoring;
	
	public RoughnessColoringProcessor()
	{
		
	}
	
	public RoughnessColoringProcessor(ModelContext modelContext, ModelGrid modelGrid)
	{
		super(modelContext, modelGrid);
	}
	
	@Override
	public void prepare() throws RenderEngineException
	{
		RoughnessColoringOptionModel optionModel = (RoughnessColoringOptionModel) this.getProcessOptionModel();
		
		latitudeResolution = getModelDimensions().getTextureLatitudeResolution();
		longitudeResolution = getModelDimensions().getTextureLongitudeResolution();
		
		modelColoring = ColoringRegistry.getInstance(optionModel.getColorTint()).getImpl();
		
		band = optionModel.getBand();
		bandHalf = (int) Math.round(((double)band / 2.0));
		
		if (bandHalf < 1) {
			bandHalf = 1;
		}
		
		pass = 0;
	}
	
	@Override
	public void process() throws RenderEngineException
	{
		log.info("Roughness Processor 1st Pass...");
		super.process();
		
		log.info("Minimum: " + min);
		log.info("Maximum: " + max);
		
		log.info("Roughness Processor 2nd Pass...");
		pass++;
		super.process();
	}
	
	@Override
	public void onModelPoint(double latitude, double longitude)
			throws RenderEngineException
	{

		if (pass == 0) {
			firstPass(latitude, longitude);
		} else {
			secondPass(latitude, longitude);
		}
		
	}
	
	protected void firstPass(double latitude, double longitude)
	{
		double r = calculateRoughness(latitude, longitude);
		if (r != DemConstants.ELEV_NO_DATA) {
			min = MathExt.min(min, r);
			max = MathExt.max(max, r);
		}
	}
	
	protected void secondPass(double latitude, double longitude)
	{
		double r = calculateRoughness(latitude, longitude);
		if (r != DemConstants.ELEV_NO_DATA) {
			double ratio = (r - min) / (max - min);
			
			ColorAdjustments.interpolateColor(minColor, maxColor, colorBuffer, ratio);
			modelGrid.setRgba(latitude, longitude, colorBuffer);
		} else {
			colorBuffer[3] = 0x0;
			modelGrid.setRgba(latitude, longitude, colorBuffer);
		}
	}
	
	protected double calculateRoughness(double latitude, double longitude)
	{
		double c = modelGrid.getElevation(latitude, longitude);
		if (c == DemConstants.ELEV_NO_DATA) {
			return DemConstants.ELEV_NO_DATA;
		}
		
		double north = latitude + (latitudeResolution * bandHalf);
		double south = latitude - (latitudeResolution * bandHalf);
		
		double east = longitude + (longitudeResolution * bandHalf);
		double west = longitude - (longitudeResolution * bandHalf);

		double elevationPoint = 0.0;
		
		double maxDiff = 0;
		
		for (double lat = north; lat >= south; lat-=latitudeResolution) {
			
			for (double lon = west; lon <= east; lon+=longitudeResolution) {
				elevationPoint =  modelGrid.getElevation(lat, lon);
				
				if (elevationPoint != DemConstants.ELEV_NO_DATA) {
					maxDiff = MathExt.max(maxDiff, MathExt.sqr(elevationPoint - c));
				}
			}
			
		}
		return MathExt.sqrt(maxDiff);

	}
	
	
	
}

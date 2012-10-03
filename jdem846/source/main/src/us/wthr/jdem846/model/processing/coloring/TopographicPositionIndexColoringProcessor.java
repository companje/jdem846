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


@GridProcessing(id="us.wthr.jdem846.model.processing.coloring.TopographicPositionIndexColoringProcessor",
	name="TPI Color Process",
	type=GridProcessingTypesEnum.COLORING,
	optionModel=TopographicPositionIndexColoringOptionModel.class,
	enabled=true
)
/** TPI (Topographic Position Index) is the difference between the central
 * elevation and the mean of it's surrounding points. If any given point
 * does not contain a valid elevation value, then it is discarded and the 
 * mean divisor is reduced by one (from eight). If no valid elevation values
 * are found, then the TPI is returned as 0.
 * 
 * Equation: (Ec denotes the central elevation)
 * TPI = sqrt([E1^2 + E2^2 + ... + E8^2] / 8) - Ec
 * 
 * @author Kevin M. Gill
 *
 */
public class TopographicPositionIndexColoringProcessor extends AbstractGridProcessor implements GridProcessor, ModelPointHandler
{
	private static Log log = Logging.getLog(TopographicPositionIndexColoringProcessor.class);
	
	private double latitudeResolution;
	private double longitudeResolution;
	
	private double[] gridElevationBuffer = new double[8];
	
	// TODO: Make these configurable
	private int[] minTpiColor = {0, 255, 0, 255};
	private int[] maxTpiColor = {255, 0, 0, 255};
	private int[] colorBuffer = new int[4];
	
	private double minTpi = 10000000;
	private double maxTpi = -10000000;
	
	private double pass = 0;
	
	
	private int band = 1;
	private int bandHalf = 1;
	private ModelColoring modelColoring;
	
	public TopographicPositionIndexColoringProcessor()
	{
		
	}
	
	public TopographicPositionIndexColoringProcessor(ModelContext modelContext, ModelGrid modelGrid)
	{
		super(modelContext, modelGrid);
	}
	
	
	@Override
	public void prepare() throws RenderEngineException
	{
		TopographicPositionIndexColoringOptionModel optionModel = (TopographicPositionIndexColoringOptionModel) this.getProcessOptionModel();
		
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
		log.info("TPI Processor 1st Pass...");
		super.process();
		
		log.info("Minimum TPI: " + minTpi);
		log.info("Maximum TPI: " + maxTpi);
		
		log.info("TPI Processor 2nd Pass...");
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
		double tpi = calculateTpi(latitude, longitude);
		if (tpi != DemConstants.ELEV_NO_DATA) {
			minTpi = MathExt.min(minTpi, tpi);
			maxTpi = MathExt.max(maxTpi, tpi);
		}
	}
	
	protected void secondPass(double latitude, double longitude)
	{
		double tpi = calculateTpi(latitude, longitude);
		if (tpi != DemConstants.ELEV_NO_DATA) {
			double ratio = (tpi - minTpi) / (maxTpi - minTpi);
			ColorAdjustments.interpolateColor(minTpiColor, maxTpiColor, colorBuffer, ratio);
		} else {
			colorBuffer[3] = 0x0;
		}
		modelGrid.setRgba(latitude, longitude, colorBuffer);
	}
	
	
	protected double calculateTpi(double latitude, double longitude)
	{
		
		double c = modelGrid.getElevation(latitude, longitude);
		if (c == DemConstants.ELEV_NO_DATA) {
			return DemConstants.ELEV_NO_DATA;
		}
		
		double north = latitude + (latitudeResolution * bandHalf);
		double south = latitude - (latitudeResolution * bandHalf);
		
		double east = longitude + (longitudeResolution * bandHalf);
		double west = longitude - (longitudeResolution * bandHalf);
		
		double samples = 0.0;
		double elevationSum = 0.0;
		double elevationPoint = 0.0;
		
		for (double lat = north; lat >= south; lat-=latitudeResolution) {
			
			for (double lon = west; lon <= east; lon+=longitudeResolution) {
				elevationPoint = modelGrid.getElevation(lat, lon);
				
				if (elevationPoint != DemConstants.ELEV_NO_DATA) {
					elevationSum += MathExt.sqr(elevationPoint);
					samples++;
				}
			}
			
		}
		
		double tri = 0;
		
		if (samples > 0) {
			tri = MathExt.sqrt(elevationSum / samples) - c;
		}
		
		return tri;

	}
	
	
	
}

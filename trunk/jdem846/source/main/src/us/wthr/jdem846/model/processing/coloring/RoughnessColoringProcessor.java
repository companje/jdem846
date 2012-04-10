package us.wthr.jdem846.model.processing.coloring;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.color.ColorAdjustments;
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
		
		latitudeResolution = getModelDimensions().getOutputLatitudeResolution();
		longitudeResolution = getModelDimensions().getOutputLongitudeResolution();
		
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
		
		ModelPoint modelPoint = modelGrid.get(latitude, longitude);
		
		if (pass == 0) {
			firstPass(modelPoint, latitude, longitude);
		} else {
			secondPass(modelPoint, latitude, longitude);
		}
		
	}
	
	protected void firstPass(ModelPoint modelPoint, double latitude, double longitude)
	{
		double r = calculateRoughness(modelPoint, latitude, longitude);
		
		min = MathExt.min(min, r);
		max = MathExt.max(max, r);
	}
	
	protected void secondPass(ModelPoint modelPoint, double latitude, double longitude)
	{
		double r = calculateRoughness(modelPoint, latitude, longitude);
		
		double ratio = (r - min) / (max - min);
		
		ColorAdjustments.interpolateColor(minColor, maxColor, colorBuffer, ratio);
		modelPoint.setRgba(colorBuffer, false);
	}
	
	protected double calculateRoughness(ModelPoint modelPoint, double latitude, double longitude)
	{
		
		
		
		double c = modelPoint.getElevation();
		
		
		
		gridElevationBuffer[0] = getElevationAtPoint(latitude + latitudeResolution, longitude);
		gridElevationBuffer[1] = getElevationAtPoint(latitude + latitudeResolution, longitude - longitudeResolution);
		gridElevationBuffer[2] = getElevationAtPoint(latitude, longitude - longitudeResolution);
		gridElevationBuffer[3] = getElevationAtPoint(latitude - latitudeResolution, longitude - longitudeResolution);
		gridElevationBuffer[4] = getElevationAtPoint(latitude - latitudeResolution, longitude);
		gridElevationBuffer[5] = getElevationAtPoint(latitude - latitudeResolution, longitude + longitudeResolution);
		gridElevationBuffer[6] = getElevationAtPoint(latitude, longitude + longitudeResolution);
		gridElevationBuffer[7] = getElevationAtPoint(latitude + latitudeResolution, longitude + longitudeResolution);
		
		
		double maxDiff = 0;
		
		for (double sample : gridElevationBuffer) {
			if (sample != DemConstants.ELEV_NO_DATA) {
				maxDiff = MathExt.max(maxDiff, MathExt.sqr(sample - c));
			}
		}

		return MathExt.sqrt(maxDiff);
	}
	
	
	
}

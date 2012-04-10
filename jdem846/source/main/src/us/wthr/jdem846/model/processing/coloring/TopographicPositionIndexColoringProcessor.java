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
		
		latitudeResolution = getModelDimensions().getOutputLatitudeResolution();
		longitudeResolution = getModelDimensions().getOutputLongitudeResolution();
		
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
		
		ModelPoint modelPoint = modelGrid.get(latitude, longitude);
		
		if (pass == 0) {
			firstPass(modelPoint, latitude, longitude);
		} else {
			secondPass(modelPoint, latitude, longitude);
		}
		
	}
	
	
	protected void firstPass(ModelPoint modelPoint, double latitude, double longitude)
	{
		double tpi = calculateTpi(modelPoint, latitude, longitude);
		
		minTpi = MathExt.min(minTpi, tpi);
		maxTpi = MathExt.max(maxTpi, tpi);
	}
	
	protected void secondPass(ModelPoint modelPoint, double latitude, double longitude)
	{
		double tpi = calculateTpi(modelPoint, latitude, longitude);
		
		double ratio = (tpi - minTpi) / (maxTpi - minTpi);
		
		ColorAdjustments.interpolateColor(minTpiColor, maxTpiColor, colorBuffer, ratio);
		modelPoint.setRgba(colorBuffer, false);
	}
	
	
	protected double calculateTpi(ModelPoint modelPoint, double latitude, double longitude)
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
		
		
		double validPoints = 0;
		
		double a = 0;
		for (double sample : gridElevationBuffer) {
			if (sample != DemConstants.ELEV_NO_DATA) {
				a += MathExt.sqr(sample);
				validPoints++;
			}
		}
		
		
		double tri = 0;
		
		if (validPoints > 0) {
			tri = MathExt.sqrt(a / validPoints) - c;
		}
		
		return tri;
	}
	
	
	
}

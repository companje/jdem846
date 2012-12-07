package us.wthr.jdem846.model.processing.shading;

import us.wthr.jdem846.color.ColorAdjustments;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.model.ModelPoint;
import us.wthr.jdem846.model.annotations.GridProcessing;
import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846.model.processing.GridProcessor;
import us.wthr.jdem846.model.processing.util.SurfaceNormalCalculator;

@GridProcessing(id = "us.wthr.jdem846.model.processing.shading.SlopeShadingProcessor", name = "Slope Shading Process", type = GridProcessingTypesEnum.SHADING, optionModel = SlopeShadingOptionModel.class, enabled = false)
public class SlopeShadingProcessor extends GridProcessor
{
	private static Log log = Logging.getLog(SlopeShadingProcessor.class);

	protected int[] rgbaBuffer = new int[4];
	private Vector normal = new Vector();
	private SurfaceNormalCalculator normalsCalculator;

	private int pass = 0;
	private double minSlope = 10000000;
	private double maxSlope = -10000000;

	protected double relativeLightIntensity;
	protected double relativeDarkIntensity;
	protected int spotExponent;
	private double lightingMultiple = 1.0;

	public SlopeShadingProcessor()
	{

	}

	@Override
	public void prepare() throws RenderEngineException
	{

		SlopeShadingOptionModel optionModel = (SlopeShadingOptionModel) this.getOptionModel();

		lightingMultiple = optionModel.getLightMultiple();
		relativeLightIntensity = optionModel.getLightIntensity();
		relativeDarkIntensity = optionModel.getDarkIntensity();
		spotExponent = optionModel.getSpotExponent();

		normalsCalculator = new SurfaceNormalCalculator(modelGrid, PlanetsRegistry.getPlanet(getGlobalOptionModel().getPlanet()), getModelDimensions().getTextureLatitudeResolution(),
				getModelDimensions().getTextureLongitudeResolution());
	}

	public void process() throws RenderEngineException
	{
		log.info("Slope Shading Processor 1st Pass...");
		// super.process();

		log.info("Max Slope: " + maxSlope);
		log.info("Min Slope: " + minSlope);

		log.info("Slope Shading Processor 2nd Pass...");
		pass++;
		// super.process();
	}

	@Override
	public void onLatitudeStart(double latitude) throws RenderEngineException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onModelPoint(double latitude, double longitude) throws RenderEngineException
	{

		if (pass == 0) {
			onModelPointFirstPass(latitude, longitude);
		} else {
			onModelPointSecondPass(latitude, longitude);
		}

	}

	public void onModelPointFirstPass(double latitude, double longitude) throws RenderEngineException
	{

		ModelPoint modelPoint = modelGrid.get(latitude, longitude);

		// modelPoint.getNormal(normal);
		normalsCalculator.calculateNormalFlat(latitude, longitude, normal);
		double slope = MathExt.degrees(MathExt.pow(MathExt.cos(normal.z), -1));

		minSlope = MathExt.min(minSlope, slope);
		maxSlope = MathExt.max(maxSlope, slope);

	}

	public void onModelPointSecondPass(double latitude, double longitude) throws RenderEngineException
	{
		ModelPoint modelPoint = modelGrid.get(latitude, longitude);

		// modelPoint.getNormal(normal);
		normalsCalculator.calculateNormalFlat(latitude, longitude, normal);
		double slope = MathExt.degrees(MathExt.pow(MathExt.cos(normal.z), -1));

		double shade = 1.0 - (2.0 * ((slope - minSlope) / (maxSlope - minSlope)));

		if (shade > 0) {
			shade *= relativeLightIntensity;
		} else if (shade < 0) {
			shade *= relativeDarkIntensity;
		}

		if (spotExponent != 1) {
			shade = MathExt.pow(shade, spotExponent);
		}

		processPointColor(modelPoint, latitude, longitude, shade);
	}

	protected void processPointColor(ModelPoint modelPoint, double latitude, double longitude, double shade) throws RenderEngineException
	{
		modelPoint.getRgba(rgbaBuffer);
		ColorAdjustments.adjustBrightness(rgbaBuffer, shade);
		modelPoint.setRgba(rgbaBuffer);
	}

	@Override
	public void onLatitudeEnd(double latitude) throws RenderEngineException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onProcessBefore() throws RenderEngineException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onProcessAfter() throws RenderEngineException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() throws RenderEngineException
	{
		// TODO Auto-generated method stub

	}

}

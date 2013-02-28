package us.wthr.jdem846.model.processing.shading;


import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.model.annotations.GridProcessing;
import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846.model.processing.GridProcessor;
import us.wthr.jdem846.model.processing.util.Aspect;
import us.wthr.jdem846.model.processing.util.SurfaceNormalCalculator;
import us.wthr.jdem846.util.ColorUtil;

@GridProcessing(id = "us.wthr.jdem846.model.processing.shading.AspectShadingProcessor", name = "Aspect Shading Process", type = GridProcessingTypesEnum.SHADING, optionModel = AspectShadingOptionModel.class, enabled = true)
public class AspectShadingProcessor extends GridProcessor
{
	private static Log log = Logging.getLog(AspectShadingProcessor.class);

	protected int[] rgbaBuffer = new int[4];
	private Vector normal = new Vector();
	private SurfaceNormalCalculator normalsCalculator;

	protected double relativeLightIntensity;
	protected double relativeDarkIntensity;
	protected int spotExponent;
	private double lightingMultiple = 1.0;

	public AspectShadingProcessor()
	{

	}

	@Override
	public void prepare() throws RenderEngineException
	{

		AspectShadingOptionModel optionModel = (AspectShadingOptionModel) this.getOptionModel();

		lightingMultiple = optionModel.getLightMultiple();
		relativeLightIntensity = optionModel.getLightIntensity();
		relativeDarkIntensity = optionModel.getDarkIntensity();
		spotExponent = optionModel.getSpotExponent();

		normalsCalculator = new SurfaceNormalCalculator(modelGrid, PlanetsRegistry.getPlanet(getGlobalOptionModel().getPlanet()), getModelDimensions().getTextureLatitudeResolution(),
				getModelDimensions().getTextureLongitudeResolution());
	}

	@Override
	public void onModelPoint(double latitude, double longitude) throws RenderEngineException
	{
		// TODO: Reimplement sans ModelPoint
		
		//ModelPoint modelPoint = modelGrid.get(latitude, longitude);
		modelGrid.getElevation(latitude, longitude, false);
			
		normalsCalculator.calculateNormal(latitude, longitude, normal);
		// modelPoint.getNormal(normal);
		double degrees = Aspect.aspectInDegrees(normal);
		if (degrees > 180) {
			degrees = 180 - (degrees - 180);
		}

		double shade = 1.0 - (2.0 * (degrees / 180.0));

		// log.info("Degrees: " + degrees + ", Shade: " + shade);

		if (shade > 0) {
			shade *= relativeLightIntensity;
		} else if (shade < 0) {
			shade *= relativeDarkIntensity;
		}

		if (spotExponent != 1) {
			shade = MathExt.pow(shade, spotExponent);
		}

		processPointColor(latitude, longitude, shade);

	}

	protected void processPointColor(double latitude, double longitude, double shade) throws RenderEngineException
	{
		this.modelGrid.getRgba(latitude, longitude, rgbaBuffer);
		ColorUtil.adjustBrightness(rgbaBuffer, shade);
		this.modelGrid.setRgba(latitude, longitude, rgbaBuffer);
	}

	@Override
	public void onLatitudeStart(double latitude) throws RenderEngineException
	{

	}

	@Override
	public void onLatitudeEnd(double latitude) throws RenderEngineException
	{

	}

	@Override
	public void onProcessBefore() throws RenderEngineException
	{

	}

	@Override
	public void onProcessAfter() throws RenderEngineException
	{

	}

	@Override
	public void dispose() throws RenderEngineException
	{

	}

}

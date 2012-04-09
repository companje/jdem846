package us.wthr.jdem846.canvas;


public enum CanvasProjectionTypeEnum
{
	PROJECT_FLAT("us.wthr.jdem846.render.canvasProjection.flat", CanvasProjection.class),
	PROJECT_3D("us.wthr.jdem846.render.canvasProjection.3d", CanvasProjection3d.class),
	PROJECT_SPHERE("us.wthr.jdem846.render.canvasProjection.sphere", CanvasProjectionGlobe.class);
	
	private final String projectionName;
	private final Class<CanvasProjection> provider;
	
	
	CanvasProjectionTypeEnum(String projectionName, Class<?> provider)
	{
		this.projectionName = projectionName;
		this.provider = (Class<CanvasProjection>) provider;
	}
	
	public String projectionName() { return projectionName; }
	

	public String identifier() { return projectionName; }
	public Class<CanvasProjection> provider() { return provider; }
	
	public static CanvasProjectionTypeEnum getCanvasProjectionEnumFromIdentifier(String identifier)
	{
		for (CanvasProjectionTypeEnum value : CanvasProjectionTypeEnum.values()) {
			if (value.identifier().equalsIgnoreCase(identifier)) {
				return value;
			}
		}
		
		return null;
	}
}

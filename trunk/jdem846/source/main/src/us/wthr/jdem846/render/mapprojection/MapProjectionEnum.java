package us.wthr.jdem846.render.mapprojection;

import us.wthr.jdem846.i18n.I18N;

public enum MapProjectionEnum
{
	EQUIRECTANGULAR("us.wthr.jdem846.render.mapProjection.equirectangularProjection.name", EquirectangularProjection.class),
	EQUIRECTANGULAR3D("us.wthr.jdem846.render.mapProjection.equirectangular3dProjection.name", Equirectangular3dProjection.class),
	AITOFF("us.wthr.jdem846.render.mapProjection.aitoffProjection.name", AitoffProjection.class),
	ROBINSON("us.wthr.jdem846.render.mapProjection.robinsonProjection.name", RobinsonProjection.class),
	WINKELTRIPEL("us.wthr.jdem846.render.mapProjection.winkelTripelProjection.name", WinkelTripelProjection.class),
	HAMMER("us.wthr.jdem846.render.mapProjection.hammerProjection.name", HammerProjection.class),
	MOLLWEIDE("us.wthr.jdem846.render.mapProjection.mollweideProjection.name", MollweideProjection.class),
	WAGNERVI("us.wthr.jdem846.render.mapProjection.WagnerViProjection.name", WagnerVIProjection.class);
	
	private final String projectionName;
	private final Class<MapProjection> provider;
	
	
	MapProjectionEnum(String projectionName, Class<?> provider)
	{
		this.projectionName = projectionName;
		this.provider = (Class<MapProjection>) provider;
	}
	
	
	public String identifier() { return projectionName; }
	public String projectionName() { return I18N.get(projectionName, projectionName); }
	public Class<MapProjection> provider() { return provider; }
	
	public static MapProjectionEnum getMapProjectionEnumFromIdentifier(String identifier)
	{
		for (MapProjectionEnum value : MapProjectionEnum.values()) {
			if (value.identifier().equalsIgnoreCase(identifier)) {
				return value;
			}
		}
		
		return null;
	}
}

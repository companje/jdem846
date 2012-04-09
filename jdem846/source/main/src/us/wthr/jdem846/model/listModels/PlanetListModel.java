package us.wthr.jdem846.model.listModels;

import java.util.List;

import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.model.OptionListModel;

public class PlanetListModel extends OptionListModel<String>
{
	
	public PlanetListModel()
	{
		List<Planet> planetList = PlanetsRegistry.getPlanetList();
		for (Planet planet : planetList) {
			addItem(planet.getName(), planet.getName());
		}
	}
	
}

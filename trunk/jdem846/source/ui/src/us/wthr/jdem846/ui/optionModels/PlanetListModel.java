package us.wthr.jdem846.ui.optionModels;

import java.util.List;

import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.ui.base.JComboBoxModel;

public class PlanetListModel extends JComboBoxModel<String>
{
	
	public PlanetListModel()
	{
		
		List<Planet> planetList = PlanetsRegistry.getPlanetList();
		for (Planet planet : planetList) {
			addItem(planet.getName(), planet.getName());
		}
	}
	
	
}

package us.wthr.jdem846.model;

import java.util.Map;

import junit.framework.TestCase;

public class ViewPerspectiveTest extends TestCase
{
	
	
	public void testDeserialize()
	{
		String testViewPerspective = "rotate:[0,1,2];shift:[3,4,5];zoom:[6]";
		
		Map<String, double[]> values = SimpleNumberListMapSerializer.parseDoubleListString(testViewPerspective);
		
		assert(values.size() == 3);
		assert(values.containsKey("rotate"));
		assert(values.containsKey("shift"));
		assert(values.containsKey("zoom"));
		
		double[] rotate = values.get("rotate");
		double[] shift = values.get("shift");
		double[] zoom = values.get("zoom");
		
		assert(rotate.length == 3);
		assert(shift.length == 3);
		assert(zoom.length == 1);
		
		assert(rotate[0] == 0);
		assert(rotate[1] == 1);
		assert(rotate[2] == 2);
		
		assert(shift[0] == 3);
		assert(shift[1] == 4);
		assert(shift[2] == 5);
		
		assert(zoom[0] == 6);

		
	}

	public void testSerialize()
	{
		ViewPerspective viewPerspective = new ViewPerspective();
		
		viewPerspective.setRotateX(0);
		viewPerspective.setRotateY(1);
		viewPerspective.setRotateZ(2);
		
		viewPerspective.setShiftX(3);
		viewPerspective.setShiftY(4);
		viewPerspective.setShiftZ(5);
		
		viewPerspective.setZoom(6);
		
		String s = viewPerspective.toString();
		
		assert(s.equals("rotate:[0,1,2];shift:[3,4,5];zoom:[6]"));
		
	}
	
	
}

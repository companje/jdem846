package us.wthr.jdem846.model;

import java.util.List;

import us.wthr.jdem846.AbstractTestCase;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.exceptions.InvalidProcessOptionException;
import us.wthr.jdem846.model.exceptions.MethodContainerInvokeException;

public class OptionModelContainerTest extends AbstractTestCase
{
	private static Log log = null;
	
	private TestingOptionModel optionModel;
	private OptionModelContainer container;
	
	
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		
		log = Logging.getLog(OptionModelContainerTest.class);
		
		optionModel = new TestingOptionModel();
		container = new OptionModelContainer(optionModel);
	}
	

	
	public void testPropertyCount()
	{
		assert container.getPropertyCount() == 6;
	}
	
	
	public void testPropertyNames()
	{
		List<String> propertyNames = container.getPropertyNames();
		assertTrue(propertyNames.size() == 6);
	}
	
	public void testString1GetAndSetByPropertyName()
	{
		OptionModelPropertyContainer propertyContainer = container.getPropertyByName("String1");
		assert propertyContainer != null;
		
		try {
			propertyContainer.setValue("Test String 1");
		} catch (MethodContainerInvokeException e) {
			fail();
		}
		
		Object returned = null;
		try {
			returned = propertyContainer.getValue();
		} catch (MethodContainerInvokeException e) {
			fail();
		}
		
		assertTrue(returned instanceof String);
		
		String value = (String) returned;
		assertTrue(value.equals("Test String 1"));
	}
	
	
	public void testString2GetAndSetByPropertyName()
	{
		OptionModelPropertyContainer propertyContainer = container.getPropertyByName("String2");
		assert propertyContainer != null;
		
		try {
			propertyContainer.setValue("Test String 2");
		} catch (MethodContainerInvokeException e) {
			fail();
		}
		
		Object returned = null;
		try {
			returned = propertyContainer.getValue();
		} catch (MethodContainerInvokeException e) {
			fail();
		}
		
		assertTrue(returned instanceof String);
		
		String value = (String) returned;
		assertTrue(value.equals("Test String 2"));
	}
	
	
	public void testDouble1GetAndSetByPropertyName()
	{
		OptionModelPropertyContainer propertyContainer = container.getPropertyByName("Double1");
		assert propertyContainer != null;
		
		try {
			propertyContainer.setValue(2.0d);
		} catch (MethodContainerInvokeException e) {
			fail();
		}
		
		Object returned = null;
		try {
			returned = propertyContainer.getValue();
		} catch (MethodContainerInvokeException e) {
			fail();
		}
		
		assertTrue(returned instanceof Double);
		
		Double value = (Double) returned;
		assertTrue(value == 2.0);
	}
	
	
	public void testDouble2GetAndSetByPropertyName()
	{
		OptionModelPropertyContainer propertyContainer = container.getPropertyByName("Double2");
		assert propertyContainer != null;
		
		try {
			propertyContainer.setValue(4.0d);
		} catch (MethodContainerInvokeException e) {
			fail();
		}
		
		Object returned = null;
		try {
			returned = propertyContainer.getValue();
		} catch (MethodContainerInvokeException e) {
			fail();
		}
		
		assertTrue(returned instanceof Double);
		
		Double value = (Double) returned;
		assertTrue(value == 4.0);
	}
	
	public void testInt1GetAndSetByPropertyName()
	{
		OptionModelPropertyContainer propertyContainer = container.getPropertyByName("Int1");
		assert propertyContainer != null;
		
		try {
			propertyContainer.setValue(2);
		} catch (MethodContainerInvokeException e) {
			fail();
		}
		
		Object returned = null;
		try {
			returned = propertyContainer.getValue();
		} catch (MethodContainerInvokeException e) {
			fail();
		}
		
		assertTrue(returned instanceof Integer);
		
		Integer value = (Integer) returned;
		assertTrue(value == 2);
	}
	
	public void testInt2GetAndSetByPropertyName()
	{
		OptionModelPropertyContainer propertyContainer = container.getPropertyByName("Int2");
		assert propertyContainer != null;
		
		try {
			propertyContainer.setValue(4);
		} catch (MethodContainerInvokeException e) {
			fail();
		}
		
		Object returned = null;
		try {
			returned = propertyContainer.getValue();
		} catch (MethodContainerInvokeException e) {
			fail();
		}
		
		assertTrue(returned instanceof Integer);
		
		Integer value = (Integer) returned;
		assertTrue(value == 4);
	}
	
	// TODO: ... And many more needed ...
}

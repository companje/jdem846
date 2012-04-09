package us.wthr.jdem846.model.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import us.wthr.jdem846.annotations.Discoverable;

import us.wthr.jdem846.model.processing.GridProcessingTypes;

@Discoverable
@Retention(RetentionPolicy.RUNTIME)
public @interface GridProcessing
{
	
	String id();
	
	String name();
	
	GridProcessingTypes type();

	Class<?> optionModel();
	
	boolean enabled() default true;
	
}

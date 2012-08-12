package us.wthr.jdem846.model.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import us.wthr.jdem846.annotations.Discoverable;

import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;

@Discoverable
@Retention(RetentionPolicy.RUNTIME)
public @interface GridProcessing
{
	
	String id();
	
	String name();
	
	GridProcessingTypesEnum type();

	Class<?> optionModel();
	
	boolean enabled() default true;
	
	boolean isFilter() default false;
}

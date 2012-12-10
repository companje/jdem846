package us.wthr.jdem846.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import us.wthr.jdem846.annotations.Discoverable;

@Discoverable
@Target(value={ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProcessOption
{
	
	String id();
	
	String label();
	
	String tooltip();
	
	String optionGroup() default "General";

	Class<?> listModel() default Object.class;
	
	Class<?> validator() default Object.class;
	
	Class<?> enabler() default Object.class;
	
	boolean enabled() default true;
	
	
}

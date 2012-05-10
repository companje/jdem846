package us.wthr.jdem846.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import us.wthr.jdem846.annotations.Discoverable;


@Discoverable
@Target(value={ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueBounds
{
	
	double minimum() default -100000;
	double maximum() default 100000;
	double stepSize() default 1;
	
}

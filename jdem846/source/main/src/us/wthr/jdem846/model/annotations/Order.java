package us.wthr.jdem846.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import us.wthr.jdem846.annotations.Discoverable;

@Discoverable
@Target(value={ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Order
{
	public static final int NOT_SET = 9999;
	
	int value() default NOT_SET;
}

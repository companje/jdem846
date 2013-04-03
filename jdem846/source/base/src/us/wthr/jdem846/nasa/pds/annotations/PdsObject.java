package us.wthr.jdem846.nasa.pds.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import us.wthr.jdem846.annotations.Discoverable;

@Discoverable
@Retention(RetentionPolicy.RUNTIME)
public @interface PdsObject
{
	String name();
	String[] aliases() default { };
}

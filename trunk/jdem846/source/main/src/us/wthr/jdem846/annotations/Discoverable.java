package us.wthr.jdem846.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
public @interface Discoverable
{
	
	
	/** Specifies whether the annotated class is enabled
	 * 
	 * @return
	 */
	boolean enabled() default true;
}

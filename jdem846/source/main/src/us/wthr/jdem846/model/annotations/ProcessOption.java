package us.wthr.jdem846.model.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import us.wthr.jdem846.annotations.Discoverable;
import us.wthr.jdem846.model.OptionListModel;

@Discoverable
@Retention(RetentionPolicy.RUNTIME)
public @interface ProcessOption
{
	
	String id();
	
	String label();
	
	String tooltip();

	Class<OptionListModel<?>> listModel();
	
	boolean enabled() default true;
	
}

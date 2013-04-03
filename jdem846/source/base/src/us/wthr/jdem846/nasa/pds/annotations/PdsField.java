package us.wthr.jdem846.nasa.pds.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import us.wthr.jdem846.nasa.pds.StandardValueTypeEnum;

@Retention(RetentionPolicy.RUNTIME)
public @interface PdsField
{
	String name();
	String[] aliases() default { };
	
	boolean required() default false;
	
	String[] standardValues() default { };
	StandardValueTypeEnum standardValueType() default StandardValueTypeEnum.DYNAMIC;
}

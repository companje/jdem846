/*
 * Copyright (C) 2011 Kevin M. Gill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.wthr.jdem846.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Annotation for identifying model coloring classes.
 * 
 * @author Kevin M. Gill
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DemColoring {
	
	/** Human-readable name for the class.
	 * 
	 * @return
	 */
	String name();
	
	/** Unique machine-readable name for the class.
	 * 
	 * @return
	 */
	String identifier();
	
	/** Controls whether the UI should display a gradient configuration widget.
	 * 
	 * @return
	 */
	boolean allowGradientConfig() default true;
	
	/** Specifies whether the class needs a minimum and maximum elevation determined from the data.
	 * 
	 * @return
	 */
	boolean needsMinMaxElevation() default true;
}

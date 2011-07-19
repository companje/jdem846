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

/** Identifies model engine classes.
 * 
 * @author Kevin M. Gill
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DemEngine {
	
	/** Human-readable name for the class.
	 * 
	 * @return
	 */
	String name();
	
	/** Unique, machine-readable identifier for the class.
	 * 
	 * @return
	 */
	String identifier();
	
	/** Specifies whether the engine is enabled and available for use on the UI.
	 * 
	 * @return
	 */
	boolean enabled() default true;
	
	/** Specifies whether the engine uses the width property from the model options.
	 * 
	 * @return
	 */
	boolean usesWidth() default true;
	
	/** Specifies whether the engine uses the height property from the model options.
	 * 
	 * @return
	 */
	boolean usesHeight() default true;
	
	/** Specifies whether the engine uses the background color property from the model options.
	 * 
	 * @return
	 */
	boolean usesBackgroundColor() default true;
	
	/** Specifies whether the engine uses the coloring engine property from the model options.
	 * 
	 * @return
	 */
	boolean usesColoring() default true;
	
	/** Specifies whether the engine uses the hill shading property from the model options.
	 * 
	 * @return
	 */
	boolean usesHillshading() default true;
	
	/** Specifies whether the engine uses the light multiple property from the model options.
	 * 
	 * @return
	 */
	boolean usesLightMultiple() default true;
	
	/** Specifies whether the engine uses the spot exponent property from the model options.
	 * 
	 * @return
	 */
	boolean usesSpotExponent() default true;
	
	/** Specifies whether the engine uses the tile size property from the model options.
	 * 
	 * @return
	 */
	boolean usesTileSize() default true;
	
	/** Specifies whether the engine uses the generates image property from the model options.
	 * 
	 * @return
	 */
	boolean generatesImage() default true;
	
	/** Specifies which output file type is required to be asked of the user before running. 
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	Class needsOutputFileOfType() default Object.class;
}

/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2016 Rafael Math
*
*  OpenDS is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*
*  OpenDS is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  You should have received a copy of the GNU General Public License
*  along with OpenDS. If not, see <http://www.gnu.org/licenses/>.
*/

package eu.opends.drivingTask.interaction;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Biasutti, Rafael Math
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Parameter 
{

    /**
     * Name used to identify this parameter inside an action.
     * 
	 * @return
	 * 		Name of parameter
	 */
    String name() default "";

    
    /**
     * Parameter's data type.
     * 
     * @return
	 * 		Type of parameter
     */
    String type() default "";
    
    
    /**
     * Default value.
     * 
     * @return
	 * 		Default value of parameter
     */
    String defaultValue() default "";

    
    /**
     * Description of the parameter.
     * 
     * @return
	 * 		Description of parameter
     */
    String description() default "No description available.";
}

/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2015 Rafael Math
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.opends.oculusRift;

import com.jme3.asset.AssetManager;
import com.jme3.post.filters.FogFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.shadow.DirectionalLightShadowFilter;

/**
 *
 * @author Rickard
 */
public class FilterUtil {
    
    public static FogFilter cloneFogFilter(FogFilter fogFilter){
        FogFilter filterClone = new FogFilter();
        filterClone.setFogColor(fogFilter.getFogColor());
        filterClone.setFogDensity(fogFilter.getFogDensity());
        filterClone.setFogDistance(fogFilter.getFogDistance());
        filterClone.setName(fogFilter.getName() + " Clone");
        
        return filterClone;
    }

    public static SSAOFilter cloneSSAOFilter(SSAOFilter filter){
        SSAOFilter clone = new SSAOFilter();
        clone.setSampleRadius(filter.getSampleRadius());
        clone.setIntensity(filter.getIntensity());
        clone.setScale(filter.getScale());
        clone.setBias(filter.getBias());
        return clone;
    }
    
    public static DirectionalLightShadowFilter cloneDirectionalLightShadowFilter(AssetManager assetManager, DirectionalLightShadowFilter filter){
        DirectionalLightShadowFilter clone = new DirectionalLightShadowFilter(assetManager, 512, 3);
        clone.setLight(filter.getLight());
        clone.setLambda(filter.getLambda());
        clone.setShadowIntensity(filter.getShadowIntensity());
        clone.setEdgeFilteringMode(filter.getEdgeFilteringMode());
//        clone.setEnabled(filter.isEnabled());
        return clone;
    }
    
}

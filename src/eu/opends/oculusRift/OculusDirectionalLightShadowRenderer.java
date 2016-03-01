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

package eu.opends.oculusRift;

import com.jme3.asset.AssetManager;
import com.jme3.shadow.DirectionalLightShadowRenderer;

/**
 * DirectionalLightShadowRenderer renderer use Parrallel Split Shadow Mapping
 * technique (pssm)<br> It splits the view frustum in several parts and compute
 * a shadow map for each one.<br> splits are distributed so that the closer they
 * are from the camera, the smaller they are to maximize the resolution used of
 * the shadow map.<br> This result in a better quality shadow than standard
 * shadow mapping.<br> for more informations on this read this <a
 * href="http://http.developer.nvidia.com/GPUGems3/gpugems3_ch10.html">http://http.developer.nvidia.com/GPUGems3/gpugems3_ch10.html</a><br>
 * <p/>
 * @author Rémy Bouquet aka Nehon
 */
public class OculusDirectionalLightShadowRenderer extends DirectionalLightShadowRenderer {

    /**
     * Create a OculusDirectionalLightShadowRenderer More info on the technique at <a
     * href="http://http.developer.nvidia.com/GPUGems3/gpugems3_ch10.html">http://http.developer.nvidia.com/GPUGems3/gpugems3_ch10.html</a>
     *
     * @param assetManager the application asset manager
     * @param shadowMapSize the size of the rendered shadowmaps (512,1024,2048,
     * etc...)
     * @param nbSplits the number of shadow maps rendered (the more shadow maps
     * the more quality, the less fps).
     */
    public OculusDirectionalLightShadowRenderer(AssetManager assetManager, int shadowMapSize, int nbSplits) {
        super(assetManager, shadowMapSize, nbSplits);
    }
    
    @Override
    public OculusDirectionalLightShadowRenderer clone() {
        OculusDirectionalLightShadowRenderer clone = new OculusDirectionalLightShadowRenderer(assetManager, (int)shadowMapSize, nbShadowMaps);
        clone.assetManager = assetManager;
        clone.setEdgeFilteringMode(edgeFilteringMode);
        clone.setEdgesThickness((int)edgesThickness);
        clone.setEnabledStabilization(this.isEnabledStabilization());
        clone.setFlushQueues(flushQueues);
        clone.setLambda(lambda);
        clone.setLight(light);
        clone.setShadowCompareMode(shadowCompareMode);
        clone.setShadowIntensity(shadowIntensity);
        clone.setShadowZExtend(zFarOverride);
        clone.setShadowZFadeLength(fadeLength);                
        return clone;
    }
}

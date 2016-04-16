                   
package distraction;

/**
 *
 * @author Johnny
 */
import eu.opends.effects.EffectCenter;


public class Weather {
    
    public static void setWeather (){
        if (DistractionSettings.isRain()){
            EffectCenter.setRainingPercentage(DistractionSettings.getIntensityRain());
        }
        if (DistractionSettings.isSnow()){
            EffectCenter.setSnowingPercentage(DistractionSettings.getIntensitySnow());
        }
        if (DistractionSettings.isFog()){
            EffectCenter.setFogPercentage(DistractionSettings.getIntensityFog());
        }
    }
    
}



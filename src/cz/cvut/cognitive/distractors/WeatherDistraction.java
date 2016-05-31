                   
package cz.cvut.cognitive.distractors;

/**
 *
 * @author Johnny
 * 
 * Class handles distractions of enviroment (rain, snow and fog) according to 
 * options.
 * 
 */
import eu.opends.effects.EffectCenter;


public class WeatherDistraction {
    
    public static float COG_SCORE;
    
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



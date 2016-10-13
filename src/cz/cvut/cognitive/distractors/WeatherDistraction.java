                   
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


public class WeatherDistraction extends DistractionClass {
    
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

    public WeatherDistraction(float diff) {
        super(1, 0.1f, diff);
    }

    @Override
    public void update(float tpf, float probability) { //FIXME implement 
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}



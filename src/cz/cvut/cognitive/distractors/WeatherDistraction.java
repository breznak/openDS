                   
package cz.cvut.cognitive.distractors;

/**
 *
 * @author Johnny
 * 
 * Class handles distractions of enviroment (rain, snow and fog) according to 
 * options.
 * 
 */
import cz.cvut.cognitive.load.CognitiveFunction;
import eu.opends.effects.EffectCenter;
import eu.opends.main.Simulator;


public class WeatherDistraction extends DistractionClass {
    public enum Type {RAIN, SNOW, FOG};
    
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

    public WeatherDistraction(Simulator sim, float reward, float probability, Type weatherType) {
        super(sim, reward, probability, probability*0.03f);
    }

    @Override
    public void spawn(float tpf) {
        return; 
    }

    @Override
    public void remove_local() {
        return;
    }

    @Override
    public void collision(float tpf) {
        return; //TODO can we detect crash during the duration?
    }
    
}



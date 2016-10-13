                   
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

    public WeatherDistraction(Simulator sim) {
        super(sim, 1, 0.1f, (float)((DistractionSettings.getIntensityFog() + DistractionSettings.getIntensityRain() + DistractionSettings.getIntensitySnow())*0.03));
        CognitiveFunction.distScore += this.COG_DIFFICULTY;
    }

    @Override
    public void spawn(float tpf) { //TODO should do sth?
        return; 
    }

    @Override
    public void remove_local() {
        return; //TODO sth?
    }

    @Override
    public void collision(float tpf) {
        return; //TODO can we detect crash during the duration?
    }
    
}



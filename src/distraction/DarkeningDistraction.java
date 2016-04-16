 package distraction;

import eu.opends.main.Simulator;

/**
 *
 * @author Johnny
 */
import eu.opends.effects.EffectCenter;
 
public class DarkeningDistraction extends DistractionClass{

    @Override
    public void initialize(Simulator sim) {
        
    }

    @Override
    public void update(float tpf, float intensity) {
        EffectCenter.setFogPercentage(50);
    }

    @Override
    public void remove() {
        EffectCenter.setFogPercentage(DistractionSettings.getIntensityFog());
    }
    
}

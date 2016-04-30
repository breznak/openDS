 package cz.cvut.cognitive.distractors;

import eu.opends.main.Simulator;

/**
 *
 * @author Johnny
 * 
 * Class for Darkening-type distraction. When this option is selected in
 * program, there is always (set) probability to darken the player's screen by
 * adding fog for short period of time.
 * 
 */
import eu.opends.effects.EffectCenter;
 
public class DarkeningDistraction extends DistractionClass{

    
    private boolean darkOn = false;
    
    /**
     *Empty constructor for DarkeningDistraction
     * @param sim - Simulator
     */
    public DarkeningDistraction(Simulator sim){
        
    }

    /**
     * Update function: if preset probability of screen darkening is higher than
     * random generated number (1-100), screen will get dark (foggy). 
     */
    @Override
    public void update(float tpf, float propability) {
        int n = (int)(Math.random() * 100) + 1;
        if (n < propability){
            EffectCenter.setFogPercentage(50);
            darkOn = true;
            DistractionSettings.setDistRunning(true);
        }
    }

    /**
     * Remove function: if distractor was spawned before this call, it will be
     * removed from the scene.
     */
    @Override
    public void remove() {
        if(darkOn){
            EffectCenter.setFogPercentage(DistractionSettings.getIntensityFog());
            darkOn = false;
        }
    }
    
}

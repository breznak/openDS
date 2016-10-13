 package cz.cvut.cognitive.distractors;

import cz.cvut.cognitive.load.CognitiveFunction;
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
        super(sim, 1, 0.1f, 3f);
    }

    /**
     * Update function: if preset probability of screen darkening is higher than
     * random generated number (1-100), screen will get dark (foggy). 
     */
    @Override
    public void update(float tpf) {
        int n = (int)(Math.random() * 100) + 1;
        if (n <= this.PROBABILITY){
            EffectCenter.setFogPercentage(50);
            darkOn = true;
            CognitiveFunction.distScore += this.COG_DIFFICULTY;
            CognitiveFunction.activeDistCount++;
            CognitiveFunction.activeDistNames[2] = 1;
            DistractionSettings.distRunning++;
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
            CognitiveFunction.distScore -= this.COG_DIFFICULTY;
            CognitiveFunction.activeDistCount--;
            CognitiveFunction.activeDistNames[2] = 0;
            DistractionSettings.distRunning--;
        }
    }

    @Override
    public void collision(float tpf) {
        //FIXME can we detect crash during this active period?
        return; //NOOP
    }
    
}

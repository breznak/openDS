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

    /**
     *Empty constructor for DarkeningDistraction
     * @param sim - Simulator
     */
    public DarkeningDistraction(Simulator sim, float reward, float probability, float cogDifficulty){
        super(sim, reward, probability, cogDifficulty);
    }

    /**
     * Update function: if preset probability of screen darkening is higher than
     * random generated number (1-100), screen will get dark (foggy). 
     */
    @Override
    public void spawn(float tpf) {
            EffectCenter.setFogPercentage(50);
    }

    /**
     * Remove function: if distractor was spawned before this call, it will be
     * removed from the scene.
     */
    @Override
    public void remove_local() {
            EffectCenter.setFogPercentage(0f);
    }

    @Override
    public void collision(float tpf) {
        //FIXME can we detect crash during this active period?
        return; //NOOP
    }
    
}

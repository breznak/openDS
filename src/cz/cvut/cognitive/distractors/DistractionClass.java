

package cz.cvut.cognitive.distractors;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Johnny
 * 
 * Abstract class for different types of distractions.
 * Ensures that every distraction contains update (spawn) and remove method.
 * Update method parameters: tpf - time per frame of simulator
 *                           probability - preset value of every initialized
 *                                         distraction
 */
public abstract class DistractionClass {
    /**
     * 
     * @param reward positive(>0) means it's good to collect this, added on "hit", substracted from score on "miss"
     * @param probability 0..100, how often should the distraction occur
     * @param difficulty how "cognitively demanding" this stress factor is
     */
    protected DistractionClass(float reward, float probability, float difficulty) {
        this.PROBABILITY=probability;
        this.REWARD=reward;
        this.COG_DIFFICULTY = difficulty;
        DistractionClass.activeDistractors.add(this); //append to available distractors
    }
    public abstract void update(float tpf);
    public abstract void remove();
    
    // lets discuss
    //public abstract void showReward();
    //public
    public static List<DistractionClass> getDistractors() {
        return DistractionClass.activeDistractors;
    }
    //protected
    protected final float PROBABILITY;
    protected final float REWARD;
    public final float COG_DIFFICULTY; //FIXME make protected too (WeatherD)
    //private
    private final static ArrayList<DistractionClass> activeDistractors = new ArrayList<>();

    public void collide() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

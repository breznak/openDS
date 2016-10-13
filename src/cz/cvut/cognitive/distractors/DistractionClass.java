

package cz.cvut.cognitive.distractors;

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
    public abstract void update(float tpf, float probability);
    public abstract void remove();
    // lets discuss
    
}

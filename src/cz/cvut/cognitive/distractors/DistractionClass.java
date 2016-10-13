

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
    private boolean isDIstractor = false;
    private float disProbability = 0;

    public boolean isIsDIstractor() {
        return isDIstractor;
    }

    public void setIsDIstractor(boolean isDIstractor) {
        this.isDIstractor = isDIstractor;
    }

    public float getDisProbability() {
        return disProbability;
    }

    public void setDisProbability(float disProbability) {
        this.disProbability = disProbability;
    }
   
    public abstract void update(float tpf);
    public abstract void remove();
    
}

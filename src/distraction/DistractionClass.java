

package distraction;

import eu.opends.main.Simulator;

/**
 *
 * @author Johnny
 */
public abstract class DistractionClass {
    
    public abstract void initialize(Simulator sim);
    public abstract void update(float tpf, float propability);
    public abstract void remove();
    
}

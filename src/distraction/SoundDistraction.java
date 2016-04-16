package distraction;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import eu.opends.main.Simulator;

/**
 *
 * @author Johnny
 */
public class SoundDistraction extends DistractionClass{
    
    private AudioNode soundTest;
    private AssetManager manager;
    
    @Override
    public void initialize(Simulator sim) {
        this.manager = sim.getAssetManager();
        soundTest = new AudioNode(manager,"Sounds/World1.wav",false);
        soundTest.setPositional(false);
    }

    @Override
    public void update(float tpf, float propability) {
        if(DistractionSettings.isSound()){
            soundTest.play();
        }else{
            soundTest.stop();
        }
        
    }
    
    @Override
    public void remove()
    {
        soundTest.stop();
    }
}

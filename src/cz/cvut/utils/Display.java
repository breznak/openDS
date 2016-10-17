package cz.cvut.utils;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import eu.opends.main.Simulator;

/**
 *
 * @author mmm
 */
public class Display {

    public static void playSound(Simulator sim, String filePath, boolean loop) {
        System.out.println("SOUND!");
        AssetManager manager = sim.getAssetManager();
        AudioNode soundNode = new AudioNode(manager,filePath,false);
        soundNode.setLooping(loop);
        soundNode.setPositional(false);
        soundNode.playInstance(); // must be before attach below! 
        sim.getGuiNode().attachChild(sim.taskCogLoad.getRewardNode()); //TODO try setting soundNode
    }
}

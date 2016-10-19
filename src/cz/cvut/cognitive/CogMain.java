package cz.cvut.cognitive;

import com.jme3.font.BitmapText;
import com.jme3.scene.Node;
import cz.cvut.cognitive.distractors.DistractionClass;
import cz.cvut.cognitive.load.CognitiveFunction;
import eu.opends.main.SimulationDefaults;
import eu.opends.main.Simulator;
import java.io.File;

/**
 * Main class to rule them all, main class to bind them.
 *
 * @author mmm
 */
public class CogMain {

    private CognitiveFunction cogFunction;
    private BitmapText healthText;
    public static int playerHealth = 100;
    private Simulator sim;
    private Node rewardNode = new Node();

    public CogMain(Simulator sim) {
        if (!isActiveTask()) {
            return;
        } //not our task
        this.sim = sim;

        cogFunction = new CognitiveFunction(sim);
        CognitiveFunction.activeDistCount = 0;
        healthText = new BitmapText(sim.getAssetManager().loadFont("Interface" + File.separator + "Fonts" + File.separator + "Default.fnt"), false);
        healthText.setSize(sim.getAssetManager().loadFont("Interface" + File.separator + "Fonts" + File.separator + "Default.fnt").getCharSet().getRenderedSize());
        healthText.setText("Car Health: " + playerHealth);
        healthText.setLocalTranslation(1100, 250, 0); //TODO replace with showText
        sim.getGuiNode().attachChild(healthText);

    }

    public static boolean isActiveTask() {
        String currentTaskName = SimulationDefaults.drivingTaskFileName.substring(SimulationDefaults.drivingTaskFileName.lastIndexOf(File.separator) + 1);
        return currentTaskName.equalsIgnoreCase("A_DistractionTest.xml");
    }

    public void update(float tpf) {
        if(!isActiveTask()) { return; }
        cogFunction.update();

        for (DistractionClass d : DistractionClass.getDistractors()) {
            d.update(tpf);
        }
        
        updateHealth();
    }

    public void updateHealth() {
        this.sim.getGuiNode().detachChild(healthText);
        healthText.setText("Car Health: " + playerHealth);
        this.sim.getGuiNode().attachChild(healthText);
    }

    public Node getRewardNode() {
        return rewardNode;
    }
}

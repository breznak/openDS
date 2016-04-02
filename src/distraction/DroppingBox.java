package distraction;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import eu.opends.car.SteeringCar;
import eu.opends.main.Simulator;
/**
 *
 * @author Johnny Marek
 */
public class DroppingBox {
    
    private final Simulator sim;
    private final SteeringCar car;
    private final AssetManager manager;
    private final Geometry DropBox;
    private final Spatial dropingBox;
    private float time;
    private float m = 0;
    
    public DroppingBox(Simulator sim){
        
        this.sim = sim;
        this.car = sim.getCar();
        this.manager = sim.getAssetManager();
        
        
        Box box = new Box(1, 1, 1f);  
        DropBox = new Geometry("gula", box);
        Material portal_mat = new Material(manager,
        "Common/MatDefs/Misc/Unshaded.j3md");
        portal_mat.setColor("Color", ColorRGBA.Gray);
        DropBox.setMaterial(portal_mat);
        dropingBox = DropBox;

        
    
}
    public void updateDropingBox (float tpf, float propability){
        int n = (int)(Math.random() * 10) + 1;
        //System.out.println("Box clicked");
        time += tpf;
//        dropingBox.setLocalTranslation(car.getPosition().x, car.getPosition().y, car.getPosition().z-5);
//        sim.getSceneNode().attachChild(dropingBox);
        if (time > m+5){
            m = time;
            if (n < propability){
                System.out.println("Box placed");
                dropingBox.setLocalTranslation(car.getPosition().x, car.getPosition().y, car.getPosition().z+3);
                sim.getSceneNode().attachChild(dropingBox);
                    if (time > m+2){
                        m = time;
                        System.out.println("Box removed");
                        sim.getSceneNode().detachChild(dropingBox);
                    }
            }
        }
        
    }
}

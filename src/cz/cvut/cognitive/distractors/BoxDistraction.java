package cz.cvut.cognitive.distractors;

import com.jme3.asset.TextureKey;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.Matrix3f;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import cz.cvut.cognitive.CogMain;
import eu.opends.main.Simulator;
/**
 *
 * @author Johnny Marek
 * 
 * Class for Dropping-Box-type distraction. When this option is selected in
 * program, there is always (set) probability to spawn physical Box in front of
 * the car.
 * 
 */
public class BoxDistraction extends DistractionClass{
    
    private final Geometry DropBox;
    private final Spatial droppingBox;
    private final RigidBodyControl box_phy;
    private final float y_offSet;
    private boolean boxHit = false;
    private float Timer;
    private final int flatDamage = 10;
    //private BetterCharacterControl box_phy;
    public static float boxHitCount;

    /**
     *Constructor for DroppingBox
     *@param: sim, 
     * x - sizesOfBox(x)
     * y - sizesOfBox(y)
     * z - sizesOfBox(y)
     * mass - mass of the box.
     *          
     */
    public BoxDistraction(Simulator sim, float reward, float probability, float cogDifficulty, float x, float y, float z, float mass, String texturePath) {
        super(sim, reward, probability, cogDifficulty);
        //Creates an offset for placing box in world
        y_offSet = y;
        
        //initialization of box and its physics
        box_phy = new RigidBodyControl(mass);
        //box_phy = new BetterCharacterControl(1f, 4f, 50f);
        DropBox = new Geometry("DropBox", new Box(x, y, z));
        Material box_mat = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        //box_mat.setColor("Color", ColorRGBA.Red);
        try{
            TextureKey textureKey = new TextureKey(texturePath, false);
            Texture texture = sim.getAssetManager().loadTexture(textureKey);
            texture.setWrap(Texture.WrapMode.Repeat); //This should set the texture to repeat.
            box_mat.setTexture("ColorMap",texture);
			
	} catch (Exception e){
            e.printStackTrace();
            System.err.println("Error loading texture file " + texturePath);
	}
        DropBox.setMaterial(box_mat);
        DropBox.addControl(box_phy);
        droppingBox = DropBox;
    }

    /**
     * Update function: if preset probability of Box spawning is higher than
     * random generated number (1-100), box will spawn. 
     * Physical properties of box are set to default 0 at the beginning of every
     * new spawn to prevent it to remember properties from earlier call (box 
     * won't be flying around as soon as it spawns if it was hit right before
     * despawn in earlier call).
     */
    @Override
    public void spawn (float tpf){
        
                CollisionResults results = new CollisionResults();
                Ray ray = new Ray(camera.getLocation(), camera.getDirection());
                sim.getSceneNode().collideWith(ray, results);
        
                if (results.size() <= 0 || results.getClosestCollision().getDistance() > 30) { //FIXME document constants
                    //generates offset for box spawn in front of the car
                    int distance_offset = (int)(Math.random() * 5) + 1; //FIXME doc constants

                    //add node to scene
                    sim.getSceneNode().attachChild(droppingBox);
                    bulletAppState.getPhysicsSpace().add(box_phy); 
                    box_phy.setLinearVelocity(new Vector3f(0,0,0));
                    box_phy.setAngularVelocity(new Vector3f(0,0,0));
                    box_phy.setPhysicsRotation(Matrix3f.IDENTITY);

                    /*creates spawn position of car - always in front of the car 
                    * TODO: fix for all camera modes (works best for first camera position)
                    */
                    Vector3f carPosition = new Vector3f(car.getPosition().x,car.getPosition().y+y_offSet+1.1f,car.getPosition().z );
                    Vector3f carHeading = new Vector3f(camera.getDirection());
                    float distance = 15+distance_offset;
                    Vector3f spawn = new Vector3f(carPosition.add(carHeading.mult(distance)));

                    //box_phy.setPhysicsLocation(new Vector3f (car.getPosition().x+(float)Math.cos((double)degree),car.getPosition().y+y_offSet+0.1f,car.getPosition().z+(float)Math.sin((double)degree)-(5+z_offset)));   

                    box_phy.setPhysicsLocation(spawn);
                    //box_phy.setLinearVelocity(new Vector3f (1,0,0));

                    //droppingBox.setLocalTranslation(spawn);

                    /*box_phy.warp(spawn);
                    box_phy.setWalkDirection(new Vector3f(2,0,0));*/
                 }
    }
    
    /**
     * Remove function: if distractor was spawned before this call, it will be
     * removed from the scene.
     */
    @Override
    public void remove_local (){
            sim.getSceneNode().detachChild(droppingBox);
            bulletAppState.getPhysicsSpace().remove(box_phy); 
    }

    @Override
    public void collision(float tpf){
            if(!boxHit){
                CollisionResults results = new CollisionResults();
                car.getCarNode().collideWith(droppingBox.getWorldBound(), results);
                if(results.size()>0 && results.getClosestCollision().getDistance() <= 1){
                    CogMain.playerHealth -= (flatDamage + (int)(car.getCurrentSpeedKmhRounded()*0.5));
                    sim.taskCogLoad.updateHealth();
                    
                    boxHitCount++; 
                    boxHit = true;
                }
            } else {
                CollisionResults results = new CollisionResults();
                car.getCarNode().collideWith(droppingBox.getWorldBound(), results);
                if(results.size()<=0){
                    Timer = Timer + tpf;
                    if (Timer > 2) {
                    Timer = 0;
                    boxHit=false;
                    }
                } else Timer = 0;
            }
    }
    
}

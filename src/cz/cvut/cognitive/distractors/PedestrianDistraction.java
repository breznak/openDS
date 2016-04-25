package cz.cvut.cognitive.distractors;

import com.jme3.animation.LoopMode;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.cinematic.MotionPathListener;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import eu.opends.car.SteeringCar;
import eu.opends.main.Simulator;

/**
 *
 * @author Johnny
 * 
 * Class for Pedestrian-type distraction. When this option is selected in
 * options, there is always (set) probability to spawn pedestrian box next to
 * the car.
 * 
 * TODO: add ghostcontrol with physics.
 * 
 */
public class PedestrianDistraction extends DistractionClass{
    public static Node pedestrianNode = new Node();
    private final Geometry boxGeometry;
    private final MotionPath path;
    private MotionEvent motionControl;
    private final Simulator sim;
    private final SteeringCar car;
    private final AssetManager manager;
    private final BulletAppState bulletAppState;
    //private final RigidBodyControl pedestrianPhysics;
    public static CollisionResults results;
    private boolean pedestrianOn = false;
     

    /**
     *Constructor for PedestrianDistraction
     *@param sim - simulator.
     */
      public PedestrianDistraction(Simulator sim) {
        this.sim = sim;
        this.car = sim.getCar();
        this.manager = sim.getAssetManager();
        this.bulletAppState = sim.getBulletAppState();
        results = new CollisionResults();

        //initialize box node
        Material mat = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        boxGeometry = new Geometry("DropBox", new Box(1, 1, 1));
        boxGeometry.setMaterial(mat);
        pedestrianNode.attachChild(boxGeometry);
        
/*      
        //initialize physics
        pedestrianPhysics = new RigidBodyControl(2f);
        pedestrianNode.addControl(pedestrianPhysics);
        pedestrianPhysics.setKinematic(false);
*/        
        //initialize path of pedestrian        
        path = new MotionPath();
        path.addListener(new MotionPathListener() {
            
            @Override
            public void onWayPointReach(MotionEvent me, int i) {

            }
        });
        
        
      }

    /**
     * Update function: if preset probability of Pedestrian is higher than
     * random generated number (1-100), pedestrian will spawn. 
     * Always generates new motionpath of preset positions (TODO: called posit)
     * 
     * TODO: like box add position always next to car / in front (random?)
     * 
     */
    @Override
    public void update(float tpf, float propability) {
        int n = (int)(Math.random() * 100) + 1;
        if (n < propability){
            System.out.println("Pedestrian placed"); 
            sim.getSceneNode().attachChild(pedestrianNode);
            pedestrianNode.setLocalTranslation(new Vector3f (car.getPosition().x-10,car.getPosition().y+1.1f,car.getPosition().z-10));
/*            
            bulletAppState.getPhysicsSpace().addAll(pedestrianNode);
            bulletAppState.getPhysicsSpace().add(pedestrianPhysics); 
            pedestrianPhysics.setLinearVelocity(new Vector3f(0,0,0));
            pedestrianPhysics.setAngularVelocity(new Vector3f(0,0,0));
            pedestrianPhysics.setPhysicsRotation(Matrix3f.IDENTITY);
            pedestrianPhysics.setPhysicsLocation(new Vector3f (car.getPosition().x,car.getPosition().y+1.1f,car.getPosition().z-10)); 
*/
            Vector3f carPosition = new Vector3f(car.getPosition().x-10,car.getPosition().y+1.1f,car.getPosition().z-10);
            path.addWayPoint(carPosition);
            path.addWayPoint(new Vector3f(carPosition.x-5,carPosition.y,carPosition.z-15));
            path.addWayPoint(new Vector3f(carPosition.x-5,carPosition.y,carPosition.z-5));
            path.addWayPoint(carPosition);
            motionControl = new MotionEvent(pedestrianNode,path);
            motionControl.setSpeed(2f); 
            motionControl.play();
            motionControl.setLoopMode(LoopMode.Loop);
            pedestrianOn = true;
            DistractionSettings.setDistRunning(true);
        } 
    }

    /**
     * Remove function: if distractor was spawned before this call, it will be
     * removed from the scene.
     */
    @Override
    public void remove() {
        if (pedestrianOn){
            motionControl.stop();
            path.clearWayPoints();
            sim.getSceneNode().detachChild(pedestrianNode);
/*
            bulletAppState.getPhysicsSpace().remove(pedestrianPhysics); 
            bulletAppState.getPhysicsSpace().removeAll(pedestrianNode);
*/
            pedestrianOn = false;
        } 
    }
     
}

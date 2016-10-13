
package cz.cvut.cognitive.distractors;

import cz.cvut.cognitive.load.CognitiveFunction;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import eu.opends.car.SteeringCar;
import eu.opends.main.Simulator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Johnny
 */
public class CollectObjectDistraction extends DistractionClass{
    
    private final Simulator sim;
    private final SteeringCar car;
    private final AssetManager manager;
    private final Geometry greenGeo;
    private final Spatial greenSpatial;
    private final Geometry redGeo;
    private final Spatial redSpatial;
    private final float y_offSet;
    public static boolean collectOn = false;
    public GhostControl greenGhost;
    public GhostControl redGhost;
    private Camera camera;
    private final int flatDamage = 5;
    private BitmapText rewardText;
    private BitmapText greenText;
    private BitmapText redText;
    private boolean greenCorrect;
    private boolean redCorrect;
    public static int [] correctScore = new int [2];
    private boolean collected = false;
    public float COG_SCORE;
    private String outputFolder;
    public Vector3f spawn;
    public float distanceLeft;
    public float distanceRight;
    
    private final AudioNode rewardSoundNode;
    
    CollectObjectDistraction(Simulator sim, String texturePathGreen, String texturePathRed){
        COG_SCORE = 3;
        
        this.sim = sim;
        this.car = sim.getCar();
        this.manager = sim.getAssetManager();
        this.camera = sim.getCamera();
        
        rewardSoundNode = new AudioNode(manager,"Sounds/FF_Victory.wav",false);
        rewardSoundNode.setLooping(false);
        rewardSoundNode.setPositional(false);
        
        rewardText = new BitmapText(manager.loadFont("Interface/Fonts/Default.fnt"), false);
        rewardText.setSize(40);
        rewardText.setColor(ColorRGBA.Black);
        rewardText.setText("Well done! You did it!");
        rewardText.setLocalTranslation(400, 700, 0);
        sim.getRewardNode().attachChild(rewardText);
        
        greenText = new BitmapText(manager.loadFont("Interface"+File.separator+"Fonts"+File.separator+"Default.fnt"), false);
        greenText.setSize(40);
        greenText.setColor(ColorRGBA.Green);
        greenText.setText("Pick up the GREEN ball");
        greenText.setLocalTranslation(500, 700, 0);
  
        redText = new BitmapText(manager.loadFont("Interface"+File.separator+"Fonts"+File.separator+"Default.fnt"), false);
        redText.setSize(40);
        redText.setColor(ColorRGBA.Red);
        redText.setText("Pick up the RED ball");
        redText.setLocalTranslation(500, 700, 0);
        
        //Creates an offset for placing objects in world
        y_offSet = 0.5f;
        
        //initialization of objects
        
        greenGeo = new Geometry("GreenSphere", new Sphere (15, 15, 0.4f));
        Material collect_mat = new Material(manager,
        "Common"+File.separator+"MatDefs"+File.separator+"Misc"+File.separator+"Unshaded.j3md");
        try{
            TextureKey textureKey = new TextureKey(texturePathGreen, false);
            Texture texture = sim.getAssetManager().loadTexture(textureKey);
            collect_mat.setTexture("ColorMap",texture);
			
	} catch (Exception e){
            e.printStackTrace();
            System.err.println("Error loading texture file " + texturePathGreen);
	}
        CollisionShape greenShape =
            CollisionShapeFactory.createMeshShape(greenGeo);
        greenGeo.setMaterial(collect_mat);
        greenGhost = new GhostControl(greenShape);
        greenSpatial = greenGeo;
        greenSpatial.addControl(greenGhost);
        
        redGeo = new Geometry("RedSphere", new Sphere (15, 15, 0.4f));
        Material avoid_mat = new Material(manager,
        "Common"+File.separator+"MatDefs"+File.separator+"Misc"+File.separator+"Unshaded.j3md");
        try{
            TextureKey textureKey = new TextureKey(texturePathRed, false);
            Texture texture = sim.getAssetManager().loadTexture(textureKey);
            avoid_mat.setTexture("ColorMap",texture);
			
	} catch (Exception e){
            e.printStackTrace();
            System.err.println("Error loading texture file " + texturePathRed);
	}
        CollisionShape redShape =
            CollisionShapeFactory.createMeshShape(redGeo);
        redGeo.setMaterial(avoid_mat);
        redGhost = new GhostControl(redShape);
        redSpatial = redGeo;
        redSpatial.addControl(redGhost);
    }

    
    
    @Override
    public void update(float tpf) {
            CollisionResults results = new CollisionResults();
            Ray ray = new Ray(camera.getLocation(), camera.getDirection());
            sim.getSceneNode().collideWith(ray, results);
        
            if (results.size() <= 0 || results.getClosestCollision().getDistance() > 31) {
                

                Vector3f carPosition = new Vector3f(car.getPosition().x,car.getPosition().y+1.3f,car.getPosition().z );
                Vector3f carHeading = new Vector3f(camera.getDirection());
                float distance = 22;
                spawn = new Vector3f(carPosition.add(carHeading.mult(distance)));
               // Vector3f spawnUp = new Vector3f(spawn.add(carHeading.mult(distance + 5)));
                
                CollisionResults results2 = new CollisionResults();
                Ray ray2 = new Ray(spawn, camera.getLeft());
                sim.getSceneNode().collideWith(ray2, results2);
                CollisionResults results3 = new CollisionResults();
                Ray ray3 = new Ray(spawn, camera.getLeft().negate());
                sim.getSceneNode().collideWith(ray3, results3);
                if (results2.size() > 0 && results2.getClosestCollision().getDistance() < 8) {
                    if (results3.size() > 0 && results3.getClosestCollision().getDistance() < 8) {
                        distanceLeft = results2.getClosestCollision().getDistance() - 3.2f;
                        distanceRight = results3.getClosestCollision().getDistance() - 3.2f;
                        createObjects();
                    }
                }   
            }
             
        
    }
    
    private void createObjects(){
        //add node to scene
            sim.getSceneNode().attachChild(greenSpatial);
            sim.getSceneNode().attachChild(redSpatial);
//            sim.getBulletAppState().getPhysicsSpace().add(greenGhost);
//            sim.getBulletAppState().getPhysicsSpace().add(redGhost);

            /*creates spawn position of car - always in front of the car 
            * TODO: fix for all camera modes (works best for first camera position)
            */
            Vector3f spawnLeft = new Vector3f(spawn.add(camera.getLeft().mult(distanceLeft)));
            Vector3f spawnRight = new Vector3f(spawn.add(camera.getLeft().negate().mult(distanceRight)));;

            int color_pattern = (int)(Math.random() * 2) + 1;
            if (color_pattern > 1){
                greenSpatial.setLocalTranslation(spawnLeft);
                redSpatial.setLocalTranslation(spawnRight);
            } else {
                greenSpatial.setLocalTranslation(spawnRight);
                redSpatial.setLocalTranslation(spawnLeft);
            }

            color_pattern = (int)(Math.random() * 2) + 1;
            if (color_pattern > 1){
                sim.getGuiNode().attachChild(greenText);
                greenCorrect = true;
                redCorrect = false;
            } else {
                sim.getGuiNode().attachChild(redText);
                redCorrect = true;
                greenCorrect = false;
            }
            
            collected = false;

            collectOn = true;
            CognitiveFunction.distScore += COG_SCORE;
            CognitiveFunction.activeDistCount++;
            CognitiveFunction.activeDistNames[1] = 1;
            DistractionSettings.distRunning++;
    }

    @Override
    public void remove() {
        if(collectOn){
            if(!collected) correctScore[1]++;
            outputFolder = CognitiveFunction.saveHere +File.separator+"Collectible.txt";
            try (BufferedWriter writer2 = new BufferedWriter(new FileWriter(outputFolder, true))) {
            writer2.write(correctScore[0] + " " + correctScore[1]);
            writer2.newLine();
           
            } catch (IOException e) {
                e.printStackTrace();
            } 
            CognitiveFunction.activeDistCount--;
            CognitiveFunction.activeDistNames[1] = 0;
            sim.getSceneNode().detachChild(greenSpatial);
            sim.getSceneNode().detachChild(redSpatial);

//            sim.getBulletAppState().getPhysicsSpace().remove(greenGhost);
//            sim.getBulletAppState().getPhysicsSpace().remove(redGhost);
            
            sim.getGuiNode().detachChild(greenText);
            sim.getGuiNode().detachChild(redText);
            CognitiveFunction.distScore -= COG_SCORE;
            collectOn = false;
            Simulator.Timer = 0;
            DistractionSettings.distRunning--;
        }
    }
    
    public void collision(float tpf){
        if(collectOn){
            CollisionResults results_1 = new CollisionResults();
            car.getCarNode().collideWith(greenSpatial.getWorldBound(), results_1);
            if(results_1.size()>0 && results_1.getClosestCollision().getDistance() <= 1){
                if(greenCorrect) {
                    correctScore[0]++;
                    rewardPlayer();
                } 
                else { 
                    correctScore[1]++;
                    Simulator.playerHealth = Simulator.playerHealth - (flatDamage);
                    sim.updateHealth();
                }
                collected = true;
                remove();
            }
            CollisionResults results_2 = new CollisionResults();
            car.getCarNode().collideWith(redSpatial.getWorldBound(), results_2);
            if(results_2.size()>0 && results_2.getClosestCollision().getDistance() <= 1){
                if(redCorrect) {
                    correctScore[0]++;
                    rewardPlayer();
                } 
                else { 
                    correctScore[1]++;
                    Simulator.playerHealth = Simulator.playerHealth - (flatDamage);
                    sim.updateHealth();
                }
                collected = true;
                remove();
                
            }
            
        }
        
    }
    
    private void rewardPlayer(){
        rewardSoundNode.playInstance();
        sim.getGuiNode().attachChild(sim.getRewardNode());
    }
    
}

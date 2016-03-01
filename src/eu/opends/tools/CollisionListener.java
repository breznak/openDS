package eu.opends.tools;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;

public class CollisionListener implements PhysicsCollisionListener
{
	@Override
	public void collision(PhysicsCollisionEvent event) 
	{
		/*
		System.err.println(event.getNodeA().getName() + " <--> " + event.getNodeB().getName());	
		
        if ("Box".equals(event.getNodeA().getName()) || "Box".equals(event.getNodeB().getName())) {
            if ("bullet".equals(event.getNodeA().getName()) || "bullet".equals(event.getNodeB().getName())) {
                System.err.println("You hit the box!");
            }
        }
        */
	}
}

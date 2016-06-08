/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2016 Rafael Math
*
*  OpenDS is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*
*  OpenDS is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  You should have received a copy of the GNU General Public License
*  along with OpenDS. If not, see <http://www.gnu.org/licenses/>.
*/

package eu.opends.effects;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterBoxShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

/**
 * 
 * @author Rafael Math
 */
public class RainParticleEmitter extends ParticleEmitter
{
	private AssetManager assetManager;
	private float percentage;
   
   
	public RainParticleEmitter(AssetManager assetManager, float percentage)
	{
		super("Emitter", ParticleMesh.Type.Triangle, 10000 /*(int) (50 * percentage)*/);
		this.assetManager = assetManager;
		this.percentage = percentage;
		setupMaterial();
	}

	
	private void setupMaterial() 
	{
		Material mat_red = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
		mat_red.setTexture("Texture", assetManager.loadTexture("Effects/Weather/rain.png"));
		this.setMaterial(mat_red);
		this.setParticlesPerSec(40 * percentage);
		this.setImagesX(1);
		this.setImagesY(1); // 1x1 texture animation
		this.setStartColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 0.99f));
		this.setEndColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 0.99f));
		this.setStartSize(0.10f);
		this.setEndSize(0.10f);
		this.setGravity(0,1,0);
		this.setLowLife(1f);
		this.setHighLife(2f);
		this.getParticleInfluencer().setInitialVelocity(new Vector3f(0, -6, 0));
		this.getParticleInfluencer().setVelocityVariation(0f);
		this.setShape(new EmitterBoxShape(new Vector3f(-20f,3.0f,-20f),new Vector3f(20f,4.5f,20f)));
	}
	
	
	public void setPercentage(float percentage)
	{
		//super.setNumParticles((int) (50 * percentage));
		this.setParticlesPerSec(40 * percentage);
	}
}

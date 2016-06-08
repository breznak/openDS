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

package eu.opends.audio;

import java.util.Map;

import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.audio.AudioNode;

import eu.opends.main.Simulator;

/**
 * 
 * @author Rafael Math
 */
public class AudioFiles 
{
	private static Map<String,AudioNode> audioNodeList;
	private static AssetManager assetManager;
	
	
	public static void addAudioFile(String audioID, String path,
			boolean isLooping, float volume, float pitch)
	{	
		AudioNode audioNode = new AudioNode(assetManager, path);
		audioNode.setLooping(isLooping);
		audioNode.setVolume(volume);
		audioNode.setPitch(pitch);
		audioNodeList.put(audioID,audioNode);	
	}
	
	
	public static Map<String, AudioNode> load(Simulator sim) 
	{
		assetManager = sim.getAssetManager();
		assetManager.registerLocator("assets", FileLocator.class);
		
		audioNodeList = Simulator.getDrivingTask().getSceneLoader().getAudioNodes();
		
		addAudioFile("engineStart", "Sounds/Effects/start2.wav", false, 0.25f, 1f);
		addAudioFile("engineIdle", "Sounds/Effects/idle2.wav", true, 0.25f, 1f);
		addAudioFile("engineStop", "Sounds/Effects/stop2.wav", false, 0.25f, 1f);
		
		addAudioFile("collision",  "Sounds/Effects/collision.wav", false, 0.5f, 1f);
		addAudioFile("crash", "Sounds/Effects/crash.wav", false, 1f, 1f);
		addAudioFile("horn", "Sounds/Effects/horn.wav", true, 0.5f, 1f);
		addAudioFile("potHole", "Sounds/Effects/wheelTouch.wav", false, 0.3f, 1f);
		
		addAudioFile("turnSignal", "Sounds/Effects/turnSignal.wav", false, 0.25f, 1.0f);
		
		return audioNodeList;
	}
	
}

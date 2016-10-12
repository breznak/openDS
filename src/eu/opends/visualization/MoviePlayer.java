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

package eu.opends.visualization;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.TreeMap;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3x.jfx.media.TextureMovie;
import com.jme3x.jfx.media.TextureMovie.LetterboxMode;

import eu.opends.main.Simulator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;


public class MoviePlayer 
{
	private Simulator sim;
	private TreeMap<String, MovieData> moviesMap = new TreeMap<String, MovieData>();
	private String play = null;
	private String playedLast = null;
	private boolean stop = false;
	private MediaPlayer	mediaPlayer = null;
	private TextureMovie textureMovie;
	private Geometry screen;
	
	
	public MoviePlayer(Simulator sim)
	{
		this.sim = sim;
		moviesMap = Simulator.getDrivingTask().getSceneLoader().getMoviesMap();
	}

	
	// play and stop movies only by update() method to avoid update errors of the scene graph
	public void update(float tpf)
	{
		if(play != null)
		{
			playSynchronized(play);
			playedLast = play;
			play = null;
		}
		
		if(stop)
		{
			stopSynchronized();
			stop = false;
		}
	}

	
	// align play to update() method
	public void play(String id)
	{
		play = id;
	}

	
	// align play to update() method
	public void playNext()
	{
		if(playedLast != null)
		{
			play = moviesMap.higherKey(playedLast);
			if(play == null)
				play = moviesMap.firstKey(); 
		}
		else
			play = moviesMap.firstKey();
	}
	
	
	private void playSynchronized(String id)
	{
		try {
			
			MovieData movie = moviesMap.get(id);
			if(movie != null)
			{
				// stop movie if other movie is still running
				stopSynchronized();
				
				int width = movie.getWidth();
				int height = movie.getHeight();
				float zoomingFactor = movie.getZoomingFactor();
				
				final Media media = new Media(new File(movie.getPath()).toURI().toASCIIString());
				media.errorProperty().addListener(new ChangeListener<MediaException>() 
				{
					@Override
					public void changed(final ObservableValue<? extends MediaException> observable, final MediaException oldValue, final MediaException newValue) 
					{
						newValue.printStackTrace();
					}
				});
				
				mediaPlayer = new MediaPlayer(media);
				mediaPlayer.play();
		
				textureMovie = new TextureMovie(sim, mediaPlayer, LetterboxMode.VALID_SQUARE);
				textureMovie.setLetterboxColor(ColorRGBA.Black);
		
				// set up quad geometry as screen (quad is larger than actual movie)
				float quadWidth = width * zoomingFactor;
				float quadHeight = height * zoomingFactor;
				screen = new Geometry("Screen", new Quad(quadWidth, quadHeight));
				final Material movieShaderMaterial = new Material(sim.getAssetManager(), "com/jme3x/jfx/media/MovieShader.j3md");
				movieShaderMaterial.setTexture("ColorMap", textureMovie.getTexture());
				movieShaderMaterial.setInt("SwizzleMode", textureMovie.useShaderSwizzle());
				screen.setMaterial(movieShaderMaterial);
				
				// set geometry "screen" to the center of the rendering window
				int offsetX = (int) ((quadWidth-width)/2.0f);
				int offsetY = (int) ((quadHeight-height)/2.0f);
				screen.setLocalTranslation(new Vector3f(-offsetX, -offsetY, 1));
				sim.getGuiNode().attachChild(screen);
				
				String newLine = System.getProperty("line.separator");
				String time = new SimpleDateFormat("HH:mm:ss.SSS").format(System.currentTimeMillis());
				Simulator.getDrivingTaskLogger().reportText(time + " --> Start movie '" + id + "'" + newLine);
			}
			else
				System.err.println("Movie '" + id + "' could not be found");
		
		} catch (MediaException e) {
			e.printStackTrace();
		}
	}

	
	// align stop to update() method
	public void stop() 
	{
		stop = true;
	}
	
	
	private void stopSynchronized()
	{
		if(mediaPlayer != null)
		{
			mediaPlayer.stop();
			sim.getGuiNode().detachChild(screen);
			
			String newLine = System.getProperty("line.separator");
			String time = new SimpleDateFormat("HH:mm:ss.SSS").format(System.currentTimeMillis());
			Simulator.getDrivingTaskLogger().reportText(time + " --> Stop movie" + newLine);
		}
	}
}

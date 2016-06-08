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


package eu.opends.traffic;

import com.jme3.animation.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;

import java.util.*;

/**
 * Animates multi mesh models with multiple AnimControllers.
 *
 * @author Tommi S.E. Laukkanen
 */
public class AnimationController 
{
    /**
     * Character animation controllers.
     */
    final Map<String, AnimControl> animControls = new HashMap<>();
    /**
     * Character animation channels.
     */
    final Map<String, AnimChannel> animChannels = new HashMap<>();
    /**
     * The character animator listener.
     */
    private AnimationListener animationListener;
    /**
     * Name of the last animation played.
     */
    private String animationName = null;
    /**
     * The animation speed multiplier.
     */
    private float speedMultiplier = 0f;
    /**
     * The loop count.
     */
    private int loopCount = 0;
    /**
     * The animation max time.
     */
    private float animationMaxTime = 0f;
    /**
     * The animation time.
     */
    private float animationTime = 0f;

    
    /**
     * Constructor which gets animation controls from character spatial recursively
     * and create animation channels.
     * @param character the character spatial
     */
    public AnimationController(final Spatial character) 
    {
        SceneGraphVisitorAdapter visitor = new SceneGraphVisitorAdapter()
        {
            @Override
            public void visit(final Geometry geometry)
            {
                super.visit(geometry);
                checkForAnimControl(geometry);
            }

            @Override
            public void visit(final Node node)
            {
                super.visit(node);
                checkForAnimControl(node);
            }

            /**
             * Checks whether spatial has animation control and constructs animation channel
             * of it has.
             * @param spatial the sptial
             */
            private void checkForAnimControl(final Spatial spatial) 
            {
                AnimControl animControl = spatial.getControl(AnimControl.class);
                if (animControl == null)
                    return;

                final AnimChannel animChannel = animControl.createChannel();
                animControls.put(spatial.getName(), animControl);
                animChannels.put(spatial.getName(), animChannel);
            }
        };
        character.depthFirstTraversal(visitor);
    }

    
    /**
     * Plays animation.
     *
     * @param animationName the animation
     * @param speedMultiplier the speed multiplier (1 = animation native speed, 2 = double speed...)
     * @param blendTime the blend time in seconds
     * @param loopCount the loop count or 0 for infinite looping
     */
    public void animate(final String animationName, final float speedMultiplier, final float blendTime, final int loopCount) 
    {
        this.loopCount = loopCount - 1;
        this.animationName = animationName;
        this.speedMultiplier = speedMultiplier;
        this.animationTime = 0;
        this.animationMaxTime = 0;
        for (final String spatialName : animChannels.keySet())
        {
            final AnimControl animControl = animControls.get(spatialName);
            final Animation animation = animControl.getAnim(animationName);
            if (animation != null)
            {
                final AnimChannel animChannel = animChannels.get(spatialName);
                if (blendTime != 0)
                    animChannel.setAnim(animationName, blendTime);
                else
                    animChannel.setAnim(animationName);

                animChannel.setLoopMode(LoopMode.Loop);
                animChannel.setSpeed(speedMultiplier);
                this. animationMaxTime = animChannel.getAnimMaxTime();
            }
        }
    }

    
    /**
     * Gets list of spatial names with animations.
     * @return list of spatial names
     */
    public Collection<String> getSpatialNamesWithAnimations()
    {
        return animControls.keySet();
    }

    
    /**
     * Gets animation control with spatial name.
     * @param spatialName the spatial name
     * @return the animation control or null
     */
    public AnimControl getAnimControl(final String spatialName)
    {
        return animControls.get(spatialName);
    }
    

    /**
     * Gets last animation played.
     * @return the last animation name or null
     */
    public String getAnimationName()
    {
        return animationName;
    }

    
    /**
     * Updates animation manually.
     * @param tpf time per frame
     */
    public void update(final float tpf)
    {
        if (animationTime > 0f && animationTime > animationMaxTime)
        {
            if (loopCount == 0)
            {
                speedMultiplier = 0f;
                animationListener.onAnimCycleDone(animationName);
            }
            else
                loopCount--;

            animationTime = 0f;
        }

        animationTime = animationTime + speedMultiplier * tpf;

        for (final AnimChannel channel : animChannels.values())
        {
            channel.setSpeed(0f);
            channel.setTime(animationTime);
        }
    }

    
    /**
     * Sets character animator listener.
     * @param animationListener the character animator listener
     */
    public void setAnimationListener(final AnimationListener animationListener)
    {
        this.animationListener = animationListener;
    }

}

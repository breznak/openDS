/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2015 Rafael Math
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


package eu.opends.hmi;

import java.util.HashMap;

import com.jme3.math.Vector3f;

import eu.opends.car.Car;

/**
 * @author Rafael Math
 *
 */
public class SituationEvaluationPresentationModel extends PresentationModel 
{
	private int appId;
	private int currentPrioritisation;
	private int questionType;
	private String questionText;
	private long end;
	
	
	private static final int F_4342_SITUATION_EVALUATION = 1;
	
	
	public SituationEvaluationPresentationModel(Car car, int questionType, String questionText)
	{
		this.car = car;
		this.targetPosition = new Vector3f(0,0,0);
			
		appId = F_4342_SITUATION_EVALUATION;
		currentPrioritisation = 0;
		this.questionType = questionType;
		this.questionText = questionText;
	}
	
	
	@Override
	public long createPresentation() 
	{	
		// show presentation task only once for at most 60 seconds
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		
		long start = System.currentTimeMillis() + 5500;
		end = start + 10000;
		
		// parameters according to "presentationModel.xml"
		parameters.put("timeStart", start);
		parameters.put("timeEnd", end);
		parameters.put("currentPrioritisation", currentPrioritisation);
	
		// parameters according to "F4342_model.xml"
		parameters.put("questionType", questionType);
		parameters.put("questionText", questionText);

		// send parameters to HMI bundle
		// TODO return controller.createPresentationTask(0, appId, parameters);
		return 0;
	}


	@Override
	public void updatePresentation(long presentationID) 
	{
		// send question only once --> no update at all
		/*		
		long now = System.currentTimeMillis();

			end = now + 3000;
			
			HashMap<String, Object> parameters = new HashMap<String, Object>();
	
			// parameters according to "presentationModel.xml" 
			parameters.put("timeEnd", end);

			// send parameters to HMI bundle
			// TODO controller.update(presentationID, parameters);
		*/
	}
	
	
	@Override
	public boolean hasChangedParameter() 
	{
		// suppresses any update
		return false;
		
		/*
		long now = System.currentTimeMillis();
		return ((end - (long) now) < 2000);
		*/
	}
	
	
	@Override
	public String generateMessage() 
	{
		return "Situation Evaluation - Question: " + questionText;
	}

}

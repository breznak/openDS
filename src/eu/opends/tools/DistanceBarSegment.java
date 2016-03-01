package eu.opends.tools;

public class DistanceBarSegment 
{
	private String name;
	private SegmentType type;
	private float minDistance;
	private float maxDistance;
	
	
	public enum SegmentType 
	{
		RED ("Textures/DistanceBar/red.png"), 
		GREEN ("Textures/DistanceBar/green.png"), 
		REDTOGREEN ("Textures/DistanceBar/red2green.png"), 
		GREENTORED ("Textures/DistanceBar/green2red.png");
		
		
		private String path;

		private SegmentType(String path)
		{
			this.path = path;
		}
		
		public String getPath()
		{
			return path;
		}
	}

	
	public DistanceBarSegment(String name, SegmentType type, float minDistance, float maxDistance) 
	{
		this.name = name;
		this.type = type;
		this.minDistance = minDistance;
		this.maxDistance = maxDistance;
	}


	public String getName() 
	{
		return name;
	}	
	
	
	public String getPath()
	{
		return type.getPath();
	}	
	
	
	public float getMinimumDistance() 
	{
		return minDistance;
	}
	
	
	public float getMaximumDistance() 
	{
		return maxDistance;
	}	
}

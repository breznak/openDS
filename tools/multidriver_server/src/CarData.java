import java.util.ArrayList;


public class CarData 
{
	private String modelPath;
	private String driverName;
	private double posX;
	private double posY;
	private double posZ;
	private Float rotW;
	private Float rotX;
	private Float rotY;
	private Float rotZ;
	private Float heading;
	private ArrayList<String> knownClients;
	private ArrayList<String> sentToClients;
	private float wheelSteering;
	private float wheelPos;
	
	
	public CarData()
	{
		this("","");
	}


	public CarData(String modelPath, String driverName) 
	{
		this.modelPath = modelPath;
		this.driverName = driverName;
		posX = 0;
		posY = 0;
		posZ = 0;
		rotW = 0f;
		rotX = 0f;
		rotY = 0f;
		rotZ = 0f;
		heading = 0f;
		knownClients = new ArrayList<String> ();
		sentToClients = new ArrayList<String> ();
		wheelSteering = 0;
		wheelPos = 0;
	}


	public String getModelPath() {
		return modelPath;
	}


	public void setModelPath(String modelPath) {
		this.modelPath = modelPath;
	}


	public String getDriverName() {
		return driverName;
	}


	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}


	public double getPosX() {
		return posX;
	}


	public void setPosX(double posX) {
		this.posX = posX;
	}


	public double getPosY() {
		return posY;
	}


	public void setPosY(double posY) {
		this.posY = posY;
	}


	public double getPosZ() {
		return posZ;
	}


	public void setPosZ(double posZ) {
		this.posZ = posZ;
	}


	public Float getRotW() {
		return rotW;
	}


	public void setRotW(Float rotW) {
		this.rotW = rotW;
	}


	public Float getRotX() {
		return rotX;
	}


	public void setRotX(Float rotX) {
		this.rotX = rotX;
	}


	public Float getRotY() {
		return rotY;
	}


	public void setRotY(Float rotY) {
		this.rotY = rotY;
	}


	public Float getRotZ() {
		return rotZ;
	}


	public void setRotZ(Float rotZ) {
		this.rotZ = rotZ;
	}
	
	
	public Float getHeading() {
		return heading;
	}
	
	
	public void setHeading(Float heading) {
		this.heading = heading;
	}
	
	
	public ArrayList<String> getKnownClients() {
		return knownClients;
	}


	public void setKnownClients(ArrayList<String> knownClients) {
		this.knownClients = knownClients;
	}


	public boolean isUpdateSent(String threadID) {
		return sentToClients.contains(threadID);
	}


	public void setUpdateSent(String threadID) {
		this.sentToClients.add(threadID);		
	}
	
	
	public void setUpdateNotSent() {
		this.sentToClients.clear();		
	}


	public float getWheelSteering() {
		return wheelSteering;
	}	
	
	
	public void setWheelSteering(float wheelSteering) {
		this.wheelSteering = wheelSteering;		
	}
	
	
	public float getWheelPos() {
		return wheelPos;
	}	
	
	
	public void setWheelPos(float wheelPos) {
		this.wheelPos = wheelPos;		
	}


}

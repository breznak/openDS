package eu.opends.settingsController;

public class UpdateSender extends Thread {
	
	private APIData  data;
	ConnectionHandler connectionHandler;
	
	public UpdateSender(APIData data, ConnectionHandler connectionHandler){
		this.data = data;
		this.connectionHandler = connectionHandler;
	}
	
	public void run(){
		while(!isInterrupted()){
			
			String response = "<Message><Event Name=\"SubscribedValues\">\n" + data.getAllSubscribedValues(false) + "\n</Event></Message>\n";
			connectionHandler.sendResponse(response);
						
			try {
				Thread.sleep(connectionHandler.getUpdateInterval());
			} catch (InterruptedException e) {
				this.interrupt();//e.printStackTrace();
			}
		}
	}

}

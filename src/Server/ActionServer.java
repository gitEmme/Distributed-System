package Server;

import org.cads.ev3.middleware.CaDSEV3RobotHAL;
import org.cads.ev3.middleware.CaDSEV3RobotType;
import org.cads.ev3.middleware.hal.ICaDSEV3RobotFeedBackListener;
import org.cads.ev3.middleware.hal.ICaDSEV3RobotStatusListener;
import org.json.simple.JSONObject;

import Middleware.ServerMiddleware.Skeleton;
import lejos.utility.Delay;

public class ActionServer implements ICaDSEV3RobotStatusListener, ICaDSEV3RobotFeedBackListener{

	protected CaDSEV3RobotHAL simul;
	
	private int percentV;
	private int percentH;
	private String move;
	private int lastPercentV;
	private int lastPercentH;
	private int fbV;
	private int fbH;
	private int serverPort;
	
	public ActionServer() {
		simul = CaDSEV3RobotHAL.createInstance(CaDSEV3RobotType.SIMULATION, this, this);
		this.percentV=0;
		this.percentH=0;
		this.move="down";
		this.lastPercentV=0;
		this.lastPercentH=0;
		this.fbH=0;
		this.fbV=0;
		this.serverPort=50003;
	}
	
	@Override
	public void giveFeedbackByJSonTo(JSONObject arg0) {
		// TODO Auto-generated method stub
		String state= arg0.get("state").toString();
		if(arg0.containsKey("percent")) {
			int p=Integer.parseInt((String)arg0.get("percent").toString());
			if(state.equals("vertical")) {
				this.fbV=p;
			}
			if(state.equals("horizontal")) {
				this.fbH=p;
			}
		}
	}

	@Override
	public void onStatusMessage(JSONObject arg0) {
		// TODO Auto-generated method stub
		String state= arg0.get("state").toString();
		if(arg0.containsKey("percent")) {
			String p=(String)arg0.get("percent").toString();
			int pMove=Integer.parseInt(p);
			if(state.equals("vertical")) {
				System.out.println("vertical: ");
				System.out.println(pMove);
				if((pMove>=percentV&&move.equals("up"))||(pMove<=percentV&&move.equals("down"))){
					simul.stop_v();
					simul.giveFeedbackByJSonTo(arg0);
				}
				 //Delay.msDelay(100);
			}
			if(state.equals("horizontal")) {
				System.out.println("horizontal: ");
				System.out.println(pMove);
				if((pMove>=percentH&&move.equals("left"))||(pMove<=percentH&&move.equals("right"))){
					simul.stop_h();
					simul.giveFeedbackByJSonTo(arg0);
				}
				//Delay.msDelay(100);
			}			
		}
		
	}
	
	public int moveVertical(int integer, String string) {
		this.move=string;
		switch(string) {
		case "up" : 
			this.percentV=Math.min(100,this.percentV+integer);
			simul.moveUp();
			this.lastPercentV=this.fbV;
			break;
		case "down" :
			this.percentV=Math.max(0,this.percentV-integer);
			simul.moveDown();
			this.lastPercentV=this.fbV;
			break;
		}
		return this.fbV;
	}

	public int moveHorizontal(int integer, String string) {
		this.move=string;
		switch(string) {
		case "left" : 
			this.percentH=Math.min(100,this.lastPercentH+integer);
			simul.moveLeft();
			this.lastPercentH=this.fbH;
			break;
		case "right" : 
			this.percentH=Math.max(0,this.lastPercentH-integer);
			simul.moveRight();
			this.lastPercentH=this.fbH;
			break;
		}
		return this.fbH;
	}
	
	public String getServerAddress() {
		return "localhost";
	}
	public String getServerName() {
		return "Server1";
	}
	public int getServerPort() {
		return serverPort;
	}
	
	public String getMove() {
		return move;
	}

	public void setMove(String move) {
		this.move = move;
	}
	public void setPercentV(int percent) {
		this.percentV=percent;
	}
	public int getPercentV() {
		return this.percentV;
	}
	public void setPercentH(int percent) {
		this.percentH=percent;
	}
	public int getPercentH() {
		return this.percentH;
	}

}
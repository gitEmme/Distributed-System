package Server;

import org.cads.ev3.middleware.CaDSEV3RobotHAL;
import org.cads.ev3.middleware.CaDSEV3RobotType;
import org.cads.ev3.middleware.hal.ICaDSEV3RobotFeedBackListener;
import org.cads.ev3.middleware.hal.ICaDSEV3RobotStatusListener;
import org.json.simple.JSONObject;

import lejos.utility.Delay;

public class ActionServer implements ICaDSEV3RobotStatusListener, ICaDSEV3RobotFeedBackListener{

	protected CaDSEV3RobotHAL simul=CaDSEV3RobotHAL.createInstance(CaDSEV3RobotType.SIMULATION, this, this);
	
	private int percentV;
	private int percentH;
	private String move;
	private int lastPercentV;
	private int lastPercentH;
	private int fbV;
	private int fbH;
	private int state=0;
	private String currentValue;
	private String nextState;
	private int serverPortV;
	private int serverPortH;
	private int serverPortOC;
	private int serverPortS;
	private int id=0;
	private int idH=0;
	
	private static final ActionServer server= new ActionServer();
	
	private ActionServer() {
		simul = CaDSEV3RobotHAL.getInstance();
		this.percentV=0;
		this.percentH=0;
		this.move="down";
		this.lastPercentV=0;
		this.lastPercentH=0;
		this.fbH=0;
		this.fbV=0;
		this.state=0;
		this.move=new String("open");
		this.currentValue=new String("close");
		this.nextState=new String("close");
		this.serverPortH=50006;
		this.serverPortV=50005;
		this.serverPortOC=50004;
		this.serverPortS=50007;
	}
	
	public static ActionServer getInstance() {
		return server;
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
		//System.out.println(arg0.toJSONString());
		String state= arg0.get("state").toString();
		if(state.equals("vertical")) {
			String p=(String)arg0.get("percent").toString();
			int pMove=Integer.parseInt(p);
			if((pMove>=percentV&&lastPercentV<=percentV)||(pMove<=percentV&&lastPercentV>percentV)){
				simul.stop_v();
				//setChanged();
				simul.giveFeedbackByJSonTo(arg0);
			}
			 //Delay.msDelay(100);
		}
		if(state.equals("horizontal")) {
			String p=(String)arg0.get("percent").toString();
			int pMove=Integer.parseInt(p);
			if((pMove>=percentH&&lastPercentH<=percentH)||(pMove<=percentH&&lastPercentH>percentH)){
				simul.stop_h();
				//setChanged();
				simul.giveFeedbackByJSonTo(arg0);
			}
			 //Delay.msDelay(100);
		}
		
	}
	
	public int moveVertical(int transactionID, int percent) {
		System.out.println("moveVertical "+ percent);
		this.id=transactionID;
		this.percentV=percent;
		if(lastPercentV<=percent) { 
			//this.percentV=Math.min(100,percent);
			simul.moveUp();
			this.lastPercentV=this.fbV;
		}else {
			//this.percentV=Math.max(0,percent);
			simul.moveDown();
			this.lastPercentV=this.fbV;
		}
		return this.fbV;
	}

	public int moveHorizontal(int transactionID, int percent) {
		System.out.println("moveHorizontal "+ percent);
		this.idH=transactionID;
		this.percentH=percent;
		if(lastPercentH<=percent) { 
			//this.percentV=Math.min(100,percent);
			simul.moveLeft();
			this.lastPercentH=this.fbH;
		}else {
			//this.percentV=Math.max(0,percent);
			simul.moveRight();
			this.lastPercentH=this.fbH;
		}
		
		return this.fbH;
	}
	
	public int grabRelease(int transactionID,String string) {
		System.out.println("grabRelease "+ string);
		this.move=string;
		switch(string) {
		case "open" : 
			this.nextState=new String("open");
			simul.doOpen();
			this.state=0;
			this.currentValue=new String("open");
			break;
		case "close" :
			this.nextState=new String("close");
			simul.doClose();
			this.state=1;
			this.currentValue=new String("close");
			break;
		}
		return this.state;
	}
	
	public int stopMovement(int transactionID) {
		System.out.println("stopMovement "+ transactionID);
		simul.stop_v();
		simul.stop_h();
		return -1000000000;
	}
	
	
	public String getServerAddress() {
		return "localhost";
	}
	public String getServerNameV() {
		return "Robot1";
	}
	public String getServerNameOC() {
		return "Robot1";
	}
	public String getServerNameH() {
		return "Robot1";
	}
	public String getServerNameS() {
		return "Robot1";
	}
	
	public int getServerPortH() {
		return serverPortH;
	}
	
	public int getServerPortV() {
		return serverPortV;
	}
	
	public int getServerPortOC() {
		return serverPortOC;
	}
	public int getServerPortS() {
		return serverPortS;
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
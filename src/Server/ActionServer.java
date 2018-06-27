package Server;

import java.net.SocketTimeoutException;
import java.util.logging.Logger;

import org.cads.ev3.middleware.CaDSEV3RobotHAL;
import org.cads.ev3.middleware.CaDSEV3RobotType;
import org.cads.ev3.middleware.hal.ICaDSEV3RobotFeedBackListener;
import org.cads.ev3.middleware.hal.ICaDSEV3RobotStatusListener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import Middleware.Connection;
import lejos.utility.Delay;

public class ActionServer implements Runnable, ICaDSEV3RobotStatusListener, ICaDSEV3RobotFeedBackListener{
	private static Logger LOG = Logger.getLogger(ActionServer.class.getName());

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
	private int serverPortF;
	private int id=0;
	private int idH=0;
	private String brokerAddr;
	private int brokerPort;
	//private static int idMsg=Integer.MIN_VALUE;
	private Connection network;
	private int countExep;
	private Thread boh;
	//private boolean stop;
	
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
		this.serverPortF=50021;
		//this.brokerAddr=new String("192.168.0.105");
		this.brokerPort=50001;
		this.network= new Connection();
		this.setCountExep(0);
		//this.stop=false;
		//giveFood();
	}
	
	public static ActionServer getInstance() {
		return server;
	}
	
	@Override
	public void giveFeedbackByJSonTo(JSONObject arg0) {
		// TODO Auto-generated method stub
		//System.out.println("GIVE FEEEED"+arg0.toJSONString());
	/*
		if(arg0!=null) {
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
		*/
	}

	@Override
	public void onStatusMessage(JSONObject arg0) {
		// TODO Auto-generated method stub
		//System.out.println(arg0.toJSONString());
		//LOG.info("V:"+fbV+" H:"+fbH);
		if(countExep==1) {
			percentV=fbV;
			percentH=fbH;
			simul.stop_h();
			simul.stop_v();
			//simul.giveFeedbackByJSonTo(arg0);
		}
		else {
			String state= arg0.get("state").toString();
			if(state.equals("vertical")) {
				String p=(String)arg0.get("percent").toString();
				int pMove=Integer.parseInt(p);
				//simul.giveFeedbackByJSonTo(arg0);
				this.fbV=pMove;
				if((pMove>=percentV&&lastPercentV<=percentV)||(pMove<=percentV&&lastPercentV>percentV)){
					simul.stop_v();
					//setChanged();
				}
				 //Delay.msDelay(100);
			}
			if(state.equals("horizontal")) {
				String p=(String)arg0.get("percent").toString();
				int pMove=Integer.parseInt(p);
				//simul.giveFeedbackByJSonTo(arg0);
				this.fbH=pMove;
				if((pMove>=percentH&&lastPercentH<=percentH)||(pMove<=percentH&&lastPercentH>percentH)){
					simul.stop_h();
					//setChanged();
				}
				 //Delay.msDelay(100);
			}
		}
	}
	
	public int moveVertical(int transactionID, int percent) {
		//System.out.println("moveVertical "+ percent);
		LOG.info("moveVertical "+ percent);
		this.id=transactionID;
		this.percentV=percent;
		if(countExep==2) {
			simul.stop_h();
			simul.stop_v();
		}
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
		LOG.info("moveHorizontal "+ percent);
		//System.out.println("moveHorizontal "+ percent);
		this.idH=transactionID;
		this.percentH=percent;
		if(countExep==2) {
			simul.stop_h();
			simul.stop_v();
		}
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
		//System.out.println("grabRelease "+ string);
		LOG.info("grabRelease "+ string);
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
		//System.out.println("stopMovement "+ transactionID);
		LOG.info("stopMovement "+ transactionID);
		//this.stop=true;
		percentH=fbH;
		percentV=fbV;
		simul.stop_v();
		simul.stop_h();
		//this.stop=false;
		
		return -1000000000;
	}
	
	public int stopV(int transactionID) {
		percentV=fbV;
		simul.stop_v();
		return -1000000000;
	}
	
	public int stopH(int transactionID) {
		percentH=fbH;
		simul.stop_h();
		return -1000000000;
	}
	
	/*
	public String getServerAddress() {
		return "192.168.0.105";
	}

	public String getServerName() {
		return "Robot1";
	}
*/
	public int getServerPortF() {
		return serverPortF;
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

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		
	}
	
	public int getFH() {
		return this.fbH;
	}
	
	public int getFV() {
		return this.fbV;
	}

	public int getCountExep() {
		return countExep;
	}

	public void setCountExep(int countExep) {
		this.countExep = countExep;
	}
/*
	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}
*/
}
package Server;

import org.cads.ev3.middleware.CaDSEV3RobotHAL;
import org.cads.ev3.middleware.CaDSEV3RobotType;
import org.cads.ev3.middleware.hal.ICaDSEV3RobotFeedBackListener;
import org.cads.ev3.middleware.hal.ICaDSEV3RobotStatusListener;
import org.json.simple.JSONObject;

public class ActionServer1 implements ICaDSEV3RobotStatusListener, ICaDSEV3RobotFeedBackListener{ 
	protected CaDSEV3RobotHAL simul;
	private String move;
	private int state=0;
	private int serverPort;



public ActionServer1() {
	simul = CaDSEV3RobotHAL.createInstance(CaDSEV3RobotType.SIMULATION, this, this);
	this.serverPort=50004;
	this.state=0;
	this.move=new String("open");
}
@Override
public void giveFeedbackByJSonTo(JSONObject arg0) {
	// TODO Auto-generated method stub
	System.out.println(arg0.toJSONString());
	
}

@Override
public void onStatusMessage(JSONObject arg0) {
	// TODO Auto-generated method stub
	System.out.println(arg0.toJSONString());
	
}


public int grabRelease(String string) {
	this.move=string;
	switch(string) {
	case "open" : 
		simul.doOpen();
		this.state=0;
		break;
	case "close" :
		simul.doClose();
		this.state=1;
		break;
	}
	return this.state;
}

public String getServerAddress() {
	return "localhost";
}
public String getServerName() {
	return "TestService2";
}
public int getServerPort() {
	return serverPort;
}
}

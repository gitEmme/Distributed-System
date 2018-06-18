package Middleware.ClientMiddleware;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import Middleware.*;
public class CStubS implements Runnable, StopMovement {
	private Connection network=new Connection();
	private JSONObject message;
	private static int counter=1;
	private String brokerAddr= new String("localhost");
	private int brokerPort=50001;
	private String clientName;
	private String serviceName;
	private Thread move;
	private boolean running= false;
	private int result;
	public CStubS(String clientName,String serviceName) {
		this.clientName=clientName;
		this.serviceName=serviceName;
	}
	
public int stopMovement(final int transactionID) {
	move= new Thread("stopMovement") {
		public void run() {
		message=new JSONObject();
		JSONObject header=new JSONObject();
		header.put("sourceName",clientName);
		header.put("destName", serviceName);
		JSONObject body=new JSONObject();
		JSONArray params=new JSONArray();
		JSONObject param1=new JSONObject();
		param1.put("name",Integer.toString(transactionID));
		param1.put("position","1");
		param1.put("type","int");
		params.add(param1);
		header.put("messageID", Integer.toString(transactionID));
		message.put("header", header);
		body.put("methodName","stopMovement");
		body.put("parameters",params);
		body.put("returnType","int");
		message.put("body", body);
		System.out.println(message.toJSONString());
		counter++;
		network=new Connection();
		network.sendTo(message,brokerAddr,brokerPort);
		}
		};
	move.start();
	return this.result;
	}
public void run() {
		running=true;
		}
	
}
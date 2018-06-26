package Middleware.ClientMiddleware;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import Middleware.*;
import cads.test.junit.gui.GuiController;
public class CStubH implements Runnable, MoveHorizontal {
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
	public CStubH(String clientName,String serviceName,String brokerAddr) {
		this.clientName=clientName;
		this.serviceName=serviceName;
		this.brokerAddr=brokerAddr;
	}
	
public int moveHorizontal(final int transactionID, final int percent) {
	move= new Thread("moveHorizontal") {
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
		JSONObject param2=new JSONObject();
		param2.put("name",Integer.toString(percent));
		param2.put("position","2");
		param2.put("type","int");
		params.add(param2);
		header.put("messageID", Integer.toString(transactionID));
		message.put("header", header);
		body.put("methodName","moveHorizontal");
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
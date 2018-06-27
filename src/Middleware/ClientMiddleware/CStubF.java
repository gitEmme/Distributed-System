package Middleware.ClientMiddleware;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import Middleware.CResult;
import Middleware.Connection;
import Middleware.Feedback;

public class CStubF implements Runnable, Feedback{
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
	public CStubF(String clientName,String serviceName,String brokerAddr) {
		this.clientName=clientName;
		this.serviceName=serviceName;
		this.brokerAddr=brokerAddr;
	}
	
	public int feedback(String robotName) {
		network=new Connection();
		JSONObject env=new JSONObject();
		JSONObject header=new JSONObject();
		JSONObject body=new JSONObject();
		JSONObject result=new JSONObject();
		JSONArray params=new JSONArray();
		JSONObject param1=new JSONObject();
		header.put("sourceName", this.clientName);
		header.put("destName", "broker");
		header.put("messageID","giveMeResults");
		body.put("methodName", "getResults");
		param1.put("name", robotName);
		param1.put("type", "String");
		param1.put("position", Integer.toString(1));
		params.add(param1);
		body.put("parameters", params);
		body.put("returnType", "Set<String>");
		env.put("header", header);
		env.put("body", body);
		env.put("result", result);
		int tries=5;
		boolean receivedResponse= false;
		boolean sent=false;
		JSONObject received;
		CResult fb= new CResult();
		network.sendTo(env, brokerAddr, brokerPort);
		return 1;
		}

	@Override
	public int feedback() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		running=true;
		
	}
}

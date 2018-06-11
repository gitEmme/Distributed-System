package Middleware.ClientMiddleware;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import Middleware.*;
public class CStub1 implements OpenClose {
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
	public CStub1(String clientName,String serviceName) {
		this.clientName=clientName;
		this.serviceName=serviceName;
	}
	
public int grabRelease(String movement) {
	move= new Thread("grabRelease") {
		public void run() {
		message=new JSONObject();
		JSONObject header=new JSONObject();
		header.put("sourceName",clientName);
		header.put("destName", serviceName);
		header.put("messageID","grabRelease"+Integer.toString(counter));
		message.put("header", header);
		JSONObject body=new JSONObject();
		JSONArray params=new JSONArray();
		JSONObject param1=new JSONObject();
		param1.put("name",movement);
		param1.put("position","1");
		param1.put("type","String");
		params.add(param1);
		body.put("methodName","grabRelease");
		body.put("parameters",params);
		body.put("returnType","int");
		message.put("body", body);
		System.out.println(message.toJSONString());
		counter++;
		network=new Connection();
		network.sendTo(message,brokerAddr,brokerPort);
		JSONObject res= (JSONObject) network.recvObjFrom(50002);
		result=(int)res.get("result");
		}
		};
	move.start();
	try {
		move.join();
	}
	catch (InterruptedException e) {
	e.printStackTrace();
	}
	return this.result;
	}
public void registerClient(String clientAddress) {
		network=new Connection();
		JSONObject env=new JSONObject();
		JSONObject header=new JSONObject();
		JSONObject body=new JSONObject();
		JSONObject result=new JSONObject();
		JSONArray params=new JSONArray();
		JSONObject param1=new JSONObject();
		JSONObject param2=new JSONObject();
		JSONObject param3=new JSONObject();
		header.put("sourceName", this.clientName);
		header.put("destName", "broker");
		header.put("messageID","registerMe");
		body.put("methodName", "registerService");
		param1.put("name", clientName);
		param1.put("type", "String");
		param1.put("position", Integer.toString(1));
		param2.put("name", clientAddress);
		param2.put("type", "String");
		param2.put("position", Integer.toString(2));
		param3.put("name", "50002");
		param3.put("type", "String");
		param3.put("position", Integer.toString(3));
		params.add(param1);
		params.add(param2);
		params.add(param3);
		body.put("parameters", params);
		body.put("returnType", "String");
		env.put("header", header);
		env.put("body", body);
		env.put("result", result);
		network.sendTo(env, brokerAddr, brokerPort);
		}
	public void run() {
		running=true;
		}
	
}
package Middleware;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Registration implements Runnable {
	private Connection network;
	private int brokerPort=50001;
	private String brokerAddr;
	private String entityName;
	private String entityAddr;
	private int servicePort;
	private String serviceName;
	private boolean isRunning=false;
	
	public Registration(String entityName,String brokerAddr,String entityAddr) {
		this.entityName=entityName;
		//this.serviceName=serviceName;
		this.brokerAddr=brokerAddr;
		//this.clientPort=clientPort;
		this.entityAddr=entityAddr;
	}

	public void registerClient(String serviceName,int servicePort) {
	network=new Connection();
	JSONObject env=new JSONObject();
	JSONObject header=new JSONObject();
	JSONObject body=new JSONObject();
	JSONObject result=new JSONObject();
	JSONArray params=new JSONArray();
	JSONObject param1=new JSONObject();
	JSONObject param2=new JSONObject();
	JSONObject param3=new JSONObject();
	JSONObject param4=new JSONObject();
	header.put("sourceName", this.entityName);
	header.put("destName", "broker");
	header.put("messageID","registerMe");
	body.put("methodName", "registerClient");
	param1.put("name", this.entityName);
	param1.put("type", "String");
	param1.put("position", Integer.toString(1));
	param2.put("name", entityAddr);
	param2.put("type", "String");
	param2.put("position", Integer.toString(2));
	param3.put("name", Integer.toString(servicePort));
	param3.put("type", "String");
	param3.put("position", Integer.toString(3));
	param4.put("name", serviceName);
	param4.put("type", "String");
	param4.put("position", Integer.toString(4));
	params.add(param1);
	params.add(param2);
	params.add(param3);
	params.add(param4);
	body.put("parameters", params);
	body.put("returnType", "String");
	env.put("header", header);
	env.put("body", body);
	env.put("result", result);
	int tries=5;
	boolean receivedResponse= false;
	boolean sent=true;
	do{
		network.sendTo(env, brokerAddr, brokerPort);
		sent = true;
		JSONObject received=(JSONObject) network.recvObjFrom(servicePort,false);
		if (received!=null) {
			System.out.println(received.toJSONString());
			receivedResponse=true;
		}else{
			tries -=1;
			System.out.println("Timed out:" + Integer.toString(tries) + " tries left");
			}
		}while(((!receivedResponse)&& tries> 0) && (!sent));
	}
	
	public void registerServer(String serviceName,int servicePort) {
		network=new Connection();
		JSONObject env=new JSONObject();
		JSONObject header=new JSONObject();
		JSONObject body=new JSONObject();
		JSONObject result=new JSONObject();
		JSONArray params=new JSONArray();
		JSONObject param1=new JSONObject();
		JSONObject param2=new JSONObject();
		JSONObject param3=new JSONObject();
		JSONObject param4=new JSONObject();
		header.put("sourceName", this.entityName);
		header.put("destName", "broker");
		header.put("messageID","registerMe");
		body.put("methodName", "registerServer");
		param1.put("name", this.entityName);
		param1.put("type", "String");
		param1.put("position", Integer.toString(1));
		param2.put("name", entityAddr);
		param2.put("type", "String");
		param2.put("position", Integer.toString(2));
		param3.put("name", Integer.toString(servicePort));
		param3.put("type", "String");
		param3.put("position", Integer.toString(3));
		param4.put("name", serviceName);
		param4.put("type", "String");
		param4.put("position", Integer.toString(4));
		params.add(param1);
		params.add(param2);
		params.add(param3);
		params.add(param4);
		body.put("parameters", params);
		body.put("returnType", "String");
		env.put("header", header);
		env.put("body", body);
		env.put("result", result);
		int tries=5;
		boolean receivedResponse= false;
		boolean sent=true;
		do{
			network.sendTo(env, brokerAddr, brokerPort);
			sent = true;
			JSONObject received=(JSONObject) network.recvObjFrom(servicePort,false);
			if (received!=null) {
				System.out.println(received.toJSONString());
				receivedResponse=true;
			}else{
				tries -=1;
				System.out.println("Timed out:" + Integer.toString(tries) + " tries left");
				}
			}while(((!receivedResponse)&& tries> 0) && (!sent));
		}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		isRunning=true;
	}
}

package Middleware.ServerMiddleware;

import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import Broker.Coordinator;
import Middleware.Connection;
import Server.ActionServer;

public class CSkeletonF implements Runnable{
	private static Logger LOG = Logger.getLogger(CSkeletonF.class.getName());
	private Connection network=new Connection();
	ActionServer action=ActionServer.getInstance();
	private int brokerPort=50001;
	private String brokerAddr;
	private int port;
	private String serverName;
	private String serverIP;
	private static int idMsg=Integer.MIN_VALUE;

	
	public CSkeletonF(String serverName,String serverIP,String brokerAddr,int port) {
		this.port=action.getServerPortF();
		this.serverName=serverName;
		this.serverIP=serverIP;
		this.brokerAddr=brokerAddr;
		this.port=port;
		}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		registerService();
		while(true) {
			JSONObject jenv=new JSONObject();
			JSONObject jheader=new JSONObject();
			JSONObject jbody=new JSONObject();
			JSONObject jresult=new JSONObject();
			JSONArray params = new JSONArray();
			jheader.put("sourceName",this.serverName);
			jheader.put("destName", "broker" );
			jheader.put("messageID",Integer.toString(idMsg));
			jbody.put("methodName", "feedback");
			jbody.put("parameters", params);
			jbody.put("returnType", "int");
			jenv.put("header", jheader);
			jenv.put("body", jbody);
			jresult.put("vertical", Integer.toString(action.getFV()));
			jresult.put("horizontal", Integer.toString(action.getFH()));
			jenv.put("result", jresult);
			idMsg++;
			network.sendTo(jenv, brokerAddr, brokerPort);
			//System.out.println("robot sent feedback "+ jenv.toJSONString());
			JSONObject received=(JSONObject)network.recvObjFrom(this.port,true );
			if(received==null) {
				action.setCountExep(action.getCountExep() + 1);
			}else {
				LOG.info("received C0ORDINATOR ALIVE");
				action.setCountExep(0);
			}
			if(action.getCountExep()==1) {
				action.stopMovement(idMsg);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		
	}
	
	public void registerService() {
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
		header.put("sourceName", this.serverName);
		header.put("destName", "broker");
		header.put("messageID","registerMe");
		body.put("methodName", "registerServer");
		param1.put("name", this.serverName);
		param1.put("type", "String");
		param1.put("position", Integer.toString(1));
		param2.put("name", this.serverIP);
		param2.put("type", "String");
		param2.put("position", Integer.toString(2));
		param3.put("name", Integer.toString(this.port));
		param3.put("type", "int");
		param3.put("position", Integer.toString(3));
		param4.put("name", "feedback");
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
		boolean sent=false;
		do{
			network.sendTo(env, brokerAddr, brokerPort);
			sent = true;
			JSONObject received=(JSONObject) network.recvObjFrom(this.port,false);
			if (received!=null) {
			receivedResponse=true;
				System.out.println(received.toJSONString());
			}else{
			tries --;
				System.out.println("Timed out: "+  tries + " tries left");
				}
			}while(((!receivedResponse)&& tries!= 0) && (!sent));
		}

}

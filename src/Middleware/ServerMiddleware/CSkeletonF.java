package Middleware.ServerMiddleware;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import Middleware.Connection;
import Server.ActionServer;

public class CSkeletonF implements Runnable{
	private Connection network=new Connection();
	ActionServer action=ActionServer.getInstance();
	private int brokerPort=50001;
	private String brokerAddr;
	private int port;
	private String serverName;
	private String serverIP;
	private static int idMsg=Integer.MIN_VALUE;

	
	public CSkeletonF(String serverName,String serverIP,String brokerAddr) {
		this.port=action.getServerPortF();
		this.serverName=serverName;
		this.serverIP=serverIP;
		this.brokerAddr=brokerAddr;
		}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
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
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(action.getFV()+ " "+ action.getFH());
			System.out.println(jenv.toJSONString());
			System.out.println(brokerPort);
			System.out.println(brokerAddr);
			network.sendTo(jenv, brokerAddr, brokerPort);
			
			JSONObject received=(JSONObject)network.recvObjFrom(50021, true);
			if(received==null) {
				action.setCountExep(action.getCountExep() + 1);
			}else {
				action.setCountExep(0);
			}
			if(action.getCountExep()==2) {
				action.stopMovement(idMsg);
			}
			}
		
	}

}

package Middleware.ServerMiddleware;

import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import Broker.Coordinator;
import Middleware.Connection;
import Middleware.Feedback;
import Middleware.Registration;
import Server.ActionServer;

public class CSkeletonF implements Runnable, Feedback{
	private static Logger LOG = Logger.getLogger(CSkeletonF.class.getName());
	private Connection network=new Connection();
	ActionServer action=ActionServer.getInstance();
	private int brokerPort=50001;
	private String brokerAddr;
	private int port;
	private String serverName;
	private String serverIP;
	private static int idMsg=Integer.MIN_VALUE;
	private Registration reg;
	
	public CSkeletonF(String serverName,String serverIP,String brokerAddr,int port) {
		this.port=action.getServerPortF();
		this.serverName=serverName;
		this.serverIP=serverIP;
		this.brokerAddr=brokerAddr;
		this.port=port;
		reg= new Registration(this.serverName, this.brokerAddr, this.serverIP);
		}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		reg.registerServer("feedback", this.port);
		//registerService();
		while(true) {
			feedback();
			}
		
	}
	
	public int feedback() {
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
		return 1;
	}
	
}

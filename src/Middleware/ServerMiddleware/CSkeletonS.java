package Middleware.ServerMiddleware;
import Middleware.*;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import Server.ActionServer;

public class CSkeletonS implements Runnable, StopMovement {
	private Connection network;
	ActionServer action=ActionServer.getInstance();
	private int port;
	private int brokerPort=50001;
	private String brokerAddr;

	private String serverName= new String();

	private String serverIP= new String();
	private Registration reg;
	
	public CSkeletonS() {
		this.port=action.getServerPortS();
	}
	
public CSkeletonS(String serverName,String serverIP,String brokerAddr, int port) {
		//this.port=action.getServerPortS();
		this.serverName=serverName;
		this.serverIP=serverIP;
		this.brokerAddr=brokerAddr;
		this.port=port;
		reg= new Registration(this.serverName, this.brokerAddr, this.serverIP);
		}
	public int execute(CProcedure p) {
		int result=0;
		switch (p.getName()) {
			case "stopMovement":
				result= stopMovement((int)p.getParam(1).getValue(p.getParam(1).getName()));
				break;
			case "stopH":
				result= stopH((int)p.getParam(1).getValue(p.getParam(1).getName()));
				break;
			
			case "stopV":
				result= stopV((int)p.getParam(1).getValue(p.getParam(1).getName()));
				break;
			}
		
			return result;
			}

	public CEnvelope unmarshall() {
		network=new Connection();
		JSONObject received=(JSONObject) network.recvObjFrom(this.port,false);
		CEnvelope env= new CEnvelope();
		CHeader h= new CHeader();
		JSONObject header= (JSONObject) received.get("header");
		h.setMessageID((String)header.get("messageID"));
		h.setSourceName((String) header.get("sourceName"));
		h.setDestName((String)header.get("destName"));
		env.setHeader(h);
		JSONObject body=(JSONObject) received.get("body");
		String methodName=(String) body.get("methodName");
		String returnType= (String) body.get("returnType");
		JSONArray params = (JSONArray) body.get("parameters");
		CProcedure procedure= new CProcedure(methodName, returnType);
		Iterator<JSONObject> paramsIterator = params.iterator();
		while (paramsIterator.hasNext()) {
			JSONObject param= paramsIterator.next();
			String type = (String) param.get("type");
			String name = (String) param.get("name");
			int position= Integer.parseInt((String)param.get("position"));
			CParameter unmPar = new CParameter(name,type,position);
			procedure.AddParam(unmPar);
			}
		env.setProcedure(procedure);
		//System.out.println(body.toJSONString());
		return env;
		}

	public void run() {
	//registerService();
	reg.registerServer("stopMovement", this.port);
	while(true) {
		CEnvelope envelope=unmarshall();
		CHeader head=envelope.getHeader();
		CProcedure invoked=envelope.getProcedure();
		int p=execute(invoked);
		network=new Connection();
		JSONObject body=new JSONObject();
		JSONArray params=new JSONArray();
		JSONObject header= new JSONObject();
		JSONObject env= new JSONObject();
		header.put("sourceName",head.getDestName());
		header.put("destName",head.getSourceName());
		header.put("messageID", head.getMessageID());
		body.put("methodName", invoked.getName());
		body.put("parameters", params);
		body.put("returnType", invoked.getReturnType());
		env.put("header", header);
		env.put("body", body);
		env.put("result", p);
		//network.sendTo(env, brokerAddr, brokerPort);
		//System.out.println("STOPPED RESULT"+env.toJSONString());
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}

	public int stopMovement(int transactionID) {
		//action.setStop(true);
		int p = action.stopMovement(transactionID);
		
		return p;
	}
	
	public int stopH(int transactionID) {
		//action.setStop(true);
		int p = action.stopH(transactionID);
		
		return p;
	}
	
	public int stopV(int transactionID) {
		//action.setStop(true);
		int p = action.stopV(transactionID);
		
		return p;
	}
/*
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
		param4.put("name", "stopMovement");
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
				//System.out.println(received.toJSONString());
			}else{
			tries --;
				//System.out.println("Timed out: "+  tries + " tries left");
				}
			}while(((!receivedResponse)&& tries!= 0) && (!sent));
		}
		*/
	}
package Middleware.ServerMiddleware;
import Middleware.*;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import Server.ActionServer;

public class CSkeletonOC implements Runnable, OpenClose {
	private Connection network;
	ActionServer action=ActionServer.getInstance();
	private int port;
	private int brokerPort=50001;
	private String brokerAddr= new String("localhost");

	private String serverName= new String();

	private String serverIP= new String();

	public CSkeletonOC() {
		this.port=action.getServerPortOC();
	}
	
public CSkeletonOC(String serverName,String serverIP) {
		this.port=action.getServerPortS();
		this.serverName=serverName;
		this.serverIP=serverIP;
		}
	public int execute(CProcedure p) {
		int result=0;
		switch (p.getName()) {
			case "grabRelease":
				result= grabRelease((int)p.getParam(1).getValue(p.getParam(1).getName()), (String)p.getParam(2).getValue(p.getParam(2).getName()));
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
		System.out.println(body.toJSONString());
		return env;
		}

	public void run() {
	registerService();
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
		network.sendTo(env, brokerAddr, brokerPort);
		}
	}

	public int grabRelease(int transactionID, String movement) {
		int p = action.grabRelease(transactionID, movement);
		return p;
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
		header.put("sourceName", action.getServerNameOC());
		header.put("destName", "broker");
		header.put("messageID","registerMe");
		body.put("methodName", "registerServer");
		param1.put("name", action.getServerNameOC());
		param1.put("type", "String");
		param1.put("position", Integer.toString(1));
		param2.put("name", action.getServerAddress());
		param2.put("type", "String");
		param2.put("position", Integer.toString(2));
		param3.put("name", Integer.toString(action.getServerPortOC()));
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
		int tries=5;
		boolean receivedResponse= false;
		boolean sent=false;
		do{
			network.sendTo(env, brokerAddr, brokerPort);
			sent = true;
			JSONObject received=(JSONObject) network.recvObjFrom(action.getServerPortOC(),false);
			if (received!=null) {
			receivedResponse=true;
				System.out.println(received.toJSONString());
			}else{
			tries --;
				System.out.println("Timed out: "+  tries + " tries left");
				}
			}while(((!receivedResponse)&& tries!= 0) && (!sent));
		}
	public static void main(String[] args) {
		CSkeletonOC sk= new CSkeletonOC();
		Thread s = new Thread(sk);
		s.start();
		}
	}
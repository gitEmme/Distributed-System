package Middleware.ServerMiddleware;
import Middleware.*;
import java.util.Iterator;

import org.jfree.util.Log;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import Server.ActionServer;

public class CSkeletonV implements Runnable, MoveVertical {
	private Connection network;
	ActionServer action=ActionServer.getInstance();
	private int port;
	private int brokerPort=50001;
	private String brokerAddr= new String("localhost");

	private String serverName= new String();

	private String serverIP= new String();
	
	private Registration reg;

	public CSkeletonV() {
		this.port=action.getServerPortV();
	}
	
public CSkeletonV(String serverName,String serverIP,String brokerAddr,int port) {
		this.port=action.getServerPortV();
		this.serverName=serverName;
		this.serverIP=serverIP;
		this.brokerAddr=brokerAddr;
		this.port=port;
		reg= new Registration(this.serverName, this.brokerAddr, this.serverIP);
		
		}
	public int execute(CProcedure p) {
		int result=0;
		switch (p.getName()) {
			case "moveVertical":
				result= moveVertical((int)p.getParam(1).getValue(p.getParam(1).getName()), (int)p.getParam(2).getValue(p.getParam(2).getName()));
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
	reg.registerServer("moveVertical", this.port);
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
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}

	public int moveVertical(int transactionID, int percent) {
		int p = action.moveVertical(transactionID, percent);
		return p;
	}

	
	}
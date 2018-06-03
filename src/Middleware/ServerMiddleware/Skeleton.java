package Middleware.ServerMiddleware;


import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import Middleware.CEnvelope;
import Middleware.CHeader;
import Middleware.CParameter;
import Middleware.CProcedure;
import Middleware.Connection;
import Middleware.MoveAround;
import Server.ActionServer;
public class Skeleton implements Runnable, MoveAround {
	private Connection network;
	ActionServer action=new ActionServer();
	private int port;
	public Skeleton(int port) {
		this.port=port;
	}
	
	public int execute(CProcedure p) {
		int result=0;
		switch (p.getName()) {
		case "moveVertical":
			result=moveVertical((int)p.getParam(1).getValue(p.getParam(1).getName()),(String)p.getParam(2).getValue(p.getParam(2).getName()));
			break;
		case "moveHorizontal":
			result=moveHorizontal((int)p.getParam(1).getValue(p.getParam(1).getName()),(String)p.getParam(2).getValue(p.getParam(2).getName()));
			break;
		}
		return result;
	}
/*	
	public int execute(String mName, int first,String second ) {
		int result=0;
		switch (mName) {
		case "moveVertical":
			result=moveVertical(first,second);
			break;
		case "moveHorizontal":
	    	 result=moveHorizontal(first,second);
	    	 break;
	    }
		return result;
	}
	*/
	
	public CEnvelope unmarshall() {
		network=new Connection();
		JSONObject received=(JSONObject) network.recvObjFrom(this.port);
		CEnvelope env= new CEnvelope();
		CHeader h= new CHeader();
		JSONObject header= (JSONObject) received.get("header");
		h.setProcedureID((String)header.get("procedureID"));
		h.setStubAddress((String)header.get("stubAddress"));
		h.setStubPort((Integer)header.get("stubPort"));
		h.setServiceName((String)header.get("serviceName"));
		h.setSourceName((String) header.get("sourceName"));
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

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			CEnvelope envelope=unmarshall();
			CHeader head=envelope.getHeader();
			CProcedure invoked=envelope.getProcedure();
		    //int p=execute(invoked.getName(),Integer.parseInt(invoked.getParam(1).getName()),invoked.getParam(2).getName());
		    int p=execute(invoked);
			System.out.println("executed!");
		    int stubPort=head.getStubPort();
		    String stubAddr=head.getStubAddress();
		    network=new Connection();
			JSONObject result=new JSONObject();
			result.put("result", p);
			network.sendTo(result, stubAddr, stubPort);
			
		}
		
	}

	@Override
	public int moveHorizontal(int integer, String string) {
		//action.setMove(string);
		int p =action.moveHorizontal(integer, string);
		return p;
	}

	@Override
	public int moveVertical(int integer, String string) {
		//action.setMove(string);
		int p=action.moveVertical(integer, string);
		return p;
	}
	
	public static void main(String[] args) {
		Skeleton sk= new Skeleton(50001);
		Thread s = new Thread(sk);
		s.start();
	}

}

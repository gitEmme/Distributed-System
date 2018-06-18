package Broker;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import Middleware.CEnvelope;
import Middleware.CHeader;
import Middleware.CParameter;
import Middleware.CProcedure;
import Middleware.Connection;


public class Coordinator implements Runnable {
	private static Logger log=Logger.getLogger("Coordinator busy");
	private Connection network= new Connection();
	private int brokerPort=50001;
	private String brokerAddress= new String("172.16.1.64");
	private NameService registered = new NameService();
	private LinkedList<JSONObject> responses;
	private JSONObject received;
	private HashMap<JSONObject,String> clientMsgMap= new HashMap();
	private LinkedList<JSONObject> robotMsgList= new LinkedList();
	HashMap<JSONObject,String>  msgAddr= new HashMap();
	HashMap<JSONObject,CEnvelope>  msgEnv= new HashMap();
	HashMap<Integer,String> checkResponse = new HashMap();
	private boolean isHorizontalBusy=false;
	private boolean isVerticalBusy=false;
	private boolean isOpenCloseBusy=false;
	private int countV=0;
	private int countH=0;
	
	@Override
	public void run() {
		// TODO Auto-generated method Stub
		while(true) {
			try {
				processMsg();
				forwardToRobot();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	} 
	
	public CEnvelope processMsg() throws InterruptedException {
		int destPort=0;
		network=new Connection();
		received=(JSONObject) network.recvObjFrom(this.brokerPort,false);
		System.out.println("Coordinator received message: "+received.toJSONString());
		CEnvelope env= new CEnvelope();
		CHeader h= new CHeader();
		JSONObject header= (JSONObject) received.get("header");
		h.setMessageID((String)header.get("messageID"));
		//h.setStubAddress((String)header.get("stubAddress"));
		//h.setStubPort((Integer)header.get("stubPort"));
		//h.setServiceName((String)header.get("serviceName"));
		h.setSourceName((String) header.get("sourceName"));
		/*addeddestName*/
		h.setDestName((String) header.get("destName"));
		/*till here*/
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
		String dest=env.getHeader().getDestName();
		if(dest.equals("broker")) {
			String result=new String();
			CProcedure called=env.getProcedure();
			String method=called.getName();
			if(method.equals("registerServer")) {
				result=registerServer(called.getParam(1).getName(),called.getParam(2).getName(),Integer.parseInt(called.getParam(3).getName()));
				System.out.println("result: "+result);
				
			}
			if(method.equals("registerClient")){
				result=registerClient(called.getParam(1).getName(),called.getParam(2).getName(),Integer.parseInt(called.getParam(3).getName()));
				
			}
			if(method.equals("getServiceList")){
				//System.out.println("Coordinator received getListRequest from client: ");
				//System.out.println(received.toJSONString());
				Set<String> available=registered.getAvailable();
				JSONObject jenv=new JSONObject();
				JSONObject jheader=new JSONObject();
				JSONObject jbody=new JSONObject();
				JSONObject jresult=new JSONObject();
				JSONArray jRobotList = new JSONArray();
				jheader.put("sourceName", "broker");
				jheader.put("destName", h.getSourceName());
				jheader.put("messageID","robotList");
				jbody.put("methodName", "updatedRobotList");
				jbody.put("returnType", "LinkedList<String>");
				jenv.put("header", jheader);
				jenv.put("body", body);
				for(String robot :registered.getAvailableRobot()) {
					JSONObject rName=new JSONObject();
					rName.put("name", robot);
					jRobotList.add(robot);
				}
				jenv.put("result", jRobotList);
				String destAddress=registered.getServiceAddress(h.getSourceName());
				destPort=registered.getServicePort(h.getSourceName());
				network.sendTo(jenv, destAddress, destPort);
			}
			if(method.equals("getResults")) {
				//System.out.println("Coordinator received getResults from client: ");
				//System.out.println(received.toJSONString());
				JSONObject resEnv=new JSONObject();
				JSONObject jheader=new JSONObject();
				JSONObject jbody=new JSONObject();
				JSONObject jresult=new JSONObject();
				JSONArray jMsgResult = new JSONArray();
				jheader.put("sourceName",env.getHeader().getDestName());
				jheader.put("destName", env.getHeader().getSourceName());
				jheader.put("messageID","resultList");
				jbody.put("methodName", "getResult");
				jbody.put("returnType", "JSONArray");
				resEnv.put("header", jheader);
				resEnv.put("body", body);
				for(JSONObject res : clientMsgMap.keySet()) {
					if(clientMsgMap.get(res).equals(h.getSourceName())) {
						jMsgResult.add(res);
						//clientMsgMap.remove(res);
					}
				}
				resEnv.put("result", jMsgResult);
				String destAddress=registered.getServiceAddress(h.getSourceName());
				//destPort=registered.getServicePort(h.getSourceName());
				destPort=50012;
				//System.out.println("AnswerBack :");
				//System.out.println(resEnv.toJSONString());
				network.sendTo(resEnv, destAddress, destPort);
				
			}
		}else {
			if(registered.getClientList().contains(dest)) {
				//responses.add(received);
				//System.out.println("Message for client"+received.toJSONString());
				clientMsgMap.put(received, dest);
				JSONObject rHead=(JSONObject)received.get("header");
				String client=(String) rHead.get("destName");
				int msgID= Integer.parseInt((String)rHead.get("messageID"));
				checkResponse.put(msgID, client);
				//System.out.println("List result to client: "+checkResponse.toString());
			}else {
				//System.out.println("Coordinator received message: "+received.toJSONString());
				//System.out.println(received.toJSONString());
				msgAddr.put(received, dest);
				robotMsgList.add(received);
				msgEnv.put(received, env);
			}
		}
		return env;
		}
	
	public void forwardToRobot() throws InterruptedException {
		int destPort=0;
		int currentV=0;
		int currentH=0;
		boolean isBusy=false;
		for(JSONObject m : robotMsgList) {
			while(isBusy) {}
			JSONObject firstMsg= robotMsgList.getFirst();
			CEnvelope env=msgEnv.get(firstMsg);
			String destAddress=registered.getServiceAddress(msgAddr.get(firstMsg));
			JSONObject body=(JSONObject) firstMsg.get("body");
			String methodName=(String) body.get("methodName");
			if(methodName.equals("moveHorizontal")) {
				destPort=50006;
				//isOpenCloseBusy=false;
				//while(isBusy) {}
				countH++;
				countV=0;
			}
			if(methodName.equals("moveVertical")) {
				destPort=50005;
				//while(isBusy) {}
				countV++;
				countH=0;
				//isOpenCloseBusy=false;
			}
			if(methodName.equals("grabRelease")) {
				destPort=50004;
				//isOpenCloseBusy=true;
			}
			
			//System.out.println("From queue:   "+firstMsg.toJSONString());
			if(countV>=2 || countH>=2) {
				isBusy=true;
				
				JSONObject cHead=(JSONObject)firstMsg.get("header");
				String cDest=(String) cHead.get("destName");
				String sName=(String) cHead.get("sourceName");
				int cID= Integer.parseInt((String)cHead.get("messageID"));
				System.out.println("Sending stopMovement "+cID);
				stopMovement(cID, cDest, destAddress,sName);
				Thread.sleep(1000);
				if(methodName.equals("moveVertical")) {
					countV--;
				}
				if(methodName.equals("moveHorizontal")) {
					countH--;
				}
				isBusy=false;
			}
			System.out.println("Sending msg:   "+firstMsg.toJSONString());
			network.sendTo(firstMsg, destAddress, destPort);
			robotMsgList.removeFirst();	
			}
		//System.out.println("List results: ");
		//System.out.println(clientMsgMap.toString());
		}
			
	public String registerServer(String serviceName, String serviceAddress, int servicePort) {
		if(!registered.isServerRegistered(serviceName)) {
			registered.addServiceLocation(serviceName, serviceAddress, servicePort);
			registered.addServer(serviceName, serviceAddress);
			JSONObject ack= new JSONObject();
			ack.put("registrationResult",serviceName+ " registered");
			network.sendTo(ack, serviceAddress, servicePort);
			return "Service "+serviceName+" registered!";
		}else {
			JSONObject ack= new JSONObject();
			ack.put("registrationResult",serviceName+ " already registered");
			network.sendTo(ack, serviceAddress, servicePort);
			return "Service  "+serviceName+" already registered!";
		}
		
	}
	
	public String registerClient(String serviceName, String serviceAddress, int servicePort) {
		if(!registered.isClientRegistered(serviceName)) {
			registered.addServiceLocation(serviceName, serviceAddress, servicePort);
			registered.addClient(serviceName, serviceAddress);
			JSONObject ack= new JSONObject();
			ack.put("registrationResult",serviceName+ " registered");
			network.sendTo(ack, serviceAddress, servicePort);
			return "Client "+serviceName+" registered!";
		}else {
			JSONObject ack= new JSONObject();
			ack.put("registrationResult",serviceName+ " already registered");
			network.sendTo(ack, serviceAddress, servicePort);
			return "Client  "+serviceName+" already registered!";
		}
		
	}
	
	
	public int stopMovement(int transactionID,String dest,String destAddr,String source) {
		JSONObject message=new JSONObject();
		JSONObject header=new JSONObject();
		header.put("sourceName",source);
		header.put("destName", dest);
		JSONObject body=new JSONObject();
		JSONArray params=new JSONArray();
		JSONObject param1=new JSONObject();
		param1.put("name",Integer.toString(transactionID));
		param1.put("position","1");
		param1.put("type","int");
		params.add(param1);
		header.put("messageID", Integer.toString(transactionID));
		message.put("header", header);
		body.put("methodName","stopMovement");
		body.put("parameters",params);
		body.put("returnType","int");
		message.put("body", body);
		//System.out.println(message.toJSONString());;
		network=new Connection();
		network.sendTo(message,destAddr,50007);
		return 1;
		}
	
	public static void main(String[] args) {
		Coordinator c= new Coordinator();
		Thread t= new Thread(c);
		t.start();
	}
}

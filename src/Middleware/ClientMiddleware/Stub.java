package Middleware.ClientMiddleware;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import Middleware.*;
public class Stub implements Runnable{
	private Connection network=new Connection();
	private JSONObject message;
	private CStubH cH;
	private CStubV cV;
	private CStubOC cOC;
	private String clientName;
	private String clientAddr;
	private String serverName;
	private int clientPort=50002;
	private int  clientPortRes=50012;
	private boolean isrunning=false;
	private String brokerAddr= new String("localhost");
	private int brokerPort=50001;
	private boolean running= false;
	private int result;
	private LinkedList<Integer>responses =new LinkedList();
	private HashMap<Integer,Integer> VFb= new HashMap();
	private HashMap<Integer,Integer> HFb= new HashMap();
	private int currentH=0;
	private int currentV=0;
	private Thread h,v,oc;
	
	
	public Stub(String clientName,String clientAddr) {
		this.clientName=clientName;
		this.clientAddr=clientAddr;
		
	}
	
	public Stub(String clientName,String clientAddr, int clientPort) {
		this.clientName=clientName;
		this.clientAddr=clientAddr;
		this.clientPort=clientPort;
	
	}
	
	public int moveHorizontal(final int transactionID, final int percent) throws InterruptedException {
		h=new Thread("sending-Thread") {
			public void run() {
		cH=new CStubH(Stub.this.clientName,Stub.this.serverName);
		Stub.this.result=cH.moveHorizontal(transactionID, percent);
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!responses.contains(transactionID)) {
			Stub.this.result=cH.moveHorizontal(transactionID, percent);
		}
			};
		};
		h.start();
		return this.result;
		
		}
	
	public int moveVertical(final int transactionID, final int percent) throws InterruptedException {
		v=new Thread("sending-Thread") {
			public void run() {
		cV=new CStubV(Stub.this.clientName,Stub.this.serverName);
		result=cV.moveVertical(transactionID, percent);
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!responses.contains(transactionID)) {
			result=cV.moveVertical(transactionID, percent);
		}
			};
		};
		v.start();
		return result;
		}
	
	public int grabRelease(final int transactionID, final String movement) throws InterruptedException {
		oc=new Thread("sending-Thread") {
			public void run() {
		cOC=new CStubOC(Stub.this.clientName,Stub.this.serverName);
		result=cOC.grabRelease(transactionID, movement);
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!responses.contains(transactionID)) {
			result=cOC.grabRelease(transactionID, movement);
		}
			};
		};
		oc.start();
		return result;
	}
	
	public void setCurrentServer(String serverName) {
		this.serverName=serverName;
	}
	
	public void registerClient() {
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
		body.put("methodName", "registerClient");
		param1.put("name", clientName);
		param1.put("type", "String");
		param1.put("position", Integer.toString(1));
		param2.put("name", clientAddr);
		param2.put("type", "String");
		param2.put("position", Integer.toString(2));
		param3.put("name", Integer.toString(clientPort));
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
		boolean sent=true;
		do{
			network.sendTo(env, brokerAddr, brokerPort);
			sent = true;
			JSONObject received=(JSONObject) network.recvObjFrom(clientPort,true);
			if (received!=null) {
				System.out.println(received.toJSONString());
				receivedResponse=true;
			}else{
				tries -=1;
				System.out.println("Timed out:" + Integer.toString(tries) + " tries left");
				}
			}while(((!receivedResponse)&& tries> 0) && (!sent));
		}
	
	public LinkedList<String> getRobotList() {
		network=new Connection();
		JSONObject env=new JSONObject();
		JSONObject header=new JSONObject();
		JSONObject body=new JSONObject();
		JSONObject result=new JSONObject();
		JSONArray params=new JSONArray();
		header.put("sourceName", this.clientName);
		header.put("destName", "broker");
		header.put("messageID","giveMeServiceList");
		body.put("methodName", "getServiceList");
		body.put("parameters", params);
		body.put("returnType", "Set<String>");
		env.put("header", header);
		env.put("body", body);
		env.put("result", result);
		int tries=5;
		boolean receivedResponse= false;
		boolean sent=false;
		JSONObject received;
		do{
			network.sendTo(env, brokerAddr, brokerPort);
			sent = true;
			received=(JSONObject) network.recvObjFrom(clientPort,false);
			if (received!=null) {
				System.out.println(received.toJSONString());
				receivedResponse=true;
			}else{
				tries -=1;
				System.out.println("Timed out:" + Integer.toString(tries) + " tries left");
				}
			}while(((!receivedResponse)&& tries!= 0) && (!sent));
		LinkedList<String> robotList = new LinkedList();
		JSONArray jrobotList=(JSONArray) received.get("result");
		Iterator<String> robotIterator =jrobotList.iterator();
		while(robotIterator.hasNext()) {
			String r= robotIterator.next();
			System.out.println(r);
			robotList.add(r);
		}
		System.out.println(responses.toString());
		return robotList;
		}
	
	public CResult getResults() {
		network=new Connection();
		JSONObject env=new JSONObject();
		JSONObject header=new JSONObject();
		JSONObject body=new JSONObject();
		JSONObject result=new JSONObject();
		JSONArray params=new JSONArray();
		header.put("sourceName", this.clientName);
		header.put("destName", "broker");
		header.put("messageID","giveMeResults");
		body.put("methodName", "getResults");
		body.put("parameters", params);
		body.put("returnType", "Set<String>");
		env.put("header", header);
		env.put("body", body);
		env.put("result", result);
		int tries=5;
		boolean receivedResponse= false;
		boolean sent=false;
		JSONObject received;
		CResult fb= new CResult();
		do{
			network.sendTo(env, brokerAddr, brokerPort);
			sent = true;
			received=(JSONObject) network.recvObjFrom(clientPortRes,false);
			if (received!=null) {
				System.out.println(received.toJSONString());
				receivedResponse=true;
			}else{
				tries --;
				System.out.println("Timed out:" + Integer.toString(tries) + " tries left");
				}
			}while(((!receivedResponse)&& tries> 0) && (!sent));
		if(!(received==null)) {
		JSONArray jResList=(JSONArray) received.get("result");
		Iterator<JSONObject> resIterator =jResList.iterator();
		int maxV=0;
		int maxH=0;
		while(resIterator.hasNext()) {
			JSONObject r= resIterator.next();
			JSONObject head= (JSONObject)r.get("header");
			JSONObject b=(JSONObject)r.get("body");
			int value=(Integer)r.get("result");
			int messageID=Integer.parseInt((String)head.get("messageID"));
			String mName=(String)b.get("methodName");
			responses.add(messageID);
			if(mName.equals("moveVertical")) {
				if(maxV==0) {
					maxV=Math.min(maxV, messageID);
				}else {
					maxV=Math.max(maxV, messageID);
				}
				if(messageID==maxV) {
					currentV=value;
				}else {
					currentV=currentV;
				}
			}
			if(mName.equals("moveHorizontal")) {
				if(maxH==0) {
					maxH=Math.min(maxH, messageID);
				}else {
					maxH=Math.max(maxH, messageID);
				}
				if(messageID==maxH) {
					currentH=value;
				}else {
					currentH=currentH;
				}
			}
			System.out.println(r);
		}
		fb.setResultH(currentH);
		fb.setResultV(currentV);
		}
		return fb;
		}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		isrunning=true;
		
	}
}

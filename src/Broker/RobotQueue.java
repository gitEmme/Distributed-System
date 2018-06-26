package Broker;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import Middleware.CEnvelope;
import Middleware.Connection;

public class RobotQueue implements Runnable{
	private String robotName;
	//map robotName --> list of messages to forward to the robot
	private HashMap<String,LinkedList<JSONObject>> robotQueue= new HashMap();
	private HashMap<String,String> robotAddressMap = new HashMap();
	private int countV=0;
	private int countH=0;
	private boolean isRunning=false;
	private ConcurrentHashMap<String,String> lastContactedRobot= new ConcurrentHashMap();
	private static ConcurrentLinkedQueue<String> clientsAlive= new ConcurrentLinkedQueue<String>();
	private Connection network=new Connection();
	private String sorgente;
	private Thread ck;
	private NameService maps=NameService.getInstance();
	
	
	
	public void addRobot(String robotName,String robotAddress) {
		robotQueue.put(robotName, new LinkedList());
		robotAddressMap.put(robotName, robotAddress);
	}
	
	public void removeRobot(String robotName) {
		robotQueue.remove(robotName);
		robotAddressMap.remove(robotName);
	}
	
	public LinkedList<JSONObject> getRobotQueue(String robotName) {
		return robotQueue.get(robotName);
	}
	
	public void addMessageForRobot(String robotName, JSONObject msg) throws InterruptedException {
		getRobotQueue(robotName).add(msg);
		forwardToRobot(robotName);
		System.out.println("ROBOTNAME    "+robotName);
	
	}
	
	
	
	public void forwardToRobot(String robotName) throws InterruptedException {
		int destPort=0;
		int currentV=0;
		int currentH=0;
		LinkedList<JSONObject> robotMsgList=getRobotQueue(robotName);
		HashMap<String,Integer> registryPort=maps.getServicePortMap(robotName);
		for(JSONObject m :robotMsgList ) {
			JSONObject firstMsg= robotMsgList.getFirst();
			JSONObject testa=(JSONObject) firstMsg.get("header");
			this.sorgente=(String)testa.get("sourceName");
			lastContactedRobot.put(sorgente, robotName);
			if(clientsAlive.contains(sorgente)) {
				String destAddress=robotAddressMap.get(robotName);
				JSONObject body=(JSONObject) firstMsg.get("body");
				String methodName=(String) body.get("methodName");
				if(methodName.equals("moveHorizontal")) {
					destPort=registryPort.get("moveHorizontal");
					countH++;
					countV=0;
				}
				if(methodName.equals("moveVertical")) {
					destPort=registryPort.get("moveVertical");
					countV++;
					countH=0;
				}
				if(methodName.equals("grabRelease")) {
					destPort=registryPort.get("grabRelease");
				}
				
				//System.out.println("From queue:   "+firstMsg.toJSONString());
				if(countV==2 || countH==2) {
					JSONObject cHead=(JSONObject)firstMsg.get("header");
					String cDest=(String) cHead.get("destName");
					String sName=(String) cHead.get("sourceName");
					int cID= Integer.parseInt((String)cHead.get("messageID"));
					System.out.println("Sending stopMovement "+cID);
					stopMovement(cID, cDest, destAddress,sName);
					//Thread.sleep(1000);
					if(methodName.equals("moveVertical")) {
						countV-=2;
					}
					if(methodName.equals("moveHorizontal")) {
						countH-=2;
					}
				}
				System.out.println("Sending msg:   "+firstMsg.toJSONString());
				System.out.println(firstMsg + destAddress + destPort);
				network.sendTo(firstMsg, destAddress, destPort);
				robotMsgList.removeFirst();	
				}
			}
			//System.out.println("List results: ");
			//System.out.println(clientMsgMap.toString());
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
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		isRunning=true;
	}
	
	public void addClientAlive(String clientN) {
		this.clientsAlive.add(clientN);
	}
	public void removeDead(String clientN) {
		this.clientsAlive.remove(clientN);
	}
	
	public String getLastContactedRobot(String clientN){
		return this.lastContactedRobot.get(clientN);
		
	}
	
	
}

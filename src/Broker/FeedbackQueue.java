package Broker;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.json.simple.JSONObject;

import Middleware.Connection;

public class FeedbackQueue implements Runnable{
	private String robotName;
	//map robotName --> list of messages to forward to the robot
	private HashMap<String,LinkedList<JSONObject>> robotFeedbackQueue= new HashMap();
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
		robotFeedbackQueue.put(robotName, new LinkedList());
		robotAddressMap.put(robotName, robotAddress);
	}
	
	public void removeRobot(String robotName) {
		robotFeedbackQueue.remove(robotName);
		robotAddressMap.remove(robotName);
	}
	
	public LinkedList<JSONObject> getRobotQueue(String robotName) {
		return robotFeedbackQueue.get(robotName);
	}
	
	public void addFeedbackForRobot(String robotName, JSONObject msg) throws InterruptedException {
		getRobotQueue(robotName).add(msg);
		forwardToRobot(robotName);
		System.out.println("ROBOTNAME    "+robotName);
	
	}
	
	
	
	public void forwardToRobot(String robotName) throws InterruptedException {
		int currentV=0;
		int currentH=0;
		LinkedList<JSONObject> robotMsgList=getRobotQueue(robotName);
		HashMap<String,Integer> registryPort=maps.getServicePortMap(robotName);
		String destAddress=robotAddressMap.get(robotName);
		int destPort=registryPort.get("feedback");
		System.out.println("PORT FOOD "+destPort);
		for(JSONObject m :robotMsgList ) {
			JSONObject firstMsg= robotMsgList.getFirst();
			JSONObject testa=(JSONObject) firstMsg.get("header");
			this.sorgente=(String)testa.get("sourceName");
			lastContactedRobot.put(sorgente, robotName);
				network.sendTo(firstMsg, destAddress, destPort);
				robotMsgList.removeFirst();	
				}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		isRunning=true;
	}
}

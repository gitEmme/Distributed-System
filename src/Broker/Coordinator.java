package Broker;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import Middleware.CEnvelope;
import Middleware.CHeader;
import Middleware.CParameter;
import Middleware.CProcedure;
import Middleware.CResult;
import Middleware.Connection;


public class Coordinator implements Runnable {
	private static Logger log=Logger.getLogger("Coordinator busy");
	private Connection network= new Connection();
	private int brokerPort=50001;
	private String brokerAddress;
	private NameService registered = new NameService();
	private LinkedList<JSONObject> responses;
	private JSONObject received;
	private ConcurrentHashMap<JSONObject,String> clientMsgMap= new ConcurrentHashMap();
	private ConcurrentHashMap<String,CResult> mappaFood= new ConcurrentHashMap();
	private ConcurrentHashMap<String,Long> timestampFood= new ConcurrentHashMap();

	private LinkedList<JSONObject> robotMsgList= new LinkedList();
	HashMap<JSONObject,String>  msgAddr= new HashMap();
	HashMap<JSONObject,CEnvelope>  msgEnv= new HashMap();
	HashMap<Integer,String> checkResponse = new HashMap();
	private boolean isHorizontalBusy=false;
	private boolean isVerticalBusy=false;
	private boolean isOpenCloseBusy=false;
	private int countV=0;
	private int countH=0;
	private long lastFood;
	private ConcurrentHashMap<String,Long> timestampClients= new ConcurrentHashMap();
	private long lastClient;
/**!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!TO DO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * IMPLEMENTA IL CAZZO DI RIMUOVI ROBOT DAI DISPONIBILI!!!!!!*/	
	/// servirebbe un lastFood per ogni robot, per ora solo per uno
	
	private RobotQueue handler= new RobotQueue();
	
	public Coordinator() {
		
	}
	
	public Coordinator(String brokerAddress) {
		this.brokerAddress=brokerAddress;
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method Stub
		while(true) {
			try {
				processMsg();
				//forwardToRobot();
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
		
		Enumeration<String> robots=timestampFood.keys();
		while (robots.hasMoreElements()) {
			String robotN=robots.nextElement();
			long lastF=timestampFood.get(robotN);
			if(System.currentTimeMillis() -lastF >= 3000) {
				//System.out.println(System.currentTimeMillis() -lastF);
				registered.removeServers(robotN);
				//remove its message list
				handler.removeRobot(robotN);
				timestampFood.replace(robotN, System.currentTimeMillis());
				////// implements remove robot here !!!!!
			}
		}
		Enumeration<String> clients=timestampClients.keys();
		while (clients.hasMoreElements()) {
			String clientN=clients.nextElement();
			long lastC=timestampClients.get(clientN);
			//System.out.println(System.currentTimeMillis() -lastC);
			if(System.currentTimeMillis() -lastC >= 1500) {
				
				registered.removeClients(clientN);
				//removed CLIENT from registered list
				System.out.println("***********CLIENT "+clientN+" IS OFF:DEREGISTERED************");
				////// implements remove robot here !!!!!
				String robotToStop=handler.getLastContactedRobot(clientN);
				String robAddr=registered.getServiceAddress(robotToStop);
				//handler.stopMovement(123456,robotToStop , robAddr, clientN);
				handler.removeDead(clientN);
				handler.stopMovement(123456, robotToStop, robAddr, clientN);
				timestampClients.replace(clientN, System.currentTimeMillis());
			}
		}
		
		
		//System.out.println("Coordinator received message: "+received.toJSONString());
		CEnvelope env= new CEnvelope();
		CHeader h= new CHeader();
		JSONObject header= (JSONObject) received.get("header");
		h.setMessageID((String)header.get("messageID"));
		h.setSourceName((String) header.get("sourceName"));
		h.setDestName((String) header.get("destName"));
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
		String dest=env.getHeader().getDestName();
		System.out.println("DESTINATION "+dest);
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
				//this.lastClient=System.currentTimeMillis();
				//timestampClients.put(h.getSourceName(), this.lastClient);
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
				//destPort=registered.getServicePort(h.getSourceName());
				network.sendTo(jenv, destAddress, 50020);
				
			}
			if(method.equals("feedback")) {
				this.lastFood=System.currentTimeMillis();
				JSONObject feedBack=(JSONObject)received.get("result");
				int fbV=Integer.parseInt((String)feedBack.get("vertical"));
				int fbH=Integer.parseInt((String)feedBack.get("horizontal"));
				String destBack=env.getHeader().getSourceName();
				if(!timestampFood.containsKey(destBack)) {
					timestampFood.put(destBack, this.lastFood);
				}else {
					timestampFood.replace(destBack, this.lastFood);
				}
				String destAddr=registered.getServiceAddress(destBack);
				System.out.println("hor "+fbH+" vert "+fbV);
				network.sendTo(received, destAddr, 50021);
				CResult food=new CResult();
				food.setResultH(fbH);
				food.setResultV(fbV);
				mappaFood.put(destBack, food);
				
			}
			if(method.equals("getResults")) {
				System.out.println("Coordinator received getResults from client: ");
				//System.out.println(received.toJSONString());
				this.lastClient=System.currentTimeMillis();
				if(!timestampClients.containsKey(h.getSourceName())) {
					timestampClients.put(h.getSourceName(), this.lastClient);
				}else {
					timestampClients.replace(h.getSourceName(), this.lastClient);
				}
				JSONObject resEnv=new JSONObject();
				JSONObject jheader=new JSONObject();
				JSONObject jbody=new JSONObject();
				JSONObject jresult=new JSONObject();
				jheader.put("sourceName",env.getHeader().getDestName());
				jheader.put("destName", env.getHeader().getSourceName());
				jheader.put("messageID","resultList");
				jbody.put("methodName", "getResult");
				jbody.put("returnType", "JSONArray");
				resEnv.put("header", jheader);
				resEnv.put("body", body);
				String robotName=env.getProcedure().getParam(1).getName();
				if(robotName!=null) {
					CResult food = mappaFood.get(robotName);
					int hF= food.getResultH();
					int vF= food.getResultV();
					jresult.put("vertical", Integer.toString(vF));
					jresult.put("horizontal",Integer.toString(hF));
					resEnv.put("result", jresult);
					String destAddress=registered.getServiceAddress(h.getSourceName());
					//destPort=registered.getServicePort(h.getSourceName());
					destPort=50012;
					//System.out.println("AnswerBack :");
					//System.out.println(resEnv.toJSONString());
					network.sendTo(resEnv, destAddress, destPort);
				}
			}
		}else {
			if(registered.getClientList().contains(dest) || methodName.equals("stopMovement")) {
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
				if(dest!=null&&received!=null) {
					handler.addMessageForRobot(dest, received);
				}
				/*
				msgAddr.put(received, dest);
				robotMsgList.add(received);
				msgEnv.put(received, env);
				*/
			}
		}
		return env;
		}
	
	
	
	public String registerServer(String serviceName, String serviceAddress, int servicePort) {
		if(!registered.isServerRegistered(serviceName)) {
			registered.addServiceLocation(serviceName, serviceAddress, servicePort);
			registered.addServer(serviceName, serviceAddress);
			JSONObject ack= new JSONObject();
			ack.put("registrationResult",serviceName+ " registered");
			handler.addRobot(serviceName, serviceAddress);
			network.sendTo(ack, serviceAddress, servicePort);
			//create queue for registered robot
			
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
			handler.addClientAlive(serviceName);
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
	
	
	public static void main(String[] args) {
		String broker=new String("192.168.0.105");
		
		//String broker= args[0];
		
		//pass just the broker address, that is the pi address
		Coordinator c= new Coordinator(broker);
		Thread t= new Thread(c);
		t.start();
	}
	
}

package Middleware.ClientMiddleware;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import Middleware.*;
public class Stub implements Runnable,MoveAround {
	private Connection network=new Connection();
	private JSONObject message;
	private static int counter=1;
	
	private int stubPort=0;
	private String stubAddress;
	private int port;
	private String address;
	
	private String brokerAddr= new String("localhost");
	private int brokerPort=50001;
	private String clientName; //added client name to identify the stub
	private String serviceName;
	private Thread moveH, moveV;
	private boolean running= false;
	private int resultV, resultH;
	private boolean msgArrived=false;
	public Stub(int port, String address, int stubPort, String stubAddress) {
		this.port=port;
		this.address=address;
		this.stubAddress=stubAddress;
		this.stubPort=stubPort;
	}
	/* added constructor */
	public Stub(String clientName,String serviceName) {
		this.clientName=clientName;
		this.serviceName=serviceName;
	}
	
public int moveHorizontal(int integer, String string) {
	moveH= new Thread("horizontal movement") {
		public void run() {
			message=new JSONObject();
			JSONObject header=new JSONObject();
			//header.put("serviceName","MoveAround");
			header.put("sourceName",clientName);
			header.put("destName", serviceName);
			//header.put("stubAddress",stubAddress);
			//header.put("stubPort",stubPort);
			header.put("messageID","moveHorizontal"+Integer.toString(counter));
			message.put("header", header);
			JSONObject body=new JSONObject();
			JSONArray params=new JSONArray();
			JSONObject param1=new JSONObject();
			param1.put("name",Integer.toString(integer));
			param1.put("position","1");
			param1.put("type","int");
			params.add(param1);
			JSONObject param2=new JSONObject();
			param2.put("name",string);
			param2.put("position","2");
			param2.put("type","String");
			params.add(param2);
			body.put("methodName","moveHorizontal");
			body.put("parameters",params);
			body.put("returnType","int");
			message.put("body", body);
			System.out.println(message.toJSONString());
			counter++;
			network=new Connection();
			network.sendTo(message,brokerAddr,brokerPort);
			JSONObject res= (JSONObject) network.recvObjFrom(50002);
			resultH=(int)res.get("result");
		}
	};
	moveH.start();
	try {
		moveH.join();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return this.resultH;
	
	}

public int moveVertical(int integer, String string) {
	moveV= new Thread("vertical movement") {
		public void run() {
		message=new JSONObject();
		JSONObject header=new JSONObject();
		//header.put("serviceName","MoveAround");
		header.put("sourceName",clientName);
		header.put("destName", serviceName);
		//header.put("stubAddress",stubAddress);
		//header.put("stubPort",stubPort);
		header.put("messageID","moveVertical"+Integer.toString(counter));
		message.put("header", header);
		JSONObject body=new JSONObject();
		JSONArray params=new JSONArray();
		JSONObject param1=new JSONObject();
		param1.put("name",Integer.toString(integer));
		param1.put("position","1");
		param1.put("type","int");
		params.add(param1);
		JSONObject param2=new JSONObject();
		param2.put("name",string);
		param2.put("position","2");
		param2.put("type","String");
		params.add(param2);
		body.put("methodName","moveVertical");
		body.put("parameters",params);
		body.put("returnType","int");
		message.put("body", body);
		System.out.println(message.toJSONString());
		counter++;
		network=new Connection();
		network.sendTo(message,brokerAddr,brokerPort);
		JSONObject res= (JSONObject) network.recvObjFrom(50002);
		resultV=(int)res.get("result");
		}
	};
	moveV.start();
	try {
		moveV.join();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return resultV;
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
	body.put("methodName", "registerService");
	param1.put("name", clientName);
	param1.put("type", "String");
	param1.put("position", Integer.toString(1));
	param2.put("name", "localhost");
	param2.put("type", "String");
	param2.put("position", Integer.toString(2));
	param3.put("name", "50002");
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
	network.sendTo(env, brokerAddr, brokerPort);
}

public synchronized void giveBackResV(int resultV) {
	this.resultV=resultV;
}

public synchronized void giveBackResH(int resultH) {
	this.resultH=resultH;
}

@Override
public void run() {
	running=true;
	
}

}

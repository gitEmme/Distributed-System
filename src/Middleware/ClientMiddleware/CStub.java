package Middleware.ClientMiddleware;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import Middleware.*;
public class CStub implements MoveAround {
	private Connection network=new Connection();
	private JSONObject message;
	private static int counter=1;
	private int stubPort=0;
	private String stubAddress;
	private int port;
	private String address;
	
	public CStub(int port, String address, int stubPort, String stubAddress) {
		this.port=port;
		this.address=address;
		this.stubAddress=stubAddress;
		this.stubPort=stubPort;
	}
	
public int moveHorizontal(int integer, String string) {
	message=new JSONObject();
	JSONObject header=new JSONObject();
	header.put("serviceName","MoveAround");
	header.put("source","clientStub");
	header.put("id","moveHorizontal"+Integer.toString(counter));
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
	network.sendTo(message,this.address,port);
	JSONObject res= (JSONObject) network.recvObjFrom(this.stubPort);
	int result=(int)res.get("result");
	return result;
	}

public int moveVertical(int integer, String string) {
	message=new JSONObject();
	JSONObject header=new JSONObject();
	header.put("serviceName","MoveAround");
	header.put("source","clientStub");
	header.put("id","moveVertical"+Integer.toString(counter));
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
	network.sendTo(message,this.address,port);
	JSONObject res= (JSONObject) network.recvObjFrom(this.stubPort);
	int result=(int)res.get("result");
	return result;
	}

}
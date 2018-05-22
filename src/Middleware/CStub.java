package Middleware;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
public class CStub implements moveAround {
	private Connection sender=new Connection();
	private JSONObject message;
	private static int counter=1;
	
public int moveHorizontal(int integer, String string) {
	message=new JSONObject();
	JSONObject header=new JSONObject();
	header.put("serviceName","IDLMoveAround");
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
	sender.sendTo(message, "localhost", 50001);
	return 1;
	}

public int moveVertical(int integer, String string) {
	message=new JSONObject();
	JSONObject header=new JSONObject();
	header.put("serviceName","IDLMoveAround");
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
	sender.sendTo(message, "localhost", 50001);
	return 1;
	}

}
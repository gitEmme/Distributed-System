package Middleware;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Stub implements moveAround {
	private Sender sender=new Sender();
	private Receiver receiver;
	private JSONObject message;
	private static int counter=0;
	private int port;
	private String address;
	public Stub(int port, String address) {		
		this.port=port;
		this.address=address;
	}
	
	public void marshall(String methodName,int integer,String string) {
		message=new JSONObject();
		JSONObject header=new JSONObject();
		header.put("serviceName","moveAround");
		header.put("source","clientStub");
		header.put("id",methodName+Integer.toString(counter));
		message.put("header", header);
		JSONObject body=new JSONObject();
		JSONArray params=new JSONArray();
		JSONObject param1=new JSONObject();
		param1.put("name", Integer.toString(integer));
		param1.put("position",Integer.toString(1));
		param1.put("type","int");
		JSONObject param2=new JSONObject();
		param2.put("name", string);
		param2.put("position",Integer.toString(2));
		param2.put("type","String");
		params.add(param1);
		params.add(param2);
		body.put("methodName", methodName);
		body.put("parameters", params);
		body.put("returnType", "int");
		message.put("body", body);
		System.out.println(message.toJSONString());
		counter++;
		sender.sendTo(message,this.address,port);
	}


	@Override
	public int moveHorizontal(int integer, String string) {
		marshall("moveHorizontal",integer,string);
		return 1;
	}

	@Override
	public int moveVertical(int integer, String string) {
		marshall("moveVertical",integer,string);
		return 0;
	}

}

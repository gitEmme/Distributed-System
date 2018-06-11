package Middleware.ServerMiddleware;
import Middleware.*;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import Server.ActionServer;

public class CSkeleton implements Runnable, MoveAround {
	private Connection network;
	ActionServer action=new ActionServer();
	private int port;
	private int brokerPort=50001;
	private String brokerAddr= new String("localhost");
	public CSkeleton(int port) {
		this.port=action.getServerPort();
	}
	
public int execute(CProcedure p) {
		int result=0;
		switch (p.getName()) {
			case "moveHorizontal":
				result= moveHorizontal((int)p.getParam(1).getValue(p.getParam(1).getName()), (String)p.getParam(2).getValue(p.getParam(2).getName()));
				break;
			case "moveVertical":
				result= moveVertical((int)p.getParam(1).getValue(p.getParam(1).getName()), (String)p.getParam(2).getValue(p.getParam(2).getName()));
				break;
			}
			return result;
			}

	public CEnvelope unmarshall() {
		network=new Connection();
		JSONObject received=(JSONObject) network.recvObjFrom(this.port);
		CEnvelope env= new CEnvelope();
		CHeader h= new CHeader();
		JSONObject header= (JSONObject) received.get("header");
		h.setProcedureID((String)header.get("messageID"));
		h.setSourceName((String) header.get("sourceName"));
		h.setStubAddress((String)header.get("destName"));
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

	public void run() {
	registerService();
	while(true) {
		CEnvelope envelope=unmarshall();
		CHeader head=envelope.getHeader();
		CProcedure invoked=envelope.getProcedure();
		int p=execute(invoked);
		System.out.println("executed!");
		network=new Connection();
		JSONObject body=new JSONObject();
		JSONArray params=new JSONArray();
		JSONObject header= new JSONObject();
		JSONObject env= new JSONObject();
		header.put("sourceName",head.getDestName());
		header.put("destName",head.getSourceName());
		header.put("messageID", head.getProcedureID());
		body.put("methodName", "answerBack");
		body.put("parameters", params);
		body.put("returnType", invoked.getReturnType());
		env.put("header", header);
		env.put("body", body);
		env.put("result", p);
		network.sendTo(env, brokerAddr, brokerPort);
		}
	}

	public int moveHorizontal(int integer, String string) {
		int p = action.moveHorizontal(integer, string);
		return p;
	}

	public int moveVertical(int integer, String string) {
		int p = action.moveVertical(integer, string);
		return p;
	}

	public void registerService() {
		network=new Connection();
		JSONObject env=new JSONObject();
		JSONObject header=new JSONObject();
		JSONObject body=new JSONObject();
		JSONObject result=new JSONObject();
		JSONArray params=new JSONArray();
		JSONObject param1=new JSONObject();
		JSONObject param2=new JSONObject();
		JSONObject param3=new JSONObject();
		header.put("sourceName", action.getServerName());
		header.put("destName", "broker");
		header.put("messageID","registerMe");
		body.put("methodName", "registerService");
		param1.put("name", action.getServerName());
		param1.put("type", "String");
		param1.put("position", Integer.toString(1));
		param2.put("name", action.getServerAddress());
		param2.put("type", "String");
		param2.put("position", Integer.toString(2));
		param3.put("name", Integer.toString(action.getServerPort()));
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
	public static void main(String[] args) {
		Skeleton sk= new Skeleton(50001);
		Thread s = new Thread(sk);
		s.start();
		}
	}
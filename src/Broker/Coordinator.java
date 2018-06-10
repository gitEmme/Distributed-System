package Broker;

import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import Middleware.CEnvelope;
import Middleware.CHeader;
import Middleware.CParameter;
import Middleware.CProcedure;
import Middleware.Connection;

public class Coordinator implements Runnable {
	private Connection network= new Connection();
	private int brokerPort=50001;
	private String brokerAddress= new String("localhost");
	private NameService registered = new NameService();

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			processMsg();
		}
		
	}
	
	
	public CEnvelope processMsg() {
		network=new Connection();
		JSONObject received=(JSONObject) network.recvObjFrom(this.brokerPort);
		//System.out.println(received.toJSONString());
		CEnvelope env= new CEnvelope();
		CHeader h= new CHeader();
		JSONObject header= (JSONObject) received.get("header");
		h.setProcedureID((String)header.get("messageID"));
		//h.setStubAddress((String)header.get("stubAddress"));
		//h.setStubPort((Integer)header.get("stubPort"));
		h.setServiceName((String)header.get("serviceName"));
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
		System.out.println(dest);
		if(dest.equals("broker")) {
			CProcedure called=env.getProcedure();
			String method=called.getName();
			if(method.equals("registerService")) {
				String result=registerService(called.getParam(1).getName(),called.getParam(2).getName(),Integer.parseInt(called.getParam(3).getName()));
				System.out.println("result: "+result);
			}
		}else {
			String destAddress=registered.getServiceAddress(dest);
			System.out.println(destAddress);
			int destPort=registered.getServicePort(dest);
			System.out.println(destPort);
			network.sendTo(received, destAddress, destPort);
			
		}
		return env;
		}
	
	public String registerService(String serviceName, String serviceAddress, int servicePort) {
		if(!registered.isRegistered(serviceName)) {
			registered.addServiceLocation(serviceName, serviceAddress, servicePort);
			registered.addServers(serviceName, serviceAddress);
			return "Service "+serviceName+" registered!";
		}else {
			return "Service  "+serviceName+" already registered!";
		}
		
	}
	
	public static void main(String[] args) {
		Coordinator c= new Coordinator();
		Thread t= new Thread(c);
		t.start();
	}
}

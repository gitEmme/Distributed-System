package Middleware;


import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import simulation.Action;
public class Skeleton implements Runnable, moveAround {
	private Sender sender;
	private Receiver receiver;
	Action action=new Action();
	private int port;
	public Skeleton(int port) {
		this.port=port;
	}
	
	public void execute(String mName, int first,String second ) {
		switch (mName) {
		case "moveVertical":
			moveVertical(first,second);
			break;
		case "moveHorizontal":
	    	 moveHorizontal(first,second);
	    	 break;
	    }
	}
	public CProcedure unmarshall() {
		receiver=new Receiver();
		JSONObject received=(JSONObject) receiver.recvObjFrom(this.port);
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
		System.out.println(body.toJSONString());
		return procedure;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			CProcedure invoked=unmarshall();
		    execute(invoked.getName(),Integer.parseInt(invoked.getParam(1).getName()),invoked.getParam(2).getName());
		    System.out.println("executed!");
		}
		
	}

	@Override
	public int moveHorizontal(int integer, String string) {
		//action.setMove(string);
		int p =action.moveHorizontal(integer, string);
		return p;
	}

	@Override
	public int moveVertical(int integer, String string) {
		//action.setMove(string);
		int p=action.moveVertical(integer, string);
		return p;
	}
	
	public static void main(String[] args) {
		Skeleton sk= new Skeleton(50001);
		Thread s = new Thread(sk);
		s.start();
	}

}

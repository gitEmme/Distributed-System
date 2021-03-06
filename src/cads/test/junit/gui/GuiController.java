package cads.test.junit.gui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import Middleware.*;
import Middleware.ClientMiddleware.CStubF;
import Middleware.ClientMiddleware.CStubH;
import Middleware.ClientMiddleware.CStubOC;
import Middleware.ClientMiddleware.CStubV;
public class GuiController implements Runnable{
	private Connection network=new Connection();
	private JSONObject message;
	private CStubH cH;
	private CStubV cV;
	private CStubOC cOC;
	private CStubF cF;
	
	
	private String clientName;
	private String clientAddr;
	private String serverName;
	private int clientPort=50002;
	private int  clientPortRes=50012;
	private int clientRobPort=50020;
	private boolean isrunning=false;
	private String brokerAddr= new String("localhost");
	private int brokerPort=50001;
	private boolean running= false;
	private int result;
	private LinkedList<Integer>responses =new LinkedList();
	private HashMap<Integer,Integer> VFb= new HashMap();
	private HashMap<Integer,Integer> HFb= new HashMap();
	private int currentH=0;
	private int currentV=0;
	private Thread h,v,oc;
	private int countExc=0;
	private PortFinder finder=new PortFinder();
	private Registration reg;
	
	
	public GuiController(String clientName,String clientAddr) {
		this.clientName=clientName;
		this.clientAddr=clientAddr;
		
	}
	
	public GuiController(String clientName,String clientAddr, int clientPort,String brokerAddr) {
		this.clientName=clientName;
		this.clientAddr=clientAddr;
		this.clientPort=clientPort;
		this.brokerAddr=brokerAddr;
		reg= new Registration(this.clientName, this.brokerAddr, this.clientAddr);
	
	}
	
	public int moveHorizontal(final int transactionID, final int percent) throws InterruptedException {
		h=new Thread("sending-Thread") {
			public void run() {
		cH=new CStubH(GuiController.this.clientName,GuiController.this.serverName,GuiController.this.brokerAddr);
		GuiController.this.result=cH.moveHorizontal(transactionID, percent);
		/*try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!responses.contains(transactionID)) {
			Stub.this.result=cH.moveHorizontal(transactionID, percent);
		}
		*/
			};
		};
		h.start();
		return this.result;
		
		}
	
	public int moveVertical(final int transactionID, final int percent) throws InterruptedException {
		v=new Thread("sending-Thread") {
			public void run() {
		cV=new CStubV(GuiController.this.clientName,GuiController.this.serverName,GuiController.this.brokerAddr);
		result=cV.moveVertical(transactionID, percent);
			};
		};
		v.start();
		return result;
		}
	
	public int grabRelease(final int transactionID, final String movement) throws InterruptedException {
		oc=new Thread("sending-Thread") {
			public void run() {
		cOC=new CStubOC(GuiController.this.clientName,GuiController.this.serverName,GuiController.this.brokerAddr);
		result=cOC.grabRelease(transactionID, movement);
			};
		};
		oc.start();
		return result;
	}
	
	public void setCurrentServer(String serverName) {
		this.serverName=serverName;
	}
	
	public void registerClient() {
		this.clientPort=finder.findFreePort();
		this.clientPortRes=finder.findFreePort();
		this.clientRobPort=finder.findFreePort();
		reg.registerClient( "registerClient",clientPort);
		reg.registerClient("getServiceList", clientRobPort);
		reg.registerClient("getResults", clientPortRes);
		}
	
	public LinkedList<String> getRobotList() {
		network=new Connection();
		JSONObject env=new JSONObject();
		JSONObject header=new JSONObject();
		JSONObject body=new JSONObject();
		JSONObject result=new JSONObject();
		JSONArray params=new JSONArray();
		header.put("sourceName", this.clientName);
		header.put("destName", "broker");
		header.put("messageID","giveMeServiceList");
		body.put("methodName", "getServiceList");
		body.put("parameters", params);
		body.put("returnType", "Set<String>");
		env.put("header", header);
		env.put("body", body);
		env.put("result", result);
		int tries=5;
		boolean receivedResponse= false;
		boolean sent=false;
		JSONObject received;
		do{
			network.sendTo(env, brokerAddr, brokerPort);
			sent = true;
			received=(JSONObject) network.recvObjFrom(clientRobPort,false);
			if (received!=null) {
				System.out.println(received.toJSONString());
				receivedResponse=true;
			}else{
				tries -=1;
				System.out.println("Timed out:" + Integer.toString(tries) + " tries left");
				}
			}while(((!receivedResponse)&& tries!= 0) && (!sent));
		LinkedList<String> robotList = new LinkedList();
		JSONArray jrobotList=(JSONArray) received.get("result");
		Iterator<String> robotIterator =jrobotList.iterator();
		while(robotIterator.hasNext()) {
			String r= robotIterator.next();
			System.out.println(r);
			robotList.add(r);
		}
		System.out.println(responses.toString());
		return robotList;
		}
	
/***********************************************************************************************/	
	// feedback da robot # invia direttamente dal metodo givefeedbackbyjsonto
	//tieni tempo ultimo feedback al coordinatore cosi sai quando il robot è off e manda risposta cosi il robot sa quando il coordinatore è off 
	// registra il cliente con la porta del feedback 
	public CResult getResults(String robotName) {
		cF=new CStubF(GuiController.this.clientName,GuiController.this.serverName,GuiController.this.brokerAddr);
		network=new Connection();
		int tries=5;
		boolean receivedResponse= false;
		boolean sent=false;
		JSONObject received;
		CResult fb= new CResult();
		do{	
			cF.feedback(GuiController.this.serverName);
			//network.sendTo(env, brokerAddr, brokerPort);
			sent = true;
			received=(JSONObject) network.recvObjFrom(clientPortRes,false);
			if(received==null) {
				setCountExc(getCountExc() + 1);
				//tries --;
				if(countExc==2) {
					System.out.println("**************Coordinator down!!!!!***************");
				}
			}else {
				setCountExc(0);
				System.out.println(received.toJSONString());
				receivedResponse=true;
			}
			}while(((!receivedResponse)&& tries> 0) && (!sent));
		if(!(received==null)) {
		int resV=0;
		int resH=0;
		JSONObject feedBack=(JSONObject)received.get("result");
		resV=Integer.parseInt((String)feedBack.get("vertical"));
		resH=Integer.parseInt((String)feedBack.get("horizontal"));
		fb.setResultH(resH);
		fb.setResultV(resV);
		//System.out.println(resV+" "+resH);
		}
		return fb;
		
		}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		isrunning=true;
		
	}

	public int getCountExc() {
		return countExc;
	}

	public void setCountExc(int countExc) {
		this.countExc = countExc;
	}
}

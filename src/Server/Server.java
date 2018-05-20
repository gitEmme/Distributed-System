package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


import org.cads.ev3.middleware.CaDSEV3RobotHAL;
import org.cads.ev3.middleware.CaDSEV3RobotType;
import org.cads.ev3.middleware.hal.ICaDSEV3RobotFeedBackListener;
import org.cads.ev3.middleware.hal.ICaDSEV3RobotStatusListener;
import org.json.simple.JSONObject;

public class Server implements  ICaDSEV3RobotStatusListener, ICaDSEV3RobotFeedBackListener {

	private CaDSEV3RobotHAL simul;
	static ICaDSEV3RobotStatusListener statusListener;
	static ICaDSEV3RobotFeedBackListener feedbackListener;
	/*
	public static int SERVERPORT = 4444;
	private DatagramSocket socket;
	private DatagramPacket packet = new DatagramPacket(new byte[255], 255);
	private String movement;
	private int percent;
	*/
	private String movement;
	private int percent;
	
	public Server(int percent,String movement) {
		simul = CaDSEV3RobotHAL.createInstance(CaDSEV3RobotType.SIMULATION, this, this);
		this.percent=percent;
		this.movement=movement;
		}
/*
	public void run() {
		
		try {
			this.socket = new DatagramSocket(SERVERPORT);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("[Error] - Failed to create socket");
			}
		while(true) {
			try {
				packet=new DatagramPacket(new byte[255], 255);
				socket.receive(packet);
				System.out.println("Handling client at "+packet.getAddress().getHostAddress() +" on port "+ packet.getPort());
				String message = new String(packet.getData());
				message=message.trim();
				byte[] sendBack=(message+" message arrived!").getBytes();
				DatagramPacket ack= new DatagramPacket(sendBack, sendBack.length,packet.getAddress(),packet.getPort());
				socket.send(ack);
				packet.setLength(255);
				move(message);
			//socket.close();
			//System.out.println("[INFO] - Closing socket connection...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	}
	*/
	
	@Override
	public void giveFeedbackByJSonTo(JSONObject arg0) {
		// TODO Auto-generated method stub
		//state type value percent are the fields in the json object
		/*
		String positionFeedback;
		String gripperFeedback;
		String state= arg0.get("state").toString();
		String type= arg0.get("type").toString();
		if(arg0.containsKey("percent")) {
			String percent= arg0.get("percent").toString();
			int moving=Integer.parseInt(percent);
			if(state=="vertical"&&moving==0) {
				positionFeedback="at the bottom";
			}else if(state=="vertical"&&moving==100) {
				positionFeedback="at the top";
			}else if(state=="vertical"&&moving<moving+1) {
				positionFeedback="moving up";
			}else if(state=="vertical"&&moving>moving+1) {
				positionFeedback="moving up";
			}else if(state=="horizontal"&&moving==100) {
				positionFeedback="on the left";
			}else if(state=="horizontal"&&moving==0) {
				positionFeedback="on the right";
			}else if(state=="horizontal"&&moving<moving+1) {
				positionFeedback="moving left";
			}else {
				positionFeedback="moving right";
			}
			System.out.println(positionFeedback);
		}else {
			String value= arg0.get("value").toString();
			System.out.println(state+' '+type+' '+value);
			if(state=="gripper") {
				if(value=="open") {
					gripperFeedback="opened";
				}else {
					gripperFeedback="closed";
				}
				System.out.println(gripperFeedback);
			}
		}
		*/
	}
	 
	/*When we move up-down-left-right the json object returning has the following format:
	 * { "state": vertical/horizontal,"type":GRIPPER_INFO, "percent: 0-100}
	 * When we open-close the robot the json object returned has the following format:
	 * { "state":gripper,"type":GRIPPER_INFO, "value": open/close}*/
	@Override
	public void onStatusMessage(JSONObject arg0) {
		// TODO Auto-generated method stub
		//System.out.println(arg0.keySet());
		
		if(arg0.get("percent").equals(Integer.toString(this.percent))){
			simul.stop_h();
			simul.stop_v();
		}
		
		
	}
	
	public int move(int percent,String movement) {
		
		switch(movement) {
		case "left" : 
			simul.moveLeft();
			break;
		case "right" : 
			//simul.stop_h();
			simul.moveRight();
			break;
		case "up" : 
			//simul.stop_v();
			simul.moveUp();
			break;
		case "down" : 
			//simul.stop_v();
			simul.moveDown();
			break;
		case "open" : 
			//simul.stop_h();
			simul.doOpen();
			break;
		case "close" : 
			//simul.stop_h();
			simul.doClose();
			break;
		case "exit" :
			break;
		}
		return 1;
	}
	
	public static void main(String[] args) throws IOException {
		//new Server().runServer();
		//new Server().start();
		//Server server = new Server();
		//Thread s = new Thread(server);
		//server.run();
		//s.start();
		}
		
		
	}

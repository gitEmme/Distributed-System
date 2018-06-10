package Client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Scanner;

import Broker.Coordinator;
import Middleware.ClientMiddleware.CStub;
import Middleware.ClientMiddleware.Stub;
import Middleware.ServerMiddleware.CSkeleton;
import Middleware.ServerMiddleware.Skeleton;

public class Client implements Runnable{
	/*
	InetAddress serverAddress;
	//byte[] bytesToSend = "Hello from CLient!".getBytes();
	int serverPort = 50001;
	DatagramSocket socket;
	//socket.setSoTimeout(3000);	//3 seconds timeout
	
	static PrintWriter printWriter;
	static DataOutputStream output = null;
	private static Scanner sc;
	String host;
	int p;
	static LinkedList<String> toSend= new LinkedList<String>();
	*/
	private static Scanner sc;
	public Client() {
		
	}
	/*
	public Client(String serverAddress, int serverPort) throws UnknownHostException {
		this.serverAddress = InetAddress.getByName(serverAddress);
		this.serverPort = serverPort;
		try {
			this.socket= new DatagramSocket();
			this.socket.setSoTimeout(3000);
		}catch (IOException e) {
			e.printStackTrace();
			System.out.println("[Error] - Failed to create socket");
		}
	}
	*/
	public void menu() {
		System.out.println("\t\t..::Controller Simulation Menu::..");
		System.out.println("[left]");
		System.out.println("[right]");
		/*
		System.out.println("[up]");
		System.out.println("[down]");
		System.out.println("[open]");
		System.out.println("[close]");
		*/
	}
	public void menuPerc() {
		System.out.println("\t\t..::Controller Simulation Menu::..");
		System.out.println("[Insert percentage]");
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		//Stub clientStub = new Stub(50001,"localhost",50002,"localhost");
		Stub clientStub =new Stub("client1","Server1");
		clientStub.registerClient();
		new Thread(clientStub).start();
		boolean exit= false;	
		sc = new Scanner(System.in);
		int risultato=0;
		
		while(true && !exit) {
				this.menu();
				System.out.print("Command> ");
				String command = sc.next();
				if (command == "exit")
					exit=true;
				this.menuPerc();
				int p = sc.nextInt();
				System.out.println("["+command+"]");
				switch(command) {
				case "left" : 
					risultato=clientStub.moveHorizontal(p,command);
					System.out.println("position horizontal: " + Integer.toString(risultato));
					break;
				case "right" : 
					risultato=clientStub.moveHorizontal(p,command);
					System.out.println("position horizontal: " + Integer.toString(risultato));
					break;
				case "up" : 
					risultato=clientStub.moveVertical(p,command);
					System.out.println("position vertical: " + Integer.toString(risultato));
					break;
				case "down" : 
					risultato=clientStub.moveVertical(p,command);
					System.out.println("position vertical: " + Integer.toString(risultato));
					break;
				}
				
		
				
			}
		
	}
	/*
	public void move(String movement) {
		boolean exit= false;
		DatagramPacket sendPacket;
		DatagramPacket receivePacket;
		boolean receivedResponse= false;
		boolean sent=false;
		int maxTries;
		if (movement == "exit")
			exit=true;
		byte[] bytesToSend = movement.getBytes();
		sendPacket = new DatagramPacket(bytesToSend, bytesToSend.length,this.serverAddress,this.serverPort);
		
		do {
			try {
				this.socket.send(sendPacket);
				sent=true;
				System.out.println("message sent!");
				maxTries=5;
				receivedResponse= false;
				do {
					try{
						receivePacket=new DatagramPacket(new byte[255], 255);
						this.socket.receive(receivePacket);
						if (!receivePacket.getAddress().equals(this.serverAddress)) {// Check source			
							throw new IOException("Received packet from an unknown source");
						}
						receivedResponse=true;
						String response = new String(receivePacket.getData());
						System.out.println("response: " + response.trim() );
					}catch (InterruptedIOException e) {
						if(maxTries==1) {
							System.out.println("Message lost or server crash or to big latency?!");
						}else {
							maxTries -=1;
							System.out.println("Timed out:" + maxTries + " tries left");
						}
					}
					
				}while(!receivedResponse && maxTries!=0 && sent);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}while((!sent));
		if(exit==true) {
			this.socket.close();
			System.out.println("Closing Client's socket");
		}
	}
	*/
	public static void main(String[] args) throws IOException {
		/*
		Skeleton sk= new Skeleton(50001);
		Thread s = new Thread(sk);
		s.start();
		*/
		Client client = new Client(); //gustavo's ip 192.168.1.236 
		Thread c = new Thread(client);
		c.start();
		
		}

	
	}
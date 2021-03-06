package Middleware;

import java.io.*;
import java.net.*;
import java.util.HashMap;

public class Connection implements Runnable{
	//private String hostName;
	//private int desPort;
	private InetAddress sourceAddr;
	//private int stubPort;
	private boolean running=false;
	private Thread send,receive;
	private Object msg;
	private static HashMap<Integer,DatagramSocket> mappaSock=new HashMap();
	
	public Connection() {
		
	}
public void sendTo(final Object msg, final String hostName, final int desPort){ 
	send=new Thread("sending-Thread") {
		public void run() {
			try{
				InetAddress address = InetAddress.getByName(hostName);
				DatagramSocket dSock = new DatagramSocket();
				ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
				ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
				os.flush();
				os.writeObject(msg);
				os.flush();
				//retrieves byte array
				byte[] sendBuf = byteStream.toByteArray();
				DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length, address, desPort);
				int byteCount = packet.getLength();
				dSock.send(packet);
				os.close();
				dSock.close();
			}catch (UnknownHostException e){
				System.err.println("Exception:  " + e);
				e.printStackTrace();    
			}catch (IOException e){
				e.printStackTrace();
			}	
		};
	};
	send.start();
	}

public Object recvObjFrom(int desPort, boolean timeout)  {
	DatagramSocket dSock=mappaSock.get(desPort);
	if(dSock==null) {
		try {
			dSock=new DatagramSocket(desPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mappaSock.put(desPort, dSock);
	}
	try{
	  //DatagramSocket dSock=new DatagramSocket(desPort);
	  if(timeout) {
		  dSock.setSoTimeout(1500);	//8 seconds timeout
	  }else {
		  dSock.setSoTimeout(0);
	  }
	  byte[] recvBuf = new byte[5000];
	  DatagramPacket packet = new DatagramPacket(recvBuf,recvBuf.length);
	  dSock.receive(packet);
	  this.sourceAddr=packet.getAddress();
	  int byteCount = packet.getLength();
	  //this.senderAddr= packet.getAddress();
	  ByteArrayInputStream byteStream = new ByteArrayInputStream(recvBuf);
	  ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
	  Object o = is.readObject();
	  is.close();
	  //dSock.close();
	  return(o);
	}catch (IOException e){
	      System.err.println("Exception:  " + e);
	      e.printStackTrace();
	}catch (ClassNotFoundException e){
		e.printStackTrace(); 
	}
	return(null);  }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		running=true;
	}
	
}

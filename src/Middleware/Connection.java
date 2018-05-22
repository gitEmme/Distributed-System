package Middleware;

import java.io.*;
import java.net.*;

public class Connection implements Runnable{
	private String hostName;
	private int desPort;
	private InetAddress stubAddr;
	private int stubPort;
	private boolean running=false;
	private Thread send,receive;
	private Object msg;
	public Connection() {
	}
public void sendTo(Object msg, String hostName, int desPort){ 
	send=new Thread("receiving-Thread") {
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
/*
	public Object recvObjFrom(int desPort)  {
		receive=new Thread("receiving-Thread") {
			public void run() {
				//while(running) {
					try{
						  DatagramSocket dSock=new DatagramSocket(desPort);
						  byte[] recvBuf = new byte[5000];
						  DatagramPacket packet = new DatagramPacket(recvBuf,recvBuf.length);
						  dSock.receive(packet);
						  setSourceAddr(packet.getAddress());
						  setSourcePort(packet.getPort());
						  int byteCount = packet.getLength();
						  //this.senderAddr= packet.getAddress();
						  ByteArrayInputStream byteStream = new
						                              ByteArrayInputStream(recvBuf);
						  ObjectInputStream is = new
						       ObjectInputStream(new BufferedInputStream(byteStream));
						  Object o = is.readObject();
						  is.close();
						  dSock.close();
						  msg=o;
					}catch (IOException e){
						System.err.println("Exception:  " + e);
						e.printStackTrace();
				    }catch (ClassNotFoundException e){
				    	e.printStackTrace(); 
						    }
				//}
				};
		};
		receive.start();
		return msg;
	}
	*/
public Object recvObjFrom(int desPort)  {
	try{
	  DatagramSocket dSock=new DatagramSocket(desPort);
	  byte[] recvBuf = new byte[5000];
	  DatagramPacket packet = new DatagramPacket(recvBuf,recvBuf.length);
	  dSock.receive(packet);
	  this.stubAddr=packet.getAddress();
	  int byteCount = packet.getLength();
	  //this.senderAddr= packet.getAddress();
	  ByteArrayInputStream byteStream = new
	                              ByteArrayInputStream(recvBuf);
	  ObjectInputStream is = new
	       ObjectInputStream(new BufferedInputStream(byteStream));
	  Object o = is.readObject();
	  is.close();
	  dSock.close();
	  return(o);
	    }
	    catch (IOException e)
	    {
	      System.err.println("Exception:  " + e);
	      e.printStackTrace();
	    }
	    catch (ClassNotFoundException e)
	    { e.printStackTrace(); }
	    return(null);  }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		running=true;
	}
	public int getstubPort() {
		return stubPort;
	}
	public void setstubPort(int sourcePort) {
		this.stubPort = sourcePort;
	}
	public InetAddress getstubAddr() {
		return stubAddr;
	}
	public void setstubAddr(InetAddress sourceAddr) {
		this.stubAddr = sourceAddr;
	}
}

package Middleware;

import java.io.*;
import java.net.*;

public class Receiver{
	private InetAddress senderAddr;
	private DatagramSocket dSock;
	
	public Object recvObjFrom(int desPort)  {
		try{
		  dSock=new DatagramSocket(desPort);
		  byte[] recvBuf = new byte[5000];
		  DatagramPacket packet = new DatagramPacket(recvBuf,recvBuf.length);
		  this.dSock.receive(packet);
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
	public String getSenderAddr() {
		return this.senderAddr.toString();
		
	}
		}

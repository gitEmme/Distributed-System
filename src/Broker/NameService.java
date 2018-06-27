package Broker;

import java.util.HashMap;
import java.util.Set;

public class NameService {
	private HashMap<String,String> registry = new HashMap(); // map name --> ip , all together clients and servers
	private HashMap<String,Integer> registryPort = new HashMap(); //map name --> port , all together clients and servers
	private HashMap<String,String> robotAddrMap = new HashMap(); //map serverName --> ip
	private HashMap<String,String> registryClients = new HashMap(); //map clientName --> ip
	private HashMap<String,HashMap<String,Integer>> registryServicePort = new HashMap(); //map robot --> (serviceName --> port)
	private HashMap<String,HashMap<String,Integer>> registryClientPort = new HashMap();
	
private static NameService maps= new NameService();
	
private NameService() {
	
}

public static NameService getInstance() {
	return maps;
}
	
public String getServiceAddress(String serviceName) {
	if(registry.containsKey(serviceName)) {
		return registry.get(serviceName);
	}else {
		return "Service not available!";
	}
}

public int getServicePort(String serviceName) {
	if(registryPort.containsKey(serviceName)) {
		return registryPort.get(serviceName);
	}else {
		return -1;
	}
}

public void addServiceLocation(String serviceName, String serviceAddress, int servicePort) {
	registry.put(serviceName, serviceAddress);
	registryPort.put(serviceName, servicePort);
	System.out.println(registry.toString());
	System.out.println(registryPort.toString());
}

public void addClient(String clientName, String clientAddress) {
	registryClients.put(clientName, clientAddress);
	System.out.println(registryClients.toString());
}

public void addServer(String serviceName, String serviceAddress) {
	robotAddrMap.put(serviceName, serviceAddress);
	System.out.println(robotAddrMap.toString());
}

//remove just server from its map 
public void removeServers(String serviceName) {
	robotAddrMap.remove(serviceName);
	registry.remove(serviceName);
	registryPort.remove(serviceName);
	registryServicePort.remove(serviceName);
	}

//remove a client or a server from all the maps
public void removeClients(String clientName) {
	registry.remove(clientName);
	registryPort.remove(clientName);
	registryClients.remove(clientName);
	registryClientPort.remove(clientName);
}

public Set<String> getAvailable(){
	return registry.keySet();
}

public Set<String> getAvailableRobot(){
	return robotAddrMap.keySet();
}

public boolean isServerRegistered(String serviceName) {
	if(registry.containsKey(serviceName)) {
		return true;
	}else {
		return false;
	}
}

public boolean isClientRegistered(String serviceName) {
	if(registryClients.containsKey(serviceName)) {
		return true;
	}else {
		return false;
	}
}

public Set<String> getClientList(){
	return registryClients.keySet();
}

public HashMap<String,HashMap<String,Integer>> getRegistryServicePort() {
	return registryServicePort;
}

public HashMap<String,Integer> getServicePortMap(String robotName){
	return registryServicePort.get(robotName);
}

public void setRegistryServicePort(HashMap<String,HashMap<String,Integer>> registryServicePort) {
	this.registryServicePort = registryServicePort;
}

public boolean isRegisteredRobotService(String robotName, String serviceName) {
	if(registryServicePort.containsKey(robotName)) {
		return registryServicePort.get(robotName).containsKey(serviceName);
	}else {
		return false;
	}
	
}

public void addServicePort(String robotName,String serviceName,int port) {
	if(registryServicePort.containsKey(robotName)) {
		registryServicePort.get(robotName).put(serviceName, port);
	}else {
		HashMap<String,Integer> service=new HashMap();
		service.put(serviceName, port);
		registryServicePort.put(robotName,service);
	}
}

public boolean isRegisteredClientService(String clientName, String serviceName) {
	if(registryClientPort.containsKey(clientName)) {
		return registryClientPort.get(clientName).containsKey(serviceName);
	}else {
		return false;
	}
	
}

public void addClientPort(String clientName,String serviceName,int port) {
	if(registryClientPort.containsKey(clientName)) {
		registryClientPort.get(clientName).put(serviceName, port);
	}else {
		HashMap<String,Integer> service=new HashMap();
		service.put(serviceName, port);
		registryClientPort.put(clientName,service);
	}
}

public int getClientPort(String clientName, String serviceName) {
	int servicePort=0;
	if(registryClientPort.containsKey(clientName)) {
		if(registryClientPort.get(clientName).containsKey(serviceName)) {
			servicePort=registryClientPort.get(clientName).get(serviceName);
			}
	}
	return servicePort;
	}
}
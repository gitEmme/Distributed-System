package Broker;

import java.util.HashMap;
import java.util.Set;

public class NameService {
	private HashMap<String,String> registry = new HashMap(); // map name --> ip , all together clients and servers
	private HashMap<String,Integer> registryPort = new HashMap(); //map name --> port , all together clients and servers
	private HashMap<String,String> registryService = new HashMap(); //map serverName --> ip
	private HashMap<String,String> registryClients = new HashMap(); //map clientName --> ip

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
	registryService.put(serviceName, serviceAddress);
	System.out.println(registryService.toString());
}

//remove just server from its map 
public void removeServers(String serviceName) {
	registryService.remove(serviceName);
	registry.remove(serviceName);
	registryPort.remove(serviceName);
}

//remove a client or a server from all the maps
public void removeClients(String serviceName) {
	registry.remove(serviceName);
	registryPort.remove(serviceName);
	registryClients.remove(serviceName);
}

public Set<String> getAvailable(){
	return registry.keySet();
}

public Set<String> getAvailableRobot(){
	return registryService.keySet();
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

}
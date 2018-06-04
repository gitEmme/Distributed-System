package Broker;

import java.util.HashMap;
import java.util.Set;

public class NameService {
	private HashMap<String,String> registry = new HashMap();
	private HashMap<String,Integer> registryPort = new HashMap();

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
}

public void removeService(String serviceName) {
	registry.remove(serviceName);
	registryPort.remove(serviceName);
}

public Set<String> getAvailable(){
	return registry.keySet();
}

public boolean isRegistered(String serviceName) {
	if(registry.containsKey(serviceName)) {
		return true;
	}else {
		return false;
	}
}

}
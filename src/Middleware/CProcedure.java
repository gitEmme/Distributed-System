package Middleware;

public class CProcedure {
	private String serviceName;
	private String interfaceName;
	private String name;
	private String returnType;
	private java.util.HashMap<Integer,CParameter> mParams;
	
	public CProcedure(String name,String returnType) {
		this.setName(name);
		this.setReturnType(returnType);
		this.mParams = new java.util.HashMap();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public void AddParam(CParameter newParam){
		mParams.put(newParam.getPosition(), newParam);
	}
	public CParameter getParam(int position) {
		return mParams.get(position);
	}
	public int GetParamsCount(){
	    return mParams.size();
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
}

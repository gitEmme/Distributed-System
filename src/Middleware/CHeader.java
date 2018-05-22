package Middleware;

public class CHeader {
	private int stubPort=0;
	private String stubAddress=new String();
	private int skeletonPort=0;
	private String skeletonAddress=new String();
	private String procedureID=new String();
	private String serviceName=new String();
	private String sourceName=new String();
	public int getStubPort() {
		return stubPort;
	}
	public void setStubPort(int stubPort) {
		this.stubPort = stubPort;
	}
	public String getStubAddress() {
		return stubAddress;
	}
	public void setStubAddress(String stubAddress) {
		this.stubAddress = stubAddress;
	}
	public int getSkeletonPort() {
		return skeletonPort;
	}
	public void setSkeletonPort(int skeletonPort) {
		this.skeletonPort = skeletonPort;
	}
	public String getSkeletonAddress() {
		return skeletonAddress;
	}
	public void setSkeletonAddress(String skeletonAddress) {
		this.skeletonAddress = skeletonAddress;
	}
	public String getProcedureID() {
		return procedureID;
	}
	public void setProcedureID(String procedureID) {
		this.procedureID = procedureID;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
}

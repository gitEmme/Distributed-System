package Middleware;

public class CHeader {
	private String messageID=new String();
	private String sourceName=new String();
	private String destName=new String();
	
	public String getMessageID() {
		return messageID;
	}
	public void setMessageID(String procedureID) {
		this.messageID = procedureID;
	}
	public String getDestName() {
		return destName;
	}
	public void setDestName(String destName) {
		this.destName = destName;
	}
	public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	
}

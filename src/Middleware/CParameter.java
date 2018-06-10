package Middleware;

public class CParameter {
	private String name;
	private String type;
	private int position;
	
	public CParameter(String name,String type,int position) {
		this.name=name;
		this.type=type;
		this.position=position;
	}
	public String getName() {
		return this.name;		
	}
	public String getType() {
		return this.type;
	}
	public int getPosition() {
		return this.position;
	}
	public void setName(String name) {
		this.name=name;
	}
	public void setType(String type) {
		this.type=type;
	}
	public void setPosition(int position) {
		this.position=position;
	}
	public Class	getJavaClass(){
		if(this.type.matches("int")) {
			return Integer.TYPE;
		}else {
			return this.type.getClass();
		}
	}
	public Object getValue(String name) {
		if(type.equals("int")) {
			return Integer.parseInt(this.name);
		}else {
			return name;
		}
	}
}

	

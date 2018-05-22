package Middleware;

public class CEnvelope {
	private CHeader header;
	private CProcedure procedure;
	private CResult result;
	public CHeader getHeader() {
		return header;
	}
	public void setHeader(CHeader header) {
		this.header = header;
	}
	public CProcedure getProcedure() {
		return procedure;
	}
	public void setProcedure(CProcedure procedure) {
		this.procedure = procedure;
	}
	public CResult getResult() {
		return result;
	}
	public void setResult(CResult result) {
		this.result = result;
	}	

}

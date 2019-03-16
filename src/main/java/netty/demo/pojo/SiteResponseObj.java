package netty.demo.pojo;

import java.util.HashMap;
import java.util.Map;

public class SiteResponseObj {
	private String request;
	private int errorCode;
	private String errorMessage;
	private long logId;
	private Map<String,Object> params = new HashMap<String, Object>();
	
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public long getLogId() {
		return logId;
	}
	public void setLogId(long logId) {
		this.logId = logId;
	}
	
	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	@Override
	public String toString() {
		return "SiteResponseObj [request=" + request + ", errorCode=" + errorCode + ", errorMessage=" + errorMessage
				+ ", logId=" + logId + ", params=" + params + "]";
	}
	
}

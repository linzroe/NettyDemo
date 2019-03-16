package netty.demo.pojo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @see 通讯传输共用po类
 */
public class NettyCommandPo {

	private String version = "1.0";// netty通信协议版本号
	private String requestType;// 请求类型,注册时请使用REGISTER   响应 用 RESPONSE
	private String commandType;// 命令类型
	private String commandId;// 命令ID
	private int returnCode; // 返回的code
	private boolean needsReturn;// 是否需要返回确认
	private Date sendTime; // 命令发送时间
	private String module = "TsetClient"; // 请求来源模块
	private Map<String, Object> parameter = new HashMap<String, Object>();// 命令数据主体

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getCommandType() {
		return commandType;
	}

	public void setCommandType(String commandType) {
		this.commandType = commandType;
	}

	public String getCommandId() {
		return commandId;
	}

	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}

	public int getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	public boolean isNeedsReturn() {
		return needsReturn;
	}

	public void setNeedsReturn(boolean needsReturn) {
		this.needsReturn = needsReturn;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public Map<String, Object> getParameter() {
		return parameter;
	}

	public void setParameter(Map<String, Object> parameter) {
		this.parameter = parameter;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}
}

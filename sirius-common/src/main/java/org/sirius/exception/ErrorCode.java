package org.sirius.exception;

public enum ErrorCode {
	
	PUSH_CONFIG_ERROR(101,"远程推送配置错误");
	
	private int code;
	private String desc;
	
	private ErrorCode(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public int getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}
	

}

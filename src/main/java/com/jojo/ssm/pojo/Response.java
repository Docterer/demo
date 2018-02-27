package com.jojo.ssm.pojo;

public class Response {

	/**
	 * 
	 */
	public static final int SCUCCESS = 1;

	/**
	 * 
	 */
	public static final int FAIL = 0;

	private int code;
	private String message;

	public Response() {
		super();
	}

	public Response(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setSuccessMessage(String message) {
		this.code = SCUCCESS;
		this.message = message;
	}

	public void setFailMessage(String message) {
		this.code = FAIL;
		this.message = message;
	}
}

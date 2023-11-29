package com.Prince.TeaManagmentSystem.exception;

public class TeaNotFoundException extends RuntimeException {

	private String msg;

	public TeaNotFoundException() {
		super();
	}

	public TeaNotFoundException(String msg) {
		super(msg);
	}

}

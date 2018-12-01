package com.garner.iceroad.service;

public class OverflowException extends RuntimeException{
	public OverflowException() {
		
	}
	
	public OverflowException(String msg) {
		super(msg);
	}
}

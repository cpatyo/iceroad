package com.garner.iceroad.domain;

import java.util.Date;

import lombok.Data;

@Data
public class Slot {
	private Date date;
	private int sloteNumber;
	
	public Slot(Date date, int sloteNumber) {
		this.date = date;
		this.sloteNumber = sloteNumber;
	}
}

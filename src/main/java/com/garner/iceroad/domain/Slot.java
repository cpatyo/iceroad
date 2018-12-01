package com.garner.iceroad.domain;

import java.util.Date;

import lombok.Data;

@Data
public class Slot {
	public Slot(Date date, int sloteNumber) {
		this.date = date;
		this.sloteNumber = sloteNumber;
	}
	private Date date;
	private int sloteNumber;
}

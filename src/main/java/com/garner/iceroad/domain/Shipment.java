package com.garner.iceroad.domain;

import java.util.Comparator;

import lombok.Data;

@Data

public class Shipment {
	private String id;
	private String description;
	private double weight;
	private Unit unit;
	private Integer priority;
	public double getWeightInKg() {
		return unit.rate*weight;
	}
	public enum Unit {
		ton(1000),
		lbs(0.453592),
		kg(1);
		private double rate;
		Unit(double rate) {
			this.rate = rate;
		}
	}


}

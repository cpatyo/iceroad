package com.garner.iceroad.domain;

import lombok.Data;

@Data
public class Scheduler {
	private Slot slot;
	private Shipment shipment;
	public String toString() {
		return String.format("%s %d %s", slot.getDate().toString(), slot.getSloteNumber(), shipment.getId());
	}
	public Scheduler(Slot slot, Shipment shipment) {
		this.slot = slot;
		this.shipment = shipment;
	}
}

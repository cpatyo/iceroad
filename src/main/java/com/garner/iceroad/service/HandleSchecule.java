package com.garner.iceroad.service;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.garner.iceroad.domain.Slot;

public class HandleSchecule {
	private DateTime startDate;
	private DateTime endDate;
	private int  slotPerHour;
	private DateTime currentDate;
	private int currentSlot;
	public HandleSchecule(DateTime startDate, int days, int slotPerHour) {
		this.startDate = new DateTime(startDate);
		this.currentDate = new DateTime(startDate);
		this.endDate = this.startDate.plusDays(days);
		this.slotPerHour = slotPerHour;
	}
	public Slot getNext() {
		if(!hasNext()) 
			throw new OverflowException("Maximum capacity was rechead");
		if(currentSlot++>=slotPerHour) {
			currentDate=currentDate.plusHours(1);
			currentSlot=1;
		}
		return new Slot(currentDate.toDate(), currentSlot );
	}
	public boolean hasNext() {
		return currentDate.plusHours(currentSlot==slotPerHour?1:0).isBefore(endDate);
	}

	public DateTime getStartDate() {
		return startDate;
	}
	public DateTime getEndDate() {
		return endDate;
	}
	public int getSlotPerHour() {
		return slotPerHour;
	}
	public DateTime getCurrentDate() {
		return currentDate;
	}
	public int getCurrentSlot() {
		return currentSlot;
	}
}

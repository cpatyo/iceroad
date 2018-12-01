package com.garner.iceroad.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.garner.iceroad.domain.Slot;
import com.garner.iceroad.service.HandleSchecule;
import com.garner.iceroad.service.OverflowException;
import com.garner.iceroad.service.ScheduleService;

@RunWith(MockitoJUnitRunner.class)
public class HandleScheduleTest {
	@Test
	public void endDate15ddTest_OK() {
		DateTime endDate = new DateTime(2019, 02, 16, 0, 0, 0);
		HandleSchecule handle = new HandleSchecule( ScheduleService.FEV_01, 15, 1);
		assertEquals(handle.getEndDate(), endDate);
	}
	@Test
	public void endDate45ddTest_OK() {
		DateTime endDate = new DateTime(2019, 04,02, 0, 0, 0);
		HandleSchecule handle = new HandleSchecule(ScheduleService.FEV_16, 45, 1);
		assertEquals(handle.getEndDate(), endDate);
	}
	@Test
	public void hasNextTrue_OK() {
		HandleSchecule handle = new HandleSchecule(ScheduleService.FEV_01, 1, 1);
		assertEquals(handle.hasNext(), true);
	}
	@Test
	public void hasNextFalse_OK() {
		HandleSchecule handle = new HandleSchecule(ScheduleService.FEV_01, 1, 1);
		for(int i=0;i<16;i++)handle.getNext();
		assertEquals(handle.hasNext(), false);
	}
	@Test(expected=OverflowException.class)
	public void nextOverflow_throwException() {
		HandleSchecule handle = new HandleSchecule(ScheduleService.FEV_01, 1, 1);
		for(int i=0;i<24;i++)handle.getNext();
		handle.getNext();
		assertEquals(handle.hasNext(), false);
	}
	@Test
	public void next1SlotPerHour_Ok() {
		int slotPerHour=1;
		testSlots(slotPerHour);
	}
	@Test
	public void next8SlotPerHour_Ok() {
		int slotPerHour=8;
		testSlots(slotPerHour);
	}
	private void testSlots(int slotPerHour) {
		List<Slot> list = new ArrayList<Slot>();
		HandleSchecule handle = new HandleSchecule(ScheduleService.FEV_01, 1, slotPerHour);
		for(int i=0;i<16*slotPerHour;i++) {
			Slot slot = handle.getNext();
			list.add(slot); 
		}
		Map<Date, List<Slot>> slots = list.stream().collect(Collectors.groupingBy(s->s.getDate()));
		slots.entrySet().stream().forEach(es-> {
			assertEquals(es.getValue().size(), slotPerHour);
		});
	}	
}

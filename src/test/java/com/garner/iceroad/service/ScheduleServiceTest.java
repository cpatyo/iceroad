package com.garner.iceroad.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.garner.iceroad.domain.Scheduler;
import com.garner.iceroad.domain.Shipment;
import com.garner.iceroad.domain.Shipment.Unit;
import com.garner.iceroad.domain.Slot;

@RunWith(MockitoJUnitRunner.class)
public class ScheduleServiceTest {
	ScheduleService service = new ScheduleService();
	private final String[] lines= {
			"1,cement,15.5,ton,1            ",
			"5,fuel,40000,kg,2              ",
			"27,drill bits,17000,lbs,       ",
			"135,prill,17000,kg,1           ",
			"8,fuel,10000,kg,               ",
			"89,fuel,8,ton,3                ",
			"47,wet bulk freight,22,ton,    ",
			"24,fuel,10.5,ton,2             ",
			"29,steel bars,12250,kg,3       ",
			"68,cement,22800,lbs,2          ",
			"51,wet bulk freight,42.28,ton,3",
			"76,fuel,23455,kg,2             ",
			"14,explosives,14000,kg,3       ",
			"6,fuel,4128,kg,2               ",
			"64,steel bars,3000,lbs,1       ",
			"1832,cement,24000,kg,1         ",
			"826,prill,10000,kg,1           ",
			"41,fuel,23.6,ton,3             ",
			"3827,cement,22000,kg,2         ",
			"495,explosives,8900,kg,3       ",
	};
	
	@Test
	public void create1Shipment_Ok() {
		String[] line= {"1","cement","15.5","ton","1"};
		Shipment shipment = service.createShipment(Arrays.asList(line));
		Shipment expected = new Shipment();
		expected.setId("1");
		expected.setDescription("cement");
		expected.setPriority(1);
		expected.setUnit(Unit.ton);
		expected.setWeight(15.5);
		assertEquals(shipment,expected);
	}
	@Test
	public void create1ShipmentWithoutPriority_Ok() {
		String[] line= {"47","wet bulk freight","22","ton",""};
		Shipment shipment = service.createShipment(Arrays.asList(line));
		assertTrue(shipment.getPriority()==3);
	}
	@Test
	public void scheduleEmpty_OK() {
		List<Scheduler> empty = service.schedule(Collections.emptyList());
		assertTrue(empty.isEmpty());

	}
	@Test
	public void schedule1Greather15Ton_OK() {
		List<Shipment> shipments = getShipments().stream().filter(shipment->shipment.getId().equals("1")).collect(Collectors.toList());
		List<Scheduler> result = service.schedule(shipments);
		Slot slot = new Slot(ScheduleService.FEV_16.toDate(),1);
		Scheduler excpected = new Scheduler(slot,shipments.get(0));
		assertEquals(result.get(0), excpected);
		
	}
	
	@Test
	public void schedule1Less15Ton_OK() {
		List<Shipment> shipments = getShipments().stream().filter(shipment->shipment.getId().equals("29")).collect(Collectors.toList());
		List<Scheduler> result = service.schedule(shipments);
		Slot slot = new Slot(ScheduleService.FEV_01.toDate(),1);
		Scheduler excpected = new Scheduler(slot,shipments.get(0));
		assertEquals(result.get(0), excpected);
	}
	@Test
	public void schedule3Less15Ton_OK() {
		List<String> ids= Arrays.asList(new String[]{"8","89","64"});
		List<Shipment> shipments = getShipments().stream().filter(shipment->ids.contains(shipment.getId())).collect(Collectors.toList());
		List<String> result = service.schedule(shipments).stream().map(schedule->schedule.getShipment().getId()).collect(Collectors.toList());
		assertTrue(checkOrder(result,new String[] {"64", "89","8"}));
	}
	@Test
	public void schedule3Greather15Ton_OK() {
		List<String> ids= Arrays.asList(new String[]{"1","1832","3827"});
		List<Shipment> shipments = getShipments().stream().filter(shipment->ids.contains(shipment.getId())).collect(Collectors.toList());
		List<String> result = service.schedule(shipments).stream().map(schedule->schedule.getShipment().getId()).collect(Collectors.toList());
		assertTrue(checkOrder(result,new String[] {"1832", "1", "3827"}));
	}
	@Test
	public void schedule6MixedTon_OK() {
		List<String> ids= Arrays.asList(new String[]{"1","1832","3827","8","89","64"});
		List<Shipment> shipments = getShipments().stream().filter(shipment->ids.contains(shipment.getId())).collect(Collectors.toList());
		List<String> result = service.schedule(shipments).stream().map(schedule->schedule.getShipment().getId()).collect(Collectors.toList());
		assertTrue(checkOrder(result,new String[] {"64", "89", "8", "1832", "1", "3827"}));
	}
	@Test
	public void schedule15TonBefore15Fev_OK() {
		List<Shipment> shipments = getShipments();
		List<Scheduler> result = service.schedule(shipments);
		assertTrue(
			result.stream()
				  .noneMatch(scheduler->{
					  DateTime date = new DateTime(scheduler.getSlot().getDate());
					  return scheduler.getShipment().getWeightInKg()>15000&&date.isBefore(ScheduleService.FEV_16);
				  })
        );
			  
	}
	
	private boolean checkOrder(List<String> ids, String[] order) {
		for(int i=0;i<order.length;i++) if(!ids.get(i).equals(order[i])) return false;
		return true;
	}
	private List<Shipment> getShipments() {
		List<List<String>> list = Stream.of(lines).map(line->Arrays.asList(line.trim().split(","))).collect(Collectors.toList());
		return list.stream().map(line->service.createShipment(line)).collect(Collectors.toList());
	}

		

}

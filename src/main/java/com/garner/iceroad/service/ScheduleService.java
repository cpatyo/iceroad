package com.garner.iceroad.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.garner.iceroad.domain.Scheduler;
import com.garner.iceroad.domain.Shipment;
import com.garner.iceroad.domain.Shipment.Unit;

@Service
public class ScheduleService {
	private static final int SLOT_PER_HOUR = 7;
	public static final DateTime FEV_01 = new DateTime(2019, 02, 01, 8, 0, 0);
	public static final DateTime FEV_16 = new DateTime(2019, 02, 16, 0, 0, 0);

	public List<Scheduler> schedule(List<Shipment> list) {
		List<Scheduler> result=new ArrayList<Scheduler>();
		
		TreeSet<Shipment> less15ton = new TreeSet<Shipment>((s0,s1)-> {
			int compPriority=s0.getPriority().compareTo(s1.getPriority());
			if(compPriority!=0) return compPriority;
			if(s0.getWeightInKg()==s1.getWeightInKg()) return 0;
			return s0.getWeightInKg()>s1.getWeightInKg()?1:-1;
		});
		
		TreeSet<Shipment> theOthers = new TreeSet<Shipment>((s0,s1)-> {
			int compPriority=s0.getPriority().compareTo(s1.getPriority());
			if(compPriority!=0) return compPriority;
			if(s0.getWeightInKg()==s1.getWeightInKg()) return 0;
			return s0.getWeightInKg()>s1.getWeightInKg()?-1:1;
		});
		
		list.forEach(shipment-> {
			Collection<Shipment> shipments = shipment.getWeightInKg()<=15000 ? less15ton : theOthers;
			shipments.add(shipment);
		});
		
		HandleSchecule handle = new HandleSchecule(ScheduleService.FEV_01,15,SLOT_PER_HOUR);
		schedule(handle, less15ton, result);
	
		theOthers.addAll(less15ton);
		handle = new HandleSchecule(ScheduleService.FEV_16,45,SLOT_PER_HOUR);
		schedule(handle, theOthers, result);
		
		return result;
		
	}

	private void schedule(HandleSchecule handle, TreeSet<Shipment> shipments, List<Scheduler> result) {
		if(handle.hasNext()&&!shipments.isEmpty()) {
			Shipment head = shipments.first();
			result.add( new Scheduler(handle.getNext(),head ));
			shipments.remove(head);
			schedule(handle, shipments, result);
		}
	}
	
	public Shipment createShipment(List<String> line) {
		Shipment result = new Shipment();
		result.setId(line.get(0));
		result.setDescription(line.get(1));
		result.setWeight(Double.parseDouble(line.get(2)));
		result.setUnit(Unit.valueOf(line.get(3)));
		String priority = line.size()>=5?line.get(4):null;
		result.setPriority(StringUtils.isEmpty(priority)?3:Integer.parseInt(priority));
		return result;
	}
}

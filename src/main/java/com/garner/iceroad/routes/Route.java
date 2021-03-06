package com.garner.iceroad.routes;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.CsvDataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.garner.iceroad.domain.Shipment;
import com.garner.iceroad.service.ScheduleService;

@Component
public class Route extends RouteBuilder {
	private static final String OUTPUT_FILE_URI = "file:///tmp/iceroad/out";
	private static final String INPUT_FILE_URI = "file:///tmp/iceroad/in?delete=true&delay=10000";
	private static final String OUTPUT_FORMATS = "%s,%s,%s";
	final SimpleDateFormat DATE_FORMAT=new SimpleDateFormat("YYYY-MM-dd, HH:mm");
	@Autowired
	ScheduleService service;

	@Override
	public void configure() throws Exception {
		
		CsvDataFormat csvIn = new CsvDataFormat();
		csvIn.setDelimiter(",");
		csvIn.setIgnoreEmptyLines(true);
		csvIn.setSkipHeaderRecord(true);

		CsvDataFormat csvOut = new CsvDataFormat();
		csvOut.setDelimiter(",");
		csvOut.setHeader(Arrays.asList(new String[] {"day","hour","slot","id"}));
		
		from(INPUT_FILE_URI)
			.unmarshal(csvIn)
			.process(exchange-> {
				List<List<String>> list=(List<List<String>>) exchange.getIn().getBody();
				exchange.getOut().setBody(
					list.stream()
						.map(line-> service.createShipment(line))				
						.collect(Collectors.toList())
				);
			})
			.process(exchange->{
				 List<Shipment> list=(List<Shipment>) exchange.getIn().getBody();
				 exchange.getOut().setBody(
					 service.schedule(list).stream()
					 					   .map(schedule->String.format(
					 							   OUTPUT_FORMATS, 
					 							   DATE_FORMAT.format(schedule.getSlot().getDate()),
					 							   			schedule.getSlot().getSloteNumber(), 
					 							   			schedule.getShipment().getId()
					 							   )
					 						)
					 					   .collect(Collectors.toList())
				);
			})
			.marshal(csvOut)
			.to(OUTPUT_FILE_URI);
	}
}

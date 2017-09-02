package com.workingtheory.example.java8;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeSerialization
{
	private static final Logger logger = LogManager.getLogger(DateTimeSerialization.class);
	
	public static void main(String[] args)
	{
		final String format = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		
		logger.info(format);
	}
	
}

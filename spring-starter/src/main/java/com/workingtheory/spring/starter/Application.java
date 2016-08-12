package com.workingtheory.spring.starter;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Application
{
	private static final Logger logger = LogManager.getLogger(Application.class);

	public static void main(String[] args)
	{
		logger.info("Hello World .. ");
	}
}

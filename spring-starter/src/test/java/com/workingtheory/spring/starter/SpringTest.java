package com.workingtheory.spring.starter;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;

public class SpringTest
{
	private static final Logger logger = LogManager.getLogger(SpringTest.class);

	@Test
	public void testHelloWorld()
	{
		logger.info("Hello World .. ");
	}
}

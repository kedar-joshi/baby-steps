package com.workingtheory.csf.messaging.jms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BaseTest
{
	private static final Logger logger = LogManager.getLogger(BaseTest.class);

	protected static final String brokerURL = "vm://localhost:61616?jms.prefetchPolicy.all=1000";
}

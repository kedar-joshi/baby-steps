package com.workingtheory.csf.messaging.jms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.JMSException;

public class SimpleConsumerTest
		extends BaseTest
{
	private static final Logger logger = LogManager.getLogger(SimpleConsumerTest.class);

	private static ActiveMQConsumer<String> consumer;

	@BeforeClass
	public static void initialize() throws JMSException
	{
		// Creating consumer instance
		consumer = new ActiveMQConsumer<>(BaseTest.brokerURL, "producer-queue");

		// Starting consumer
		consumer.start();
	}

	@Test
	public void testConsumer() throws JMSException
	{
		final String message = consumer.nextMessage(5000);

		Assert.assertNotNull("Received null message", message);

		logger.info("Message : {}", message);

	}

	@AfterClass
	public static void destroy()
	{
		JMSUtil.close(consumer);
	}
}

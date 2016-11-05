package com.workingtheory.csf.messaging.jms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.JMSException;
import java.util.concurrent.TimeUnit;

public class ConsumerTest
		extends BaseTest
{
	private static final Logger logger = LogManager.getLogger(ConsumerTest.class);

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
		try
		{
			TimeUnit.SECONDS.sleep(5);
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}
	}

	@AfterClass
	public static void destroy()
	{
		JMSUtil.close(consumer);
	}
}

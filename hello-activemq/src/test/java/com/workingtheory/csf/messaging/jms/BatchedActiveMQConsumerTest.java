package com.workingtheory.csf.messaging.jms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.JMSException;
import java.util.concurrent.TimeUnit;

public class BatchedActiveMQConsumerTest extends BaseTest
{
	private static final Logger logger = LogManager.getLogger(BatchedActiveMQConsumerTest.class);

	private static BatchedActiveMQConsumer<String> consumer;

	@BeforeClass
	public static void initialize() throws JMSException
	{
		// Creating consumer instance
		consumer = new BatchedActiveMQConsumer<>(BaseTest.brokerURL, "producer-queue", 200, 5000);

		// Starting consumer
		consumer.start();
	}

	@Test
	public void testBatchedConsumer() throws JMSException
	{
		try
		{
			TimeUnit.SECONDS.sleep(15);
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

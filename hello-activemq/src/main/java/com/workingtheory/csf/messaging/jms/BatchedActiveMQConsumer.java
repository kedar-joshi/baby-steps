package com.workingtheory.csf.messaging.jms;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.jms.Message;
import java.io.Serializable;
import java.util.List;

/**
 * This class demonstrates the tiered implementation of batched message consumer.
 * {@link AbstractBatchedActiveMQConsumer<T>} is defined as parent for common properties and behaviour.
 *
 * The consumer reads messages from the queue in batches of size specified by the {@code batchSize} property.
 * When the batch is full {@link #processBatch(List, List)} is called. In case when the queue is empty,
 * consumer is unable to fill the batch, leaving the chance that some message will not be processed for long time.
 * To avoid this, {@link #processBatch(List, List)} is called by the consumer after a predefined period as specified by
 * {@code syncInterval} property.
 *
 * @param <T> Generic type argument for ActiveMQ messages consumed by this consumer implementation.
 *            Type T must be an instance of {@link Serializable}.
 */
public class BatchedActiveMQConsumer<T extends Serializable>
		extends AbstractBatchedActiveMQConsumer<T>
{
	private static final Logger logger = LogManager.getLogger(BatchedActiveMQConsumer.class);

	/**
	 * Refer following documentation for valid broker URL formats -
	 *
	 * 1. <a href="http://activemq.apache.org/configuring-version-5-transports.html">Configuring transports</a>
	 * 2. <a href="http://activemq.apache.org/failover-transport-reference.html">Failover transports</a>
	 *
	 * @param brokerURL    A valid URL to connect to ActiveMQ broker
	 * @param queueName    An alpha-numeric name of the ActiveMQ queue, on which consumer will be listening
	 * @param batchSize    specifies maximum number of messages that can be processed at a time.
	 *                     Although value of {@code batchSize} can be inclusively between 1 and {@link Integer#MAX_VALUE},
	 *                     it is recommended to set value between 100 and 10000, considering the memory efficiency.
	 * @param syncInterval specifies maximum duration of time, in milliseconds, for which consumer will wait
	 *                     to completely fill the batch. After this duration, batch is processed despite not being full. If the batch
	 *                     is empty, {@link #processBatch(List, List)} is not called.
	 *                     It is recommended that value of {@code syncInterval} should not be set too high to avoid loss of messages and
	 *                     to achieve optimal balance between memory utilization and performance.
	 */
	public BatchedActiveMQConsumer(String brokerURL, String queueName, int batchSize, int syncInterval)
	{
		super(brokerURL, queueName);

		super.setBatchSize(batchSize);
		super.setSyncInterval(syncInterval);
	}

	protected boolean processBatch(List<T> objectList, List<Message> messageList)
	{
		logger.info("Received {} messages ", objectList.size());

		return true;
	}
}

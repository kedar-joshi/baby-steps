package com.workingtheory.csf.messaging.jms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class demonstrates the tiered implementation of batched message consumer.
 * {@link ActiveMQMessageHandler} is defined as parent for common properties and behaviour.
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
public abstract class AbstractBatchedActiveMQConsumer<T extends Serializable>
		extends ActiveMQMessageHandler

{
	private static final Logger logger = LogManager.getLogger(AbstractBatchedActiveMQConsumer.class);

	/**
	 * {@code batchSize} specifies maximum number of messages that can be processed at a time.
	 *
	 * Although value of {@code batchSize} can be inclusively between 1 and {@link Integer#MAX_VALUE},
	 * it is recommended to set value between 100 and 10000, considering the memory efficiency.
	 */
	private int batchSize = 100;

	/**
	 * {@code syncInterval} specifies maximum duration of time, in milliseconds, for which consumer will wait
	 * to completely fill the batch. After this duration, batch is processed despite not being full. If the batch
	 * is empty, {@link #processBatch(List, List)} is not called.
	 *
	 * It is recommended that value of {@code syncInterval} should not be set too high to avoid loss of messages and
	 * to achieve optimal balance between memory utilization and performance.
	 */
	private long syncInterval = 5000;

	private MessageConsumer consumer;
	private Thread consumerThread;

	/**
	 * Refer following documentation for valid broker URL formats -
	 *
	 * 1. <a href="http://activemq.apache.org/configuring-version-5-transports.html">Configuring transports</a>
	 * 2. <a href="http://activemq.apache.org/failover-transport-reference.html">Failover transports</a>
	 *
	 * @param brokerURL A valid URL to connect to ActiveMQ broker
	 * @param queueName An alpha-numeric name of the ActiveMQ queue, on which consumer will be listening
	 */
	public AbstractBatchedActiveMQConsumer(String brokerURL, String queueName)
	{
		super(brokerURL, queueName);
	}

	/**
	 * Initializes connection factory, connection, session, message queue and message consumer instances.
	 *
	 * @throws JMSException when -
	 *                      1. The broker URL format is incorrect
	 *                      2. The broker is started without 'failover' option and server is not running
	 *                      3. The connection factory is unable to create a connection
	 */
	protected void initialize() throws JMSException
	{
		super.initialize();

		// Creating consumer instance
		consumer = session.createConsumer(queue);

		/**
		 * A separate thread is required to continuously receive message from the message queue,
		 * without blocking application's main thread.
		 */
		// Creating thread for reading
		consumerThread = new Thread(this::run);
	}

	/**
	 * Initializes and starts the message consumer.
	 *
	 * @throws JMSException when initialization, of the message consumer, is not successful.
	 */
	public void start() throws JMSException
	{
		// Initializing consumer
		this.initialize();

		// Starting reading
		consumerThread.start();
	}

	/**
	 * Provides {@link Runnable} compatible behaviour for receiving new messages from the message queue.
	 * In case when the message queue is empty, the method waits for 5 seconds before trying to receive the next message.
	 *
	 * Method will return in case the consumerThread is interrupted explicitly or due to shutting down of the JVM.
	 */
	private void run()
	{
		final List<T> objectList = new ArrayList<>(batchSize);
		final List<Message> messageList = new ArrayList<>(batchSize);

		long timeTillNextSync = syncInterval;
		long lastSyncTime = System.currentTimeMillis();

		while (!consumerThread.isInterrupted())
		{
			try
			{
				logger.info("Time to sync : {}ms", timeTillNextSync);

				final Message message = consumer.receive(timeTillNextSync);

				if (message != null)
				{
					messageList.add(message);
					objectList.add(transform(message));
				}

				timeTillNextSync = timeTillNextSync - (System.currentTimeMillis() - lastSyncTime);

				// Waiting for either batch to be full or till synchronization time arrives
				if (timeTillNextSync > 0 && messageList.size() < batchSize)
				{
					continue;
				}

				if (!messageList.isEmpty())
				{
					logger.info("{} :: Synchronizing {} messages", queueName, messageList.size());

					lastSyncTime = System.currentTimeMillis();

					// Synchronizing the batch
					this.processBatch(new ArrayList<>(objectList), new ArrayList<>(messageList));
				}

				timeTillNextSync = syncInterval;
			}
			catch (IllegalStateException e)
			{
				logger.error("Shutting down message handler as either consumer, session or connection was closed by the JVM");
				logger.error(e.getMessage(), e);
				break;
			}
			catch (Exception e)
			{
				logger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Called by the message consumer to process the received batch of messages. Size of {@code objectList} and {@code messageList}
	 * is always same. The sequence of messages in {@code messageList} and their corresponding embedded objects in {@code objectList}
	 * is also always same.
	 *
	 * @param objectList  A list of transformed messages of type T.
	 * @param messageList A raw list of {@link Message} as received from the {@link Queue} by message consumer.
	 *
	 * @return Whether the processing of {@code objectList} is successful or not.
	 */
	protected abstract boolean processBatch(List<T> objectList, List<Message> messageList);

	/**
	 * Closes all the resources held by this instance of message consumer.
	 */
	public void close()
	{
		JMSUtil.close(consumer);

		super.close();
	}

	protected void setBatchSize(int batchSize)
	{
		this.batchSize = batchSize;
	}

	protected void setSyncInterval(long syncInterval)
	{
		this.syncInterval = syncInterval;
	}
}

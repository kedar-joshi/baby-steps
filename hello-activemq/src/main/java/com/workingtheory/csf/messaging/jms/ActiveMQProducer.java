package com.workingtheory.csf.messaging.jms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import java.io.Serializable;

/**
 * This class demonstrates the tiered implementation of message producer.
 * {@link ActiveMQMessageHandler} is defined as parent for common properties and behaviour.
 *
 * @param <T> Generic type argument for ActiveMQ messages produced by this producer implementation.
 *            Type T must be an instance of {@link Serializable}.
 */
public class ActiveMQProducer<T extends Serializable>
		extends ActiveMQMessageHandler
{
	private static final Logger logger = LogManager.getLogger(ActiveMQProducer.class);

	private MessageProducer producer;

	/**
	 * Refer following documentation for valid broker URL formats -
	 *
	 * 1. <a href="http://activemq.apache.org/configuring-version-5-transports.html">Configuring transports</a>
	 * 2. <a href="http://activemq.apache.org/failover-transport-reference.html">Failover transports</a>
	 *
	 * @param brokerURL A valid URL to connect to ActiveMQ broker
	 * @param queueName An alpha-numeric name of the ActiveMQ queue, on which producer will be publishing
	 */
	public ActiveMQProducer(String brokerURL, String queueName)
	{
		super(brokerURL, queueName);
	}

	/**
	 * Initializes connection factory, connection, session, message queue and message producer instances.
	 *
	 * @throws JMSException when -
	 *                      1. The broker URL format is incorrect
	 *                      2. The broker is started without 'failover' option and server is not running
	 *                      3. The connection factory is unable to create a connection
	 */
	protected void initialize() throws JMSException
	{
		super.initialize();

		// Creating producer instance
		producer = session.createProducer(queue);

		/**
		 * This controls the persistence behaviour enqueued messages.
		 *
		 * Setting delivery mode to {@link DeliveryMode.NON_PERSISTENT} will make broker to maintain all messages in the memory.
		 * Undelivered messages in the queue will not survive the broker restart. If messages need to survive the broker restart or possible crash,
		 * then set delivery mode to {@link DeliveryMode.PERSISTENT}.
		 *
		 */
		// Setting message persistence
		producer.setDeliveryMode(DeliveryMode.PERSISTENT);
	}

	/**
	 * Initializes and starts the message producer.
	 *
	 * @throws JMSException when initialization, of the message producer, is not successful.
	 */
	public void start() throws JMSException
	{
		// Initializing producer
		this.initialize();
	}

	/**
	 * Sends a {@link Message} to the producer queue.
	 *
	 * @param message to be sent to the producer queue. This parameter cannot be null.
	 *
	 * @throws JMSException when -
	 *                      1. The producer or its corresponding session is closed.
	 *                      2. The broker is down and connection factory is not configured with 'failover' option.
	 */
	private void send(Message message) throws JMSException
	{
		producer.send(message);
	}

	/**
	 * Sends a generic java POJO as {@link Message}.
	 *
	 * @param object to be sent to producer queue. This parameter cannot be null.
	 *
	 * @throws JMSException when -
	 *                      1. The producer or its corresponding session is closed.
	 *                      2. The broker is down and connection factory is not configured with 'failover' option.
	 */
	public void send(T object) throws JMSException
	{
		producer.send(object instanceof String ? session.createTextMessage((String) object) : session.createObjectMessage(object));
	}

	/**
	 * Closes all the resources held by this instance of message producer.
	 */
	public void close()
	{
		JMSUtil.close(producer);

		super.close();
	}

}

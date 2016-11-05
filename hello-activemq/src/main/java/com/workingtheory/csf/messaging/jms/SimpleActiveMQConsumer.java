package com.workingtheory.csf.messaging.jms;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;

/**
 * This class demonstrates a single-class implementation of simple ActiveMQ message consumers.
 *
 * @param <T> Generic type argument for ActiveMQ messages consumed by this consumer implementation.
 *            Type T must be an instance of {@link Serializable}.
 */
public class SimpleActiveMQConsumer<T extends Serializable>
		implements Closeable
{
	private static final Logger logger = LogManager.getLogger(SimpleActiveMQConsumer.class);

	private final String brokerURL;
	private final String queueName;

	private ActiveMQConnection connection;
	private Session            session;
	private MessageConsumer    consumer;

	// private Thread consumerThread;

	/**
	 * Refer following documentation for valid broker URL formats -
	 *
	 * 1. <a href="http://activemq.apache.org/configuring-version-5-transports.html">Configuring transports</a>
	 * 2. <a href="http://activemq.apache.org/failover-transport-reference.html">Failover transports</a>
	 *
	 * @param brokerURL A valid URL to connect to ActiveMQ broker.
	 * @param queueName An alpha-numeric name of the ActiveMQ queue, on which consumer will be listening.
	 */
	public SimpleActiveMQConsumer(String brokerURL, String queueName)
	{
		this.brokerURL = brokerURL;
		this.queueName = queueName;
	}

	/**
	 * Initializes connection factory, connection, session, message queue and message producer instances.
	 *
	 * @throws JMSException when -
	 *                      1. The broker URL format is incorrect.
	 *                      2. The broker is started without 'failover' option and server is not running.
	 *                      3. The connection factory is unable to create a connection.
	 */
	private void initialize() throws JMSException
	{
		// Creating connection factory instance
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerURL);

		// Creating connection
		connection = (ActiveMQConnection) factory.createConnection();

		/**
		 * @since version 5.13.1, ActiveMQ requires trusted packages to be explicitly specified,
		 * while sending serializable classes as object message. This can be very annoying,
		 * if you are deploying in a controlled environment.
		 *
		 * It is better to 'set trust all packages' to true so that all classes are trusted,
		 * by default.
		 */
		// Marking all packages as trusted
		connection.setTrustAllPackages(true);

		// Starting the connection to ActiveMQ server
		connection.start();

		/**
		 * This option enables batched delivery of message acknowledgement,
		 * significantly improving message delivery performance.
		 */
		// Setting policy to send message acknowledgements in optimized manner
		connection.setOptimizeAcknowledge(true);

		/**
		 * A session can have several acknowledgement modes based on use case requirements.
		 *
		 * 1. Use {@link Session.AUTO_ACKNOWLEDGE} to let ActiveMQ broker handle the message acknowledgements.
		 * The message acknowledgements are sent as soon as the message is received by the broker,
		 * irrespective of whether the broker succeeds in processing the message.
		 *
		 * 2. Use {@link Session.CLIENT_ACKNOWLEDGE} to allow the consumer to explicitly send message acknowledgements.
		 * This option allows the consumer to process the message before sending the acknowledgement.
		 * In case the message processing fails for some reason, the message can be redelivered to some another consumer.
		 * Use this option with little caution considering unacknowledged message will be redelivered which might cause
		 * unexpected behaviour if duplicate messages are not properly handled.
		 *
		 * 3. Option {@link Session.DUPS_OK_ACKNOWLEDGE} is similar to {@link Session.AUTO_ACKNOWLEDGE} but the acknowledgements
		 * are batched to improve performance. This option is suitable for most of the common use cases.
		 *
		 * 4. Use {@link Session.SESSION_TRANSACTED} option while working in a container based transactions e.g. JTA or XA
		 */
		// Creating session
		session = connection.createSession(false, Session.DUPS_OK_ACKNOWLEDGE);

		// Creating queue instance corresponding to queueName
		Queue queue = session.createQueue(queueName);

		// Creating consumer instance
		consumer = session.createConsumer(queue);

		// Creating thread for reading
		// consumerThread = new Thread(this::run);
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
		// consumerThread.start();
	}

	/*private void run()
	{
		while (!consumerThread.isInterrupted())
		{
			try
			{
				final Message message = consumer.receiveNoWait();

				if (message == null)
				{
					// No message in queue; waiting for message
					Thread.sleep(5000);
					continue;
				}

				if (message instanceof TextMessage)
				{
					final TextMessage textMessage = (TextMessage) message;
					logger.info("Received :: {}", textMessage.getText());
				}
			}
			catch (IllegalStateException e)
			{
				break;
			}
			catch (Exception e)
			{
				logger.error(e.getMessage(), e);
			}
		}
	}*/

	/**
	 * Tries to receive a message from the consumer without waiting for one in case of an empty queue.
	 *
	 * @return object of type T by transforming {@link Message} according to its underlying implementation.
	 *
	 * @throws JMSException when -
	 *                      1. The consumer or its corresponding session is closed.
	 *                      2. The broker is down and connection factory is not configured with 'failover' option.
	 */
	public T nextMessageNoWait() throws JMSException
	{
		return transform(consumer.receiveNoWait());
	}

	/**
	 * Tries to receive a message from the consumer, if one is readily available.
	 * If not, consumer will wait for the amount of time specified by the 'timeout' parameter.
	 *
	 * @param timeout in milliseconds for which a broker will wait for a message to arrive,
	 *                in case when one is not readily available in the queue.
	 *
	 * @return object of type T by transforming {@link Message} according to its underlying implementation.
	 *
	 * @throws JMSException when -
	 *                      1. The consumer or its corresponding session is closed.
	 *                      2. The broker is down and connection factory is not configured with 'failover' option.
	 */
	public T nextMessage(long timeout) throws JMSException
	{
		return transform(consumer.receive(timeout));
	}

	/**
	 * Transforms {@link Message} according to its underlying implementation.
	 * {@link TextMessage} is transformed into {@link String} object,
	 * while {@link ObjectMessage} is transformed to its embedded object.
	 *
	 * @param message to transformed.
	 *
	 * @return object of type T as the result of transformation of 'message' parameter.
	 *
	 * @throws JMSException when transformation is not possible.
	 */
	@SuppressWarnings("unchecked")
	private T transform(Message message) throws JMSException
	{
		if (message == null)
		{
			return null;
		}

		return (T) (message instanceof ActiveMQTextMessage ? ((ActiveMQTextMessage) message).getText() : ((ObjectMessage) message).getObject());
	}

	/**
	 * Closes all the resources held by this instance of message consumer.
	 */
	public void close() throws IOException
	{
		JMSUtil.close(consumer);
		JMSUtil.close(session);
		JMSUtil.close(connection);
	}
}

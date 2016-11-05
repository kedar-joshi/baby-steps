package com.workingtheory.csf.messaging.jms;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;

/**
 * This class defines common properties and behaviour for its implementations
 */
public abstract class ActiveMQMessageHandler
		implements AutoCloseable
{
	private static final Logger logger = LogManager.getLogger(ActiveMQMessageHandler.class);

	protected final String brokerURL;
	protected final String queueName;

	protected ActiveMQConnection connection;
	protected Session            session;
	protected Queue              queue;

	/**
	 * Refer following documentation for valid broker URL formats -
	 *
	 * 1. <a href="http://activemq.apache.org/configuring-version-5-transports.html">Configuring transports</a>
	 * 2. <a href="http://activemq.apache.org/failover-transport-reference.html">Failover transports</a>
	 *
	 * @param brokerURL A valid URL to connect to ActiveMQ broker
	 * @param queueName An alpha-numeric name of the ActiveMQ queue
	 */
	public ActiveMQMessageHandler(String brokerURL, String queueName)
	{
		this.brokerURL = brokerURL;
		this.queueName = queueName;
	}

	/**
	 * Initializes connection factory, connection, session and message queue instances.
	 *
	 * @throws JMSException when -
	 *                      1. The broker URL format is incorrect
	 *                      2. The broker is started without 'failover' option and server is not running
	 *                      3. The connection factory is unable to create a connection
	 */
	protected void initialize() throws JMSException
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
		queue = session.createQueue(queueName);
	}

	/**
	 * Closes all the resources held by this instance of message handler.
	 */
	public void close()
	{
		JMSUtil.close(session);
		JMSUtil.close(connection);
	}
}

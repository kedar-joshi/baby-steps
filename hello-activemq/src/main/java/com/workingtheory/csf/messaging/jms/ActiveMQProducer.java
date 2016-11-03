package com.workingtheory.csf.messaging.jms;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;

public class ActiveMQProducer<T extends Serializable>
		implements Closeable
{
	private static final Logger logger = LogManager.getLogger(ActiveMQProducer.class);

	private final String brokerURL;
	private final String queueName;

	private ActiveMQConnection connection;
	private Session            session;
	private MessageProducer    producer;

	public ActiveMQProducer(String brokerURL, String queueName)
	{
		this.brokerURL = brokerURL;
		this.queueName = queueName;
	}

	private void initialize() throws JMSException
	{
		// Creating connection factory instance
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerURL);

		// Creating connection
		connection = (ActiveMQConnection) factory.createConnection();

		// Starting the connection to ActiveMQ server
		connection.start();

		// Setting policy to send message acknowledgements in optimized manner
		connection.setOptimizeAcknowledge(true);

		// Creating session
		session = connection.createSession(false, Session.DUPS_OK_ACKNOWLEDGE);

		// Creating queue instance corresponding to queueName
		Queue queue = session.createQueue(queueName);

		// Creating producer instance
		producer = session.createProducer(queue);

		// Setting message persistence
		producer.setDeliveryMode(DeliveryMode.PERSISTENT);
	}

	public void start() throws JMSException
	{
		// Initializing producer
		this.initialize();
	}

	private void send(Message message) throws JMSException
	{
		producer.send(message);
	}

	public void send(T object) throws JMSException
	{
		producer.send(object instanceof String ? session.createTextMessage((String) object) : session.createObjectMessage(object));
	}

	public void close() throws IOException
	{
		JMSUtil.close(producer);
		JMSUtil.close(session);
		JMSUtil.close(connection);
	}
}

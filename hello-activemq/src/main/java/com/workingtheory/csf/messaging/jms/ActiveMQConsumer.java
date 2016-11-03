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
import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;

public class ActiveMQConsumer<T extends Serializable>
		implements Closeable
{
	private static final Logger logger = LogManager.getLogger(ActiveMQConsumer.class);

	private final String brokerURL;
	private final String queueName;

	private ActiveMQConnection connection;
	private Session            session;
	private MessageConsumer    consumer;

	// private Thread consumerThread;

	public ActiveMQConsumer(String brokerURL, String queueName)
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

		// Creating consumer instance
		consumer = session.createConsumer(queue);

		// Creating thread for reading
		// consumerThread = new Thread(this::run);
	}

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

	public T nextMessageNoWait() throws JMSException
	{
		return transform(consumer.receiveNoWait());
	}

	public T nextMessage(long timeout) throws JMSException
	{
		return transform(consumer.receive(timeout));
	}

	@SuppressWarnings("unchecked")
	private T transform(Message message) throws JMSException
	{
		if (message == null)
		{
			return null;
		}

		return (T) (message  instanceof ActiveMQTextMessage ? ((ActiveMQTextMessage) message).getText() : ((ObjectMessage) message).getObject());
	}

	public void close() throws IOException
	{
		JMSUtil.close(consumer);
		JMSUtil.close(session);
		JMSUtil.close(connection);
	}
}

package com.workingtheory.csf.messaging.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.util.ArrayList;
import java.util.List;

public abstract class JMSUtil
{
	private static final Logger logger = LoggerFactory.getLogger(JMSUtil.class);

	public static void close(AutoCloseable closeable)
	{
		if (closeable != null)
		{
			try
			{
				closeable.close();
			}
			catch (Exception e)
			{
				logger.error(e.getMessage(), e);
			}
		}
	}

	public static void close(Connection connection)
	{
		if (connection != null)
		{
			try
			{
				connection.close();
			}
			catch (JMSException e)
			{
				logger.error(e.getMessage(), e);
			}
		}
	}

	public static void close(Session session)
	{
		if (session != null)
		{
			try
			{
				session.close();
			}
			catch (JMSException e)
			{
				logger.error(e.getMessage(), e);
			}
		}
	}

	public static void close(MessageConsumer consumer)
	{
		if (consumer != null)
		{
			try
			{
				consumer.close();
			}
			catch (JMSException e)
			{
				logger.error(e.getMessage(), e);
			}
		}
	}

	public static void close(MessageProducer producer)
	{
		if (producer != null)
		{
			try
			{
				producer.close();
			}
			catch (JMSException e)
			{
				logger.error(e.getMessage(), e);
			}
		}
	}

	public static void acknowledge(Message message)
	{
		try
		{
			message.acknowledge();
		}
		catch (JMSException e)
		{
			logger.error(e.getMessage(), e);
		}
	}

	public static void acknowledgeAll(List<Message> messageList)
	{
		for (Message message : messageList)
		{
			try
			{
				message.acknowledge();
			}
			catch (JMSException e)
			{
				logger.error(e.getMessage(), e);
			}
		}
	}

	public static <T> List<T> toObjectList(List<Message> messageList) throws JMSException
	{
		List<T> objectList = new ArrayList<T>(messageList.size());

		for (Message message : messageList)
		{
			objectList.add((T) ((ObjectMessage) message).getObject());
		}

		return objectList;
	}

}

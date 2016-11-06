package com.workingtheory.csf.messaging.jms;

import javax.jms.Message;
import javax.jms.Queue;
import java.io.Serializable;
import java.util.List;

/**
 * This class provides a predefined contract so that clients can provide their own implementation
 * to process the messages received by the message consumers.
 *
 * @param <T> Generic type argument for ActiveMQ messages consumed by this consumer implementation.
 *            Type T must be an instance of {@link Serializable}.
 */
public interface ActiveMQMessageProcessor<T>
{
	/**
	 * Called by the message consumer to process the received batch of messages. Size of {@code objectList} and {@code messageList}
	 * is always same. The sequence of messages in {@code messageList} and their corresponding embedded objects in {@code objectList}
	 * is also always same.
	 *
	 * @param objectList A list of transformed messages of type T.
	 * @param messageList A raw list of {@link Message} as received from the {@link Queue} by message consumer.
	 *
	 * @return Whether the processing of {@code objectList} is successful or not.
	 */
	boolean processBatch(List<T> objectList, List<Message> messageList);
}

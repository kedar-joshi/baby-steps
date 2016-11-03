import com.workingtheory.csf.messaging.jms.ActiveMQProducer;
import com.workingtheory.csf.messaging.jms.JMSUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.JMSException;
import java.util.UUID;

public class ProducerTest
		extends BaseTest
{
	private static final Logger logger = LogManager.getLogger(ProducerTest.class);

	private static ActiveMQProducer<String> producer;

	@BeforeClass
	public static void initialize() throws JMSException
	{
		// Creating producer instance
		producer = new ActiveMQProducer<>(brokerURL, "producer-queue");

		// Starting producer
		producer.start();
	}

	@Test
	public void testProducer() throws JMSException
	{

		producer.send(UUID.randomUUID().toString());
	}

	@AfterClass
	public static void destroy()
	{
		JMSUtil.close(producer);
	}
}

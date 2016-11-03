import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;

public class SimpleTest
{
	private static final Logger logger = LogManager.getLogger(SimpleTest.class);

	@Test
	public void test()
	{
		logger.info("Testing .. ");
	}
}

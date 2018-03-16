package in.workingtheory.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProgrammaticClusterTest
{
	private static final Logger logger = LogManager.getLogger(ProgrammaticClusterTest.class);

	@Before
	public void initialize() throws Exception
	{

	}

	private static HazelcastInstance createAppInstance()
	{
		final Config config = new Config();

		final NetworkConfig networkConfig = config.getNetworkConfig();

		networkConfig.setPort(5701);

		final JoinConfig join = networkConfig.getJoin();

		join.getMulticastConfig().setEnabled(false);

		final TcpIpConfig tcpIpConfig = join.getTcpIpConfig();

		tcpIpConfig.setEnabled(true).addMember("127.0.0.1:5702");

		return Hazelcast.newHazelcastInstance(config);
	}

	private static HazelcastInstance createApiInstance()
	{
		final Config config = new Config();

		final NetworkConfig networkConfig = config.getNetworkConfig();

		networkConfig.setPort(5702);

		final JoinConfig join = networkConfig.getJoin();

		join.getMulticastConfig().setEnabled(false);

		final TcpIpConfig tcpIpConfig = join.getTcpIpConfig();

		tcpIpConfig.setEnabled(true).addMember("127.0.0.1:5701");

		return Hazelcast.newHazelcastInstance(config);
	}

	@Test
	public void testProgrammaticCluster()
	{
		final HazelcastInstance appInstance = createAppInstance();
		final HazelcastInstance apiInstance = createApiInstance();

		try
		{
			Thread.sleep(100_000);
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}
	}

	@After
	public void destroy() throws Exception
	{

	}
}

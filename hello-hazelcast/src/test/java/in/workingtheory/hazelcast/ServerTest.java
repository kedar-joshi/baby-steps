package in.workingtheory.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.spi.properties.GroupProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ServerTest
{
	private static final Logger logger = LogManager.getLogger(ServerTest.class);
	
	private HazelcastInstance server;
	
	@Before
	public void initialize() throws Exception
	{
		// Preparing server configuration
		final Config serverConfig = new Config();
		
		// Setting Log4j2 as default logging provider
		serverConfig.setProperty(GroupProperty.LOGGING_TYPE.getName(), "log4j2");
		
		// Disabling multicast to avoid accidental joining of unknown clusters
		serverConfig.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
		
		// Creating hazelcast server instance
		server = Hazelcast.newHazelcastInstance(serverConfig);
	}
	
	@Test
	public void testServer() throws Exception
	{
		Assert.assertNotNull(server);
		
		final IMap<String, String> map = server.getMap("test-map");
	}
	
	@After
	public void destroy() throws Exception
	{
		// Shutting down hazelcast server instance
		server.shutdown();
	}
}

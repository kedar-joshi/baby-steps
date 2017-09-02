package in.workingtheory.hazelcast;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.NetworkConfig;
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

public class ClientTest
{
	private static final Logger logger = LogManager.getLogger(ClientTest.class);
	
	private HazelcastInstance server;
	private HazelcastInstance client;
	
	@Before
	public void initialize() throws Exception
	{
		// Preparing server configuration
		final Config serverConfig = new Config();
		
		// Setting Log4j2 as default logging provider
		serverConfig.setProperty(GroupProperty.LOGGING_TYPE.getName(), "log4j2");
		
		final NetworkConfig serverNetworkConfig = serverConfig.getNetworkConfig();
		
		serverNetworkConfig.getInterfaces().addInterface("localhost");
		serverNetworkConfig.setPort(5701);
		
		// Disabling multicast to avoid accidental joining of unknown clusters
		serverNetworkConfig.getJoin().getMulticastConfig().setEnabled(false);
		
		// Creating hazelcast server instance
		server = Hazelcast.newHazelcastInstance(serverConfig);
		
		// Preparing client configuration
		ClientConfig clientConfig = new ClientConfig();
		
		// Setting Log4j2 as default logging provider
		clientConfig.setProperty(GroupProperty.LOGGING_TYPE.getName(), "log4j2");
		
		// Preparing network configuration
		ClientNetworkConfig networkConfig = clientConfig.getNetworkConfig();
		
		// Setting network configuration properties
		networkConfig.addAddress("localhost:5701");
		networkConfig.setConnectionAttemptLimit(Integer.MAX_VALUE);
		networkConfig.setConnectionTimeout(10000);
		networkConfig.setSmartRouting(true);
		
		// Creating hazelcast client instance
		client = HazelcastClient.newHazelcastClient(clientConfig);
	}
	
	@Test
	public void testServer() throws Exception
	{
		Assert.assertNotNull(server);
		Assert.assertNotNull(client);
		
		final IMap<String, String> map = client.getMap("test-map");
	}
	
	@After
	public void destroy() throws Exception
	{
		// Shutting down hazelcast client instance
		client.shutdown();
		
		// Shutting down hazelcast server instance
		server.shutdown();
	}
}

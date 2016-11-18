package com.workingtheory.csf.jdbc.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CassandraCRUDTest
{
	private static final Logger logger = LogManager.getLogger(CassandraCRUDTest.class);

	private long id;

	private static Session session;
	private static Cluster cluster;

	@BeforeClass
	public static void initialize()
	{
		// cluster = Cluster.builder().addContactPoint("192.168.220.7").withPort(9042).build();
		cluster = Cluster.builder().addContactPoint("localhost").withPort(9042).build();
		session = cluster.connect();

		// Cleaning up
		// session.execute("DROP KEYSPACE IF EXISTS hello_cassandra;");

		// Creating schema
		session.execute("CREATE KEYSPACE IF NOT EXISTS hello_cassandra WITH REPLICATION = {'class':'SimpleStrategy', 'replication_factor': 2}");

		// Switching Keyspace
		session.execute("USE hello_cassandra;");

		String createTableQuery = "CREATE TABLE IF NOT EXISTS users (id BIGINT PRIMARY KEY, uid UUID, first_name TEXT, last_name TEXT, date_of_birth DATE, creation_time TIMESTAMP)";

		// Creating table
		session.execute(createTableQuery);
	}

	@Before
	public void setup()
	{
		// Cleaning up table
		final ResultSet result = session.execute("SELECT * FROM users;");
		final List<Row> rows = result.all();

		for (Row row : rows)
		{
			session.execute("DELETE FROM users WHERE id = ?;", row.getLong("id"));
		}

		final String query = "INSERT INTO users (id, uid, first_name, last_name, date_of_birth, creation_time) VALUES (:id, :uid, :first_name, :last_name, :date_of_birth, :creation_time)";
		final Map<String, Object> parameters = new HashMap<>();

		id = System.currentTimeMillis();

		parameters.put("id", id);
		parameters.put("uid", UUID.randomUUID());
		parameters.put("first_name", "Kedar");
		parameters.put("last_name", "Joshi");
		parameters.put("date_of_birth", (int) TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()));
		parameters.put("creation_time", new Timestamp(System.currentTimeMillis()));

		session.execute(query, parameters);
	}

	@Test
	public void testInsertWithNamedParameters()
	{
		final String query = "INSERT INTO users (id, uid, first_name, last_name, date_of_birth, creation_time) VALUES (:id, :uid, :first_name, :last_name, :date_of_birth, :creation_time)";
		final Map<String, Object> parameters = new HashMap<>();

		final long id = System.currentTimeMillis();
		final UUID uid = UUID.randomUUID();

		parameters.put("id", id);
		parameters.put("uid", uid);
		parameters.put("first_name", "Kedar");
		parameters.put("last_name", "Joshi");
		parameters.put("date_of_birth", (int) TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()));
		parameters.put("creation_time", new Timestamp(System.currentTimeMillis()));

		session.execute(query, parameters);

		final ResultSet result = session.execute("SELECT * FROM users WHERE id = ?;", id);
		final List<Row> rows = result.all();

		Assert.assertEquals(1, rows.size());

		final Row row = rows.get(0);

		Assert.assertNotNull(row);
		Assert.assertEquals(uid, row.getUUID("uid"));
	}

	@Test
	public void testUpdate()
	{
		logger.info("Testing update .. ");

		final String query = "UPDATE users SET first_name = :first_name, last_name = :last_name WHERE id = :id;";
		final Map<String, Object> parameters = new HashMap<>();

		final String firstName = "Thomas";
		final String lastName = "Anderson";

		parameters.put("first_name", firstName);
		parameters.put("last_name", lastName);

		parameters.put("id", id);

		session.execute(query, parameters);

		final ResultSet result = session.execute("SELECT * FROM users WHERE id = ?;", id);

		Assert.assertEquals(1, result.getAvailableWithoutFetching());

		final Row row = result.one();

		Assert.assertNotNull(row);
		Assert.assertEquals(firstName, row.getString("first_name"));
		Assert.assertEquals(lastName, row.getString("last_name"));
	}

	@Test
	public void testSelectByPrimaryKey()
	{
		logger.info("Testing select by primary key .. ");

		final String query = "SELECT * FROM users WHERE id = :id;";
		final Map<String, Object> parameters = Collections.singletonMap("id", id);
		final ResultSet result = session.execute(query, parameters);

		final Row row = result.one();

		Assert.assertNotNull(row);
		Assert.assertEquals(id, row.getLong("id"));

		logger.info("{} : {} : {}", row.getLong("id"), row.getString("first_name"), row.getString("last_name"));
	}

	@Test
	public void testSelectAll()
	{
		logger.info("Testing select all .. ");

		final ResultSet result = session.execute("SELECT * FROM users;");
		final List<Row> rows = result.all();

		Assert.assertEquals(1, rows.size());

		for (Row row : rows)
		{
			logger.info("{} : {} : {}", row.getLong("id"), row.getString("first_name"), row.getString("last_name"));
		}
	}

	@Test
	public void testDeleteByPrimaryKey()
	{
		logger.info("Testing delete by primary key .. ");

		final String query = "DELETE FROM users WHERE id = :id;";
		final Map<String, Object> parameters = Collections.singletonMap("id", id);

		session.execute(query, parameters);

		final ResultSet result = session.execute("SELECT * FROM users;");
		final List<Row> rows = result.all();

		Assert.assertEquals(0, rows.size());
	}

	@AfterClass
	public static void close()
	{
		session.close();
		cluster.close();
	}
}

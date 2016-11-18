package com.workingtheory.example.java7;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class WhatIsNew
{
	private static final Logger logger = LogManager.getLogger(WhatIsNew.class);

	/**
	 * Refer http://www.oracle.com/technetwork/java/javase/jdk7-relnotes-418459.html
	 *
	 */
	public static void main(String[] args)
	{
		// Basic language constructs

		// TODO Hexadecimal literals
		byte hexByte = 0x70; // 112
		int hex = 0xFF; // 255
		int hexShort = 0xFFFF; // ‭65535‬

		// TODO Binary Literals
		byte b = 0b01110000; // 112 = 0x70
		int i = 0b11111111; // 255 = 0xFF
		int binShort = 0b1111_1111_1111_1111; // YES NUMBERS CAN BE SEPARATED BY UNDERSCORES NOW


		// TODO Strings in switch

		switch ("Hi")
		{
			case "Hi":
			{
				logger.info("Java says 'Hi' .. ");
				break;
			}

			case "Hello":
			{
				logger.info("Java says 'Hello' .. ");
				break;
			}
		}

		/**
		 * TODO Try-with-resources
		 * TODO Java 5 {@link Closeable}
		 * TODO {@link AutoCloseable}
		 * TODO {@link Connection}
		 * JDBC (prepared) statements, JDBC Resultsets, Input / Output streams, Files streams, Socket connections, readers and writers etc.
		 */
		try (Connection connection = DriverManager.getConnection("jdbc:postgresql:///bootstrap", "postgres", "Tech8092"))
		{

		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}

		// TODO Same as

		java.sql.Connection connection = null;

		try
		{
			connection = DriverManager.getConnection("jdbc:postgresql:///bootstrap", "postgres", "Tech8092");
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}
		finally
		{
			if (connection != null)
			{
				try
				{
					connection.close();
				}
				catch (SQLException e)
				{
					logger.error(e.getMessage(), e);

				}
			}
		}

		// TODO Multi-catch

		try
		{
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			logger.info(format.parse("2016-11-15"));

			double value = Double.parseDouble("abc");
		}
		catch (ParseException | NumberFormatException e)
		{
			logger.error(e.getMessage(), e);
		}

		// TODO Same as

		try
		{
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			logger.info(format.parse("2016-11-15"));

			double value = Double.parseDouble("abc");
		}
		catch (ParseException e)
		{
			logger.error(e.getMessage(), e);
		}
		catch (NumberFormatException e)
		{
			logger.error(e.getMessage(), e);
		}

		// TODO Type inference

		// Java 6

		List<String> list1 = new ArrayList<String>();

		// Java 7

		List<String> list2 = new ArrayList<>();

		// TODO NIO 2.0

	}
}

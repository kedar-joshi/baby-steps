package com.workingtheory.example.java8;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class WhatIsNew
{
	private static final Logger logger = LogManager.getLogger(WhatIsNew.class);

	private static void staticRun()
	{
		logger.debug("Logging from static method .. ");
	}

	private void objectRun()
	{
		logger.debug("Logging from static method .. ");
	}

	interface Functional
	{
		void execute();
	}

	interface DefaultMethods
	{
		default int defaultMethod(int i, int j)
		{
			return i + j;
		}

		default int defaultMethod2(int i, int j)
		{
			return i - j;
		}

		void otherMethod();
	}

	/**
	 * Refer http://www.oracle.com/technetwork/java/javase/8-whats-new-2157071.html
	 */
	public static void main(String[] args)
	{
		ExecutorService executor = Executors.newSingleThreadExecutor();

		// TODO Oh my lambda

		executor.execute(() -> logger.debug("Lambdas are cool .. "));

		// TODO same as

		executor.execute(new Runnable()
		{
			public void run()
			{
				logger.debug("Anonymous implementations are verbose .. ");
			}
		});

		// TODO Method references

		executor.execute(WhatIsNew::staticRun); // Static method reference

		executor.execute(new WhatIsNew()::objectRun); // Object method reference

		// TODO Functional interfaces

		/**
		 * <pre>
		 *     	interface Functional
		 *      {
		 *        void execute();
		 *      }
		 * </pre>
		 */

		// TODO Default methods

		/**
		 * <pre>
		 *      interface DefaultMethods
		 *      {
		 *          default int defaultMethod(int i, int j)
		 *          {
		 *              return i + j;
		 *          }
		 *
		 *          default int defaultMethod2(int i, int j)
		 *          {
		 *              return i - j;
		 *          }
		 *
		 *          void otherMethod();
		 *      }
		 * </pre>
		 */

		// TODO Streams

		List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

		list.stream() // List to stream
		    .filter(integer -> integer % 2 == 0) // Filter all even numbers
		    .collect(Collectors.toList()) // Collect the result
		    .forEach(System.out::println); // Print everything

		// TODO Optional

		Optional<Integer> optional1 = getOptional(null);

		optional1.ifPresent(integer -> logger.info("Result 1 : {}", integer));

		int result = optional1.orElse(10);

		Optional<Integer> optional2 = getOptional(10);

		if (optional2.isPresent())

		{
			logger.info("Result : {}", optional2.get());
		}

		// TODO Date Time

		LocalDate localDate = LocalDate.now();

		final LocalDate newDate = localDate.plus(1, ChronoUnit.DAYS);

		executor.shutdownNow();

		// TODO Completable futures
	}

	private static Optional<Integer> getOptional(Integer input)
	{
		return input == null ? Optional.empty() : Optional.of(input + 10);
	}
}

package com.workingtheory.example.java8;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelloCompletableFuture
{
	private static final Logger logger = LogManager.getLogger(HelloCompletableFuture.class);

	public static void main(String[] args)
	{
		ExecutorService executorService = Executors.newSingleThreadExecutor();

		try
		{
			CompletableFuture<Timestamp> future = new CompletableFuture<>();
			future.whenComplete(HelloCompletableFuture::accept);

			executorService.execute(() ->
			{
				try
				{
					logger.info(Thread.currentThread().getName());

					// Do some work
					Thread.sleep(1000);

					future.complete(new Timestamp(System.currentTimeMillis()));
				}
				catch (Exception e)
				{
					logger.error(e.getMessage(), e);
				}
			});
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}

		executorService.shutdown();
	}

	private static void accept(Timestamp timestamp, Throwable throwable)
	{
		logger.info("Received : {}", timestamp);
	}

}

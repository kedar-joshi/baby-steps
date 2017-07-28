package com.workingtheory.scala

import org.apache.logging.log4j.{LogManager, Logger}

object Loops
{
	private val logger: Logger = LogManager.getLogger(Loops.this)

	def main(args: Array[String]): Unit =
	{

		for (i <- 1 to 10) logger.info("i : {}", i)
		for (i <- 1 to 10) logger.info("i : ${i}")

		val list = List("Hi", "Hello", "How", "Are", "You")

		for (word <- list)
		{
			logger.info("Word : {}", word)
		}

		for (word <- list if !(word equalsIgnoreCase "Hi"))
		{
			logger.info("Word : {}", word)
		}

		for (word <- list
		     if !(word equals "Hi")
		     if !(word equals "hi"))
		{
			logger.info("Word : {}", word)
		}

		val yielder = for { word <- list if !(word equalsIgnoreCase "Hi")} yield word

		logger.info("Yield : {}", yielder)

	}
}

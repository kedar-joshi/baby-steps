package com.workingtheory.scala

import org.apache.logging.log4j.{LogManager, Logger}

object OperatorOverloading
{
	private val logger: Logger = LogManager.getLogger(OperatorOverloading.this)

	def main(args: Array[String]): Unit =
	{
		val message = new Message

		message ! "Hi"

		val list = List('b', 'c', 'd')

		logger.info("List : {}", list)

		val list1 = 'a' :: list // TODO why not list :: 'a'

		logger.info("List : {}", list1)
	}
}

class Message()
{
	private val logger: Logger = LogManager.getLogger(Message.this)

	def ! (message: String): Unit =
	{
		logger.info("Message : {}", message)
	}

}

package com.workingtheory.scala

import org.apache.logging.log4j.{LogManager, Logger}

/**
  * This class demonstrates creation of class level 'static' method and object level method in Scala
  * and print a hello world using the created methods.
  */
class Methods
{
	private val logger: Logger = LogManager.getLogger(Methods.this)

	/**
	  * Defines a 'void' method 'sayHello' which takes a parameter 'caller' of type string.
	  *
	  * @param caller name of the caller to greet.
	  */
	def greet(caller: String): Unit =
	{
		logger.info("Hello {}", caller)
	}

	/**
	  * Defines an overloaded method 'sayHello' to greet multiple callers.
	  * Method accepts variable arguments of type string, as suggested by 'callers: String*' declaration;
	  * which is equivalent of 'String... callers' in Java.
	  *
	  * @param callers list of callers to greet.
	  */
	def greet(callers: String*): Unit =
	{
		for (caller <- callers)
		{
			greet(caller)
		}
	}
}

/**
  * Container for static 'main' method.
  */
object Methods
{
	def main(args: Array[String]): Unit =
	{
		val methods = new Methods // Just like semicolons empty constructor parenthesis '()' are also optional

		methods.greet("World")
		methods.greet("World", "Kedar", "Neo")
	}
}

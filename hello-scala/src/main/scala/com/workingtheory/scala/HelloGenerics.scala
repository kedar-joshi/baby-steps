package com.workingtheory.scala

import com.workingtheory.scala.generics.{AbstractClass, ConcreteClass}
import org.apache.logging.log4j.{LogManager, Logger}

object HelloGenerics
{
	def main(args: Array[String]): Unit =
	{
		val instance = new ConcreteClass("Hello World")

		instance.printValue()
	}
}


package generics
{

	abstract class AbstractClass
	{
		private val logger: Logger = LogManager.getLogger(AbstractClass.this)

		type In

		val in: In

		def printValue()
	}

	class ConcreteClass(val in: String) extends AbstractClass
	{
		private val logger: Logger = LogManager.getLogger(ConcreteClass.this)

		type In = String

		def printValue(): Unit =
		{
			logger.info("Value : {}", in)
		}
	}

}

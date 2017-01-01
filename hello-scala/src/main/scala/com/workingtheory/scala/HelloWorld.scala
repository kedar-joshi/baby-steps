package com.workingtheory.scala

import org.apache.logging.log4j.{LogManager, Logger}

/**
  * This class demonstrates 'how to write main method' in Scala and print a hello world.
  *
  * Unfortunately, Scala doesn't support class level 'static' variables or methods.
  * Instead, it has a concept of 'object' to hold all the static variables or methods of a class.
  *
  * Thus, if we need 'public static void main' in scala, we create an 'object' instead of a class and
  * then declare the method 'main' in it.
  *
  */
object HelloWorld
{
	private val logger: Logger = LogManager.getLogger(HelloWorld.this)

	/**
	  * 'def' keyword is used to declare methods.
	  * Since we are inside 'object', method {@link #main(String[]) main} becomes a static one.
	  *
	  * @param args command line arguments, passed on to the program.
	  */
	def main(args: Array[String]): Unit =
	{
		/**
		  * Scala globally defines many 'java equivalent' useful methods like 'System.out.print' and 'System.out.println'.
		  *
		  * Remember, No need of semicolons at the end of the line.
		  */

		print("Hello World .. ")

	}
}
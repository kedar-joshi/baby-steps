package com.workingtheory.scala

import java.util

import com.workingtheory.scala.declarations.classes.Person
import org.apache.logging.log4j.{LogManager, Logger}

/**
  * This class demonstrates declaration of variables of various data types and collections.
  *
  */
object Declarations
{
	private val logger: Logger = LogManager.getLogger(Declarations.this)

	def main(args: Array[String]): Unit =
	{
		// Data types

		val string: String = "String"
		val string1 = "String"

		val multiLineString =
			""" This is a
			   useful for writing SQL queries
			   just take care about the line indents
			"""

		logger.info("Multi line string : {}", multiLineString)

		// There are no primitives in scala

		val integer: Int = 100
		val long: Long = 100
		val double: Double = 100


		// Mutable variable
		var age: Int = 10 // same as 'int age = 10;'

		age = 20 // allowed

		// Immutable variable
		val height: Int = 6 // same as 'final int age = 10;'

		// height = 5 not allowed

		logger.info("Age : {}", age)
		logger.info("Height : {}", height)

		// Empty array with redundant type declaration
		val array: Array[String] = new Array[String](5)

		// Empty array with type inference
		val array1 = new Array[String](5)

		// Array with inline initialization
		val array2 = Array("Hi", "Hello", "How", "Are", "You")

		// Similarly for lists
		val list: util.List[String] = new util.ArrayList[String]
		val list1 = new util.ArrayList[String]
		val list2 = List("Hi", "Hello", "How", "Are", "You")


		// Tuples

		val tuple = (1, "Kedar", "Joshi", 30)
		val tuple1:(Int, String, String, Int) = (1, "Kedar", "Joshi", 30)

		logger.info("Tuple : {}", tuple._1)
		logger.info("Tuple : {}", tuple._2)
		logger.info("Tuple : {}", tuple._3)
		logger.info("Tuple : {}", tuple._4)

		// Maps

		val map:Map[Int, String] = Map(1 -> "Kedar Joshi", 2 -> "Thomas Anderson")
		val map1 = Map(1 -> "Kedar Joshi", 2 -> "Thomas Anderson")

		// Optional

		val optional:Option[Int] = None
		val optional1 = None

		val person = new Person("Kedar", "Joshi", 30)

		logger.info("Name : Age :: {} {} : {}", person.firstName, person.lastName, person.age)
	}
}

package declarations.classes
{
	class Person(val firstName:String, val lastName:String, val age:Int)
}

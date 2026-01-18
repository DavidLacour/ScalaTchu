package ch.epfl.tchu.net

import ch.epfl.tchu.SortedBag
import scala.util.matching.Regex

/** Trait for serialization/deserialization. */
trait Serde[T]:
  /** Serializes the given object to a string. */
  def serialize(obj: T): String

  /** Deserializes the given string to an object. */
  def deserialize(str: String): T

object Serde:
  /** Creates a serde from serialize and deserialize functions. */
  def of[T](ser: T => String, deser: String => T): Serde[T] = new Serde[T]:
    def serialize(obj: T): String = ser(obj)
    def deserialize(str: String): T = deser(str)

  /** Creates a serde for an enum or list of all values. */
  def oneOf[T](all: List[T]): Serde[T] = new Serde[T]:
    def serialize(obj: T): String = all.indexOf(obj).toString
    def deserialize(str: String): T = all(str.toInt)

  /** Creates a serde for lists of elements. */
  def listOf[T](serde: Serde[T], separator: String): Serde[List[T]] = new Serde[List[T]]:
    def serialize(list: List[T]): String =
      if list.isEmpty then ""
      else list.map(serde.serialize).mkString(separator)

    def deserialize(str: String): List[T] =
      if str.isEmpty then List.empty
      else str.split(Regex.quote(separator), -1).toList.map(serde.deserialize)

  /** Creates a serde for sorted bags of elements. */
  def bagOf[T](serde: Serde[T], separator: String)(using ord: Ordering[T]): Serde[SortedBag[T]] =
    val listSerde = listOf(serde, separator)
    new Serde[SortedBag[T]]:
      def serialize(bag: SortedBag[T]): String = listSerde.serialize(bag.toList)
      def deserialize(str: String): SortedBag[T] = SortedBag.of(listSerde.deserialize(str))

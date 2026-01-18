package ch.epfl.tchu

import Preconditions.checkArgument
import scala.collection.immutable.{SortedMap, SortedSet}
import scala.collection.mutable

/** Immutable sorted multiset.
  *
  * @tparam E the type of elements in the multiset
  */
final class SortedBag[E] private (private val elements: SortedMap[E, Int])(using Ordering[E]) extends Iterable[E]:

  /** Returns true if the multiset is empty. */
  override def isEmpty: Boolean = elements.isEmpty

  /** Returns the number of elements in the multiset. */
  override def size: Int = elements.values.sum

  /** Returns the multiplicity of the given element. */
  def countOf(element: E): Int = elements.getOrElse(element, 0)

  /** Returns true if the element belongs to the multiset (at least once). */
  def contains(element: E): Boolean = elements.contains(element)

  /** Returns true if the given multiset is a subset of this one. */
  def contains(that: SortedBag[E]): Boolean =
    that.elements.forall { case (e, n) => n <= countOf(e) }

  /** Returns the element at the given index. */
  def get(index: Int): E =
    require(index >= 0 && index < size, s"Index out of bounds: $index")
    var remaining = index
    var result: Option[E] = None
    val iter = elements.iterator
    while result.isEmpty && iter.hasNext do
      val (e, count) = iter.next()
      if remaining < count then result = Some(e)
      else remaining -= count
    result.getOrElse(throw new AssertionError("Should never reach here"))

  /** Returns the union of this multiset and the given multiset. */
  def union(that: SortedBag[E]): SortedBag[E] =
    val newElements = mutable.SortedMap.from(elements)
    that.elements.foreach { case (e, n) =>
      newElements.updateWith(e) {
        case Some(existing) => Some(existing + n)
        case None => Some(n)
      }
    }
    new SortedBag(SortedMap.from(newElements))

  /** Returns the difference between this multiset and the given multiset. */
  def difference(that: SortedBag[E]): SortedBag[E] =
    val newElements = mutable.SortedMap.from(elements)
    that.elements.foreach { case (e, n) =>
      newElements.updateWith(e) {
        case Some(existing) if existing > n => Some(existing - n)
        case _ => None
      }
    }
    new SortedBag(SortedMap.from(newElements))

  /** Returns all subsets of this multiset of the given size. */
  def subsetsOfSize(subsetSize: Int): Set[SortedBag[E]] =
    checkArgument(subsetSize >= 0 && subsetSize <= size, s"Invalid subset size: $subsetSize")
    if subsetSize == 0 then
      Set(SortedBag.of[E])
    else
      elements.keys.flatMap { e1 =>
        val s1 = SortedBag.of(e1)
        this.difference(s1).subsetsOfSize(subsetSize - 1).map(s1.union)
      }.toSet

  /** Returns a list containing the elements of the multiset, in order. */
  override def toList: List[E] =
    elements.flatMap { case (e, n) => List.fill(n)(e) }.toList

  /** Returns an iterator over the elements of the multiset. */
  override def iterator: Iterator[E] =
    elements.iterator.flatMap { case (e, n) => Iterator.fill(n)(e) }

  /** Returns an immutable map associating each element with its multiplicity. */
  def toMap: Map[E, Int] = elements

  /** Returns the set of elements in the multiset. */
  def toSet: SortedSet[E] = SortedSet.from(elements.keys)

  override def hashCode(): Int = elements.hashCode()

  override def equals(that: Any): Boolean = that match
    case other: SortedBag[?] => elements == other.elements
    case _ => false

  override def toString: String =
    elements.map { case (e, n) =>
      if n > 1 then s"$n×$e" else e.toString
    }.mkString("{", ", ", "}")

object SortedBag:
  /** Creates an empty multiset. */
  def of[E: Ordering]: SortedBag[E] = new SortedBag(SortedMap.empty)

  /** Creates a multiset containing a single element. */
  def of[E: Ordering](e1: E): SortedBag[E] = of(1, e1)

  /** Creates a multiset containing two elements. */
  def of[E: Ordering](e1: E, e2: E): SortedBag[E] = of(1, e1, 1, e2)

  /** Creates a multiset containing n occurrences of element e. */
  def of[E: Ordering](n: Int, e: E): SortedBag[E] =
    checkArgument(n >= 0, s"Count must be non-negative: $n")
    if n == 0 then of[E]
    else new SortedBag(SortedMap(e -> n))

  /** Creates a multiset containing n1 occurrences of e1 and n2 occurrences of e2. */
  def of[E: Ordering](n1: Int, e1: E, n2: Int, e2: E): SortedBag[E] =
    checkArgument(n1 >= 0 && n2 >= 0, "Counts must be non-negative")
    val builder = mutable.SortedMap.empty[E, Int]
    if n1 > 0 then builder.updateWith(e1) {
      case Some(existing) => Some(existing + n1)
      case None => Some(n1)
    }
    if n2 > 0 then builder.updateWith(e2) {
      case Some(existing) => Some(existing + n2)
      case None => Some(n2)
    }
    new SortedBag(SortedMap.from(builder))

  /** Creates a multiset containing the elements from an iterable. */
  def of[E: Ordering](iterable: Iterable[E]): SortedBag[E] =
    val builder = Builder[E]()
    iterable.foreach(builder.add)
    builder.build()

  /** Builder for SortedBag. */
  class Builder[E: Ordering]:
    private val elements = mutable.SortedMap.empty[E, Int]

    /** Adds n occurrences of the given element. */
    def add(count: Int, element: E): this.type =
      checkArgument(count >= 0, s"Count must be non-negative: $count")
      if count > 0 then
        elements.updateWith(element) {
          case Some(existing) => Some(existing + count)
          case None => Some(count)
        }
      this

    /** Adds a single occurrence of the given element. */
    def add(e: E): this.type = add(1, e)

    /** Adds all elements from another multiset. */
    def add(that: SortedBag[E]): this.type =
      that.elements.foreach { case (e, c) =>
        elements.updateWith(e) {
          case Some(existing) => Some(existing + c)
          case None => Some(c)
        }
      }
      this

    /** Returns true if the builder is empty. */
    def isEmpty: Boolean = elements.isEmpty

    /** Returns the current size of the builder. */
    def size: Int = elements.values.sum

    /** Builds and returns the multiset. */
    def build(): SortedBag[E] = new SortedBag(SortedMap.from(elements))

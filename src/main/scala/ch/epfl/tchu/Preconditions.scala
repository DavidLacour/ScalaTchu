package ch.epfl.tchu

/** Utility object for validating preconditions. */
object Preconditions:

  /** Checks that the given condition is true.
    * @param shouldBeTrue the condition that must be true
    * @throws IllegalArgumentException if the condition is false
    */
  inline def checkArgument(shouldBeTrue: Boolean): Unit =
    if !shouldBeTrue then throw IllegalArgumentException()

  /** Checks that the given condition is true with a custom message.
    * @param shouldBeTrue the condition that must be true
    * @param message the error message
    * @throws IllegalArgumentException if the condition is false
    */
  inline def checkArgument(shouldBeTrue: Boolean, message: String): Unit =
    if !shouldBeTrue then throw IllegalArgumentException(message)

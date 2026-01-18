package ch.epfl.test

import scala.util.Random

object TestRandomizer:
  /** Fixed random seed to guarantee reproducibility. */
  val Seed: Long = 2021L

  val RandomIterations: Int = 1000

  def newRandom(): Random = Random(Seed)

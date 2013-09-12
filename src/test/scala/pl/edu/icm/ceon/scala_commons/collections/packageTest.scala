/*
 * Copyright (c) 2013-2013 ICM UW
 */

package pl.edu.icm.ceon.scala_commons.collections

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author Mateusz Fedoryszak (m.fedoryszak@icm.edu.pl)
 */
class packageTest {
  @Test
  def tabulateTest() {
    val array = tabulate[Int](2, 4) {
      (array, i, j) =>
        if (i == 0 || j == 0)
          1
        else
          array(i - 1)(j) + array(i)(j - 1)
    }

    assertEquals(2, array.length)
    assertEquals(4, array(0).length)

    for (i <- 1 until array.length)
      for (j <- 1 until array(i).length)
        assertEquals(array(i - 1)(j) + array(i)(j - 1), array(i)(j))
  }

  @Test
  def generateTest() {
    assertEquals(List(), generate(0, 0))
    assertEquals(List(1, 1, 1), generate(1, 3))
  }

  @Test
  def insertTest() {
    assertEquals(List(), insert(List(), 0))
    assertEquals(List(1), insert(List(1), 0))
    assertEquals(List(1, 0, 1), insert(List(1, 1), 0))
    assertEquals(List(1, 0, 1, 0, 1), insert(List(1, 1, 1), 0))
  }

  @Test
  def characteristicListTest() {
    assertEquals(List(), characteristicList(List(), 0))
    assertEquals(List(false, false, false), characteristicList(List(), 3))
    assertEquals(List(false, true, false), characteristicList(List(1), 3))
    assertEquals(List(true, false, true), characteristicList(List(0, 2), 3))
    assertEquals(List(true, true, true), characteristicList(List(0, 1, 2), 3))
  }

  @Test
  def splitTest() {
    assertEquals(List(), split(List.empty[Int])(_ == 0))
    assertEquals(List(), split(List(0, 0, 0, 0))(_ == 0))
    assertEquals(List(List(1), List(1, 1)), split(List(0, 1, 0, 0, 1, 1, 0))(_ == 0))
    assertEquals(List(List(1), List(1, 1)), split(List(1, 0, 0, 1, 1, 0))(_ == 0))
    assertEquals(List(List(1), List(1, 1)), split(List(0, 1, 0, 0, 1, 1))(_ == 0))
  }
}

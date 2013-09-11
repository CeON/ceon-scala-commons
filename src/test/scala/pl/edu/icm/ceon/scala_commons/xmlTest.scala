package pl.edu.icm.ceon.scala_commons

import org.junit.Assert._
import org.junit.Test

import xml._

/**
 * @author Mateusz Fedoryszak (m.fedoryszak@icm.edu.pl)
 */
class xmlTest {
  @Test
  def xmlToElemsTest() {
    assertEquals(xmlToElems("<tag>"), List(StartTag("tag")))
    assertEquals(xmlToElems("<tag>text</tag>"), List(StartTag("tag"), Text("text"), EndTag("tag")))
    assertEquals(xmlToElems("begin<tag>text</tag>end"), List(Text("begin"), StartTag("tag"), Text("text"), EndTag("tag"), Text("end")))
  }
}

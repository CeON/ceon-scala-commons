/*
 * Copyright (c) 2013-2013 ICM UW
 */

package pl.edu.icm.ceon.scala_commons.nlm

import org.junit.Assert._
import org.junit.{Ignore, Test}
import nlmToDocumentProto.pubmedNlmToProtoBuf
import java.io.StringWriter
import scala.collection.JavaConversions._
import pl.edu.icm.coansys.models.DocumentProtos.DocumentWrapper

/**
 * @author Michal Oniszczuk (m.oniszczuk@icm.edu.pl)
 */
class DocumentProtosToNlmTransformerTest {

  val doc = pubmedNlmToProtoBuf(this.getClass.getResourceAsStream("/pl/edu/icm/ceon/scala_commons/nlm/sample.nxml"))
  val one = List(doc)
  val two = List(doc, doc)

  val dp2nlm = new DocumentProtosToNlmTransformer
  val nlm2dp = new NlmToDocumentProtosTransformer

  @Test
  def writeOutputShouldBeTheSameNoMatterOfInterfaceUsed() {
    writeOutputShouldBeTheSameNoMatterOfInterfaceUsed(one)
    writeOutputShouldBeTheSameNoMatterOfInterfaceUsed(two)
  }


  private def writeOutputShouldBeTheSameNoMatterOfInterfaceUsed(docs: List[DocumentWrapper]) {
    val sw = new StringWriter
    dp2nlm write(sw, docs)
    assertEquals(dp2nlm write docs, sw toString)
  }

  @Test
  @Ignore
  def convertingBackAndForthShouldNotChangeDoc() {
    assertEquals(one, nlm2dp read (dp2nlm write one))
  }
}

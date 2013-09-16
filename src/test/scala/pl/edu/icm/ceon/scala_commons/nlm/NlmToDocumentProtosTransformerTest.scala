package pl.edu.icm.ceon.scala_commons.nlm

import org.junit.Test
import org.junit.Assert._
import java.io.InputStreamReader
import org.apache.commons.io.IOUtils

/**
 * @author Mateusz Fedoryszak (m.fedoryszak@icm.edu.pl)
 */
class NlmToDocumentProtosTransformerTest {
  @Test
  def fromReaderTest() {
    val transformer = new NlmToDocumentProtosTransformer
    val docs = transformer.read(
      new InputStreamReader(this.getClass.getResourceAsStream("/pl/edu/icm/ceon/scala_commons/nlm/sample.nxml")))

    assertEquals(docs.get(0).getDocumentMetadata.getBasicMetadata.getDoi, "10.1208/s12248-008-9022-y")
  }

  @Test
  def fromStringTest() {
    val transformer = new NlmToDocumentProtosTransformer
    val contents = IOUtils.toString(this.getClass.getResourceAsStream("/pl/edu/icm/ceon/scala_commons/nlm/sample.nxml"))
    val docs = transformer.read(contents)

    assertEquals(docs.get(0).getDocumentMetadata.getBasicMetadata.getDoi, "10.1208/s12248-008-9022-y")
  }
}

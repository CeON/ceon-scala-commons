package pl.edu.icm.ceon.scala_commons.nlm

import pl.edu.icm.model.transformers.{MetadataFormat, MetadataModel, MetadataReader}
import pl.edu.icm.coansys.models.DocumentProtos.DocumentWrapper
import java.io.Reader
import org.apache.commons.io.IOUtils
import org.apache.commons.io.input.ReaderInputStream

/**
 * @author Mateusz Fedoryszak (m.fedoryszak@icm.edu.pl)
 */
class NlmToDocumentProtosTransformer extends MetadataReader[DocumentWrapper] {
  private val encoding = "UTF-8"

  def getSourceFormat = new MetadataFormat("NLM", "3.0")

  def getTargetModel = new MetadataModel[DocumentWrapper]("DocumentWrapper", classOf[DocumentWrapper])

  def read(text: String, hints: AnyRef*) =
    java.util.Arrays.asList(pubmedNlmToProtoBuf(IOUtils.toInputStream(text, encoding)))

  def read(reader: Reader, hints: AnyRef*) =
    java.util.Arrays.asList(pubmedNlmToProtoBuf(new ReaderInputStream(reader, encoding)))
}

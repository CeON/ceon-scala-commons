/*
 * Copyright (c) 2013-2013 ICM UW
 */

package pl.edu.icm.ceon.scala_commons.nlm

import pl.edu.icm.model.transformers.{MetadataModel, MetadataFormat, MetadataWriter}
import pl.edu.icm.coansys.models.DocumentProtos.DocumentWrapper
import java.io.Writer
import java.util
import scala.collection.JavaConversions._
import documentProtoToNlm.documentWrapperToNLM
import scalaz._
import Scalaz._

/**
 * @author Michal Oniszczuk (m.oniszczuk@icm.edu.pl)
 *         Created: 19.09.2013 13:26
 */
class DocumentProtosToNlmTransformer extends MetadataWriter[DocumentWrapper] {
  private val encoding = "UTF-8"

  def getSourceModel: MetadataModel[DocumentWrapper] = new MetadataModel[DocumentWrapper]("DocumentWrapper", classOf[DocumentWrapper])

  def getTargetFormat: MetadataFormat = new MetadataFormat("NLM", "3.0")

  def write(objects: util.List[DocumentWrapper], hints: AnyRef*): String =
    (objects map documentWrapperToNLM).mkString("\n\n")

  /*
   * In Haskell, it would be just:
   * msum $ intersperse (printW "\n") (map printW objects) :: IO ()
   *   where printW = writer.write
   */
  def write(writer: Writer, objects: util.List[DocumentWrapper], hints: AnyRef*) {
    objects.map(x =>
      () => writer.write(documentWrapperToNLM(x))
    )
      .toList
      .intersperse(() => writer.write("\n\n"))
      .foreach(_())
  }
}

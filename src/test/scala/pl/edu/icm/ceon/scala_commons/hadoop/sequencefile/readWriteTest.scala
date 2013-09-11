package pl.edu.icm.ceon.scala_commons.hadoop.sequencefile

import resource._
import java.io.File
import org.junit.Assert._
import org.junit.Test
import org.apache.commons.io.FileUtils

/**
 * @author Mateusz Fedoryszak (m.fedoryszak@icm.edu.pl)
 */
class readWriteTest {

  @Test
  def basicTest() {
    val content = List(("k1", "v1"), ("k2", "v2"), ("k3", "v3"))
    var tmpFile: File = null
    try {
      tmpFile = File.createTempFile("readWriteTest", ".sq")
      for (write <- managed(ConvertingSequenceFileWriter.fromUri[String, String](tmpFile.toURI.toString))) {
        content.foreach(write)
      }

      val result =
        managed(ConvertingSequenceFileIterator.fromUri[String, String](tmpFile.toURI.toString)).map(_.toList).opt.get

      assertEquals(result, content)
    }
    finally {
      FileUtils.deleteQuietly(tmpFile)
    }
  }
}

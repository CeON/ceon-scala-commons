package pl.edu.icm.ceon.scala_commons.hadoop.sequencefile

import org.apache.hadoop.io.{Writable, SequenceFile}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path


/**
 * @author Mateusz Fedoryszak (m.fedoryszak@icm.edu.pl)
 */
class SequenceFileIterator(reader: SequenceFile.Reader) extends Iterator[(Writable, Writable)] {
  private var key: Writable = null
  private var value: Writable = null
  private var preloaded = false

  private def readNext: Boolean = {
    key = reader.getKeyClass.newInstance().asInstanceOf[Writable]
    value = reader.getValueClass.newInstance().asInstanceOf[Writable]

    reader.next(key, value)
  }

  def hasNext =
    preloaded || {
      preloaded = readNext
      preloaded
    }

  def next(): (Writable, Writable) = {
    if (!preloaded) {
      readNext
    }
    else {
      preloaded = false
    }

    (key, value)
  }

  def close() {
    reader.close()
  }
}

object SequenceFileIterator {
  def fromUri(uri: String, conf: Configuration = new Configuration()): SequenceFileIterator = {
    val reader = new SequenceFile.Reader(conf, SequenceFile.Reader.file(new Path(uri)))
    new SequenceFileIterator(reader)
  }
}

package pl.edu.icm.ceon.scala_commons.hadoop.sequencefile

import org.apache.hadoop.io.SequenceFile
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import com.nicta.scoobi.io.sequence.SeqSchema

/**
 * @author Mateusz Fedoryszak (m.fedoryszak@icm.edu.pl)
 */
class ConvertingSequenceFileWriter[K, V](val writer: SequenceFile.Writer)
                                        (implicit keySchema: SeqSchema[K],
                                         valueSchema: SeqSchema[V]) extends (((K, V)) => Unit) {
  def apply(arg: (K, V)) {
    append(arg._1, arg._2)
  }

  def append(key: K, value: V) {
    keySchema.toWritable(key)
    valueSchema.toWritable(value)
    writer.append(keySchema.toWritable(key), valueSchema.toWritable(value))
  }

  def close() {
    writer.close()
  }
}

/**
 * @author Mateusz Fedoryszak (m.fedoryszak@icm.edu.pl)
 */
object ConvertingSequenceFileWriter {
  def fromUri[K, V](uri: String, conf: Configuration = new Configuration())
                   (implicit keySchema: SeqSchema[K],
                    valueSchema: SeqSchema[V]): ConvertingSequenceFileWriter[K, V] = {
    val path: Path = new Path(uri)

    val writer = SequenceFile.createWriter(conf, SequenceFile.Writer.file(path),
      SequenceFile.Writer.keyClass(keySchema.mf.runtimeClass),
      SequenceFile.Writer.valueClass(valueSchema.mf.runtimeClass))
    new ConvertingSequenceFileWriter[K, V](writer)
  }
}
/*
 * Copyright (c) 2013-2013 ICM UW
 */

package pl.edu.icm.ceon.scala_commons.hadoop.sequencefile

import com.nicta.scoobi.io.sequence.SeqSchema
import org.apache.hadoop.io.SequenceFile
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path

/**
 * @author Mateusz Fedoryszak (m.fedoryszak@icm.edu.pl)
 */
class ConvertingSequenceFileIterator[K, V](iterator: SequenceFileIterator)
                                          (implicit keySchema: SeqSchema[K], valueSchema: SeqSchema[V]) extends Iterator[(K, V)] {
  def hasNext = iterator.hasNext

  def next() = {
    val (key, value) = iterator.next()
    (keySchema.fromWritable(key.asInstanceOf[keySchema.SeqType]),
      valueSchema.fromWritable(value.asInstanceOf[valueSchema.SeqType]))
  }

  def close() {
    iterator.close()
  }
}

object ConvertingSequenceFileIterator {
  def fromUri[K, V](uri: String, conf: Configuration = new Configuration())
                   (implicit keySchema: SeqSchema[K], valueSchema: SeqSchema[V]): ConvertingSequenceFileIterator[K, V] = {
    val reader = new SequenceFile.Reader(conf, SequenceFile.Reader.file(new Path(uri)))
    assert(reader.getKeyClass == keySchema.mf.runtimeClass, "SequenceFile key type doesn't match SeqSchema")
    assert(reader.getValueClass == valueSchema.mf.runtimeClass, "SequenceFile value type doesn't match SeqSchema")
    new ConvertingSequenceFileIterator[K, V](new SequenceFileIterator(reader))
  }
}

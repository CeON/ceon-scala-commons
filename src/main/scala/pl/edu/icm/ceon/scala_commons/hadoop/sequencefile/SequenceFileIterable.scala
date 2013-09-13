/*
 * Copyright (c) 2013-2013 ICM UW
 */

package pl.edu.icm.ceon.scala_commons.hadoop.sequencefile

import org.apache.hadoop.io.Writable

/**
 * @author Michal Oniszczuk (m.oniszczuk@icm.edu.pl)
 *         Created: 09.09.2013 15:37
 */
class SequenceFileIterable(uri: String) extends Iterable[(Writable, Writable)] {
  def iterator = SequenceFileIterator fromUri uri
}

object SequenceFileIterable {
  def fromUri(uri: String): SequenceFileIterable = new SequenceFileIterable(uri)
}

object SequenceFileValuesIterable {
  def fromUri(uri: String): Iterable[Writable] = (SequenceFileIterable fromUri uri) map {
    case (k, v) => v
  }
}

object SequenceFileKeysIterable {
  def fromUri(uri: String): Iterable[Writable] = (SequenceFileIterable fromUri uri) map {
    case (k, v) => k
  }
}

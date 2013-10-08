/*
 * Copyright (c) 2013-2013 ICM UW
 */

package pl.edu.icm.ceon.scala_commons.hadoop

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{Path, FileSystem}
import java.net.URI
import resource._
import org.apache.hadoop.io.{WritableComparable, Writable, MapFile, SequenceFile}
import org.apache.hadoop.io.SequenceFile.Sorter
import com.nicta.scoobi.Scoobi._
import pl.edu.icm.ceon.scala_commons.hadoop.writables.BytesIterable
import com.typesafe.scalalogging.slf4j.Logging

/**
 * @author Mateusz Fedoryszak (m.fedoryszak@icm.edu.pl)
 */
package object sequencefile extends Logging {
  /**
   * Reads from a SequenceFile its key and value types
   * @return a pair of key and value type
   */
  def extractTypes(uri: String)(implicit conf: Configuration): (Class[_], Class[_]) = {
    val fs = FileSystem.get(URI.create(uri), conf)
    val path = new Path(uri)
    managed(new SequenceFile.Reader(fs, path, conf)).acquireAndGet {
      reader =>
        val keyClass = reader.getKeyClass
        val valueClass = reader.getValueClass
        (keyClass, valueClass)
    }
  }

  /**
   * Converts SequenceFile to a MapFile.
   */
  def convertToMapFile(uri: String)(implicit conf: Configuration) {
    val fs = FileSystem.get(URI.create(uri), conf)
    val map = new Path(uri)
    val mapContents = fs.listStatus(map).head.getPath
    val mapData = new Path(map, MapFile.DATA_FILE_NAME)
    fs.rename(mapContents, mapData)
    val (keyClass, valueClass) = extractTypes(mapData.toUri.toString)
    MapFile.fix(fs, map, keyClass.asInstanceOf[Class[_ <: Writable]], valueClass.asInstanceOf[Class[_ <: Writable]], false, conf)
  }

  /**
   * Merges and sorts all SequenceFiles in given directory.
   */
  def merge(uri: String)(implicit conf: Configuration) {
    val fs = FileSystem.get(URI.create(uri), conf)
    val dir = new Path(uri)
    val paths: Array[Path] = fs.listStatus(dir).map(_.getPath).filterNot(_.getName.startsWith("_"))
    val mapData = new Path(dir, MapFile.DATA_FILE_NAME)
    val (keyClass, valueClass) = extractTypes(paths(0).toUri.toString)
    val sorter = new Sorter(fs, keyClass.asInstanceOf[Class[_ <: WritableComparable[_]]], valueClass, conf)
    sorter.setMemory(128 * 1000 * 1000)
    sorter.sort(paths, mapData, true)
  }

  def mergeWithScoobi(uri: String)(implicit conf: ScoobiConfiguration) {
    val maxReducers = conf.getMaxReducers
    conf.setMaxReducers(1)
    val entities = fromSequenceFile[String, BytesIterable](uri)
    val sorted = entities.groupByKey.mapFlatten{case (id, iter) => for (el <- iter)  yield (id, el)}
    val tmpUri = uri + "_tmp"
    persist(sorted.toSequenceFile(tmpUri))
    conf.setMaxReducers(maxReducers)

    val fs = FileSystem.get(URI.create(uri), conf)
    fs.delete(new Path(uri), true)
    val dir = new Path(tmpUri)
    val paths: Array[Path] = fs.listStatus(dir).map(_.getPath).filterNot(_.getName.startsWith("_"))
    val mapData = new Path(new Path(uri), MapFile.DATA_FILE_NAME)
    if (paths.length > 0) {
      fs.mkdirs(new Path(uri))
      fs.rename(paths.head, mapData)
    } else {
      logger.warn("No output produced")
    }
    fs.delete(new Path(tmpUri), true)
  }
}

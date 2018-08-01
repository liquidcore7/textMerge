package com.liquidcore7.textmerge.chain.sourceconverters
import java.io.{File, FileInputStream}

import com.liquidcore7.textmerge.chain.Word


class Text2NGramsConverter(private val file: File, private val n: Int) extends ToIterableConvertible {

  class BufferedReader extends Iterable[String] with AutoCloseable {
    private val buffer: Array[Byte] = Array.ofDim(n)
    private val fileHandle = new FileInputStream(file)

    override def close(): Unit = fileHandle.close()

    override def iterator: Iterator[String] = new Iterator[String] {
      override def hasNext: Boolean = fileHandle.available > 0
      override def next(): String = {
        fileHandle.read(buffer)
        String.valueOf(buffer)
      }
    }
  }

  val reader: BufferedReader = new BufferedReader

  override def toIterable: Iterable[Word] = {
    var previousWasSentenceEnd = true
    reader.map{s => {
        val w = Word(s, previousWasSentenceEnd, s.trim.matches(".*[.?!]$"), file.getName)
        previousWasSentenceEnd = w.isSentenceEnd
        w
      }
    }
  }
}

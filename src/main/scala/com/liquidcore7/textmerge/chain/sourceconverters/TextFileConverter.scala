package com.liquidcore7.textmerge.chain.sourceconverters

import java.io.File
import java.util.Scanner

import com.liquidcore7.textmerge.chain.Word

class TextFileConverter(file: File) extends ToIterableConvertible {
  val scanner = new Scanner(file)
  override def toIterable: Iterable[Word] = new Iterable[Word] {
    override def iterator: Iterator[Word] = new Iterator[Word] {
      var sentenceEnded = true
      override def hasNext: Boolean = scanner.hasNext
      override def next(): Word = {
        val nextStr = scanner.next
        val nextWord = Word(nextStr, sentenceEnded, nextStr.matches(".*[.?!]$"), file.getName)
        sentenceEnded = nextWord.isSentenceEnd
        nextWord
      }
    }
  }
}

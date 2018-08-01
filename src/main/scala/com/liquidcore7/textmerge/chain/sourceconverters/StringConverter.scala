package com.liquidcore7.textmerge.chain.sourceconverters
import com.liquidcore7.textmerge.chain.Word

class StringConverter(text: String, source: String) extends ToIterableConvertible {
  override def toIterable: Iterable[Word] = new Iterable[Word] {

    private val words = text.split(" ")

    override def iterator: Iterator[Word] = new Iterator[Word] {

      private val stringIterator = words.iterator
      private var previousIsSentenceEnd = true

      override def hasNext: Boolean = stringIterator.hasNext
      override def next(): Word = {
        val nextStr = stringIterator.next
        val word = Word(nextStr, previousIsSentenceEnd, nextStr.matches(".*[.?!]$"), source)
        previousIsSentenceEnd = word.isSentenceEnd
        word
      }

    }
  }
}

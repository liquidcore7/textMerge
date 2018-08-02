package com.liquidcore7.textmerge.chain.seqgenerators

import com.liquidcore7.textmerge.chain.Word

import scala.util.Random

class RandomTextGenerator(randomness: Int) extends SequenceGenerator(randomness + 1) {
  override def pickVariant(from: IndexedSeq[Word], ignoringPrevious: List[Word]): Option[Word] = {
    cleanPrevious()
    if (from.isEmpty) None else Some( from(Random.nextInt(from.length)) )
  }
}

class RandomSentenceGenerator(randomness: Int) extends RandomTextGenerator(randomness) {
  override def pickVariant(from: IndexedSeq[Word], prev: List[Word]): Option[Word] = super.pickVariant(from, prev) match {
    case Some(word) => if (prev.last.isSentenceEnd) None else Some(word)
    case None => None
  }
}

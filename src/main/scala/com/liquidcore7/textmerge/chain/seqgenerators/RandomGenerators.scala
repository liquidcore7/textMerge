package com.liquidcore7.textmerge.chain.seqgenerators

import com.liquidcore7.textmerge.chain.{ChainStore, Word}

import scala.util.Random

class RandomTextGenerator(chainStore: ChainStore) extends SequenceGenerator(chainStore) {
  override val limit: Int = 3
  override def pickVariant(from: IndexedSeq[Word], ignoringPrevious: List[Word]): Option[Word] = {
    cleanPrevious()
    if (from.isEmpty) None else Some( from(Random.nextInt(from.length)) )
  }
}

class RandomSentenceGenerator(chainStore: ChainStore) extends RandomTextGenerator(chainStore) {
  override def pickVariant(from: IndexedSeq[Word], prev: List[Word]): Option[Word] = super.pickVariant(from, prev) match {
    case Some(word) => if (prev.last.isSentenceEnd) None else Some(word)
    case None => None
  }
}

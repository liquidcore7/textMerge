package com.liquidcore7.textmerge.chain.seqgenerators

import com.liquidcore7.textmerge.chain.{ChainStore, Word}

abstract class SequenceGenerator(val limit: Int) extends Iterator[Word] {
  // overridable stuff
  protected val chainStore: ChainStore = ChainStore()
  private val init: Word = chainStore.getRandomSentenceBegin()
  def pickVariant(from: IndexedSeq[Word], accordingToPrevious: List[Word]): Option[Word]

  private var previous: List[Word] = List()
  private def current: Word = previous.last
  private var nextPick: Option[Word] = Some(init)

  protected def removeOldest(): Unit = {previous = previous.tail}
  protected def cleanPrevious(): Unit = {previous = List(previous.last)}

  override def hasNext: Boolean = nextPick.isDefined
  override def next(): Word = {
    previous = previous :+ nextPick.get
    nextPick = pickVariant(chainStore.possibleAfter(current, limit), previous)
    current
  }

  override def toString(): String = this.map{_.word}.mkString(" ")
}
package com.liquidcore7.textmerge.chain.seqgenerators

import com.liquidcore7.textmerge.chain.{ChainStore, Word}

class MarkovSentenceGenerator(chainStore: ChainStore) extends SequenceGenerator(chainStore) {
  val toChangeSource: Int = 2
  override val limit: Int = 15

  override def pickVariant(from: IndexedSeq[Word], accordingToPrevious: List[Word]): Option[Word] = {
    if (from.isEmpty || accordingToPrevious.last.isSentenceEnd)
      return None

    val prevSource = accordingToPrevious.last.source
    if (accordingToPrevious.length >= toChangeSource) {
      if (chainStore.hasAlternativeSources(accordingToPrevious)) {
        cleanPrevious()
        return from.filterNot{_.source equals prevSource}.headOption
      } else removeOldest()
    }
    from.find(_.source equals prevSource)
  }
}

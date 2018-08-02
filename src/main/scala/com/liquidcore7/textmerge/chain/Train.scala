package com.liquidcore7.textmerge.chain

import java.io.File
import com.liquidcore7.textmerge.chain.sourceconverters.{TextFileConverter, ToSequenceConverter}

object Train {
  private def addSource(source: ToSequenceConverter, store: ChainStore): Unit = {
    store.populateKey()
    source.toIterable.sliding(2).foreach{wPair => store.insertWord(wPair.last, wPair.head)}
  }

  def apply[I <: ToSequenceConverter](withConverters: Iterable[I]): Unit = {
    withConverters.par.foreach{addSource(_, ChainStore())}
  }

  def apply[typeErasureWorkaround: ClassManifest](fromTextFiles: Iterable[File]): Unit =
                                                              Train.apply(fromTextFiles.map{new TextFileConverter(_)})
}

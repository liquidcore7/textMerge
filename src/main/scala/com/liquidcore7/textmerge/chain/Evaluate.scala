package com.liquidcore7.textmerge.chain

import com.liquidcore7.textmerge.chain.seqgenerators._

object Evaluate {
  def getGeneratorOfType[T <: SequenceGenerator](implicit classManifest: ClassManifest[T]): SequenceGenerator =
    classManifest.runtimeClass.getConstructor(ChainStore().getClass).newInstance(ChainStore()).asInstanceOf[SequenceGenerator]


  def fullTextStream: Stream[String] = getGeneratorOfType[RandomTextGenerator].toStream.map{_.word}
  def sentence: String = getGeneratorOfType[RandomSentenceGenerator].toString()
  def markovSentence: String = getGeneratorOfType[MarkovSentenceGenerator].toString()
}
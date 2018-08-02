package com.liquidcore7.textmerge.api

import java.io.File

import com.liquidcore7.textmerge.chain.seqgenerators.{MarkovSentenceGenerator, RandomSentenceGenerator, SequenceGenerator}
import com.liquidcore7.textmerge.chain.sourceconverters.{PDFConverter, Text2NGramsConverter, TextFileConverter, ToIterableConvertible}

class Settings(private var converterMode: String = "text", private var generatorMode: String = "random", var wordPickLimit: Int = 5) {
  private var converter: File => ToIterableConvertible = Settings.trainMapping(converterMode)
  private var generator: Int => SequenceGenerator = Settings.evalMapping(generatorMode)

  def changeConverterMode(newMode: String): Unit = {
    converterMode = newMode
    converter = Settings.trainMapping(converterMode)
  }
  def changeGeneratorMode(newMode: String): Unit = {
    generatorMode = newMode
    generator = Settings.evalMapping(generatorMode)
  }

  def getConverter(forFile: File): ToIterableConvertible = converter(forFile)
  def getGenerator(withSetting: Int = wordPickLimit): SequenceGenerator = generator(withSetting)
}

object Settings {
  type ProducerByName[From, To] = Map[String, From => To]

  def mappingFor[From, To](initializers: Seq[String], classLoaders: Seq[Class[To]]): ProducerByName[From, To] = {
    assert(initializers.length == classLoaders.length, "Unable to create mapping: number of initializers and classloaders doesnt match!")
    initializers.zip(classLoaders)
      .map{case (i, cl) => i -> { cl.getDeclaredConstructor(Class[From]).newInstance(_) }}
      .toMap
  }

  val trainMapping: ProducerByName[File, ToIterableConvertible] = mappingFor(
    Seq("text", "pdf", "ngrams"),
    Seq(Class[TextFileConverter], Class[PDFConverter], Class[Text2NGramsConverter])
  )

  val evalMapping: ProducerByName[Int, SequenceGenerator] = mappingFor(
    Seq("random", "markov"),
    Seq(Class[RandomSentenceGenerator], Class[MarkovSentenceGenerator])
  )
}
package com.liquidcore7.textmerge.chain

import scala.reflect._

import com.liquidcore7.textmerge.Settings
import org.neo4j.driver.v1._

import scala.collection.JavaConverters._

class ChainStore(private val session: Session) extends WithStats[(Int, Int)] {

  override val statUnit: Stats[(Int, Int)] = new InsertionProgress

  object ResultToCC {
    def apply[CC](record: Record)(implicit classTag: ClassTag[CC]): CC = {
      val cc = classTag.runtimeClass
      cc.getDeclaredConstructor(cc.getDeclaredFields.map{_.getType}:_*)
                  .newInstance(record.values.asScala.map{_.asObject}:_*).asInstanceOf[CC]
    }
  }

  def populateKey(): Unit = session.run(NeoQueries.populateKey)

  def insertWord(word: Word, withParent: Word): Unit = {
    statUnit.transform{case (a, b) => (a, b + 1)}
    session.runAsync(NeoQueries.createAsChildOf(withParent, word))
      .whenComplete{(_, _) => statUnit.transform{case (a, b) => (a + 1, b)}}
  }

  def possibleAfter(word: Word, limit: Int = 1): IndexedSeq[Word] = session.run(NeoQueries.bestMatchesFor(word, limit))
                                                      .list().asScala.map{ResultToCC[Word](_)}.toIndexedSeq

  def getRandomSentenceBegin(fromSource: Option[String] = None): Word = ResultToCC[Word](session.run(NeoQueries.randomSentenceBegin(fromSource)).next)

  def clearSource(source: String): Unit = session.runAsync(NeoQueries.removeWithSource(source))
  def hasAlternativeSources(path: Iterable[Word]): Boolean = session.run(NeoQueries.hasAlternativeSources(path))
                                                                      .next.get("hasAltSrc").asBoolean()
}

object ChainStore {
  private val driver: Driver = GraphDatabase.driver(Settings.neo4j_bolturl,
    AuthTokens.basic(Settings.neo4j_username, Settings.neo4j_pass))
  private val session: Session = driver.session
  private val chainStore: ChainStore = new ChainStore(session)

  def apply(): ChainStore = chainStore
  def close(): Unit = {session.close(); driver.close()}
}
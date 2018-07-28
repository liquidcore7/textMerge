package com.liquidcore7.textmerge.chain

class Word(val word: String, val isSentenceBegin: java.lang.Boolean, val isSentenceEnd: java.lang.Boolean, val source: String) {
  override def toString: String = "word: \"" + word + "\", source: \"" + source + "\""
}

object Word {
  def apply(word: String, isSentenceBegin: Boolean, isSentenceEnd: Boolean, source: String) = new Word(word, isSentenceBegin, isSentenceEnd, source)
}

object NeoQueries {

  def populateKey: String = "create index on :Words(word, source)"

  def createAsChildOf(parent: Word, child: Word): String =
    s"""merge (parent: Words {$parent})
       |on create set parent.isSentenceBegin = ${parent.isSentenceBegin}, parent.isSentenceEnd = ${parent.isSentenceEnd}
       |merge (child: Words {$child})
       |on create set child.isSentenceBegin = ${child.isSentenceBegin}, child.isSentenceEnd = ${child.isSentenceEnd}
       |merge (parent)-[w: Weight]->(child)
       |on create set w.weight = 1
       |on match set w.weight = w.weight + 1
     """.stripMargin

  def randomSentenceBegin(fromSource: Option[String]): String =
    s"""match (n: Words {isSentenceBegin: true})
       |${fromSource.map("where n.source=\"" + _ + "\"").getOrElse("")}
       |with n,
       |rand() % count(n) as r
       |return n.word as word,
       |n.isSentenceBegin as isSentenceBegin,
       |n.isSentenceEnd as isSentenceEnd,
       |n.source as source
       |order by r limit 1
     """.stripMargin

  def bestMatchesFor(parent: Word, limit: Int): String =
    s"""match (:Words {${parent.word}})-[w: Weight]->(child: Words)
       |return child.word as word,
       |child.isSentenceBegin as isSentenceBegin,
       |child.isSentenceEnd as isSentenceEnd,
       |child.source as source
       |order by w.weight desc limit $limit
     """.stripMargin

  def removeWithSource(source: String): String =
    s"""match (:Words)-[m]-(k: Words {source: "$source"})
       |delete k
       |delete m
     """.stripMargin


  def hasAlternativeSources(origSourcePath: Iterable[Word]): String =
    s"""return size(filter(path in ${origSourcePath.map{_.word}.mkString("(:Words {", "})-->(:Words {", "})")}
              |where head(nodes(path)).source <> "${origSourcePath.head.source}")) > 0 as hasAltSrc
     """.stripMargin

}
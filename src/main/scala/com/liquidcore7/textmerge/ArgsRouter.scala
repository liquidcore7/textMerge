package com.liquidcore7.textmerge

import java.io.File
import java.util.Objects

import com.liquidcore7.textmerge.chain.{ChainStore, Evaluate, Train}

object ArgsRouter {

  val usage: String =
    s"""Train: textmerge train "path/to/file1.txt" "path/to/file_n.txt"
       |Cleanup: textmerge cleanup [branch1] [branch_n]
       |Evaluate:
       |  One string(random next): textmerge evalstr
       |  One string(Markov chain): textmerge evalstrm
       |  Full text: textmerge evaltxt
     """.stripMargin

  def main(args: Array[String]): Unit = {
    if (args.isEmpty) println(usage)
    else {args(0) match {
      case "train" => Train(args.tail.map{new File(_)}.filterNot{Objects.isNull}.toIterable); println("training started")
      case "cleanup" => args.tail.foreach{ChainStore().clearSource(_)}; println("Cleaned")
      case "evalstr" => println(Evaluate.sentence)
      case "evalstrm" => println(Evaluate.markovSentence)
      case "evaltxt" => Evaluate.fullTextStream.take(200).foreach{s => print(s + " ")}; println()
      case _ => println("Unrecognized option")
    }
      ChainStore.close()
      Thread.currentThread().interrupt()
    }
  }
}

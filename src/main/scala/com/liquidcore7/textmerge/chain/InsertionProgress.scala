package com.liquidcore7.textmerge.chain

trait Stats[T] {
  var data: T
  def update(newValues: T): Unit = {data = newValues}
  def transform(action: T => T): Unit = {data = action(data)}
  def get: T = data
}

trait WithStats[T] {
  val statUnit: Stats[T]
  def getStats: Stats[T] = statUnit
}

class InsertionProgress extends Stats[(Int, Int)] {
  override var data: (Int, Int) = (0, 0)
  override def toString: String = data._1 + "/" + data._2
}

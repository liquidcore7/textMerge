package com.liquidcore7.textmerge.chain.sourceconverters

import com.liquidcore7.textmerge.chain.Word

trait ToIterableConvertible {
  def toIterable: Iterable[Word]
}
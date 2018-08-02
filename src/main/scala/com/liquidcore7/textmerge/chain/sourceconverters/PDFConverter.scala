package com.liquidcore7.textmerge.chain.sourceconverters

import java.io.File

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import com.liquidcore7.textmerge.chain.Word

class PDFConverter(private val file: File) extends ToIterableConvertible {
  private val source = new PdfReader(file.toString)

  override def toIterable: Iterable[Word] = (0 until source.getNumberOfPages)
      .map{PdfTextExtractor.getTextFromPage(source, _)}
      .map{new StringConverter(_, file.getName)}
      .flatMap{_.toIterable}
}
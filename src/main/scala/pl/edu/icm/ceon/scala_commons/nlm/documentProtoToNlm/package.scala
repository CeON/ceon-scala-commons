/*
 * Copyright (c) 2013-2013 ICM UW
 */

package pl.edu.icm.ceon.scala_commons.nlm

import pl.edu.icm.coansys.models.DocumentProtos.{DocumentWrapper, BasicMetadata}
import scala.collection.JavaConversions._

/**
 * @author Michal Oniszczuk (m.oniszczuk@icm.edu.pl)
 *         Created: 23.09.2013 11:54
 */
package object documentProtoToNlm {
  /**
   * A converter from DocumentWrapper to the JATS format (Journal Article Tag Suite, previously named NLM, National Library of Medicine format). Actually, a stripped JATS format, omitting some DocumentWrapper fields.
   *
   * To get the official description of the JATS format, please go to the website of the JATS working group http://jats.nlm.nih.gov/files.html
   */
  def documentWrapperToNLM(wrapper: DocumentWrapper): String = {
    val documentMetadata = wrapper.getDocumentMetadata
    val basicMetadata = documentMetadata.getBasicMetadata
    val pages = getFirstLastPage(basicMetadata)
    val titles = basicMetadata.getTitleList.filter(textWithLanguage => textWithLanguage.getText.nonEmpty)
    val authors = basicMetadata.getAuthorList

    <article xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:mml="http://www.w3.org/1998/Math/MathML" article-type="research-article">
      <front>
        {if (basicMetadata.getJournal.nonEmpty)
        <journal-meta>
          <journal-title-group>
            <journal-title>
              {wrapper.getDocumentMetadata.getBasicMetadata.getJournal}
            </journal-title>
          </journal-title-group>
        </journal-meta>}<article-meta>
        <article-id pub-id-type="bw2-proto-row-id">
          {wrapper.getRowId}
        </article-id>{/*
             * JATS format assumes the main title (<article-title>) to be in a language of the article body. Here, we assume that:
             * the first title is the main title (in the language of the article body)
             * the rest of titles are translations
             */
        if (titles.length > 0)
          <title-group>
            <article-title>
              {titles.head.getText}
            </article-title>{titles.tail.map(textWithLanguage =>
            if (textWithLanguage.getLanguage.nonEmpty)
              <trans-title-group xml:lang={textWithLanguage.getLanguage}>
                <trans-title>
                  {textWithLanguage.getText}
                </trans-title>
              </trans-title-group>
            else
              <trans-title-group>
                <trans-title>
                  {textWithLanguage.getText}
                </trans-title>
              </trans-title-group>
          )}
          </title-group>}{if (authors.length > 0)
          <contrib-group>
            {authors.map(
            author =>
              <contrib>
                {if (author.getName.nonEmpty)
                <string-name>
                  {author.getName}
                </string-name>}{if (author.getForenames.nonEmpty || author.getSurname.nonEmpty)
                <name>
                  {if (author.getSurname.nonEmpty)
                  <surname>
                    {author.getSurname}
                  </surname>}{if (author.getForenames.nonEmpty && author.getSurname.nonEmpty)
                  ", "}{if (author.getForenames.nonEmpty)
                  <given-names>
                    {author.getForenames}
                  </given-names>}
                </name>}
              </contrib>
          )}
          </contrib-group>}{if (basicMetadata.getYear.nonEmpty)
          <pub-date>
            <year>
              {basicMetadata.getYear}
            </year>
          </pub-date>}{if (basicMetadata.getYear.nonEmpty)
          <volume>
            {basicMetadata.getVolume}
          </volume>}{if (basicMetadata.getIssue.nonEmpty)
          <issue>
            {basicMetadata.getIssue}
          </issue>}{pages.first match {
          case Some(page) =>
            <fpage>
              {page}
            </fpage>
          case None =>
        }}{pages.last match {
          case Some(page) =>
            <lpage>
              {page}
            </lpage>
          case None =>
        }}
      </article-meta>
      </front>
    </article>
      .toString()
  }

  private case class Pages(first: Option[String], last: Option[String])

  private def getFirstLastPage(metadata: BasicMetadata): Pages = {
    if (metadata.getPages.nonEmpty) {
      val pages = metadata.getPages
      val split = pages.split("-")
      if (split.length == 2)
        Pages(Some(split(0)), Some(split(1)))
      else
        Pages(Some(pages), None)

    } else {
      Pages(None, None)
    }
  }
}

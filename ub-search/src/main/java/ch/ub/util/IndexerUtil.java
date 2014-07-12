package ch.ub.util;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.util.Version;

import ch.ub.indexer.ContentRecord;
import ch.ub.indexer.IndexHolder;
import ch.ub.parser.HTMLParser;

public class IndexerUtil {

	private final static Logger LOGGER = Logger.getLogger(IndexerUtil.class.getName()); 

	private static HTMLParser htmlParser = new HTMLParser();
	public static void indexContent(IndexHolder indexHolder,String html, String url)
	{
		ContentRecord cr = htmlParser.parse( html, url);
		addDocument(indexHolder, cr);
	}
	
	private static void addDocument(IndexHolder indexHolder, ContentRecord contentRecord)
	{
		//List<Document> documentsToAdd = new ArrayList<Document>();
		Document doc = new Document();
		String title = contentRecord.getTitle();
		Field titleField = new Field("title", title, Field.Store.YES, Field.Index.ANALYZED);
		titleField.setBoost(1.1f);
		doc.add(titleField);  // adding title field
		String content = contentRecord.getContent();
		doc.add(new Field("content", content, Field.Store.YES, Field.Index.ANALYZED)); // adding content field
		String keywords = contentRecord.getMetaKeywords();
		Field keywordsField = new Field("keywords", keywords, Field.Store.YES, Field.Index.ANALYZED);
		keywordsField.setBoost(1.5f);
		doc.add(keywordsField); // adding content field
		String description = contentRecord.getMetaDescription();
		Field descriptionField = new Field("description", description, Field.Store.YES, Field.Index.ANALYZED);
		descriptionField.setBoost(1.2f);
		doc.add(descriptionField); // adding content field
		String url = contentRecord.getUrl();
		doc.add(new Field("url", url, Field.Store.YES, Index.NOT_ANALYZED)); // adding content field
		try {
			indexHolder.getWriter().addDocument(doc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public static void openIndexWriter(IndexHolder indexHolder)
	{
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_45, indexHolder.getAnalyzer());
		try {
			indexHolder.setWriter( new IndexWriter(indexHolder.getDirectory(), conf));
		} catch (IOException e) {
			LOGGER.error("couldnt create indexwriter", e);
		}
	}
	
	public static void closeIndexWriter(IndexHolder indexHolder)
	{
		try {
			indexHolder.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



}

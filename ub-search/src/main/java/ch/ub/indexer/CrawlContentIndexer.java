package ch.ub.indexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.management.StandardEmitterMBean;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import ch.ub.parser.HTMLParser;

public class CrawlContentIndexer {

	private final static Logger LOGGER = Logger.getLogger(CrawlContentIndexer.class.getName()); 

	Analyzer analyzer ; 
	Directory directory ;
	IndexWriter writer;
	IndexWriterConfig conf; // = new IndexWriterConfig(analyzer);
	HTMLParser htmlParser;
	
	static CrawlContentIndexer cci = null; 
	
	public static CrawlContentIndexer getInstance()
	{
		if (cci==null)
		{
			cci= new CrawlContentIndexer();
		}
		return cci;
		
	}
	public CrawlContentIndexer() {
		super();
		analyzer = new StandardAnalyzer(Version.LUCENE_45); // StandardEmitterMBean(arg0, arg1, arg2) SnowballAnalyzer(Version.LUCENE_45, "German");
		directory = new RAMDirectory();
		conf = new IndexWriterConfig(Version.LUCENE_45, analyzer);
		htmlParser = new HTMLParser();
		try {
			writer = new IndexWriter(directory, conf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setIsDoneWithIndexing()
	{
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void indexContent(String html, String url)
	{
		ContentRecord cr = htmlParser.parse( html, url);
//		ContentRecord cr = new ContentRecord();
//		cr.setTitle("");
//		cr.setContent(content);
//		cr.setUrl(url);
		addDocument(cr);
	}
	
	private void addDocument(ContentRecord contentRecord)
	{
		//List<Document> documentsToAdd = new ArrayList<Document>();
		Document doc = new Document();
		String title = contentRecord.getTitle();
		doc.add(new Field("title", title, Field.Store.YES, Field.Index.ANALYZED));  // adding title field
		String content = contentRecord.getContent();
		doc.add(new Field("content", content, Field.Store.YES, Field.Index.ANALYZED)); // adding content field
		String keywords = contentRecord.getMetaKeywords();
		doc.add(new Field("keywords", content, Field.Store.YES, Field.Index.ANALYZED)); // adding content field
		String description = contentRecord.getMetaDescription();
		doc.add(new Field("description", content, Field.Store.YES, Field.Index.ANALYZED)); // adding content field
		String url = contentRecord.getUrl();
		doc.add(new Field("url", content, Field.Store.YES, Field.Index.ANALYZED)); // adding content field
		try {
			writer.addDocument(doc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	int hitsPerPage = 100;
	public void search(String querystr) throws IOException, ParseException
	{
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		
		//String querystr = args.length > 0 ? args[0] : "lucene";

	    // the "title" arg specifies the default field to use
	    // when no field is explicitly specified in the query.
	    Query q = new QueryParser(Version.LUCENE_45, "content", analyzer).parse(querystr);

	    
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
	    searcher.search(q, collector);
	    ScoreDoc[] hits = collector.topDocs().scoreDocs;
	   
	    // 4. display results
	    System.out.println("Found " + hits.length + " hits.");
	    LOGGER.debug("results for " + querystr);
	    
	    
	    for(int i=0;i<hits.length;++i) {
	      int docId = hits[i].doc;
	      Document d = searcher.doc(docId);
	      System.out.println((i + 1) + ". " + d.get("url") + "\t" + d.get("title"));
	    }

	    // reader can only be closed when there
	    // is no need to access the documents any more.
	    reader.close();	    
	}
}

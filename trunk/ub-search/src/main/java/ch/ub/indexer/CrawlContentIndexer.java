package ch.ub.indexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
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

	public static CrawlContentIndexer reload()
	{

		cci = null; 
		return getInstance();
		
	}
	
	public CrawlContentIndexer() {
		super();
		InputStream stopwordsfileIS = getClass().getResourceAsStream("/german-stopwords.txt");
		LOGGER.debug("stopwordsfileIS=" + stopwordsfileIS);
		StopWords stopwords = new StopWords();
		//stopwordsfileIS.
		BufferedReader br = new BufferedReader(new InputStreamReader(stopwordsfileIS));
		try {
			stopwords.read(br);
			LOGGER.debug("stopwords size = " + stopwords.m_Words.size());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			LOGGER.debug("error");
		}
		CharArraySet cas = new CharArraySet(Version.LUCENE_45, stopwords.m_Words, true);
		analyzer = new StandardAnalyzer(Version.LUCENE_45, cas); // StandardEmitterMBean(arg0, arg1, arg2) SnowballAnalyzer(Version.LUCENE_45, "German");
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
		addDocument(cr);
	}
	
	private void addDocument(ContentRecord contentRecord)
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
		keywordsField.setBoost(1.4f);
		doc.add(keywordsField); // adding content field
		String description = contentRecord.getMetaDescription();
		Field descriptionField = new Field("description", description, Field.Store.YES, Field.Index.ANALYZED);
		descriptionField.setBoost(1.2f);
		doc.add(descriptionField); // adding content field
		String url = contentRecord.getUrl();
		doc.add(new Field("url", url, Field.Store.YES, Index.NOT_ANALYZED)); // adding content field
		try {
			writer.addDocument(doc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public ContentRecord getContentRecordFromLuceneDoc(Document doc, float score)
	{
		ContentRecord cr = new ContentRecord();
		cr.setTitle(doc.get(SearchIndexFields.TITLE.fieldName));
		cr.setUrl(doc.get(SearchIndexFields.URL.fieldName));
		cr.setMetaDescription(doc.get(SearchIndexFields.METADESCRIPTION.fieldName));
		cr.setMetaKeywords(doc.get(SearchIndexFields.METAKEYWORDS.fieldName));
		cr.setScore(score);
		
		return cr;
	}
	int hitsPerPage = 100;
	public List<ContentRecord> search(String querystr) throws IOException, ParseException
	{
		List<ContentRecord> resultsList = new ArrayList<ContentRecord>();
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
	      //System.out.println((i + 1) + ". " + d.get("url") + "\t" + d.get("title"));
	      ContentRecord cr = getContentRecordFromLuceneDoc(d, hits[i].score);
	      resultsList.add(cr);
	    }

	    // reader can only be closed when there
	    // is no need to access the documents any more.
	    reader.close();	    
	    return resultsList;
	}

	public Integer findUrlInIndex(String url) throws IOException
	{
		
		TermQuery tq= new TermQuery(new Term("url", url));
		// BooleanClauses Enum SHOULD says Use this operator for clauses that should appear in the matching documents.
		BooleanQuery bq = new BooleanQuery();
		bq.add(tq,Occur.SHOULD);
		
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		
		TopScoreDocCollector collector = TopScoreDocCollector.create(10, true);
		searcher.search(bq, collector);
		
		 ScoreDoc[] hits = collector.topDocs().scoreDocs;
		   
		    // 4. display results
		    System.out.println("Found " + hits.length + " hits. for " + url);
		    
		    if (hits.length>0)
		    {
		    	int docId = hits[0].doc;
		    	return docId; 
		    }
		    
		    return null;
		    

	}
	
	
	public List<ContentRecord> likeThis(String url, int maxReturnedResults) throws IOException, ParseException
	{
		List<ContentRecord> resultsList = new ArrayList<ContentRecord>();
		Integer thisdoc = null;
		try {
		 thisdoc = findUrlInIndex(url);
		 LOGGER.debug("found doc in index for url with id : " + thisdoc);
		}
		catch (IOException e)
		{
			LOGGER.debug("couldnt find doc in index with url = " + url);
		}
		if (thisdoc!=null)
		{
			IndexReader reader = DirectoryReader.open(directory);
			 IndexSearcher is = new IndexSearcher(reader);
			MoreLikeThis mlt = new MoreLikeThis(reader);
			mlt.setAnalyzer(analyzer);
			mlt.setFieldNames(new String[]{SearchIndexFields.CONTENT.fieldName, SearchIndexFields.METADESCRIPTION.fieldName, SearchIndexFields.METAKEYWORDS.fieldName});
			Query likeQuery = mlt.like(thisdoc);
			
			 TopDocs topDocs = is.search(likeQuery,(maxReturnedResults+1));
			 LOGGER.debug("topDocs hits" + topDocs.totalHits);
			 
			 for ( ScoreDoc scoreDoc : topDocs.scoreDocs ) {
			        //This retrieves the actual Document from the index using
			        //the document number. (scoreDoc.doc is an int that is the
			        //doc's id
			        Document doc = is.doc( scoreDoc.doc );
			        
			        ContentRecord cr = getContentRecordFromLuceneDoc(doc, scoreDoc.score);
			        if (!cr.getUrl().equals(url))
			        {
			        resultsList.add(cr);
			        }
			        //Get the id that we previously stored in the document from
			        //hibernate and parse it back to a long.
			        //String similarUrl =  doc.get("url");
			        //LOGGER.debug("similar URL = " + similarUrl);
			    }			 
			    reader.close();	    
		}
		

		return resultsList; 
	}
	
}

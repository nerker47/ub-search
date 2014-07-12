package ch.ub.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;

import ch.ub.indexer.ContentRecord;
import ch.ub.indexer.SearchIndexFields;

public class IndexResultsRetrieverUtil {
	
	
	private final static Logger LOGGER = Logger.getLogger(IndexResultsRetrieverUtil.class.getName()); 
	

	public static Integer findUrlInIndex(Directory directory, String url) throws IOException
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

	public static List<ContentRecord> likeThis(Directory directory, Analyzer analyzer, String url, int maxReturnedResults) throws IOException, ParseException
	{
		List<ContentRecord> resultsList = new ArrayList<ContentRecord>();
		Integer thisdoc = null;
		try {
		 thisdoc = findUrlInIndex(directory, url);
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
	
	public static ContentRecord getContentRecordFromLuceneDoc(Document doc, float score)
	{
		ContentRecord cr = new ContentRecord();
		cr.setTitle(doc.get(SearchIndexFields.TITLE.fieldName));
		cr.setUrl(doc.get(SearchIndexFields.URL.fieldName));
		cr.setMetaDescription(doc.get(SearchIndexFields.METADESCRIPTION.fieldName));
		cr.setMetaKeywords(doc.get(SearchIndexFields.METAKEYWORDS.fieldName));
		cr.setScore(score);
		
		return cr;
	}
	
	public static List<ContentRecord> search(Directory directory, Analyzer analyzer,String querystr, int hitsPerPage) throws IOException, ParseException
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
	      ContentRecord cr = IndexResultsRetrieverUtil.getContentRecordFromLuceneDoc(d, hits[i].score);
	      resultsList.add(cr);
	    }

	    // reader can only be closed when there
	    // is no need to access the documents any more.
	    reader.close();	    
	    return resultsList;
	}

}

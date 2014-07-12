package ch.ub.indexer;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import ch.ub.indexer.stopwords.StopWords;
import ch.ub.indexer.stopwords.StopWordsLoader;

public class IndexHolder {

	final Analyzer analyzer ; 
	final Directory directory ;
	private IndexWriter writer;

	
	public IndexHolder() {
		StopWordsLoader swlu = new StopWordsLoader(); 
		StopWords stopwords = swlu.loadStopWords();
		CharArraySet cas = new CharArraySet(Version.LUCENE_45, stopwords.getM_Words(), true);
		analyzer = new StandardAnalyzer(Version.LUCENE_45, cas); // StandardEmitterMBean(arg0, arg1, arg2) SnowballAnalyzer(Version.LUCENE_45, "German");
		directory = new RAMDirectory();
	}

	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public Directory getDirectory() {
		return directory;
	}
	
	
	public void setWriter(IndexWriter writer) {
		this.writer = writer;
	}

	public IndexWriter getWriter() {
		return writer;
	}


	

}

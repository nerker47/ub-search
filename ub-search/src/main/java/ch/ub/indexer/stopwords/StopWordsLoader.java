package ch.ub.indexer.stopwords;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

public class StopWordsLoader {

	private final static Logger LOGGER = Logger.getLogger(StopWordsLoader.class.getName()); 

	public  StopWords loadStopWords()
	{
		InputStream stopwordsfileIS = getClass().getResourceAsStream("/german-stopwords.txt");
		LOGGER.debug("stopwordsfileIS=" + stopwordsfileIS);
		StopWords stopwords = new StopWords();
		//stopwordsfileIS.
		BufferedReader br = new BufferedReader(new InputStreamReader(stopwordsfileIS));
		try {
			stopwords.read(br);
			LOGGER.debug("stopwords size = " + stopwords.getM_Words().size());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			LOGGER.debug("error");
		}
		return stopwords;
		
	}

}

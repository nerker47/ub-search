package ch.ub.loader;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;

import ch.ub.indexer.stopwords.StopWords;
import ch.ub.indexer.stopwords.StopWordsLoader;

public class StopWordsLoaderTest {

	@Test
	public void testLoadStopWords() {
		StopWordsLoader swl = new StopWordsLoader();
		StopWords sw = swl.loadStopWords();
		Assert.assertNotNull(sw);
		Assert.assertTrue("stopwords are empty", sw.getM_Words().size()>0);

	}

}

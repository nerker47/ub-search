package ch.ub.crawler;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import ch.ub.indexer.ContentRecord;
import ch.ub.indexer.CrawlContentIndexer;
import ch.ub.parser.HTMLParser;

public class BasicCrawlControllerTest {

	private final static Logger LOGGER = Logger.getLogger(BasicCrawlControllerTest.class.getName()); 

	@Test
	public void testStartCrawler() {
		SitemapParser smParser = new SitemapParser();
		String searchTerm = "nsa";
		BasicCrawlController crawlController = new BasicCrawlController();
		try {
			List<String> urlList = smParser.getSitemap();
			crawlController.startCrawler(urlList);
			List<ContentRecord> resultsList  = CrawlContentIndexer.getInstance().search(searchTerm);
			
			LOGGER.debug("finding similar pages for the resultpages of search term '" + searchTerm + "' " + " results for nsa = " + resultsList.size());
			
			for (ContentRecord cr : resultsList)
			{
				LOGGER.debug("find similar pages for:" + cr.getUrl());
				List<ContentRecord> similarResultsList = CrawlContentIndexer.getInstance().likeThis(cr.getUrl());
				for (ContentRecord scr : similarResultsList)
				{
					LOGGER.debug("similar: " + scr.getUrl() + " / " + scr.getTitle());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

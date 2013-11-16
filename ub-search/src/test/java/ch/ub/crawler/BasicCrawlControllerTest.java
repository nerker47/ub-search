package ch.ub.crawler;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import ch.ub.indexer.ContentRecord;
import ch.ub.indexer.CrawlContentIndexer;
import ch.ub.parser.HTMLParser;
import ch.ub.service.TestServer;

public class BasicCrawlControllerTest {

	private final static Logger LOGGER = Logger.getLogger(BasicCrawlControllerTest.class.getName()); 

	@Test
	public void testStartCrawler() {
		SitemapParser smParser = new SitemapParser();
		String searchTerm = "nsa";
		BasicCrawlController crawlController = new BasicCrawlController();
		try {
			List<String> urlList = smParser.getSitemap();
			// limit for debug
//			urlList = urlList.subList(0, 20);
			crawlController.startCrawler(urlList);
			List<ContentRecord> resultsList  = CrawlContentIndexer.getInstance().search(searchTerm);
			
			LOGGER.debug("finding similar pages for the resultpages of search term '" + searchTerm + "' " + " results for " + searchTerm + " = " + resultsList.size());
			
			for (ContentRecord cr : resultsList)
			{
				LOGGER.debug("find similar pages for:" + cr.getUrl());
				List<ContentRecord> similarResultsList = CrawlContentIndexer.getInstance().likeThis(cr.getUrl());
				for (ContentRecord scr : similarResultsList)
				{
					LOGGER.debug("similar: " + scr.getUrl() + " / " + scr.getTitle());
				}
			}
			
			TestServer testServer = new TestServer();
			testServer.startServer();
			LOGGER.debug("test server started");
			while (true)
			{
				Thread.sleep(10000);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

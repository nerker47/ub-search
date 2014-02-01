package ch.ub.crawler;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import ch.ub.config.Config;
import ch.ub.indexer.ContentRecord;
import ch.ub.indexer.CrawlContentIndexer;
import ch.ub.parser.HTMLParser;

public class BasicCrawlControllerTest {

	private final static Logger LOGGER = Logger.getLogger(BasicCrawlControllerTest.class.getName()); 

	@Test
	public void testStartCrawler() {
		CrawlContentIndexer cci = new CrawlContentIndexer();
		Config config = null;
		try {
			config = new Config("testconfig.properties");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String sitempaurl = config.get(Config.CONFIG_PARAM_SITEMAPURL);
		String crawlertmpdir = config.get(Config.CONFIG_PARAM_CRAWLER_TMP_DIR);
		SitemapParser smParser = new SitemapParser(sitempaurl);
		String searchTerm = "nsa";
		BasicCrawlController crawlController = new BasicCrawlController(crawlertmpdir);
		try {
			List<String> urlList = smParser.getSitemap();
			// limit for debug
//			urlList = urlList.subList(0, 20);
			crawlController.startCrawler(urlList);
			List<ContentRecord> resultsList  = cci.search(searchTerm);
			
			LOGGER.debug("finding similar pages for the resultpages of search term '" + searchTerm + "' " + " results for " + searchTerm + " = " + resultsList.size());
			
			for (ContentRecord cr : resultsList)
			{
				LOGGER.debug("find similar pages for:" + cr.getUrl());
				List<ContentRecord> similarResultsList = cci.likeThis(cr.getUrl(),1000);
				for (ContentRecord scr : similarResultsList)
				{
					LOGGER.debug("similar: " + scr.getUrl() + " / " + scr.getTitle());
				}
			}
			/*
			
			TestServer testServer = new TestServer();
			testServer.startServer();
			LOGGER.debug("test server started");
			while (true)
			{
				Thread.sleep(10000);
			}
			*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

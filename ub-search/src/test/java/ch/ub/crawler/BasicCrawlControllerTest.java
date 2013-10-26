package ch.ub.crawler;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class BasicCrawlControllerTest {

	@Test
	public void testStartCrawler() {
		SitemapParser smParser = new SitemapParser();
		BasicCrawlController crawlController = new BasicCrawlController();
		try {
			List<String> urlList = smParser.getSitemap();
			crawlController.startCrawler(urlList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

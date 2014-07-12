package ch.ub.crawler;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import ch.ub.config.Config;

public class SitemapParserTest {

	private final static Logger LOGGER = Logger.getLogger(SitemapParserTest.class.getName()); 

	@Test
	public void testGetSitemap() {
		
		Config config = null;
		try {
			config = new Config("D://workspace-ub/ub-search/src/test/resources/testconfig.properties");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String sitempaurl = config.get(Config.CONFIG_PARAM_SITEMAPURL);
		LOGGER.debug("found config sitemap url =" + sitempaurl);
		

		SitemapParser smParser = new SitemapParser(sitempaurl);
		try {
			List<String> urlList = smParser.getSitemap();
			assertNotNull("url list is null", urlList);
			assertTrue("urllist is empty", urlList.size()>0);
			LOGGER.debug("urlList size =" + urlList.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

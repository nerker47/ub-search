package ch.ub.crawler;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import ch.ub.config.Config;

public class SitemapParserTest {

	@Test
	public void testGetSitemap() {
		
		Config config = null;
		try {
			config = new Config("config.properties");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String sitempaurl = config.get(Config.CONFIG_PARAM_SITEMAPURL);

		SitemapParser smParser = new SitemapParser(sitempaurl);
		try {
			List<String> urlList = smParser.getSitemap();
			assertNotNull("url list is null", urlList);
			assertTrue("urllist is empty", urlList.size()>0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

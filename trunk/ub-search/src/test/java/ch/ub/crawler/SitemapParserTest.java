package ch.ub.crawler;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

public class SitemapParserTest {

	@Test
	public void testGetSitemap() {
		
		SitemapParser smParser = new SitemapParser();
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

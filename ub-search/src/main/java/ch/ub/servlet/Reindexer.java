package ch.ub.servlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ch.ub.config.Config;
import ch.ub.crawler.BasicCrawlController;
import ch.ub.crawler.SitemapParser;
import ch.ub.indexer.IndexHolder;

public class Reindexer implements Runnable {

	private final static Logger LOGGER = Logger.getLogger(Reindexer.class.getName()); 

	private UBSearchServlet searchServlet; 

	public Reindexer(UBSearchServlet searchServlet ) {
	super();
	this.searchServlet = searchServlet; 
}


	public void run() {

		if (!searchServlet.isReindexing())
		{
		searchServlet.setReindexing(true); 
		
		
		
//		CrawlContentIndexer.reload(indexHolder);
		Config appConfig = searchServlet.getAppConfig();
		appConfig.require(Config.CONFIG_PARAM_SITEMAPURL);
		appConfig.require(Config.CONFIG_PARAM_LIMIT_NUM_SIMILAR_RESULTS);
		//appConfig.require(Config.CONFIG_PARAM_CRAWLER_TMP_DIR);
		String siteMapUrl = appConfig.get(Config.CONFIG_PARAM_SITEMAPURL);
		String crawlerTmpDir = System.getProperty("java.io.tmpdir"); //appConfig.get(Config.CONFIG_PARAM_CRAWLER_TMP_DIR);
		Integer numUrlsToFetch = appConfig.getInt(Config.CONFIG_PARAM_LIMIT_NUM_URLS_TO_FETCH);
	//	numResults = appConfig.getInt(Config.CONFIG_PARAM_LIMIT_NUM_SIMILAR_RESULTS);
	//	LOGGER.debug("numResults=" + numResults);
		
		
		SitemapParser smParser = new SitemapParser(siteMapUrl);
		LOGGER.debug("siteMapUrl=" + siteMapUrl);
		LOGGER.debug("smParser=" + smParser);
//		String searchTerm = "nsa";
		crawlerTmpDir = crawlerTmpDir + File.pathSeparatorChar + "ubsearch" + System.currentTimeMillis();
		
		IndexHolder newIndexHolder = new IndexHolder();
		BasicCrawlController crawlController = BasicCrawlController.createNewInstance(newIndexHolder, crawlerTmpDir);
		
//		BasicCrawlController crawlController = new BasicCrawlController(crawlerTmpDir);

			List<String> urlList = new ArrayList<String>();
			try {
				urlList = smParser.getSitemap();
				LOGGER.debug("urlList.size =" + urlList.size());
				if (numUrlsToFetch!=null && numUrlsToFetch>0)
				{
				if (urlList.size()-1>numUrlsToFetch)
					{
					urlList = urlList.subList(0, numUrlsToFetch);
					}
				}
			} catch (IOException e) {
				LOGGER.error("cannot load url list from sitemap", e);
			}
			try {
				crawlController.startCrawler(urlList);
			} catch (Exception e) {

				
				LOGGER.error("problem while crawling pages", e);
			}
			
			searchServlet.setIndexHolder(newIndexHolder);
			searchServlet.setReindexing(false);
			searchServlet.setIndexCreated(true);
		}
		else
		{
			LOGGER.debug("SORRY, ALREADY INDEXING IN OTHER THREAD");
		}
		
	}

}

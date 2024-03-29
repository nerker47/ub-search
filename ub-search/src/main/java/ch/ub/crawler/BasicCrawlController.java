package ch.ub.crawler;

import java.util.List;

import ch.ub.indexer.IndexHolder;
import ch.ub.util.IndexerUtil;
import ch.ub.util.LastModifiedCheckUtil;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * Must be singleton to provice acces to the current writer from the crwal instance 
 * (which we do not have any control, since it is instanced from the crawl framework
 * 
 * @author Daniel
 *
 */

public class BasicCrawlController {

	private final String tmpDir;
	private final IndexHolder indexHolder;
	
	private static BasicCrawlController currentInstance = null; 
	
	private BasicCrawlController(IndexHolder indexHolder, String tmpDir) {
		this.tmpDir = tmpDir;
		this.indexHolder = indexHolder;
	}

	public static BasicCrawlController getCurrentInstance()
	{
		return currentInstance;
	}

	public static BasicCrawlController createNewInstance(IndexHolder indexHolder, String tmpDir)
	{
		currentInstance = new BasicCrawlController(indexHolder, tmpDir);
		return currentInstance;
	}
	
	public void startCrawler(List<String> urlList) throws Exception {
		
		/*
		if (args.length != 2) {
			System.out.println("Needed parameters: ");
			System.out.println("\t rootFolder (it will contain intermediate crawl data)");
			System.out.println("\t numberOfCralwers (number of concurrent threads)");
			return;
		}
		*/

		/*
		 * crawlStorageFolder is a folder where intermediate crawl data is
		 * stored.
		 */
		String crawlStorageFolder = tmpDir;

		/*
		 * numberOfCrawlers shows the number of concurrent threads that should
		 * be initiated for crawling.
		 */
		int numberOfCrawlers = 3; //Integer.parseInt(args[1]);

		CrawlConfig config = new CrawlConfig();

		config.setResumableCrawling(false);
		config.setCrawlStorageFolder(crawlStorageFolder);

		/*
		 * Be polite: Make sure that we don't send more than 1 request per
		 * second (1000 milliseconds between requests).
		 */
		config.setPolitenessDelay(100);

		/*
		 * You can set the maximum crawl depth here. The default value is -1 for
		 * unlimited depth
		 */
		config.setMaxDepthOfCrawling(0);

		/*
		 * You can set the maximum number of pages to crawl. The default value
		 * is -1 for unlimited number of pages
		 */
		config.setMaxPagesToFetch(4000);

		/*
		 * Do you need to set a proxy? If so, you can use:
		 * config.setProxyHost("proxyserver.example.com");
		 * config.setProxyPort(8080);
		 * 
		 * If your proxy also needs authentication:
		 * config.setProxyUsername(username); config.getProxyPassword(password);
		 */

		/*
		 * This config parameter can be used to set your crawl to be resumable
		 * (meaning that you can resume the crawl from a previously
		 * interrupted/crashed crawl). Note: if you enable resuming feature and
		 * want to start a fresh crawl, you need to delete the contents of
		 * rootFolder manually.
		 */
		config.setResumableCrawling(false);

		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);


		for (String url: urlList)
		{
		controller.addSeed(url);
		}

		IndexerUtil.openIndexWriter(indexHolder);
		controller.start(BasicCrawler.class, numberOfCrawlers);
		IndexerUtil.closeIndexWriter(indexHolder);
	}

	public IndexHolder getIndexHolder() {
		return indexHolder;
	}
	
	
}
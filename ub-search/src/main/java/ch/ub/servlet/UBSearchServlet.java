package ch.ub.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.lucene.queryparser.classic.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ch.ub.config.Config;
import ch.ub.crawler.BasicCrawlController;
import ch.ub.crawler.SitemapParser;
import ch.ub.indexer.ContentRecord;
import ch.ub.indexer.ContentRecordSerializer;
import ch.ub.indexer.IndexHolder;
import ch.ub.util.IndexResultsRetrieverUtil;
import ch.ub.util.LastModifiedCheckUtil;

public class UBSearchServlet extends HttpServlet {

	/**
	 * 
	 */
	
	private static boolean isReindexing = false;
	
	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = Logger.getLogger(UBSearchServlet.class.getName()); 
	
	private static Config appConfig; 
	private static String siteMapUrl; 
	private boolean  indexCreated = false; 
	Integer numResults;
	
	IndexHolder indexHolder;
	static Thread indexUpdaterThread; 
	static Thread sitemapLastModifiedCheckerThread; 
	
	Long sitemapLastModified = 0l;
	
	Runnable siteMapUpdateChecker = new Runnable() {
		
		public void run() {
			while (true)
			{
				if (isIndexCreated())
				{
				long currentLastModifiedDate = LastModifiedCheckUtil.getLastModified(siteMapUrl);
				LOGGER.debug("checking lastmodified of sitemap, " + "current known: " + currentLastModifiedDate + ", last mod of sitemap on server: " + sitemapLastModified);
				if (currentLastModifiedDate>sitemapLastModified)
				{
					LOGGER.debug("sitemap lastmodified changed, going to reindex");
					createIndex();
					currentLastModifiedDate = sitemapLastModified;
				}
				}
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	};
	@Override
	public void init(ServletConfig config) throws ServletException {

		indexHolder = new IndexHolder();
		
		LOGGER.debug("init() called");

		super.init(config);
		
		sitemapLastModifiedCheckerThread = new Thread(siteMapUpdateChecker);
		sitemapLastModifiedCheckerThread.start();
	}


	
	private void createIndex()
	{

		Reindexer reindexer = new Reindexer(this);
		 indexUpdaterThread = new Thread(reindexer);
		indexUpdaterThread.start();
		
//		if (!isReindexing)
//		{
//		isReindexing = true; 
//		
//		
//		
////		CrawlContentIndexer.reload(indexHolder);
//		
//		appConfig.require(Config.CONFIG_PARAM_SITEMAPURL);
//		appConfig.require(Config.CONFIG_PARAM_LIMIT_NUM_SIMILAR_RESULTS);
//		//appConfig.require(Config.CONFIG_PARAM_CRAWLER_TMP_DIR);
//		String siteMapUrl = appConfig.get(Config.CONFIG_PARAM_SITEMAPURL);
//		String crawlerTmpDir = System.getProperty("java.io.tmpdir"); //appConfig.get(Config.CONFIG_PARAM_CRAWLER_TMP_DIR);
//		Integer numUrlsToFetch = appConfig.getInt(Config.CONFIG_PARAM_LIMIT_NUM_URLS_TO_FETCH);
//		numResults = appConfig.getInt(Config.CONFIG_PARAM_LIMIT_NUM_SIMILAR_RESULTS);
//		LOGGER.debug("numResults=" + numResults);
//		
//		
//		SitemapParser smParser = new SitemapParser(siteMapUrl);
//		LOGGER.debug("siteMapUrl=" + siteMapUrl);
//		LOGGER.debug("smParser=" + smParser);
////		String searchTerm = "nsa";
//		crawlerTmpDir = crawlerTmpDir + File.pathSeparatorChar + "ubsearch" + System.currentTimeMillis();
//		
//		IndexHolder newIndexHolder = new IndexHolder();
//		BasicCrawlController crawlController = BasicCrawlController.createNewInstance(newIndexHolder, crawlerTmpDir);
//		
////		BasicCrawlController crawlController = new BasicCrawlController(crawlerTmpDir);
//
//			List<String> urlList = new ArrayList<String>();
//			try {
//				urlList = smParser.getSitemap();
//				LOGGER.debug("urlList.size =" + urlList.size());
//				if (numUrlsToFetch!=null && numUrlsToFetch>0)
//				{
//				if (urlList.size()-1>numUrlsToFetch)
//					{
//					urlList = urlList.subList(0, numUrlsToFetch);
//					}
//				}
//			} catch (IOException e) {
//				LOGGER.error("cannot load url list from sitemap", e);
//			}
//			try {
//				crawlController.startCrawler(urlList);
//			} catch (Exception e) {
//
//				
//				LOGGER.error("problem while crawling pages", e);
//			}
//			
//			indexHolder = newIndexHolder; 
//			isReindexing=false;
//		}
	}
	private void processRequest(ServletRequest request, ServletResponse response) throws IOException
	{

		if (!isIndexCreated())
		{
			try {
				appConfig = new Config("/WEB-INF/config.properties", getServletContext());
				numResults = appConfig.getInt(Config.CONFIG_PARAM_LIMIT_NUM_SIMILAR_RESULTS);
				siteMapUrl = appConfig.get(Config.CONFIG_PARAM_SITEMAPURL);

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			long currentLastModifiedDate = LastModifiedCheckUtil.getLastModified(siteMapUrl);
			setSitemapLastModified(currentLastModifiedDate);
			createIndex();
			//indexCreated=true; 
			
		}
		response.setContentType("application/javascript;charset=utf-8");
		 //response.setStatus(HttpServletResponse.SC_OK);
		//baseRequest.setHandled(true);
		String searchString = request.getParameter("search");
		String similarUrlString = request.getParameter("similar");
		String reindexUrlString = request.getParameter("reindex");
		String callbackString = request.getParameter("callback");
		String resultsString = request.getParameter("results");
		
		if (resultsString!=null && isIndexCreated())
		{
			try {
			numResults = Integer.parseInt(resultsString);
			}
			catch (NumberFormatException nfe)
			{
				LOGGER.debug("results is not a number");
			}
		}
		
		// got call to start reindexing
		if (reindexUrlString!=null)
		{
			createIndex();
		}
		PrintWriter out = response.getWriter();

		//Gson gson = new Gson();
		Gson gson = new GsonBuilder().registerTypeAdapter(ContentRecord.class, new ContentRecordSerializer())
        .create();
//		JsonWriter jsonWriter = new JsonWriter(out);
//		jsonWriter.name("result");
		
//		jsonWriter.beginArray();

		JsonArray mainJSONcontainer = new JsonArray();
		int numSearchResults = 0;
		if (searchString!=null && isIndexCreated())
		{
			try {
				//List<ContentRecord> crList = CrawlContentIndexer.getInstance().search(searchString);
				List<ContentRecord> crList = IndexResultsRetrieverUtil.search(indexHolder.getDirectory(), indexHolder.getAnalyzer(), searchString, 100);
				numSearchResults = crList.size();
				for (ContentRecord cr : crList)
				{
					JsonObject similarPagesFor = new JsonObject();

					List<ContentRecord> crSimList = IndexResultsRetrieverUtil.likeThis(indexHolder.getDirectory(),indexHolder.getAnalyzer(),   cr.getUrl(),numResults);
					 
					JsonArray resultsFor = new JsonArray();
					
					for (ContentRecord crsim : crSimList)
					{
						JsonElement contentRecordAsJson = gson.toJsonTree(crsim);
//						out.println(" Similar page=" + crsim.toString() + "<br/>");
						resultsFor.add(contentRecordAsJson);
					}
					similarPagesFor.add(cr.getUrl(), resultsFor);
					mainJSONcontainer.add(similarPagesFor);

				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			JsonObject total = new JsonObject();
			total.addProperty("total", numSearchResults);
			mainJSONcontainer.add(total);
			response.getWriter().write(new Gson().toJson(mainJSONcontainer));
			
		}
		
		
		
		if (similarUrlString!=null&& isIndexCreated())
		{
			JsonObject similarPagesFor = new JsonObject();

			JsonArray resultsFor = new JsonArray();
			try {
				//List<ContentRecord> crSimList = CrawlContentIndexer.getInstance().likeThis(similarUrlString, numResults);
				LOGGER.debug("indexHolder:" + indexHolder);
				LOGGER.debug("numResults:" + numResults);
				List<ContentRecord> crSimList = IndexResultsRetrieverUtil.likeThis(indexHolder.getDirectory(),indexHolder.getAnalyzer(),   similarUrlString,numResults);

				for (ContentRecord crsim : crSimList)
				{
					//out.println(" Similar pages=" + crsim.toString() + "<br/>");
					JsonElement contentRecordAsJson = gson.toJsonTree(crsim);
					resultsFor.add(contentRecordAsJson);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			similarPagesFor.add("similarPages", resultsFor);
			mainJSONcontainer.add(similarPagesFor);
			
			JsonObject total = new JsonObject();
			total.addProperty("total", 1);
			mainJSONcontainer.add(total);
			response.getWriter().write(callbackString + "(" + new Gson().toJson(similarPagesFor) + ");");
			
		}		
		if (!isIndexCreated())
		{ response.getWriter().write(callbackString + "(" + "{}" + ");");}
		
	}
	@Override
	public void service(ServletRequest request, ServletResponse response)
			throws ServletException, IOException {
		super.service(request, response);
	}


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		processRequest( req,  resp); 
	}



	public static boolean isReindexing() {
		return isReindexing;
	}



	public static void setReindexing(boolean isReindexing) {
		UBSearchServlet.isReindexing = isReindexing;
	}



	public static Config getAppConfig() {
		return appConfig;
	}



	public IndexHolder getIndexHolder() {
		return indexHolder;
	}



	public void setIndexHolder(IndexHolder indexHolder) {
		this.indexHolder = indexHolder;
	}



	public boolean isIndexCreated() {
		return indexCreated;
	}



	public void setIndexCreated(boolean indexCreated) {
		this.indexCreated = indexCreated;
	}



	public Long getSitemapLastModified() {
		return sitemapLastModified;
	}



	public void setSitemapLastModified(Long sitemapLastModified) {
		this.sitemapLastModified = sitemapLastModified;
	}

	
	
	
}

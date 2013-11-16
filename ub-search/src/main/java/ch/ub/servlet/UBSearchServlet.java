package ch.ub.servlet;

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

import ch.ub.crawler.BasicCrawlController;
import ch.ub.crawler.BasicCrawlControllerTest;
import ch.ub.crawler.SitemapParser;
import ch.ub.indexer.ContentRecord;
import ch.ub.indexer.ContentRecordSerializer;
import ch.ub.indexer.CrawlContentIndexer;

public class UBSearchServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = Logger.getLogger(UBSearchServlet.class.getName()); 

	
	@Override
	public void init(ServletConfig config) throws ServletException {

		CrawlContentIndexer.reload();
		
		SitemapParser smParser = new SitemapParser();
		String searchTerm = "nsa";
		BasicCrawlController crawlController = new BasicCrawlController();

			List<String> urlList = new ArrayList<String>();
			try {
				urlList = smParser.getSitemap();
			} catch (IOException e) {
				LOGGER.error("cannot load url list from sitemap", e);
			}
			// limit for debug
		//	urlList = urlList.subList(0, 20);
			try {
				crawlController.startCrawler(urlList);
			} catch (Exception e) {

				
				LOGGER.error("problem while crawling pages", e);
			}
			
			/*
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
			*/
		
		super.init(config);
	}


	private void processRequest(ServletRequest request, ServletResponse response) throws IOException
	{

		response.setContentType("application/json;charset=utf-8");
		 //response.setStatus(HttpServletResponse.SC_OK);
		//baseRequest.setHandled(true);
		String searchString = request.getParameter("search");
		String similarUrlString = request.getParameter("similar");
		PrintWriter out = response.getWriter();

		//Gson gson = new Gson();
		Gson gson = new GsonBuilder().registerTypeAdapter(ContentRecord.class, new ContentRecordSerializer())
        .create();
//		JsonWriter jsonWriter = new JsonWriter(out);
//		jsonWriter.name("result");
		
//		jsonWriter.beginArray();

		JsonArray mainJSONcontainer = new JsonArray();
		int numSearchResults = 0;
		if (searchString!=null)
		{
			try {
				List<ContentRecord> crList = CrawlContentIndexer.getInstance().search(searchString);
				numSearchResults = crList.size();
				for (ContentRecord cr : crList)
				{
					JsonObject similarPagesFor = new JsonObject();

//					out.println("Result=" + cr.getUrl() + "<br/>");
					List<ContentRecord> crSimList = CrawlContentIndexer.getInstance().likeThis(cr.getUrl());
					
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
		
		
		
		if (similarUrlString!=null)
		{
			JsonObject similarPagesFor = new JsonObject();

			JsonArray resultsFor = new JsonArray();
			try {
				List<ContentRecord> crSimList = CrawlContentIndexer.getInstance().likeThis(similarUrlString);
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
			response.getWriter().write(new Gson().toJson(similarPagesFor));
			
		}		
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

	
}

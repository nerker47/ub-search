package ch.ub.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.queryparser.classic.ParseException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.ajax.JSON;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;

import ch.ub.indexer.ContentRecord;
import ch.ub.indexer.ContentRecordSerializer;
import ch.ub.indexer.CrawlContentIndexer;

public class TestServer extends AbstractHandler {
	
	Server server; // = new Server(8080);	
	
	public void handle(String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response) 
throws IOException, ServletException
{
		response.setContentType("application/json;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
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
			similarPagesFor.add(similarUrlString, resultsFor);
			mainJSONcontainer.add(similarPagesFor);
			
			JsonObject total = new JsonObject();
			total.addProperty("total", 1);
			mainJSONcontainer.add(total);
			response.getWriter().write(new Gson().toJson(mainJSONcontainer));
			
		}		
//response.getWriter().println("<h1>Hello World</h1>");

}

	public void startServer() throws Exception
	{
		server = new Server(8080);
		server.setHandler(new TestServer());
		server.start();
		server.join();
	}
}

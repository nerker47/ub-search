package ch.ub.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.queryparser.classic.ParseException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import ch.ub.indexer.ContentRecord;
import ch.ub.indexer.CrawlContentIndexer;

public class TestServer extends AbstractHandler {
	
	Server server; // = new Server(8080);	
	
	public void handle(String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response) 
throws IOException, ServletException
{
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		String searchString = request.getParameter("search");
		String similarUrlString = request.getParameter("similar");
		PrintWriter out = response.getWriter();

		if (searchString!=null)
		{
			try {
				List<ContentRecord> crList = CrawlContentIndexer.getInstance().search(searchString);
				for (ContentRecord cr : crList)
				{
					out.println("Result=" + cr.toString() + "<br/>");
					List<ContentRecord> crSimList = CrawlContentIndexer.getInstance().likeThis(cr.getUrl());
					for (ContentRecord crsim : crSimList)
					{
						out.println(" Similar page=" + crsim.toString() + "<br/>");
					}
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		if (similarUrlString!=null)
		{
			try {
				List<ContentRecord> crSimList = CrawlContentIndexer.getInstance().likeThis(similarUrlString);
				for (ContentRecord crsim : crSimList)
				{
					out.println(" Similar pages=" + crsim.toString() + "<br/>");
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
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

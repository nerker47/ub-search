package ch.ub.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.apache.log4j.Logger;

public class LastModifiedCheckUtil {

	private final static Logger LOGGER = Logger.getLogger(LastModifiedCheckUtil.class.getName()); 

	public static Long getLastModified(String urlString) {
		Long lastModifiedMillis = 0l;
	    URL url;
		try {
			url = new URL(urlString);
		    HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();

		    long date = httpCon.getLastModified();
		    if (date == 0)
		    {
		    	LOGGER.debug("No last-modified information for " + urlString);
		    }
		    else
		    {
		    	LOGGER.debug("Last-Modified: " + new Date(date));
		      return  date;
		    }
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return lastModifiedMillis;
	 }
}

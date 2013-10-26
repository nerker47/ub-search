package ch.ub.parser;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ch.ub.indexer.ContentRecord;

public class HTMLParser {

	private final static Logger LOGGER = Logger.getLogger(HTMLParser.class.getName()); 
	public ContentRecord parse(String html, String url)
	{
		ContentRecord cr = new ContentRecord();
		cr.setUrl(url);

		
		Document doc = Jsoup.parse(html);
		/*
		 * <title>48° 8' N, 11° 34' O - Sabina Lorenz | Lyrik | Untergrund-Blättle</title>
	  
	  
      <meta name="description" content="Eine Glastür ist eine Glastür, deshalb darf das Kind sie auch nicht anfassen, wenn die Mutter saugt, spült und wischt.">
	  
      <meta name="keywords" content="Untergrund-Blättle, Untergrund, Gesellschaft, Gedichte, 48° 8' N, 11° 34' O, Sabina Lorenz, Prosa, Lyrik, Subkultur, Gegenkultur">
	  
		 */
		
		Element metatagKeywords = doc.select("meta[name$=keywords]").first(); 
		Element metatagDescription = doc.select("meta[name$=description]").first(); 		
		Elements metatags = doc.select("meta[name]"); 
		Element titleTag = doc.select("title").first();
		String text = doc.body().text();
		Element realContentTitle = doc.select("#content_titel_entry").first();
		Element realContentContent = doc.select("#content_entry").first();
						
		if (titleTag!=null)
		{
		cr.setTitle(titleTag.text());
		}
		if (metatagDescription!=null)
		{
		cr.setMetaDescription(metatagDescription.attr("content"));
		}
		if (metatagKeywords!=null)
		{
		cr.setMetaKeywords(metatagKeywords.attr("content"));
		}
		String content = "";
		if (realContentTitle!=null)
		{
			content += " " + realContentTitle.text();
		}
		if (realContentContent!=null)
		{
			content += " " + realContentContent.text();
		}
		cr.setContent(content);
		
		LOGGER.debug(cr.toString());
		return cr; 
	}
}

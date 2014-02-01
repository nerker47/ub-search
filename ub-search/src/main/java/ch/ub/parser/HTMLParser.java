package ch.ub.parser;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ch.ub.indexer.ContentRecord;

/*
 * custom HTML parser transferring data parsed from page into data object
 * 
 */
public class HTMLParser {

	
	private final static Logger LOGGER = Logger.getLogger(HTMLParser.class.getName()); 
	public ContentRecord parse(String html, String url)
	{
		ContentRecord cr = new ContentRecord();
		cr.setUrl(url);

		
		Document doc = Jsoup.parse(html);
		
		Element metatagKeywords = doc.select("meta[name$=keywords]").first(); 
		Element metatagDescription = doc.select("meta[name$=description]").first(); 		
		Elements metatags = doc.select("meta[name]"); 
		Element titleTag = doc.select("title").first();
		String text = doc.body().text();
		Element realContentTitle = doc.select("#content_titel_entry").first();
		Element realContentContent = doc.select("#content_entry").first();
						
		if (titleTag!=null)
		{
		String titlePure = titleTag.text();
		if (titlePure.contains("|"))
		{
			titlePure = titlePure.substring(0, titlePure.indexOf("|")); //.subSequence(beginIndex, endIndex)
		}
		cr.setTitle(titlePure);
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

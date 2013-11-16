package ch.ub.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

public class SitemapParser {
	
   public List<String> getSitemap() throws IOException {

	   List<String> urlList = new ArrayList<String>();
       String str = "http://www.xn--untergrund-blttle-2qb.ch/sitemap.xml";
       URL url = new URL(str);
       InputStream is = url.openStream();
       int ptr = 0;
       StringBuilder builder = new StringBuilder();
       while ((ptr = is.read()) != -1) {
           builder.append((char) ptr);
       }
       String xml = builder.toString();

       org.jdom2.input.SAXBuilder saxBuilder = new SAXBuilder();
       try {
           org.jdom2.Document doc = saxBuilder.build(new StringReader(xml));
//           System.out.println(xml);
           Element xmlfile = doc.getRootElement();
           Namespace ns = xmlfile.getNamespace();
//           System.out.println("ROOT -->"+xmlfile);
           List<Element> list = xmlfile.getChildren();
//           System.out.println("size = " + list.size());
           for (Element e: list)
           {
//               System.out.println("URLe  -->"+e.getChildText("loc", ns));
               urlList.add(e.getChildText("loc", ns));
           }
//           List list2 = xmlfile.getChildren("url");
//           System.out.println("LIST -->"+list2);
       } catch (JDOMException e) {
           // handle JDOMExceptio n
       } catch (IOException e) {
           // handle IOException
       }

//       System.out.println("===========================");

       return urlList; 
   }
}
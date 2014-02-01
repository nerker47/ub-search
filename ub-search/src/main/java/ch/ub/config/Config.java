package ch.ub.config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;

public class Config
{
	public static final String CONFIG_PARAM_SITEMAPURL = "sitemapurl";
	public static final String CONFIG_PARAM_CRAWLER_TMP_DIR = "crawlerTmpDir";
	public static final String CONFIG_PARAM_LIMIT_NUM_URLS_TO_FETCH = "limitNumUrlsToFetch";
	
	
	
    private Properties props;

    public Config(String file_name)
            throws java.io.IOException
        {
            props = new Properties();

            props.load(new FileInputStream(file_name));
        }
    
    public Config(String file_name, ServletContext context)
            throws java.io.IOException
        {
            props = new Properties();
            InputStream inp = context.getResourceAsStream(file_name);
            props.load(inp);
        }

    
    
    
    
    public void require(String key)
    {
        if (!props.containsKey(key))
        {
            throw new RuntimeException("Missing required key: " + key);
        }
    }

    public String get(String key)
    {
        return props.getProperty(key);
    }

    public int getInt(String key)
    {
        return Integer.parseInt(get(key));
    }

    public double getDouble(String key)
    {
        return Double.parseDouble(get(key));
    }

    public List<String> getList(String key)
    {
        String big_str = get(key);

        StringTokenizer stok = new StringTokenizer(big_str, ",");

        LinkedList<String> lst = new LinkedList<String>();
        while(stok.hasMoreTokens())
        {
            String node = stok.nextToken().trim();
            lst.add(node);
        }
        return lst;
    }

    public boolean getBoolean(String key)
    {
        String v = get(key);
        v = v.toLowerCase();
        if (v.equals("1")) return true;
        if (v.equals("true")) return true;
        if (v.equals("yes")) return true;
        if (v.equals("y")) return true;
        if (v.equals("on")) return true;
        if (v.equals("hell yeah")) return true;

        return false;

    }
    
    public boolean isSet(String key)
    {
        return (get(key) != null && !get(key).isEmpty());
    }
}

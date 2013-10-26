package ch.ub.indexer;

public class ContentRecord {

	String title = ""; 
	String url = ""; 
	String content = "";
	String metaKeywords = "";
	String metaDescription = ""; 
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getMetaKeywords() {
		return metaKeywords;
	}
	public void setMetaKeywords(String metaKeywords) {
		this.metaKeywords = metaKeywords;
	}
	public String getMetaDescription() {
		return metaDescription;
	}
	public void setMetaDescription(String metaDescription) {
		this.metaDescription = metaDescription;
	}
	@Override
	public String toString() {
		return "ContentRecord [title=" + title + ", url=<a href=\"" + url + "\">" + title + "</a>, content="
				+ content + ", metaKeywords=" + metaKeywords
				+ ", metaDescription=" + metaDescription + "]";
	} 
	
	
}

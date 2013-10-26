package ch.ub.indexer;

public class ContentRecord {

	String title = ""; 
	String url = ""; 
	String content = "";
	String metaKeywords = "";
	String metaDescription = ""; 
	float score = 0;
	
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
	
	
	public float getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}
	@Override
	public String toString() {
		return "ContentRecord [score=" + score + ", title=" + title + ", url=<a href=\"" + url + "\">link</a> | <a href=\"/?similar=" + url + "\">similar</a> | " + url + ", content="
				+ content + ", metaKeywords=" + metaKeywords
				+ ", metaDescription=" + metaDescription + "]";
	} 
	
	
}

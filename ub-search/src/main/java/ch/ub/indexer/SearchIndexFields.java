package ch.ub.indexer;

/*
 * Enumeration containing names of fields
 * 
 */
public enum SearchIndexFields {

	TITLE("title"),
	CONTENT("content"),
	METADESCRIPTION("description"),
	METAKEYWORDS("keywords"),
	URL("url")
	
	;
	
	String fieldName;

	private SearchIndexFields(String fieldName) {
		this.fieldName = fieldName;
	}
	
	
}

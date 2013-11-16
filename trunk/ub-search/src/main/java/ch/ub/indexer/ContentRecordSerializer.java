package ch.ub.indexer;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ContentRecordSerializer implements JsonSerializer<ContentRecord> {
	public JsonElement serialize(final ContentRecord contentRecord, final Type type, final JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("title", new JsonPrimitive(contentRecord.getTitle()));
        result.add("url", new JsonPrimitive(contentRecord.getUrl()));
        result.add("similarityscore", new JsonPrimitive(contentRecord.getScore()));
        return result;
    }
}
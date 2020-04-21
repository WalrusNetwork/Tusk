package network.walrus.nerve.core.redis.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.shopify.graphql.support.ID;
import java.io.IOException;

/**
 * Adapter tp (de)serialize IDs for redis messages.
 *
 * @author Austin Mayes
 */
public class IDAdapter extends TypeAdapter<ID> {

  @Override
  public void write(JsonWriter jsonWriter, ID id) throws IOException {
    jsonWriter.value(id.toString());
  }

  @Override
  public ID read(JsonReader jsonReader) throws IOException {
    return new ID(jsonReader.nextString());
  }
}

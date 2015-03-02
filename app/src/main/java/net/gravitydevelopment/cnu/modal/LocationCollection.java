package net.gravitydevelopment.cnu.modal;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import net.gravitydevelopment.cnu.DiningBuddy;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LocationCollection {

    private List<LocationItem> locations;

    public LocationCollection(List<LocationItem> locations) {
        this.locations = locations;
    }

    public List<LocationItem> getLocations() {
        return locations;
    }

    public static LocationCollection deserializeLocations(String json) {
        LocationDeserializer deserializer = new LocationDeserializer();
        return deserializer.deserialize(new JsonParser().parse(json), LocationCollection.class, null);
    }

    public static class LocationDeserializer implements JsonDeserializer<LocationCollection> {
        @Override
        public LocationCollection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonArray array = json.getAsJsonObject().get("features").getAsJsonArray();
            List<LocationItem> list = new ArrayList<LocationItem>();
            for (int i = 0; i < array.size(); i++) {
                JsonObject location = array.get(i).getAsJsonObject();
                JsonObject properties = location.get("properties").getAsJsonObject();
                JsonObject geometry = location.get("geometry").getAsJsonObject();

                String name = properties.get("name").getAsString();
                int priority = properties.get("priority").getAsInt();

                JsonArray coordinates = geometry.get("coordinates").getAsJsonArray().get(0).getAsJsonArray();
                List<CoordinatePair> coordinatePairs = new ArrayList<CoordinatePair>();
                for (int j = 0; j < coordinates.size(); j++) {
                    JsonArray values = coordinates.get(j).getAsJsonArray();
                    double longitude = values.get(0).getAsDouble();
                    double latitude = values.get(1).getAsDouble();
                    coordinatePairs.add(new CoordinatePair(latitude, longitude));
                }
                list.add(new LocationItem(name, coordinatePairs, priority));
            }
            return new LocationCollection(list);
        }
    }
}

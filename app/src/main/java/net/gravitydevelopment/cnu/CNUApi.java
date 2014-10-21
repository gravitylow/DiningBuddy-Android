package net.gravitydevelopment.cnu;

import android.util.Log;

import net.gravitydevelopment.cnu.geo.CNUCoordinatePair;
import net.gravitydevelopment.cnu.geo.CNULocation;
import net.gravitydevelopment.cnu.geo.CNULocationInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CNUApi {
    private static final String API_HOST = "https://api.gravitydevelopment.net";
    private static final String API_VERSION = "v1.0";
    private static final String API_QUERY = "/cnu/api/" + API_VERSION + "/";
    private static final String API_USER_AGENT = "CNU-Android-v1";
    private static final String API_CONTENT_TYPE = "application/json";

    public static List<CNULocation> getLocations() {
        try {
            URL url = new URL(API_HOST + API_QUERY + "locations");
            String response = read(url);

            return locationsFromJson(response);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<CNULocation>();
        }
    }

    public static void addLocation(CNULocation location) {
        try {
            URL url = new URL(API_HOST + API_QUERY + "locations");
            JSONObject object = new JSONObject(location.jsonValue());

            int result = write(url, object);
            if (result != HttpURLConnection.HTTP_CREATED) {
                Log.e(CNU.LOG_TAG, "Error adding location");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<CNULocationInfo> getInfo() {
        try {
            URL url = new URL(API_HOST + API_QUERY + "info");
            String response = read(url);

            JSONArray array = new JSONArray(response);
            return infoFromArray(array);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<CNULocationInfo>();
        }
    }

    public static List<CNULocationMenuItem> getMenu(String location) {
        try {
            URL url = new URL(API_HOST + API_QUERY + "menus/" + location);
            String response = read(url);

            JSONArray array = new JSONArray(response);
            return menuFromArray(array);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<CNULocationMenuItem>();
        }
    }

    public static List<CNULocationMenuItem> menuFromArray(JSONArray info) {
        List<CNULocationMenuItem> list = new ArrayList<CNULocationMenuItem>();
        try {
            for (int i = 0; i < info.length(); i++) {
                JSONObject location = info.getJSONObject(i);
                String startTime = location.getString("start");
                String endTime = location.getString("end");
                String summary = location.getString("summary");
                String description = location.getString("description");
                CNULocationMenuItem item = new CNULocationMenuItem(startTime, endTime, summary, description);
                list.add(item);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<CNULocationMenuItem>();
        }
    }

    public static List<CNULocationFeedItem> getFeed(String location) {
        try {
            URL url = new URL(API_HOST + API_QUERY + "feed/" + location);
            String response = read(url);

            JSONArray array = new JSONArray(response);
            return feedFromArray(array);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<CNULocationFeedItem>();
        }
    }

    public static List<CNULocationFeedItem> feedFromArray(JSONArray info) {
        List<CNULocationFeedItem> list = new ArrayList<CNULocationFeedItem>();
        try {
            for (int i = 0; i < info.length(); i++) {
                JSONObject update = info.getJSONObject(i);
                String message = update.getString("feedback");
                int minutes = update.getInt("minutes");
                int crowded = update.getInt("crowded");
                long time = update.getLong("time");
                boolean pinned = update.getBoolean("pinned");
                String detail = update.getString("detail");
                CNULocationFeedItem item = new CNULocationFeedItem(message, minutes, crowded, time, pinned, detail);
                list.add(item);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<CNULocationFeedItem>();
        }
    }

    public static List<CNULocationInfo> infoFromArray(JSONArray info) {
        List<CNULocationInfo> list = new ArrayList<CNULocationInfo>();
        try {
            for (int i = 0; i < info.length(); i++) {
                JSONObject location = info.getJSONObject(i);
                String name = location.getString("location");
                int people = location.getInt("people");
                int crowded = location.getInt("crowded");
                CNULocationInfo newInfo = new CNULocationInfo(name, people, crowded);
                list.add(newInfo);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<CNULocationInfo>();
        }
    }

    public static void updateLocation(double latitude, double longitude, CNULocation location, long time, UUID id) {
        if (location == null) {
            return;
        }
        String json = "{"
                + "\"id\": \"" + id.toString() + "\""
                + ", \"lat\": " + latitude
                + ", \"lon\": " + longitude
                + ", \"location\": \"" + location.getName() + "\""
                + ", \"time\": " + time
                + "}";
        try {
            URL url = new URL(API_HOST + API_QUERY + "update");
            JSONObject object = new JSONObject(json);

            int result = write(url, object);
            if (result != HttpURLConnection.HTTP_CREATED) {
                Log.e(CNU.LOG_TAG, "Error sending update: " + result);
            }
            Log.d(CNU.LOG_TAG, "Posted update: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendFeedback(String target, CNULocation location, int crowded, int minutes, String feedback, long time, UUID id) {
        if (location == null) {
            return;
        }
        feedback = feedback.replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"");
        String json = "{"
                + "\"id\": \"" + id.toString() + "\""
                + ", \"target\": \"" + target + "\""
                + ", \"crowded\": " + crowded
                + ", \"minutes\": " + minutes
                + ", \"feedback\": \"" + feedback + "\""
                + ", \"location\": \"" + location.getName() + "\""
                + ", \"time\": " + time
                + ", \"pinned\": " + false
                + "}";
        try {
            URL url = new URL(API_HOST + API_QUERY + "feedback");
            JSONObject object = new JSONObject(json);

            int result = write(url, object);
            if (result != HttpURLConnection.HTTP_CREATED) {
                Log.e(CNU.LOG_TAG, "Error sending update: " + result);
            }
            Log.d(CNU.LOG_TAG, "Posted update: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<CNULocation> locationsFromArray(JSONArray locations) {
        List<CNULocation> list = new ArrayList<CNULocation>();
        try {
            for (int i = 0; i < locations.length(); i++) {
                JSONObject location = locations.getJSONObject(i);
                String name = location.getString("name");
                JSONArray fences = location.getJSONArray("coordinatePairs");
                List<CNUCoordinatePair> coordinatePairs = new ArrayList<CNUCoordinatePair>();
                for (int j = 0; j < fences.length(); j++) {
                    JSONObject fence = fences.getJSONObject(j);
                    double latitude = fence.getDouble("lat");
                    double longitude = fence.getDouble("lon");
                    coordinatePairs.add(new CNUCoordinatePair(latitude, longitude));
                }
                JSONArray subLocations = location.getJSONArray("subLocations");
                CNULocation newLoc;
                if (subLocations.length() > 0) {
                    newLoc = new CNULocation(name, coordinatePairs, locationsFromArray(subLocations));
                } else {
                    newLoc = new CNULocation(name, coordinatePairs);
                }
                list.add(newLoc);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<CNULocation>();
        }
    }

    public static List<CNULocation> locationsFromJson(String json) {
        try {
            JSONArray array = new JSONArray(json);
            return locationsFromArray(array);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<CNULocation>();
        }
    }

    private static String read(URL url) {
        try {
            URLConnection conn = url.openConnection();
            conn.addRequestProperty("User-Agent", API_USER_AGENT);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int write(URL url, JSONObject object) {
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", API_CONTENT_TYPE);
            conn.setRequestProperty("User-Agent", API_USER_AGENT);
            conn.setRequestMethod("POST");
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(object.toString());
            writer.flush();

            int result = conn.getResponseCode();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String getApiUrl() {
        return API_HOST + API_QUERY;
    }
}


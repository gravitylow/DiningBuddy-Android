package net.gravitydevelopment.cnu;

import android.content.pm.PackageManager;
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

public class API {
    private static final String API_HOST = "https://api.gravitydevelopment.net";
    private static final String API_VERSION = "v1.0";
    private static final String API_QUERY = "/cnu/api/" + API_VERSION + "/";
    private static final String API_CONTENT_TYPE = "application/json";
    private static String API_USER_AGENT = "CNU-Android";

    static {
        String version = "?";
        if (DiningBuddy.getContext() != null) {
            try {
                version = DiningBuddy.getContext().getPackageManager()
                        .getPackageInfo(DiningBuddy.getContext().getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        API_USER_AGENT += "-v" + version;
    }

    public static String getLocations() {
        try {
            URL url = new URL(API_HOST + API_QUERY + "locations2/");
            String response = read(url);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<CNULocationInfo> getInfo() {
        try {
            URL url = new URL(API_HOST + API_QUERY + "info/");
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
            URL url = new URL(API_HOST + API_QUERY + "menus/" + location + "/");
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

    public static List<String> getAlerts() {
        List<String> list = new ArrayList<String>();
        try {
            URL url = new URL(API_HOST + API_QUERY + "alerts/");
            String response = read(url);

            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String targetOS = object.getString("target_os");
                String targetVersion = object.getString("target_version");
                long targetTimeMin = object.getLong("target_time_min");
                long targetTimeMax = object.getLong("target_time_max");
                String message = object.getString("message");

                String thisOS = "Android";
                String thisVersion = DiningBuddy.getContext().getPackageManager()
                        .getPackageInfo(DiningBuddy.getContext().getPackageName(), 0).versionName;
                long thisTime = System.currentTimeMillis();

                // Reasons to disqualify this alert:
                if (!targetOS.equals("all") && !targetOS.equals(thisOS)) {
                    Log.i(DiningBuddy.LOG_TAG, "Alert disqualified for target: " + targetOS + ", " + thisOS);
                    continue;
                }
                if (!targetVersion.equals("all") && !targetVersion.equals(thisVersion)) {
                    Log.i(DiningBuddy.LOG_TAG, "Alert disqualified for version: " + targetVersion + ", " + thisVersion);
                    continue;
                }
                if ((targetTimeMin != 0 && targetTimeMin > thisTime) || (targetTimeMax != 0 && targetTimeMax < thisTime)) {
                    Log.i(DiningBuddy.LOG_TAG, "Alert disqualified for time: " + thisTime);
                    continue;
                }
                list.add(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<String>();
        }
        return list;
    }

    public static List<CNULocationFeedItem> getFeed(String location) {
        try {
            URL url = new URL(API_HOST + API_QUERY + "feed/" + location + "/");
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
                String detail;
                if (update.has("detail")) {
                    detail = update.getString("detail");
                } else {
                    detail = null;
                }
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
                + ", \"send_time\": " + time
                + "}";
        try {
            URL url = new URL(API_HOST + API_QUERY + "update");
            JSONObject object = new JSONObject(json);

            int result = write(url, object);
            if (result != HttpURLConnection.HTTP_CREATED) {
                Log.e(DiningBuddy.LOG_TAG, "Error sending update: " + result);
            }
            Log.d(DiningBuddy.LOG_TAG, "Posted update: " + result + " at " + System.currentTimeMillis());
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
                + ", \"send_time\": " + time
                + "}";
        try {
            URL url = new URL(API_HOST + API_QUERY + "feedback");
            JSONObject object = new JSONObject(json);

            int result = write(url, object);
            if (result != HttpURLConnection.HTTP_CREATED) {
                Log.e(DiningBuddy.LOG_TAG, "Error sending update: " + result);
            }
            Log.d(DiningBuddy.LOG_TAG, "Posted update: " + result + " at " + System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<CNULocation> locationsFromJson(String json) {
        try {
            JSONObject object = new JSONObject(json);
            return locationsFromObject(object);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<CNULocation>();
        }
    }

    private static List<CNULocation> locationsFromObject(JSONObject object) {
        List<CNULocation> list = new ArrayList<CNULocation>();
        try {
            JSONArray array = object.getJSONArray("features");
            for (int i = 0; i < array.length(); i++) {
                JSONObject location = array.getJSONObject(i);
                JSONObject properties = location.getJSONObject("properties");
                JSONObject geometry = location.getJSONObject("geometry");

                String name = properties.getString("name");
                int priority = properties.getInt("priority");

                JSONArray coordinates = geometry.getJSONArray("coordinates").getJSONArray(0);
                List<CNUCoordinatePair> coordinatePairs = new ArrayList<CNUCoordinatePair>();
                for (int j = 0; j < coordinates.length(); j++) {
                    JSONArray values = coordinates.getJSONArray(j);
                    double longitude = values.getDouble(0);
                    double latitude = values.getDouble(1);
                    coordinatePairs.add(new CNUCoordinatePair(latitude, longitude));
                }
                list.add(new CNULocation(name, coordinatePairs, priority));
            }
            return list;
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

            return conn.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String getApiUrl() {
        return API_HOST + API_QUERY;
    }
}


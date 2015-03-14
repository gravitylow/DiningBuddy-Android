package net.gravitydevelopment.cnu.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.gravitydevelopment.cnu.DiningBuddy;
import net.gravitydevelopment.cnu.modal.AlertItem;
import net.gravitydevelopment.cnu.modal.FeedItem;
import net.gravitydevelopment.cnu.modal.FeedbackItem;
import net.gravitydevelopment.cnu.modal.InfoItem;
import net.gravitydevelopment.cnu.modal.LocationCollection;
import net.gravitydevelopment.cnu.modal.LocationItem;
import net.gravitydevelopment.cnu.modal.MenuItem;
import net.gravitydevelopment.cnu.modal.UpdateItem;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;

public class API {

    public static String API_URL = "https://api.gravitydevelopment.net/cnu/api/v1.0";
    private static Service service;

    static {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocationCollection.class, new
                        LocationCollection.LocationDeserializer())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setConverter(new GsonConverter(gson))
                .build();
        service = restAdapter.create(Service.class);
    }

    public static List<AlertItem> getAlerts() {
        try {
            List<AlertItem> list = service.alertList();
            List<AlertItem> applicableAlerts = new ArrayList<AlertItem>();
            for (AlertItem item : list) {
                if (item.isApplicable()) {
                    applicableAlerts.add(item);
                }
            }
            return applicableAlerts;
        } catch (Exception ex) {
            Log.e(DiningBuddy.LOG_TAG, "Network error: " + ex.getMessage());
            return new ArrayList<AlertItem>();
        }
    }

    public static List<LocationItem> getLocations() {
        try {
            return service.locationList().getLocations();
        } catch (Exception ex) {
            Log.e(DiningBuddy.LOG_TAG, "Network error: " + ex.getMessage());
            return new ArrayList<LocationItem>();
        }
    }

    public static List<InfoItem> getInfo() {
        try {
            return service.infoList();
        } catch (Exception ex) {
            Log.e(DiningBuddy.LOG_TAG, "Network error: " + ex.getMessage());
            return new ArrayList<InfoItem>();
        }
    }

    public static InfoItem getInfo(LocationItem location) {
        try {
            return service.info(location.getName());
        } catch (Exception ex) {
                Log.e(DiningBuddy.LOG_TAG, "Network error: " + ex.getMessage());
                return null;
            }
    }

    public static List<MenuItem> getMenu(String location) {
        try {
            return service.menuList(location);
        } catch (Exception ex) {
            Log.e(DiningBuddy.LOG_TAG, "Network error: " + ex.getMessage());
            return new ArrayList<MenuItem>();
        }
    }

    public static List<FeedItem> getFeed(String location) {
        try {
            return service.feedList(location);
        } catch (Exception ex) {
            Log.e(DiningBuddy.LOG_TAG, "Network error: " + ex.getMessage());
            return new ArrayList<FeedItem>();
        }
    }

    public static void sendUpdate(UpdateItem item) {
        try {
            if (item.location == null) {
                return;
            }
            service.update(item);
        } catch (Exception ex) {
            Log.e(DiningBuddy.LOG_TAG, "Network error: " + ex.getMessage());
        }
    }

    public static void sendFeedback(FeedbackItem item) {
        try {
            if (item.location == null) {
                return;
            }
            service.feedback(item);
        } catch (Exception ex) {
            Log.e(DiningBuddy.LOG_TAG, "Network error: " + ex.getMessage());
        }
    }

}

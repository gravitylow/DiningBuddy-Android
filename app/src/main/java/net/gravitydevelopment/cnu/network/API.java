package net.gravitydevelopment.cnu.network;

import android.util.Log;

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

import java.util.ArrayList;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class API {

    public static String API_URL = "https://api.gravitydevelopment.net/cnu/api/v1.0";
    private static Service service;

    static {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocationCollection.class, new
                        LocationCollection.LocationDeserializer())
                .create();

        RestAdapter restAdapter =new RestAdapter.Builder()
            .setEndpoint(API_URL)
            .setConverter(new GsonConverter(gson))
            .build();
        service = restAdapter.create(Service.class);
    }

    public static List<AlertItem> getAlerts() {
        List<AlertItem> list = service.alertList();
        List<AlertItem> applicableAlerts = new ArrayList<AlertItem>();
        for (AlertItem item : list) {
            if (item.isApplicable()) {
                applicableAlerts.add(item);
            }
        }
        return applicableAlerts;
    }

    public static List<LocationItem> getLocations() {
        LocationCollection collection = service.locationList();
        Log.d(DiningBuddy.LOG_TAG, "Collection: " + collection);
        Log.d(DiningBuddy.LOG_TAG, "List: " + collection.getLocations());
        return service.locationList().getLocations();
    }

    public static List<InfoItem> getInfo() {
        return service.infoList();
    }

    public static InfoItem getInfo(LocationItem location) {
        return service.info(location.getName());
    }

    public static List<MenuItem> getMenu(LocationItem location) {
        return getMenu(location.getName());
    }

    public static List<MenuItem> getMenu(String location) {
        return service.menuList(location);
    }

    public static List<FeedItem> getFeed(LocationItem location) {
        return getFeed(location.getName());
    }

    public static List<FeedItem> getFeed(String location) {
        return service.feedList(location);
    }

    public static void sendUpdate(UpdateItem item) {
        if (item.location == null) {
            return;
        }
        service.update(item);
    }

    public static void sendFeedback(FeedbackItem item) {
        if (item.location == null) {
            return;
        }
        service.feedback(item);
    }

}

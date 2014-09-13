package net.gravitydevelopment.cnu.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import net.gravitydevelopment.cnu.service.BackendService;
import net.gravitydevelopment.cnu.service.LocationService;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, BackendService.class);
        context.startService(startServiceIntent);
    }
}

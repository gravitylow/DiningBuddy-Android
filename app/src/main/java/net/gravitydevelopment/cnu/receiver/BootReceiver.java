package net.gravitydevelopment.cnu.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Intent startServiceIntent = new Intent(context, BackendService.class);
        //context.startService(startServiceIntent);
    }
}

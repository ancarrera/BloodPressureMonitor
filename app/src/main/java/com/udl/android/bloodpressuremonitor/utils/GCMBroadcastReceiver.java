package com.udl.android.bloodpressuremonitor.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by adrian on 28/3/15.
 */
public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        ComponentName componentName = new ComponentName(context.getPackageName(),
                GCMIntentService.class.getName());

        startWakefulService(context,(intent.setComponent(componentName)));
        setResultCode(Activity.RESULT_OK);

    }
}

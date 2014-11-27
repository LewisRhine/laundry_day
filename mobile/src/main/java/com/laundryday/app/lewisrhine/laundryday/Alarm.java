package com.laundryday.app.lewisrhine.laundryday;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class Alarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        String name = intent.getStringExtra("name");

        Intent alarmDone = new Intent(context, AlarmDoneActivity.class);

        alarmDone.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        alarmDone.putExtra("name", name);

        context.startActivity(alarmDone);
    }


    public void setAlarm(Context context, String name, int alarmTimer, int id) {

        AlarmManager alarmManger = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, Alarm.class);

        intent.putExtra("name", name);
        intent.putExtra("id", id);

        Log.d("LR", "Alarm " + id + " started");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0);

        alarmManger.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + alarmTimer, pendingIntent);
    }

    public void cancelAlarm(Context context, int id) {

        Intent intent = new Intent(context, Alarm.class);

        PendingIntent sender = PendingIntent.getBroadcast(context, id, intent, 0);

        AlarmManager alarmManger = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Log.d("LR", "Alarm " + id + " stopped");

        alarmManger.cancel(sender);

    }

}

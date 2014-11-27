package com.laundryday.app.lewisrhine.laundryday.alarmservices;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.laundryday.app.lewisrhine.laundryday.Alarm;
import com.laundryday.app.lewisrhine.laundryday.MainActivity;
import com.laundryday.app.lewisrhine.laundryday.R;

public class AlarmService extends Service {
    // Amount to times the milliseconds timer by, 60000 for full minute
    private static final int millSecondsOffSet = 60000;
    private String BROADCAST_ACTION;
    private CountDownTimer countDownTimer;

    private Alarm alarm;
    private NotificationManager notificationManager;
    private Boolean isRunning = false;
    private SharedPreferences sharedPreferences;

    private String name;
    private Integer id;

    public AlarmService(String BROADCAST_ACTION, String name, Integer id) {
        this.BROADCAST_ACTION = BROADCAST_ACTION;
        this.id = id;
        this.name = name;
    }

    public AlarmService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        isRunning = true;

        Intent mainIntent = new Intent(this, MainActivity.class);

        Log.d("LR", name + " get intent data");

        Integer time = intent.getExtras().getInt("startTime");

        alarm = new Alarm();
        alarm.setAlarm(getApplicationContext(), name, time * millSecondsOffSet, id);

        final Intent intent2 = new Intent(BROADCAST_ACTION);

        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(name + " time:");


        PendingIntent contentIntent = PendingIntent.getActivity(this, id, mainIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);


        notificationBuilder.setContentIntent(contentIntent);
        notificationBuilder.setAutoCancel(false);
        notificationBuilder.setOngoing(true);
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(id, notificationBuilder.build());
        countDownTimer = new CountDownTimer(time * millSecondsOffSet, millSecondsOffSet) {


            @Override
            public void onTick(long millisUntilFinished) {

                intent2.putExtra("timeLeft", (millisUntilFinished / millSecondsOffSet));
                sendBroadcast(intent2);

                String timeLeftMinutes = "minutes";

                if ((millisUntilFinished / millSecondsOffSet) == 1) {
                    timeLeftMinutes = "minute";
                }

                notificationBuilder.setContentText("" + (millisUntilFinished / millSecondsOffSet) + " " + timeLeftMinutes + " till done");
                notificationManager.notify(id, notificationBuilder.build());

                //save the current time left to pref to be used if TimerFragment is recreated.
                sharedPreferences.edit().putLong(name.toLowerCase() + "_time_left", millisUntilFinished / millSecondsOffSet).apply();
            }

            @Override
            public void onFinish() {

                sharedPreferences.edit().putLong(name.toLowerCase() + "_time_left", 0l);
                intent2.putExtra("timeLeft", 0l);
                sendBroadcast(intent2);

                notificationBuilder.setContentText(name + " done!");
                notificationBuilder.setOngoing(false);
                notificationBuilder.setAutoCancel(true);
                notificationManager.notify(id, notificationBuilder.build());

                sharedPreferences.edit().putBoolean(name.toLowerCase() + "_running", false).apply();
                stopSelf();

            }
        }.start();

        return super.onStartCommand(intent, flags, startId);
    }


    private void cancelAlarm() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        alarm.cancelAlarm(getApplication(), id);
        notificationManager.cancel(id);
        isRunning = false;
        sharedPreferences.edit().putLong(name.toLowerCase() + "_time_left", 0l).apply();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAlarm();
        Log.d("LR", name + " service stopped");
    }

    public Boolean getIsRunning() {
        return isRunning;
    }
}
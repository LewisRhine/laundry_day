package com.laundryday.app.lewisrhine.laundryday;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class AlarmDoneActivity extends Activity {
    MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_done);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new AlertDialogFragment())
                    .commit();
        }

        String name = getIntent().getStringExtra("name");

        TextView doneTextView = (TextView) findViewById(R.id.doneTextView);
        doneTextView.setText("Your " + name + " is done!");


        PowerManager powerManger = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManger.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Alarm Done");
        wakeLock.acquire();

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        AudioManager audio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audio.setMode(AudioManager.MODE_NORMAL);

        try {
            mediaPlayer.setDataSource(this, soundUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.prepare();
            mediaPlayer.isLooping();
            mediaPlayer.start();
        } catch (Exception e) {
            Toast.makeText(this, "No alarm sound set :(", Toast.LENGTH_SHORT).show();
        }

        Button doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                finish();
            }
        });

        wakeLock.release();
    }


}





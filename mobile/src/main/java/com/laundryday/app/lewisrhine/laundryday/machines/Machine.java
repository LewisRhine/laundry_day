package com.laundryday.app.lewisrhine.laundryday.machines;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;
import android.widget.TextView;


public class Machine {

    public Intent intent;
    private Integer time;
    private Integer amount;
    private Double price;
    private boolean isRunning;
    private ImageView image;
    private int nonAnimated;
    private int animation;
    private AnimationDrawable animationDrawable;
    private TextView timerTextView;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (timerTextView != null) {
                updateUI(intent);
            }
        }
    };
    private Context context;
    private Boolean isReceiverReceiving = false;
    private String BROADCAST_ACTION;

    public Machine(Integer time, double price, Context context, Class alarmService) {
        this.time = time;
        this.price = price;
        this.context = context;
        this.BROADCAST_ACTION = alarmService.toString();

        intent = new Intent(context, alarmService);
        intent.putExtra("startTime", time);
    }

    //Drawables are separated out from the constructor in case you ever need a machine with out ui elements...
    //Don't know why you ever would but you know, just in case.
    public void setUIElements(ImageView image, int nonAnimated, int animation, TextView timerTextView) {
        this.image = image;
        this.nonAnimated = nonAnimated;
        this.animation = animation;
        this.timerTextView = timerTextView;
    }

    public void start() {
        isRunning = true;
        timerStart();
        if (image != null) {
            animationStart();
        }
    }

    public void registerReceiver() {
        context.registerReceiver(broadcastReceiver, new IntentFilter(BROADCAST_ACTION));
        isReceiverReceiving = true;
    }

    public void unregisterReceiver() {
        if (isReceiverReceiving) {
            context.unregisterReceiver(broadcastReceiver);
            isReceiverReceiving = false;
        }
    }

    private void updateUI(Intent intent) {
        Long countDownTime = intent.getLongExtra("timeLeft", 0);

        if (countDownTime > 0) {
            timerTextView.setText(countDownTime.toString());
        } else {
            animationStop();
            timerTextView.setText(time.toString());
            isRunning = false;
        }
    }

    public void stop() {
        timerStop();
        if (image != null) {
            animationStop();
        }
        isRunning = false;
    }

    private void timerStart() {
        context.startService(intent);
        registerReceiver();
    }

    private void timerStop() {
        context.stopService(intent);
        timerTextView.setText(time.toString());
    }

    public void animationStart() {
        image.setBackgroundResource(animation);
        animationDrawable = (AnimationDrawable) image.getBackground();
        animationDrawable.start();
    }

    public void animationStop() {
        if (animationDrawable != null) {
            animationDrawable.stop();
        }
        image.setBackgroundResource(nonAnimated);

    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
}


















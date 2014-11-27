package com.laundryday.app.lewisrhine.laundryday;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.laundryday.app.lewisrhine.laundryday.alarmservices.DryerAlarmService;
import com.laundryday.app.lewisrhine.laundryday.alarmservices.WasherAlarmService;
import com.laundryday.app.lewisrhine.laundryday.machines.Machine;
import com.shamanland.fab.FloatingActionButton;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class TimerFragment extends Fragment implements View.OnClickListener {

    //bundle id Strings
    private static final String WASHER_TIME = "washerTimePram";
    private static final String WASHER_COST = "washerCostPram";
    private static final String WASHER_AMOUNT = "washerAmount";

    private static final String DRYER_TIME = "dryerTimePram";
    private static final String DRYER_COST = "dryerCostPram";
    private static final String DRYER_AMOUNT = "dryer_amount";

    private static final String CARD_BALANCE = "cardBalancePram";

    private OnFragmentInteractionListener mListener;

    private Integer loadAmount = 0;

    private Boolean isCycleSet;

    private Integer timeToAdd;

    private Integer washerTime;
    private Double washerCost;
    private Boolean washerIsRunning;
    private Integer washerAmount = 1;

    private Integer dryerTime;
    private Double dryerCost;
    private Boolean dryerIsRunning;
    private Integer dryerAmount = 1;

    private Machine washer;
    private Machine dryer;

    private Class washerAlarmService = WasherAlarmService.class;
    private Class dryerAlarmService = DryerAlarmService.class;


    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    private TextView washerCostText;
    private TextView washerTimerText;
    private TextView washerAmountText;

    private TextView dryerCostText;
    private TextView dryerTimerText;
    private TextView dryerAmountText;


    private TextView loadAmountNumber;
    private TextView doneByNumber;

    private TextView cardBalanceText;

    private CardView cycleCard;

    private CardView cardBalanceCard;

    private FloatingActionButton setLoadsButton;

    public TimerFragment() {
        // Required empty public constructor
    }

    public static TimerFragment newInstance(Double cardBalance, Integer washerTime, Double washerCost, Integer washerAmount, Integer dryerTime, Double dryerCost, Integer dryerAmount) {
        TimerFragment fragment = new TimerFragment();
        Bundle args = new Bundle();
        args.putDouble(CARD_BALANCE, cardBalance);
        args.putInt(WASHER_TIME, washerTime);
        args.putDouble(WASHER_COST, washerCost);
        args.putInt(WASHER_AMOUNT, washerAmount);
        args.putInt(DRYER_TIME, dryerTime);
        args.putDouble(DRYER_COST, dryerCost);
        args.putInt(DRYER_AMOUNT, dryerAmount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            washerTime = getArguments().getInt(WASHER_TIME);
            washerCost = getArguments().getDouble(WASHER_COST);
            washerAmount = getArguments().getInt(WASHER_AMOUNT);
            dryerTime = getArguments().getInt(DRYER_TIME);
            dryerCost = getArguments().getDouble(DRYER_COST);
            dryerAmount = getArguments().getInt(DRYER_AMOUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timer, container, false);

        setLoadsButton = (FloatingActionButton) rootView.findViewById(R.id.setLoadsButton);
        setLoadsButton.setOnClickListener(this);

        loadAmountNumber = (TextView) rootView.findViewById(R.id.loadsAmountNumber);

        doneByNumber = (TextView) rootView.findViewById(R.id.doneByNumber);

        cardBalanceCard = (CardView) rootView.findViewById(R.id.cardBalanceCard);
        cardBalanceCard.setOnClickListener(this);


        cycleCard = (CardView) rootView.findViewById(R.id.cycleCard);
        cycleCard.setOnClickListener(this);

        ImageView washerImage = (ImageView) rootView.findViewById(R.id.washerImage);
        washerTimerText = (TextView) rootView.findViewById(R.id.washerTimerText);
        washerAmountText = (TextView) rootView.findViewById(R.id.washerAmountText);
        washerCostText = (TextView) rootView.findViewById(R.id.washerCostText);

        ImageView washerButton = (ImageView) rootView.findViewById(R.id.washerButton);

        ImageView dryerImage = (ImageView) rootView.findViewById(R.id.dryerImage);
        dryerTimerText = (TextView) rootView.findViewById(R.id.dryerTimerText);
        dryerAmountText = (TextView) rootView.findViewById(R.id.dryerAmountText);
        dryerCostText = (TextView) rootView.findViewById(R.id.dryerCostText);

        ImageView dryerButton = (ImageView) rootView.findViewById(R.id.dryerButton);

        cardBalanceText = (TextView) rootView.findViewById(R.id.cardBalanceNumber);

        washer = new Machine(washerTime, washerCost, getActivity(), washerAlarmService);
        washer.setUIElements(washerImage, R.drawable.ld_washer_dryer, R.drawable.washer_anim_list, washerTimerText);

        dryer = new Machine(dryerTime, dryerCost, getActivity(), dryerAlarmService);
        dryer.setUIElements(dryerImage, R.drawable.ld_washer_dryer, R.drawable.washer_anim_list, dryerTimerText);

        washerTimerText.setOnClickListener(this);
        washerButton.setOnClickListener(this);
        dryerTimerText.setOnClickListener(this);
        dryerButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onStart() {
        mListener.fragmentStarted();
        dryer.setRunning(dryerIsRunning);
        washer.setRunning(washerIsRunning);

        updateUI();
        startAnimations();

        if (dryerIsRunning) {
            dryer.animationStart();
        } else {
            dryer.animationStop();
        }

        if (washerIsRunning) {
            washer.animationStart();
        } else {
            washer.animationStop();
        }
        super.onStart();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("LR", "TimerFragment is attached");
        try {
            mListener = (OnFragmentInteractionListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onResume() {
        washer.registerReceiver();
        dryer.registerReceiver();
        super.onResume();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStop() {
        //unregister any receivers washer or dryer might have open
        washer.unregisterReceiver();
        dryer.unregisterReceiver();
        super.onStop();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.washerButton:
                if (!washer.isRunning()) {
                    washer.start();

                    mListener.subtractFromCard(washerCost * washerAmount);

                } else {
                    mListener.showAlertDialog("Stop Washer Alarm?", "washer");
                }
                break;
            case R.id.dryerButton:
                if (!dryer.isRunning()) {
                    dryer.start();

                    mListener.subtractFromCard(dryerCost * dryerAmount);
                    mListener.subtractFromLoads(dryerAmount);

                } else {
                    mListener.showAlertDialog("Stop Dryer Alarm?", "dryer");
                }
                break;
            case R.id.setLoadsButton:
                if (!isCycleSet) {
                    mListener.showSetLoadsFragment();
                } else {
                    mListener.showAlertDialog("Cancel This Cycle?", "cycleButton");
                }
                break;
            case R.id.cardBalanceCard:
                mListener.showAddToCardFragment();
                break;
            case R.id.cycleCard:
                mListener.showSetLoadsFragment();
                break;

        }
    }

    public void stopMachine(String id) {
        if (id.equals("washer")) {
            washer.stop();
            washerIsRunning = false;
        } else if (id.equals("dryer")) {
            dryer.stop();
            dryerIsRunning = false;
        } else if (id.equals("cycleButton")) {
            isCycleSet = false;
            updateUI();
        }
    }

    public void updateUI() {
        washerCostText.setText(decimalFormat.format(washerCost));
        washerAmountText.setText(washerAmount.toString());
        dryerCostText.setText(decimalFormat.format(dryerCost));
        dryerAmountText.setText(dryerAmount.toString());

        if (!washer.isRunning()) {
            washerTimerText.setText(washerTime.toString());
        } else {
            washerTimerText.setText(mListener.getTimeLeft("washer").toString());
        }

        if (!dryer.isRunning()) {
            dryerTimerText.setText(dryerTime.toString());
        } else {
            dryerTimerText.setText(mListener.getTimeLeft("dryer").toString());
        }

        if (isCycleSet && loadAmount > 0) {
            Animation slideUpAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
            cycleCard.startAnimation(slideUpAnimation);

            cycleCard.setVisibility(View.VISIBLE);
            loadAmountNumber.setText(loadAmount.toString());

            Calendar calendar = Calendar.getInstance();
            Calendar doneTime = (Calendar) calendar.clone();

            doneTime.add(Calendar.MINUTE, timeToAdd);

            //Set the format for the time to look
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm");

            doneByNumber.setText(simpleDateFormat.format(doneTime.getTime()));

        } else {
            cycleCard.setVisibility(View.GONE);
            isCycleSet = false;
        }

        cardBalanceText.setText(decimalFormat.format(mListener.getCardBalance()));
    }

    private void startAnimations() {
        Animation slideDownAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        Animation slideUpAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);

        setLoadsButton.startAnimation(slideUpAnimation);
        cardBalanceCard.startAnimation(slideDownAnimation);

    }

    public Integer getWasherAmount() {
        return washerAmount;
    }

    public void setWasherAmount(Integer washerAmount) {
        this.washerAmount = washerAmount;
    }

    public Integer getDryerAmount() {
        return dryerAmount;
    }

    public void setDryerAmount(Integer dryerAmount) {
        this.dryerAmount = dryerAmount;
    }

    public Integer getTimeToAdd() {
        return timeToAdd;
    }

    public void setTimeToAdd(Integer timeToAdd) {
        this.timeToAdd = timeToAdd;
    }

    public Integer getLoadAmount() {
        return loadAmount;
    }

    public void setLoadAmount(Integer loadAmount) {
        this.loadAmount = loadAmount;
    }

    public Boolean getIsCycleSet() {
        return isCycleSet;
    }

    public void setIsCycleSet(Boolean isCycleSet) {
        this.isCycleSet = isCycleSet;
    }

    public void setDryerCost(Double dryerCost) {
        this.dryerCost = dryerCost;
    }

    public void setDryerTime(Integer dryerTime) {
        this.dryerTime = dryerTime;
    }

    public void setWasherCost(Double washerCost) {
        this.washerCost = washerCost;
    }

    public void setWasherTime(Integer washerTime) {
        this.washerTime = washerTime;
    }

    public void setDryerIsRunning(Boolean dryerIsRunning) {
        this.dryerIsRunning = dryerIsRunning;
    }

    public void setWasherIsRunning(Boolean washerIsRunning) {
        this.washerIsRunning = washerIsRunning;
    }

    public boolean washerIsRunning() {
        return washer.isRunning();
    }

    public boolean dryerIsRunning() {
        return dryer.isRunning();
    }

    public interface OnFragmentInteractionListener {
        public Double getCardBalance();

        public void subtractFromCard(Double amount);

        public void subtractFromLoads(Integer dryerAmount);

        public Long getTimeLeft(String nameID);

        public void fragmentStarted();

        public void showSetLoadsFragment();

        public void showAddToCardFragment();

        public void showAlertDialog(String title, String ID);
    }
}

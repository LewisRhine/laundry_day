package com.laundryday.app.lewisrhine.laundryday;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class SetLoadFragment extends DialogFragment implements View.OnClickListener {

    private static final String WASHER_TIME = "washer_time";
    private static final String WASHER_COST = "washer_cost";
    private static final String WASHER_AMOUNT = "washer_amount";
    private static final String DRYER_TIME = "dryer_time";
    private static final String DRYER_COST = "dryer_cost";
    private static final String DRYER_AMOUNT = "dryer_amount";

    private OnFragmentInteractionListener mListener;


    private String[] amountArray;

    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    private TextView costToRun;
    private TextView doneBy;
    private Integer washerTime;
    private Double washerCost;
    private Integer washerAmount = 1;
    private Integer dryerTime;
    private Double dryerCost;
    private Integer dryerAmount = 1;

    private Integer timeToAdd;

    private Integer loads = 1;


    public SetLoadFragment() {
        // Required empty public constructor
    }

    public static SetLoadFragment newInstance(Integer washerTime, Double washerCost, Integer washerAmount, Integer dryerTime, Double dryerCost, Integer dryerAmount) {
        SetLoadFragment fragment = new SetLoadFragment();
        Bundle args = new Bundle();
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
        View rootView = inflater.inflate(R.layout.fragment_set_load, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        NumberPicker washerNumberPicker = (NumberPicker) rootView.findViewById(R.id.washerNumberPicker);
        washerNumberPicker.setMaxValue(amountArray.length - 1);
        washerNumberPicker.setMinValue(0);
        washerNumberPicker.setDisplayedValues(amountArray);
        washerNumberPicker.setValue(washerAmount - 1);
        washerNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                washerAmount = Integer.parseInt(amountArray[newVal]);

            }
        });

        NumberPicker dryerNumberPicker = (NumberPicker) rootView.findViewById(R.id.dryerNumberPicker);
        dryerNumberPicker.setMaxValue(amountArray.length - 1);
        dryerNumberPicker.setMinValue(0);
        dryerNumberPicker.setDisplayedValues(amountArray);
        dryerNumberPicker.setValue(dryerAmount - 1);
        dryerNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                dryerAmount = Integer.parseInt(amountArray[newVal]);
                setEstimations();

            }
        });

        NumberPicker loadsNumberPicker = (NumberPicker) rootView.findViewById(R.id.loadsNumberPicker);
        loadsNumberPicker.setMaxValue(amountArray.length - 1);
        loadsNumberPicker.setMinValue(0);
        loadsNumberPicker.setDisplayedValues(amountArray);
        loadsNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                loads = Integer.parseInt(amountArray[newVal]);
                setEstimations();

            }
        });

        costToRun = (TextView) rootView.findViewById(R.id.costNumber);
        doneBy = (TextView) rootView.findViewById(R.id.doneByNumber);

        setEstimations();


        Button setButton = (Button) rootView.findViewById(R.id.setLoadsSetButton);
        setButton.setOnClickListener(this);

        Button cancelButton = (Button) rootView.findViewById(R.id.setLoadsCancelButton);
        cancelButton.setOnClickListener(this);

        return rootView;
    }

    private void setEstimations() {
        costToRun.setText(decimalFormat.format(costToRun()));
        doneBy.setText(doneBy());
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        amountArray = getActivity().getResources().getStringArray(R.array.pref_dryer_amount);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private Double costToRun() {
        return (washerCost + dryerCost) * loads;
    }

    private String doneBy() {

        //Set the number we want to add to the current time.
        Integer setDryerTime = (dryerTime * loads) / dryerAmount;
        //Make sure we never have setDryerTime set to under dryerTime
        if (setDryerTime < dryerTime) {
            setDryerTime = dryerTime;
        }

        timeToAdd = setDryerTime + washerTime + 15;

        Calendar calendar = Calendar.getInstance();
        Calendar doneTime = (Calendar) calendar.clone();

        doneTime.add(Calendar.MINUTE, timeToAdd);

        //Set the format for the time to look
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm");

        return simpleDateFormat.format(doneTime.getTime());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setLoadsSetButton:
                mListener.setLoadsSetButtonClicked(washerAmount, dryerAmount, loads, timeToAdd);
                break;
            case R.id.setLoadsCancelButton:
                mListener.setLoadsCancelButtonClicked();
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        public void setLoadsSetButtonClicked(Integer washerAmount, Integer dryerAmount, Integer loads, Integer timeToAdd);

        public void setLoadsCancelButtonClicked();
    }
}

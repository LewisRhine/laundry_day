package com.laundryday.app.lewisrhine.laundryday;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import java.text.DecimalFormat;


public class AddToCardFragment extends DialogFragment implements View.OnClickListener {

    private static final String CARD_BALANCE = "cardBalance";

    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    private Double cardBalance;
    private EditText exactAmount;
    private OnFragmentInteractionListener mListener;


    // Required empty public constructor
    public AddToCardFragment() {
    }

    public static AddToCardFragment newInstance(Double cardBalance) {
        AddToCardFragment fragment = new AddToCardFragment();
        Bundle args = new Bundle();
        args.putDouble(CARD_BALANCE, cardBalance);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cardBalance = getArguments().getDouble(CARD_BALANCE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_to_card, container, false);

        getDialog().getWindow().setGravity(Gravity.CENTER);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        exactAmount = (EditText) rootView.findViewById(R.id.exactAmount);
        updateCardBalanceUI();
        exactAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setCardBalance();
            }
        });

        Button addFiveButton = (Button) rootView.findViewById(R.id.addFiveButton);
        addFiveButton.setOnClickListener(this);

        Button addTenButton = (Button) rootView.findViewById(R.id.addTenButton);
        addTenButton.setOnClickListener(this);

        Button addTwentyButton = (Button) rootView.findViewById(R.id.addTwentyButton);
        addTwentyButton.setOnClickListener(this);

        Button setButton = (Button) rootView.findViewById(R.id.setButton);
        setButton.setOnClickListener(this);

        Button cancelButton = (Button) rootView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onStart() {
        updateCardBalanceUI();
        super.onStart();
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addFiveButton:
                addButtonClicked(5d);
                updateCardBalanceUI();
                break;
            case R.id.addTenButton:
                addButtonClicked(10d);
                updateCardBalanceUI();
                break;
            case R.id.addTwentyButton:
                addButtonClicked(20d);
                updateCardBalanceUI();
                break;
            case R.id.setButton:
                setCardBalance();
                mListener.setButtonClicked(cardBalance);
                break;
            case R.id.cancelButton:
                mListener.cancelButtonClicked();
                break;
        }
    }

    private void addButtonClicked(Double amount) {
        cardBalance += amount;
        updateCardBalanceUI();
    }

    private void setCardBalance() {
        if (exactAmount.getText().toString().equals("")) {
            cardBalance = 0.00;
        } else {
            cardBalance = Double.parseDouble(exactAmount.getText().toString());
        }
    }

    //Update the ui to show new card balance value
    private void updateCardBalanceUI() {
        exactAmount.setText(decimalFormat.format(cardBalance));
    }

    public interface OnFragmentInteractionListener {
        public void setButtonClicked(Double amount);

        public void cancelButtonClicked();
    }

}

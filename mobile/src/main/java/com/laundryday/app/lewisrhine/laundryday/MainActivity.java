package com.laundryday.app.lewisrhine.laundryday;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;


public class MainActivity extends ActionBarActivity
        implements TimerFragment.OnFragmentInteractionListener, NavigationDrawerFragment.NavigationDrawerCallbacks, SetLoadFragment.OnFragmentInteractionListener, AddToCardFragment.OnFragmentInteractionListener {

    //using singleton for card object for obvious reasons
    private static Card card = new Card();
    private TimerFragment timerFragment;
    private SharedPreferences sharedPreferences;
    private Double cardBalance = 1.12;
    private Boolean isCycleSet;
    private Integer timeToAdd;
    private Double washerCost;
    private Integer washerTime;
    private Integer washerAmount;
    private Double dryerCost;
    private Integer dryerTime;
    private Integer dryerAmount;
    private Integer loadAmount;
    private Boolean washerIsRunning;
    private Boolean dryerIsRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get saved settings
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadSettings();

        setContentView(R.layout.activity_main);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle("Laundry day");
        setSupportActionBar(toolbar);


        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(drawerToggle);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


    }

    @Override
    protected void onPause() {
        Log.d("LR", "MainActivity paused");
        saveSettings();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("LR", "MainActivity Resumed");
        loadSettings();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("LR", "MainAcitivy restarted");
        loadSettings();
    }

    @Override
    protected void onStart() {
        Log.d("LR", "MainAcriviy Started");
        loadSettings();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        Log.d("LR", "MainActivity destroyed");
        super.onDestroy();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        switch (position) {
            case 0:
                loadSettings();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, timerFragment = TimerFragment.newInstance(cardBalance, washerTime, washerCost, washerAmount, dryerTime, dryerCost, dryerAmount))
                        .commit();
                break;
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, SettingsFragment.newInstance())
                        .commit();
                break;
        }

    }


    @Override
    public Double getCardBalance() {
        return card.getCardBalance();
    }

    @Override
    public void subtractFromCard(Double amount) {
        card.subtractFromCard(amount);
        saveSettings();
        timerFragment.updateUI();
    }

    @Override
    public void subtractFromLoads(Integer dryerAmount) {
        timerFragment.setLoadAmount(timerFragment.getLoadAmount() - dryerAmount);
        saveSettings();
        timerFragment.updateUI();
    }


    @Override
    public Long getTimeLeft(String nameID) {
        //get time left for prefs by using the nameID string passed
        return sharedPreferences.getLong(nameID + "_time_left", 0l);
    }

    @Override
    public void fragmentStarted() {
        //when TimerFragments starts tell it if the washer and dryer are running.
        timerFragment.setDryerIsRunning(dryerIsRunning);
        timerFragment.setWasherIsRunning(washerIsRunning);
        timerFragment.setIsCycleSet(isCycleSet);
        timerFragment.setLoadAmount(loadAmount);
        timerFragment.setTimeToAdd(timeToAdd);
        loadSettings();

    }

    @Override
    public void showSetLoadsFragment() {

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag("setLoads");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }

        // Create and show the dialog.
        DialogFragment dialogFragment = SetLoadFragment.newInstance(washerTime, washerCost, washerAmount, dryerTime, dryerCost, dryerAmount);
        dialogFragment.show(fragmentTransaction, "setLoads");

    }

    @Override
    public void showAddToCardFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag("addToCard");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }

        DialogFragment dialogFragment = AddToCardFragment.newInstance(cardBalance);
        dialogFragment.show(fragmentTransaction, "addToCard");
    }


    @Override
    public void setButtonClicked(Double amount) {
        card.setCardBalance(amount);
        saveSettings();
        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("addToCard")).commit();
        timerFragment.updateUI();
    }

    @Override
    public void cancelButtonClicked() {
        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("addToCard")).commit();
    }

    @Override
    public void setLoadsSetButtonClicked(Integer washerAmount, Integer dryerAmount, Integer loads, Integer timeToAdd) {
        timerFragment.setLoadAmount(loads);
        timerFragment.setTimeToAdd(timeToAdd);
        timerFragment.setIsCycleSet(true);
        timerFragment.setWasherAmount(washerAmount);
        timerFragment.setDryerAmount(dryerAmount);
        saveSettings();
        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("setLoads")).commit();
        timerFragment.updateUI();
    }

    @Override
    public void setLoadsCancelButtonClicked() {
        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("setLoads")).commit();

    }


    @Override
    public void showAlertDialog(String title, String ID) {
        DialogFragment newFragment = AlertDialogFragment.newInstance(title, ID);
        newFragment.show(getFragmentManager(), "alertDialog");
    }

    public void doPositiveClick(String ID) {
        timerFragment.stopMachine(ID);
        saveSettings();
    }

    public void doNegativeClick() {
    }

    private void saveSettings() {
        sharedPreferences.edit().putBoolean("washer_running", timerFragment.washerIsRunning()).apply();
        sharedPreferences.edit().putInt("washer_amount", timerFragment.getWasherAmount()).apply();
        sharedPreferences.edit().putBoolean("dryer_running", timerFragment.dryerIsRunning()).apply();
        sharedPreferences.edit().putInt("dryer_amount", timerFragment.getDryerAmount()).apply();
        sharedPreferences.edit().putString("card_balance", card.getCardBalance().toString()).apply();
        sharedPreferences.edit().putInt("load_amount", timerFragment.getLoadAmount()).apply();
        sharedPreferences.edit().putBoolean("is_cycle_set", timerFragment.getIsCycleSet()).apply();
        sharedPreferences.edit().putInt("time_to_add", timerFragment.getTimeToAdd()).apply();
        loadSettings();
    }

    public void loadSettings() {
        cardBalance = Double.parseDouble(sharedPreferences.getString("card_balance", "0.00"));
        //Set card balance after getting it for the settings
        card.setCardBalance(cardBalance);

        washerCost = Double.parseDouble(sharedPreferences.getString("washer_cost", "0.00"));
        washerTime = Integer.parseInt(sharedPreferences.getString("washer_time", "0"));
        //washerTimeLeft = sharedPreferences.getLong("washer_time_left", 0l);
        washerIsRunning = sharedPreferences.getBoolean("washer_running", false);
        washerAmount = sharedPreferences.getInt("washer_amount", 1);

        dryerCost = Double.parseDouble(sharedPreferences.getString("dryer_cost", "0.00"));
        dryerTime = Integer.parseInt(sharedPreferences.getString("dryer_time", "0"));
        //dryerTimeLeft = sharedPreferences.getLong("dryer_time_left", 0l);
        dryerIsRunning = sharedPreferences.getBoolean("dryer_running", false);
        dryerAmount = sharedPreferences.getInt("dryer_amount", 1);

        timeToAdd = sharedPreferences.getInt("time_to_add", 0);

        isCycleSet = sharedPreferences.getBoolean("is_cycle_set", false);

        loadAmount = sharedPreferences.getInt("load_amount", 1);

    }
}

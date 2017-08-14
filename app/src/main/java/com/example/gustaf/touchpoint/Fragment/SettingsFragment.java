package com.example.gustaf.touchpoint.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.gustaf.touchpoint.BaseActivity;
import com.example.gustaf.touchpoint.R;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * Class for the settingsfragment. This is where we choose the language of the app as well as the
 * max distance to locate cityobjects from.
 */
public class SettingsFragment extends Fragment {

    private View                            view;
    private Button                       swedish;
    private int                CITY_DIST = 50000;
    private Button                       english;
    private SeekBar                     distance;
    private TextView                distanceText;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Inflates the settings view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_settings, container, false);
       findViewsById();

        setupCityDistance();
        setupLanguageSelection();

        return view;
    }

    /**
     * Function to setup the Buttons for language selection.
     */
    public void setupLanguageSelection() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("selectedLanguage", Context.MODE_PRIVATE);
        String pine = sharedPreferences.getString("language","");

        if (pine.equals("sv")){
            swedish.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            english.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
            swedish.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
            english.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            english.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setLocale("en");

                }
            });
        }
        else{
            swedish.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
            english.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            swedish.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            english.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
            swedish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setLocale("sv");
                }
            });

        }
    }

    /**
     * Function to setup the Seekbar for changing the city radius.
     */
    public void setupCityDistance(){
        SharedPreferences cityDistance = getActivity().getSharedPreferences("cityDistance", Context.MODE_PRIVATE);
        int mProgress = cityDistance.getInt("seekBarProgress", 50000);
        distance.setMax(45000);
        distance.setProgress(mProgress-5000);

        distanceText.setText(String.format(getString(R.string.length_kilometers), mProgress/1000));
        distance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                CITY_DIST = progress+5000;

                distanceText.setText(String.format(getString(R.string.length_kilometers), CITY_DIST/1000));



            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setCITY_DIST();
            }
        });
    }

    /**
     * Function to cluster the findviewsbyId.
     */
    public void findViewsById() {
        swedish = (Button) view.findViewById(R.id.swedish);
        english = (Button) view.findViewById(R.id.english);
        distance = (SeekBar) view.findViewById(R.id.searchingdistance);
        distanceText = (TextView) view.findViewById(R.id.searchingdistancetext);
    }

    /**
     * Function to hold the chosen radius. Sends this to BaseActivity for update.
     */
    public void setCITY_DIST() {
        SharedPreferences mSharedPrefs = getActivity().getSharedPreferences("cityDistance", MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPrefs.edit();
        mEditor.putInt("seekBarProgress", CITY_DIST).apply();
        BaseActivity base = (BaseActivity) getActivity();
        base.refreshActivity();
    }

    /**
     * Function to hold the chosen language. Sends this to BaseActivity for update.
     * @param lang language
     */
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        SharedPreferences ensharedPreferences = getActivity().getSharedPreferences("selectedLanguage", MODE_PRIVATE);
        SharedPreferences.Editor eneditor = ensharedPreferences.edit();
        eneditor.putString("language", lang);
        eneditor.apply();
        BaseActivity base = (BaseActivity) getActivity();
        base.refreshActivity();
    }

}

package com.example.gustaf.touchpoint.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    View view;
    Button swedish;
    int CITY_DIST = 50000;
    Button english;
    SeekBar distance;
    TextView distanceText;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_settings, container, false);
       findViewsById();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("selectedLanguage", Context.MODE_PRIVATE);
        String pine = sharedPreferences.getString("language","");
        SharedPreferences cityDistance = getActivity().getSharedPreferences("cityDistance", Context.MODE_PRIVATE);
        int mProgress = cityDistance.getInt("seekBarProgress", 0);
        distance.setMax(45000);
        distance.setProgress(mProgress-5000);
        distanceText.setText(String.valueOf(mProgress/1000)+"KM");
        distance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    CITY_DIST = progress+5000;

                    int temp = (CITY_DIST/1000);
                    distanceText.setText(String.valueOf(temp)+"KM");



            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setCITY_DIST(CITY_DIST);
            }
        });

        if (pine.equals("sv")){
            swedish.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            english.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            swedish.setTextColor(getResources().getColor(R.color.colorWhite));
            english.setTextColor(getResources().getColor(R.color.colorPrimary));
            english.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setLocale("en");

                }
            });


        }
        else{
            swedish.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            english.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            swedish.setTextColor(getResources().getColor(R.color.colorPrimary));
            english.setTextColor(getResources().getColor(R.color.colorWhite));
            swedish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setLocale("sv");
                }
            });

        }





        return view;
    }
    public void findViewsById() {
        swedish = (Button) view.findViewById(R.id.swedish);
        english = (Button) view.findViewById(R.id.english);
        distance = (SeekBar) view.findViewById(R.id.searchingdistance);
        distanceText = (TextView) view.findViewById(R.id.searchingdistancetext);
    }

    public void setCITY_DIST(int progress) {
        SharedPreferences mSharedPrefs = getActivity().getSharedPreferences("cityDistance", MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPrefs.edit();
        mEditor.putInt("seekBarProgress", CITY_DIST).apply();
        BaseActivity base = (BaseActivity) getActivity();
        base.refreshActivity();
    }

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
        SharedPreferences pref = getActivity().getSharedPreferences(lang,MODE_PRIVATE);
        BaseActivity base = (BaseActivity) getActivity();
        base.refreshActivity();
    }

}

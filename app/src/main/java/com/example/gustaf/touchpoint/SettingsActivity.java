package com.example.gustaf.touchpoint;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsActivity extends Activity {

    private Toolbar toolbar;
    ImageView backButton;
    TextView toolbarText;
    Button swedish;
    Button english;




    public SettingsActivity() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViewsById();
        setToolbarTitle();
        inflateBackButton();

        SharedPreferences sharedPreferences = this.getSharedPreferences("selectedLanguage", Context.MODE_PRIVATE);
        String pine = sharedPreferences.getString("language","");


        boolean swedishIsSet = pine == "sv" ? true : false;

        if (swedishIsSet){
            swedish.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            english.setBackgroundColor(getResources().getColor(R.color.colorGreenPrimary));
            swedish.setTextColor(getResources().getColor(R.color.colorGreenPrimary));
            english.setTextColor(getResources().getColor(R.color.colorWhite));
        }
        else{
            swedish.setBackgroundColor(getResources().getColor(R.color.colorGreenPrimary));
            english.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            swedish.setTextColor(getResources().getColor(R.color.colorWhite));
            english.setTextColor(getResources().getColor(R.color.colorGreenPrimary));


        }


        swedish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setLocale("sv");
            }
        });
        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setLocale("en");

            }
        });
        // Inflate the layout for this fragment
    }

    public void findViewsById() {
        swedish = (Button) findViewById(R.id.swedish);
        english = (Button) findViewById(R.id.english);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        backButton = new ImageView(this);
    }
    public void setToolbarTitle() {
        toolbarText = (TextView) findViewById(R.id.toolbar_title);
        toolbarText.setText(R.string.settings_name);
    }

    public void inflateBackButton() {
        backButton.setImageResource(R.drawable.ic_arrow_back);
        backButton.setColorFilter(getResources().getColor(R.color.colorPrimary));
        backButton.setLayoutParams(new Toolbar.LayoutParams(70,70, Gravity.START));

        toolbar.addView(backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.this.finish();

            }
        });
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        SharedPreferences ensharedPreferences = getSharedPreferences("selectedLanguage", Context.MODE_PRIVATE);
        SharedPreferences.Editor eneditor = ensharedPreferences.edit();
        eneditor.putString("language", lang);
        eneditor.apply();
        SharedPreferences pref = getSharedPreferences(lang,MODE_PRIVATE);
        Intent refresh = new Intent(this, BaseActivity.class);
        SettingsActivity.this.finish();
        startActivity(refresh);
        overridePendingTransition(0,0);
    }
}

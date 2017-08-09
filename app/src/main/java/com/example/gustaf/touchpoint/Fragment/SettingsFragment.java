package com.example.gustaf.touchpoint.Fragment;


import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gustaf.touchpoint.BaseActivity;
import com.example.gustaf.touchpoint.R;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private Toolbar toolbar;
    private View view;
    ImageView backButton;
    TextView toolbarText;
    Button swedish;
    Button english;



    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        findViewsById();
        setToolbarTitle();
        inflateBackButton();

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
        return view;
    }

    public void findViewsById() {
        swedish = (Button) view.findViewById(R.id.swedish);
        english = (Button) view.findViewById(R.id.english);
        toolbar = (Toolbar) view.findViewById(R.id.main_toolbar);
        backButton = new ImageView(getContext());
        toolbarText = (TextView) view.findViewById(R.id.toolbar_title);
    }
    public void setToolbarTitle() {
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
                FragmentManager fm = getActivity().getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
            }
        });
    }

   /* public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.english:
                if (checked)
                    setLocale("en");
                    break;
            case R.id.swedish:
                if (checked)
                    setLocale("sv");
                    break;
        }
    }*/
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(getContext(), BaseActivity.class);
        startActivity(refresh);
    }

}

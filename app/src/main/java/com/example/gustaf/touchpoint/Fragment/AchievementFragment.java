package com.example.gustaf.touchpoint.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.gustaf.touchpoint.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AchievementFragment extends Fragment {

    Button settings;
    FrameLayout container;
    View view;

    public AchievementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_achievement, container, false);
        container = (FrameLayout) view.findViewById(R.id.achievementcontainer);
        settings = new Button(getContext());
        settings.setBackgroundResource(R.color.colorGreenPrimary);
        settings.setText(getContext().getString(R.string.settings_name));
        settings.setTextColor(getResources().getColor(R.color.colorWhite));

        settings.setLayoutParams(new FrameLayout.LayoutParams(400,150, Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL));

        container.addView(settings);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsFragment settingsFragment= new SettingsFragment();
                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, settingsFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

}

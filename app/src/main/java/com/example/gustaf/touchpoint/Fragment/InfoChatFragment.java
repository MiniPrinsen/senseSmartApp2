package com.example.gustaf.touchpoint.Fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gustaf.touchpoint.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoChatFragment extends Fragment {

    ImageView backButton;
    View view;
    protected Toolbar toolbar;

    public InfoChatFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_info_chat, container, false);

        backButton = new ImageView(getContext());
        toolbar = (Toolbar) view.findViewById(R.id.main_toolbar);
        int color = Color.parseColor("#51ACC7");
        backButton.setColorFilter(color);
        backButton.setImageResource(R.drawable.ic_arrow_back);
        backButton.setLayoutParams(new Toolbar.LayoutParams(70,70, Gravity.START));
        setToolBarTitle("INFO",view);

        toolbar.addView(backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragment();
            }
        });
        return view;
    }
    public void removeFragment(){
        getActivity().getSupportFragmentManager().popBackStack();
        //getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
    protected void setToolBarTitle(String title, View view){
        TextView toolbarText = (TextView)view.findViewById(R.id.toolbar_title);
        toolbarText.setText(title);
        toolbar.setTitle("");

    }
}



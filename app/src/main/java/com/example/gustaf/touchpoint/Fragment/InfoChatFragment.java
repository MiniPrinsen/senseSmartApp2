package com.example.gustaf.touchpoint.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

        return view;
    }
}



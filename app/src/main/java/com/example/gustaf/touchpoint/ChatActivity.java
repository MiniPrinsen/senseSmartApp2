package com.example.gustaf.touchpoint;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.example.gustaf.touchpoint.Adapters.ViewPagerAdapter;
import com.example.gustaf.touchpoint.Fragment.ChatWindowFragment;
import com.example.gustaf.touchpoint.Fragment.InfoChatFragment;
import com.example.gustaf.touchpoint.HelpClasses.CircleImageTransformation;
import com.example.gustaf.touchpoint.HelpClasses.NoSwipeViewPager;
import com.squareup.picasso.Picasso;

public class ChatActivity extends AppCompatActivity {

    private NoSwipeViewPager viewPagerDeafult;
    private ViewPagerAdapter viewPagerAdapterDeafult;
    private Toolbar toolbar;
    private ImageView backButton;
    private ImageView circleImage;
    private ImageView infoButton;
    private String circleString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        circleString = getIntent().getExtras().getString("cityobject");

        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        inflateToolbar();
        addPages();
        setOnClickListeners();
        circleImage.setTransitionName("chat_circle");
        TransitionInflater inflater = TransitionInflater.from(getApplicationContext());
        getWindow().setSharedElementEnterTransition(inflater.inflateTransition(android.R.transition.move).setDuration(400));
        getWindow().setSharedElementExitTransition(inflater.inflateTransition(android.R.transition.move));
        getWindow().setExitTransition(inflater.inflateTransition(android.R.transition.move).setDuration(400));
    }
    public void setOnClickListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionInflater inflater = TransitionInflater.from(getApplicationContext());
                getWindow().setExitTransition(inflater.inflateTransition(android.R.transition.move).setDuration(400));
                getWindow().setSharedElementExitTransition(inflater.inflateTransition(android.R.transition.move));
                ChatActivity.this.finish();
            }
        });
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPagerDeafult.setCurrentItem(1);
                hideKeyboard(ChatActivity.this);
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPagerDeafult.setCurrentItem(0);
                    }
                });
            }
        });

    }
    public void addPages(){

        viewPagerDeafult = (NoSwipeViewPager) findViewById(R.id.viewPager_deafultchat);
        viewPagerAdapterDeafult = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapterDeafult.addFragments(new ChatWindowFragment(), "CHAT");
        viewPagerAdapterDeafult.addFragments(new InfoChatFragment(), "INFO");
        viewPagerDeafult.setAdapter(viewPagerAdapterDeafult);
        viewPagerDeafult.setVisibility(View.VISIBLE);
    }
    public void inflateToolbar() {
        backButton = new ImageView(getApplicationContext());
        circleImage = new ImageView(getApplicationContext());
        infoButton = new ImageView(getApplicationContext());

        final int color = Color.parseColor("#51ACC7");

        backButton.setImageResource(R.drawable.ic_arrow_back);
        infoButton.setImageResource(R.drawable.ic_info);
        backButton.setColorFilter(color);
        infoButton.setColorFilter(color);

        Picasso.with(this).load(circleString).transform(new CircleImageTransformation())
                .into(circleImage, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {

                circleImage.setBackgroundColor(color);
                circleImage.setBackgroundColor(color);
                circleImage.setBackgroundResource(R.drawable.roundcorner);
            }

            @Override
            public void onError() {

            }
        });

        backButton.setLayoutParams(new Toolbar.LayoutParams(70,70, Gravity.START|Gravity.TOP));
        infoButton.setLayoutParams(new Toolbar.LayoutParams(80,80, Gravity.END|Gravity.TOP));
        circleImage.setLayoutParams(new Toolbar.LayoutParams(130,130, Gravity.CENTER_HORIZONTAL|Gravity.TOP));


        toolbar.addView(backButton);
        toolbar.addView(infoButton);
        toolbar.addView(circleImage);
    }
    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
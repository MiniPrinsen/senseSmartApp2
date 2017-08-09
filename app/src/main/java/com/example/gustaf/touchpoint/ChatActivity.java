package com.example.gustaf.touchpoint;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gustaf.touchpoint.Adapters.ViewPagerAdapter;
import com.example.gustaf.touchpoint.Fragment.ChatWindowFragment;
import com.example.gustaf.touchpoint.Fragment.InfoChatFragment;
import com.example.gustaf.touchpoint.HelpClasses.CircleImageTransformation;
import com.example.gustaf.touchpoint.HelpClasses.NoSwipeViewPager;
import com.squareup.picasso.Picasso;

public class ChatActivity extends AppCompatActivity {

    private NoSwipeViewPager viewPagerDeafult;
    private ViewPagerAdapter viewPagerAdapterDeafult;
    private AppBarLayout toolbar;
    private boolean keyBoardvisible = false;
    private LinearLayout linear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        String imageUrl = getIntent().getExtras().getString("cityobject");
        String name = getIntent().getExtras().getString("name");

        TextView toolbarName = (TextView)findViewById(R.id.name);
        toolbarName.setText(name);

        toolbar = (AppBarLayout) findViewById(R.id.toolbar);
        inflateToolbar(imageUrl);
        addPages();
        TransitionInflater inflater = TransitionInflater.from(getApplicationContext());
        getWindow().setSharedElementEnterTransition(inflater.inflateTransition(android.R.transition.move).setDuration(400));
        getWindow().setSharedElementExitTransition(inflater.inflateTransition(android.R.transition.move));
        getWindow().setExitTransition(inflater.inflateTransition(android.R.transition.move).setDuration(400));

         linear = (LinearLayout) findViewById(R.id.linear);
        linear.setVisibility(View.GONE);

        toolbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:

                        linear.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });


        Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        toolbar.startAnimation(slide);

        final View contentView = (View)findViewById(android.R.id.content);
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                contentView.getWindowVisibleDisplayFrame(r);
                int screenHeight = contentView.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    keyBoardvisible = true;
                }
                else {
                    keyBoardvisible = false;

                }
            }
        });
    }


    View.OnClickListener goBack = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!keyBoardvisible){
                finishAfterTransition();
            }
            else{
                hideKeyboard();
            }
        }
    };



    public void addPages(){
        viewPagerDeafult = (NoSwipeViewPager) findViewById(R.id.viewPager_deafultchat);
        viewPagerAdapterDeafult = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapterDeafult.addFragments(new ChatWindowFragment(), getApplicationContext().getString(R.string.chat_name));
        viewPagerAdapterDeafult.addFragments(new InfoChatFragment(), getApplicationContext().getString(R.string.info_name));
        viewPagerDeafult.setAdapter(viewPagerAdapterDeafult);
        viewPagerDeafult.setVisibility(View.VISIBLE);
    }
    public void inflateToolbar(String imageurl) {
        ImageView circleImage = (ImageView)findViewById(R.id.profilePicture);
        ImageView question = (ImageView) findViewById(R.id.infobutton);


        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linear.getVisibility() == View.VISIBLE) {
                    linear.setVisibility(View.GONE);
                }
                else {
                    linear.setVisibility(View.VISIBLE);
                }
            }
        });

        Picasso.with(this).load(imageurl).transform(new CircleImageTransformation())
                .into(circleImage);
        findViewById(R.id.backbutton).setOnClickListener(goBack);

    }



    public  void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) this).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    @Override
    public void finishAfterTransition(){
        Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        toolbar.startAnimation(slide);
        super.finishAfterTransition();

    }
}
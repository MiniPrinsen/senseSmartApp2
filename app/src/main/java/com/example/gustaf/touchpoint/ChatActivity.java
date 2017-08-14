package com.example.gustaf.touchpoint;

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
import com.example.gustaf.touchpoint.HelpClasses.CircleImageTransformation;
import com.example.gustaf.touchpoint.HelpClasses.NoSwipeViewPager;
import com.squareup.picasso.Picasso;

/**
 * ChatActivity is the activity that holds ChatFragment. ChatActivity itself
 * implements a ViewPager which holds the fragment and a custom toolbar.
 * The reason we have a ViewPager for 1 fragment is that we need an Activity to be able to do the
 * transition which shows when you press the circle on ChatFragment. The thing is, it is possible
 * to switch ChatWindowFragment to an Activity without a problem, but we didn't have time to do that
 */
public class ChatActivity extends AppCompatActivity {

    private AppBarLayout toolbar;
    private boolean keyBoardvisible = false;
    private LinearLayout exampleQuestions;

    /**
     * This is where we inflate the layout of ChatActivity. That includes the viewpager and toolbar.
     * We also set the transitions which are used from chatfragment to chatwindowfragment
     */
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

         exampleQuestions = (LinearLayout) findViewById(R.id.linear);
        exampleQuestions.setVisibility(View.GONE);

        //This is where we set the touch listener to the toolbar. If we swipe down, the toolbar
        //will slide down.
        toolbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:

                        exampleQuestions.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        //This is used to check if the keyboard is visible or not.
        final View contentView = findViewById(android.R.id.content);
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


    /**
     * Here we check if the keyboard is visible or not. If it is, the back button click is set
     * to just hide the keyboard. If it's not , we set the click to exit the activity.
     */
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

    /**
     * Simple function to hide the sampleQuestions.
     */
    public void hideInfo() {
        if(exampleQuestions.getVisibility() == View.VISIBLE) {
            exampleQuestions.setVisibility(View.GONE);
        }
    }


    /**
     * Here we inflate the ViewPager with the fragment.
     */
    public void addPages(){
        NoSwipeViewPager viewPagerDeafult = (NoSwipeViewPager) findViewById(R.id.viewPager_deafultchat);
        ViewPagerAdapter viewPagerAdapterDeafult = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapterDeafult.addFragments(new ChatWindowFragment(), getApplicationContext().getString(R.string.chat_name));
        viewPagerDeafult.setAdapter(viewPagerAdapterDeafult);
        viewPagerDeafult.setVisibility(View.VISIBLE);
    }

    /**
     * Here we inflate the custom toolbar. This includes the circleImage, sample questions,
     * backbutton and the title of the cityObject.
     * @param imageurl URL to the image which is shown as the small circle
     */
    public void inflateToolbar(String imageurl) {
        ImageView circleImage = (ImageView)findViewById(R.id.profilePicture);
        ImageView question = (ImageView) findViewById(R.id.infobutton);


        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exampleQuestions.getVisibility() == View.VISIBLE) {
                    exampleQuestions.setVisibility(View.GONE);

                }
                else {
                    exampleQuestions.setVisibility(View.VISIBLE);
                }
            }
        });

        Picasso.with(this).load(imageurl).transform(new CircleImageTransformation())
                .into(circleImage);
        findViewById(R.id.backbutton).setOnClickListener(goBack);

    }


    /**
     * Function to hide the keyboard.
     */
    public  void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = (this).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    /**
     * Function to tell the application to hide the activity after the transition is finished.
     */
    @Override
    public void finishAfterTransition(){
        Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        toolbar.startAnimation(slide);
        super.finishAfterTransition();

    }
}
package com.example.gustaf.touchpoint;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.gustaf.touchpoint.HelpClasses.BitmapLayout;
import com.example.gustaf.touchpoint.HelpClasses.Blur;
import com.example.gustaf.touchpoint.HelpClasses.CityObject;
import com.example.gustaf.touchpoint.HelpClasses.Coordinates;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * DetailsActivity is the activity which inflates the info page. We use this instead of a fragment
 * since we need the Activity to be able to do the animation we want.
 */
public class DetailsActivity extends AppCompatActivity {
    private CityObject              cityObject;
    private BitmapLayout            background;
    private Toolbar                 toolbar;
    private TextView                infoText;
    private ViewFlipper             flipper;
    private Button                  directions;
    private ScrollView              mScrollView;
    private Animation               fadein;
    private Animation               fadeout;

    /**
     * Inflating the layout of DetailsActivity. as it says in the comments, we inflate the toolbar
     * and starts the slideshow, sets the blurry background, adds parallax scrolling and defines
     * the shared transition.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        cityObject = getIntent().getParcelableExtra("cityobject");
        findViewsById();



        /* Set description, toolbartitle and slideshow images */
        infoText.setText(cityObject.getDescription());
        setToolbarTitle(cityObject.getName());
        createSlideShow(cityObject.getImgs());

        /* Set background */
        Picasso.with(getApplicationContext()).load(cityObject.getImgs().get(0)).transform(
                new Blur().getTransformation(getApplicationContext(), cityObject.getName())).into(background);
        /* Set parallel scroll */
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ScrollPositionObserver());

        /* Set shared element transition */
        TransitionInflater inflater = TransitionInflater.from(getApplicationContext());
        getWindow().setSharedElementEnterTransition(inflater.inflateTransition(android.R.transition.move).setDuration(300));
        getWindow().setSharedElementExitTransition(inflater.inflateTransition(android.R.transition.move).setDuration(300));

        setBackButton();
        //If the cityObject you click to read about is online, we set the "directions" button
        // to show "chat" instead.
        if(cityObject.isOnline()) {
            directions.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGreenPrimary));
            directions.setText(getApplicationContext().getString(R.string.chat_name));
            directions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                    i.putExtra("cityobject",cityObject.getImgs().get(0));
                    i.putExtra("name", cityObject.getName());
                    startActivity(i);
                }
            });
        }
        // Sets the button to "directions" and adds the google maps URL to the right position.
        // We are also adding the "are you sure" popup to make sure the user didn't press wrong.
        else {
            directions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = getApplicationContext().getString(R.string.directions_info);
                    new AlertDialog.Builder(DetailsActivity.this)
                            .setTitle(getApplicationContext().getString(R.string.open_google))
                            .setMessage(String.format(message, cityObject.getName()+"?"))
                            .setIcon(R.drawable.ic_google_maps)
                            .setNegativeButton(android.R.string.cancel, null) // dismisses by default
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialog, int which) {
                                    //String test = "https://www.google.com/maps?saddr=My+Location&daddr=%f,%f(%s)&travelmode=walking";
                                    String test = getString(R.string.directions_URL);
                                    Coordinates end = cityObject.getCoordinates();
                                    String name = cityObject.getName().replace(' ', '+');
                                    String urlString = String.format(test, end.getLatitude(), end.getLongitude(), name);

                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                                    startActivity(intent);
                                }
                            })
                            .create()
                            .show();
                }
            });
        }
    }


    /**
     * Function to cluster the findviewbyIDs.
     */
    private void findViewsById() {
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        fadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        fadeout = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
        directions = (Button) findViewById(R.id.directionsButton);
        flipper = (ViewFlipper) findViewById(R.id.slideShow);
        mScrollView = (ScrollView) findViewById(R.id.infofragment);
        infoText = (TextView) findViewById(R.id.infoText);
        background = (BitmapLayout) findViewById(R.id.blurredBG);
    }

    /**
     * Adds all the images from the server for 1 cityObject into a viewFlipper. We then define the
     * viewflipper with the animation and time.
     * @param images ArrayList of the images for 1 specific cityObject
     */
    private void createSlideShow(ArrayList<String> images){
        for (String url : images) {
            ImageView slideImage = new ImageView(this);
            slideImage.setTransitionName("myImage");
            Picasso.with(getApplicationContext()).load(url).into(slideImage);
            slideImage.setScaleType(ImageView.ScaleType.FIT_XY);
            flipper.addView(slideImage);

        }

        flipper.setInAnimation(fadein);
        flipper.setOutAnimation(fadeout);
        flipper.setAutoStart(true);
        flipper.setFlipInterval(4000);
        flipper.startFlipping();

    }

    /**
     * Sets the title for the toolbar.
     * @param title title of the toolbar
     */
    private void setToolbarTitle(String title){
        TextView toolbarText = (TextView)findViewById(R.id.toolbar_title);
        toolbarText.setText(title.toUpperCase());
    }

    /**
     * This class is used to get the parallax scrolling effect. Parallax scrolling is
     * a feature to make the scrolling look more alive. The whole point of it is to make the
     * image which will get the effect scroll at half of the speed the rest of the page is
     * scrolling.
     */
    private class ScrollPositionObserver implements ViewTreeObserver.OnScrollChangedListener {

        private final int mImageViewHeight;

        /**
         * checks the height of the photo.
         */
        private ScrollPositionObserver() {
            mImageViewHeight = getResources().getDimensionPixelSize(R.dimen.info_photo_height);
        }

        /**
         * Here we set the viewFlipper to scroll at half of the speed the rest is scrolling.
         */
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onScrollChanged() {
            int scrollY = Math.min(Math.max(mScrollView.getScrollY(), 0), mImageViewHeight);

            flipper.setTranslationY(scrollY / 2);

        }
    }

    /**
     * Inflates the back button. (parent) is used to increase the clickable area for the button
     * to be clicked.
     */
    private void setBackButton(){
        final ImageView backBtn = new ImageView(this);

        backBtn.setImageResource(R.drawable.ic_arrow_back);
        int color = Color.parseColor("#51ACC7");
        backBtn.setColorFilter(color);
        backBtn.setLayoutParams(new Toolbar.LayoutParams(70,70, Gravity.START));
        toolbar.addView(backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailsActivity.super.finishAfterTransition();
            }
        });

        View parent = findViewById(R.id.detailsView);
        parent.post(new Runnable() {
            public void run() {
                // Post in the parent's message queue to make sure the parent
                // lays out its children before we call getHitRect()
                Rect delegateArea = new Rect();
                backBtn.getHitRect(delegateArea);
                delegateArea.top -= 600;
                delegateArea.bottom += 300;
                delegateArea.left -= 600;
                delegateArea.right += 300;
                TouchDelegate expandedArea = new TouchDelegate(delegateArea,
                        backBtn);
                // give the delegate to an ancestor of the view we're
                // delegating the
                // area to
                if (View.class.isInstance(backBtn.getParent())) {
                    ((View) backBtn.getParent())
                            .setTouchDelegate(expandedArea);
                }
            }
        });

    }

}

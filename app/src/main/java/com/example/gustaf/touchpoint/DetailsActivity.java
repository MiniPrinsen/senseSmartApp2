package com.example.gustaf.touchpoint;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.gustaf.touchpoint.HelpClasses.BitmapLayout;
import com.example.gustaf.touchpoint.HelpClasses.Blur;
import com.example.gustaf.touchpoint.HelpClasses.CityObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {
    private CityObject              cityObject;
    private BitmapLayout            background;
    private Toolbar                 toolbar;
    private TextView                infoText;
    private ViewFlipper             flipper;
    private Animation               fadein, fadeout;
    private Button                  directions;
    private ScrollView              mScrollView;
    FrameLayout                     mWrapperFL;

    private String direction = "https://www.google.se/maps/dir/64.7449073,20.9557912/Lejonströmsbron," +
            "+931+44+Skellefteå/@64.7449891,20.914278,14z/" +
            "data=!3m1!4b1!4m9!4m8!1m1!4e1!1m5!1m1!1s0x467e954ff842f71f" +
            ":0x412452cb329526e!2m2!1d20.9157401!2d64.7510032?hl=sv";
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

        if(cityObject.isOnline()) {
            directions.setBackgroundColor(getResources().getColor(R.color.colorGreenPrimary));
            directions.setText("CHAT");
            directions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                    i.putExtra("cityobject",cityObject.getImgs().get(0));
                    startActivity(i);
                }
            });
        }
        else {
            directions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(direction));
                    startActivity(intent);
                }
            });
        }
    }


    public void findViewsById() {
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        directions = (Button) findViewById(R.id.directionsButton);
        flipper = (ViewFlipper) findViewById(R.id.slideShow);
        mScrollView = (ScrollView) findViewById(R.id.infofragment);
        mWrapperFL = (FrameLayout) findViewById(R.id.flWrapper);
        infoText = (TextView) findViewById(R.id.infoText);
        background = (BitmapLayout) findViewById(R.id.blurredBG);
    }

    private void createSlideShow(ArrayList<String> images){
    /*    for (String url : images) {
            ImageView slideImage = new ImageView(this);
            Picasso.with(getApplicationContext()).load(url).into(slideImage);
            slideImage.setScaleType(ImageView.ScaleType.FIT_XY);
            flipper.addView(slideImage);

        }
*/
        ImageView slideImage = new ImageView(this);
        slideImage.setTransitionName("myImage");
        Picasso.with(getApplicationContext()).load(images.get(0)).into(slideImage);
        slideImage.setScaleType(ImageView.ScaleType.FIT_XY);
        flipper.addView(slideImage);

       /* fadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        fadeout = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
        flipper.setInAnimation(fadein);
        flipper.setOutAnimation(fadeout);
        flipper.setAutoStart(true);
        flipper.setFlipInterval(3000);
        flipper.startFlipping();*/

    }

    public void setToolbarTitle(String title){
        TextView toolbarText = (TextView)findViewById(R.id.toolbar_title);
        toolbarText.setText(title.toUpperCase());
    }

    private class ScrollPositionObserver implements ViewTreeObserver.OnScrollChangedListener {

        private int mImageViewHeight;

        public ScrollPositionObserver() {
            mImageViewHeight = getResources().getDimensionPixelSize(R.dimen.info_photo_height);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onScrollChanged() {
            int scrollY = Math.min(Math.max(mScrollView.getScrollY(), 0), mImageViewHeight);

            // changing position of ImageView
            flipper.setTranslationY(scrollY / 2);

        }
    }

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
                ImageView delegate = backBtn;
                delegate.getHitRect(delegateArea);
                delegateArea.top -= 600;
                delegateArea.bottom += 300;
                delegateArea.left -= 600;
                delegateArea.right += 300;
                TouchDelegate expandedArea = new TouchDelegate(delegateArea,
                        delegate);
                // give the delegate to an ancestor of the view we're
                // delegating the
                // area to
                if (View.class.isInstance(delegate.getParent())) {
                    ((View) delegate.getParent())
                            .setTouchDelegate(expandedArea);
                }
            };
        });

    }

}

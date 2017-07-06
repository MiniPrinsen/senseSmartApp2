package com.example.gustaf.touchpoint.Fragment;


import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.gustaf.touchpoint.BaseActivity;
import com.example.gustaf.touchpoint.HelpClasses.Blur;
import com.example.gustaf.touchpoint.HelpClasses.CityObject;
import com.example.gustaf.touchpoint.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;


public class InfoFragment extends Fragment
{
    private CityObject cityObject;
    private ImageView background;
    private Toolbar toolbar;
    private TextView infoText;
    private String description;
    private View rootView;
    private ViewFlipper flipper;
    private Animation fadein, fadeout;
    private Button directions;
    private static String toolbarTitle;
    private ScrollView mScrollView;
    FrameLayout mWrapperFL;
    private String direction = "https://www.google.se/maps/dir/64.7449073,20.9557912/Lejonströmsbron," +
            "+931+44+Skellefteå/@64.7449891,20.914278,14z/" +
            "data=!3m1!4b1!4m9!4m8!1m1!4e1!1m5!1m1!1s0x467e954ff842f71f" +
            ":0x412452cb329526e!2m2!1d20.9157401!2d64.7510032?hl=sv";
    public InfoFragment() {

    }


    public static InfoFragment newInstance(String toolbar) {
        InfoFragment myFragment = new InfoFragment();

        Bundle args = new Bundle();
        args.putString("string", toolbar);
        myFragment.setArguments(args);
        toolbarTitle = toolbar;
        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_info, container, false);
        findViewsById();


        final BaseActivity activity = (BaseActivity) getContext();
        int position = getArguments().getInt("index");
        cityObject = activity.getCityObject(position);


        description = cityObject.getDescription();
        //Log.d("LÖV", description);
        infoText.setText(description);
        toolbarTitle = cityObject.getName();


        /* SET BACKGROUND */
        Picasso.with(getContext()).load(cityObject.getImgs().get(0)).transform(new Blur().getTransformation(getContext())).into(background);
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        Log.v("HEIGHT", height + " HOJD");
        background.setMinimumHeight(height);



        /*Picasso.with(getContext())
                .load(cityObject.getImgs().get(0))
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Log.v("BLUR", from.name());
                    //    background.setScaleType(ImageView.ScaleType.FIT_XY);
                        background.setImageBitmap(blr.blur(getContext(), bitmap));
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

        */
        slideShow();
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ScrollPositionObserver());
        final ImageView imgview = new ImageView(getContext());


        imgview.setImageResource(R.drawable.ic_arrow_back);
        int color = Color.parseColor("#51ACC7");
        imgview.setColorFilter(color);
        setToolbarTitle(toolbarTitle);
        imgview.setLayoutParams(new Toolbar.LayoutParams(70,70, Gravity.START));
        toolbar.addView(imgview);
        imgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           //     Intent i = new Intent(getContext(), BaseActivity.class);
              //  i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                //startActivity(i);
             //   rootView.setVisibility(View.INVISIBLE);
                removeFragment();
            }
        });
        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(direction));
                startActivity(intent);
            }
        });

        View parent = rootView.findViewById(R.id.infofragment);
        parent.post(new Runnable() {
            public void run() {
                // Post in the parent's message queue to make sure the parent
                // lays out its children before we call getHitRect()
                Rect delegateArea = new Rect();
                ImageView delegate = imgview;
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
        return rootView;
    }

    public void findViewsById() {
        toolbar = (Toolbar) rootView.findViewById(R.id.gustaf_toolbar);
        directions = (Button) rootView.findViewById(R.id.directionsButton);
        flipper = (ViewFlipper) rootView.findViewById(R.id.flipper);
        mScrollView = (ScrollView) rootView.findViewById(R.id.infofragment);
        mWrapperFL = (FrameLayout) rootView.findViewById(R.id.flWrapper);
        infoText = (TextView) rootView.findViewById(R.id.infoText);
        background = (ImageView)rootView.findViewById(R.id.blurbitmap);
    }

    public void removeFragment(){
       // getActivity().getSupportFragmentManager().popBackStack();
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
    public void slideShow() {
        ArrayList<String> imageUrls = cityObject.getImgs();
        for(String url : imageUrls){
            ImageView img = new ImageView(getContext());
            Picasso.with(getContext()).load(url).into(img);
            img.setScaleType(ImageView.ScaleType.FIT_XY);
            flipper.addView(img);
        }


        fadein = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        fadeout = AnimationUtils.loadAnimation(getContext(),R.anim.fade_out);
        flipper.setInAnimation(fadein);
        flipper.setOutAnimation(fadeout);
        flipper.setAutoStart(true);
        flipper.setFlipInterval(5000);
        flipper.startFlipping();
    }

    public void setToolbarTitle(String title){
        TextView toolbarText = (TextView)rootView.findViewById(R.id.toolbar_title);
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





}
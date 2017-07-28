package com.example.gustaf.touchpoint.Fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.gustaf.touchpoint.ChatActivity;
import com.example.gustaf.touchpoint.HelpClasses.Blur;
import com.example.gustaf.touchpoint.HelpClasses.CircleImageTransformation;
import com.example.gustaf.touchpoint.HelpClasses.CityObject;
import com.example.gustaf.touchpoint.R;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Picasso;

/**
 *
 */
public class ChatFragment extends Fragment {
    private View                rootView;
    private RippleBackground    rippleBackground;
    private ImageView           backgroundAnim;
    private ImageView           goToChatt2;
    private Boolean             firstTime = true;
    private Boolean             isShown = false;
    private GradientDrawable    drawable;
    private CityObject          closestCityObject;


    public ChatFragment() {
        // Required empty public constructor
    }
    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        rippleBackground = (RippleBackground) rootView.findViewById(R.id.content);
        goToChatt2 = (ImageView) rootView.findViewById(R.id.go_to_chat_btn2);
        goToChatt2.setOnTouchListener(onTouchListener);
        backgroundAnim = (ImageView) rootView.findViewById(R.id.backgroundImage);
        rippleBackground.startRippleAnimation();
        drawable = (GradientDrawable)goToChatt2.getBackground();
        drawable.setStroke(8, getResources().getColor(R.color.colorGreenPrimary));

        return rootView;
    }

    public View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN && !isShown) {
                Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_out_onpress);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        goToChatt2.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                anim.setFillAfter(true);
                goToChatt2.startAnimation(anim);


            } else if (event.getAction() == android.view.MotionEvent.ACTION_UP && !isShown) {
                Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in_onpress);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        goToChatt2.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                anim.setFillAfter(true);
                goToChatt2.startAnimation(anim);

            }
            return true;

        }
    };


    /**
     *
     */
    public void updateLocation(CityObject tPoint) {
        boolean newTouchPoint = !tPoint.equals(closestCityObject);
        if (newTouchPoint){
            Picasso.with(getContext()).load(tPoint.getImgs().get(0)).transform(new CircleImageTransformation()).into(goToChatt2);
        }
        closestCityObject = tPoint;

        if (tPoint.isOnline() && !isShown) {
            zomIn();
        }
        if (!tPoint.isOnline() && !firstTime) {
            zomOut();

        }

    }

    /**
     *
     */
    private View.OnClickListener chattObjectListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            Bundle args = new Bundle();
            args.putString("cityobject",closestCityObject.getImgs().get(0));

            Intent intent = new Intent(getContext(), ChatActivity.class);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),goToChatt2,ViewCompat.getTransitionName(goToChatt2));
            intent.putExtras(args);
            startActivity(intent, options.toBundle());

        }
    };


    public void zomIn(){
        Animation backgroundAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        final ImageView background = (ImageView)rootView.findViewById(R.id.backgroundImage);

        Blur b = new Blur();
        Picasso.with(getContext()).load(closestCityObject.getImgs().get(0)).transform(b.getTransformation(getContext(), closestCityObject.getName())).into(background);
        background.setVisibility(View.VISIBLE);

        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(goToChatt2, "scaleX", 1.9f).setDuration(1000);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(goToChatt2, "scaleY", 1.9f).setDuration(1000);

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleUpX).with(scaleUpY);

        scaleDown.start();


        isShown = true;
        firstTime = false;

        background.setVisibility(View.VISIBLE);

        rippleBackground.stopRippleAnimation();
        background.startAnimation(backgroundAnimation);

        drawable = (GradientDrawable)goToChatt2.getBackground();
        drawable.setStroke(8, getResources().getColor(R.color.colorGreenPrimary));
        goToChatt2.setClickable(true);
        goToChatt2.setOnClickListener(chattObjectListener);
    }

    public void zomOut(){


        final ImageView background = (ImageView)rootView.findViewById(R.id.backgroundImage);

        ObjectAnimator scaleBackX = ObjectAnimator.ofFloat(goToChatt2, "scaleX", 1.0f).setDuration(1000);
        ObjectAnimator scaleBackY = ObjectAnimator.ofFloat(goToChatt2, "scaleY", 1.0f).setDuration(1000);

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleBackX).with(scaleBackY);

        scaleDown.start();


        isShown = false;
        firstTime = true;


        rippleBackground.startRippleAnimation();

        drawable = (GradientDrawable)goToChatt2.getBackground();
        drawable.setStroke(8, getResources().getColor(R.color.colorWhite));
        goToChatt2.setClickable(false);
        goToChatt2.setOnClickListener(null);
        goToChatt2.setOnTouchListener(onTouchListener);

        final Animation backgroundAnimation2 = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        Animation fadeandZoomOut = AnimationUtils.loadAnimation(getContext(), R.anim.fadeoutzoomout);

        fadeandZoomOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }
            public void onAnimationRepeat(Animation animation) {
            }
            public void onAnimationEnd(Animation animation) {
                background.setVisibility(View.INVISIBLE);
                drawable.setVisible(true,true);
            }
        });
        backgroundAnimation2.setFillAfter(true);

        backgroundAnim.startAnimation(backgroundAnimation2);
    }

   /* public void zoomIn() {
        Animation backgroundAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        Animation resize = AnimationUtils.loadAnimation(getContext(), R.anim.fadeinzoomin);
        Animation hej = AnimationUtils.loadAnimation(getContext(), R.anim.fadeoutzoomin);

        final ImageView imgView = (ImageView)rootView.findViewById(R.id.backgroundImage);

        Blur b = new Blur();
            Picasso.with(getContext()).load(closestCityObject.getImgs().get(0)).transform(b.getTransformation(getContext(), closestCityObject.getName())).into(imgView);
        imgView.setVisibility(View.VISIBLE);

        resize.setFillAfter(true);
        resize.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                skabort.setVisibility(View.VISIBLE);
                isShown = true;
                firstTime = false;
                Picasso.with(getContext()).load(closestCityObject.getImgs().get(0)).transform(new CircleImageTransformation()).into(skabort);
                goToChatt2.setVisibility(View.INVISIBLE);
                drawable = (GradientDrawable) skabort.getBackground();
                drawable.setStroke(8, getResources().getColor(R.color.colorGreenPrimary));
            }
            public void onAnimationRepeat(Animation animation) {
            }
            public void onAnimationEnd(Animation animation) {
                skabort.setClickable(true);
                imgView.setVisibility(View.VISIBLE);
                goToChatt2.setVisibility(View.INVISIBLE);
            }
        });
        skabort.startAnimation(resize);
        goToChatt2.startAnimation(hej);
        backgroundAnim.startAnimation(backgroundAnimation);
        rippleBackground.stopRippleAnimation();
        skabort.setClickable(true);


    }

    public void zoomOut() {
        Animation fadeandZoomOut = AnimationUtils.loadAnimation(getContext(), R.anim.fadeoutzoomout);
        Animation fadeandZoomIn = AnimationUtils.loadAnimation(getContext(), R.anim.fadeinzoomout);
        final Animation backgroundAnimation2 = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        fadeandZoomOut.setFillAfter(true);
        rippleBackground.startRippleAnimation();


        fadeandZoomOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }
            public void onAnimationRepeat(Animation animation) {
            }
            public void onAnimationEnd(Animation animation) {
                goToChatt2.setVisibility(View.VISIBLE);
                backgroundAnim.setVisibility(View.INVISIBLE);
                drawable.setVisible(true,true);
            }
        });
        backgroundAnim.startAnimation(backgroundAnimation2);
        goToChatt2.startAnimation(fadeandZoomIn);
        isShown = false;
        firstTime = true;
    }*/

    /*public class CircleImageTransformation implements com.squareup.picasso.Transformation {

        @Override
        public Bitmap transform ( final Bitmap source ) {
            Blur br = new Blur();
            Bitmap output = getCircularBitmap(source);
            if (source != output) {
                source.recycle();
            }

            return output;
        }

        @Override
        public String key () {
            return "customTransformation" + "12412414";
        }

        public Bitmap getCircularBitmap(Bitmap bitmap) {
            Bitmap output;

            if (bitmap.getWidth() > bitmap.getHeight()) {
                output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            } else {
                output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            float r = 0;

            if (bitmap.getWidth() > bitmap.getHeight()) {
                r = bitmap.getHeight() / 2;
            } else {
                r = bitmap.getWidth() / 2;
            }

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle(r, r, r, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            return output;
        }
    }*/
}
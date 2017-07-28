package com.example.gustaf.touchpoint.Fragment;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

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
    private ImageView           goToChatt;
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
        goToChatt = (ImageView) rootView.findViewById(R.id.go_to_chat_btn);
        goToChatt2 = (ImageView) rootView.findViewById(R.id.go_to_chat_btn2);
        goToChatt.setOnClickListener(chattObjectListener);
        goToChatt2.setClickable(false);
        goToChatt2.setOnTouchListener(onTouchListener);
        backgroundAnim = (ImageView) rootView.findViewById(R.id.backgroundImage);
        assert goToChatt != null;
        rippleBackground.startRippleAnimation();

        return rootView;
    }

    public View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN && !isShown) {
                Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_out_onpress);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        goToChatt.setVisibility(View.INVISIBLE);
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
                        goToChatt.setVisibility(View.VISIBLE);

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
            zoomIn();
        }
        if (!tPoint.isOnline() && !firstTime) {
            zoomOut();

        }

    }

    /**
     *
     */
    private View.OnClickListener chattObjectListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            ChatWindowFragment chat = new ChatWindowFragment();
            Bundle args = new Bundle();
            args.putString("cityobject",closestCityObject.getImgs().get(0));
            FragmentManager fragManager = getActivity().getSupportFragmentManager();
            chat.setArguments(args);
            chat.setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.explode));
            chat.setEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.explode));
            //chat.setReenterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.change_size_transform));
            chat.setExitTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.explode));

            fragManager
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(android.R.id.content, chat)
                    .commit();

        }
    };

    /**
     *
     */
    public void zoomIn() {
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
                goToChatt.setVisibility(View.VISIBLE);
                isShown = true;
                firstTime = false;
                Picasso.with(getContext()).load(closestCityObject.getImgs().get(0)).transform(new CircleImageTransformation()).into(goToChatt);
                goToChatt2.setVisibility(View.INVISIBLE);
                drawable = (GradientDrawable) goToChatt.getBackground();
                drawable.setStroke(8, getResources().getColor(R.color.colorGreenPrimary));
            }
            public void onAnimationRepeat(Animation animation) {
            }
            public void onAnimationEnd(Animation animation) {
                goToChatt.setClickable(true);
                imgView.setVisibility(View.VISIBLE);
                goToChatt2.setVisibility(View.INVISIBLE);
            }
        });
        goToChatt.startAnimation(resize);
        goToChatt2.startAnimation(hej);
        backgroundAnim.startAnimation(backgroundAnimation);
        rippleBackground.stopRippleAnimation();
        goToChatt.setClickable(true);


    }

    public void zoomOut() {
        Animation fadeandZoomOut = AnimationUtils.loadAnimation(getContext(), R.anim.fadeoutzoomout);
        Animation fadeandZoomIn = AnimationUtils.loadAnimation(getContext(), R.anim.fadeinzoomout);
        final Animation backgroundAnimation2 = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        fadeandZoomOut.setFillAfter(true);
        rippleBackground.startRippleAnimation();
        goToChatt.setClickable(false);


        fadeandZoomOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }
            public void onAnimationRepeat(Animation animation) {
            }
            public void onAnimationEnd(Animation animation) {
                goToChatt2.setVisibility(View.VISIBLE);
                goToChatt.setVisibility(View.INVISIBLE);
                backgroundAnim.setVisibility(View.INVISIBLE);
                goToChatt.setClickable(false);
                drawable.setVisible(true,true);
            }
        });
        backgroundAnim.startAnimation(backgroundAnimation2);
        goToChatt.startAnimation(fadeandZoomOut);
        goToChatt2.startAnimation(fadeandZoomIn);
        isShown = false;
        firstTime = true;
    }

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
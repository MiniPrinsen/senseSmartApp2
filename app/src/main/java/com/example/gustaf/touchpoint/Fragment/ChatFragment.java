package com.example.gustaf.touchpoint.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.example.gustaf.touchpoint.HelpClasses.Blur;
import com.example.gustaf.touchpoint.HelpClasses.CityObject;
import com.example.gustaf.touchpoint.R;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 *
 */
public class ChatFragment extends Fragment {
    LruCache<Integer, Bitmap> blurredBitmaps;
    LruCache<Integer, Bitmap> circleBitmaps;

    /**

     */
    public ChatFragment() {
        // Required empty public constructor
    }
    View rootView;
    RippleBackground rippleBackground;
    ImageView backgroundAnim;
    ImageView goToChatt;
    ImageView goToChatt2;
    Location volleyboll;
    Boolean firstTime = true;
    Boolean isShown = false;
    int idOfBlurredImage;
    GradientDrawable drawable;
    CityObject closestCityObject;
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

        blurredBitmaps = new LruCache<>(4*60*60);





        goToChatt = (ImageView) rootView.findViewById(R.id.go_to_chat_btn);
        goToChatt2 = (ImageView) rootView.findViewById(R.id.go_to_chat_btn2);
        goToChatt.setOnClickListener(chattObjectListener);
        goToChatt2.setClickable(false);
        goToChatt2.setOnTouchListener(onTouchListener);
        backgroundAnim = (ImageView) rootView.findViewById(R.id.backgroundImage);
        assert goToChatt != null;
        //volleyboll.setLatitude(64.74512696);
        //volleyboll.setLongitude(20.9547472);

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

    static void startScaleAnimation(View v, ScaleAnimation scaleAnim, float pivotX, float pivotY){
        scaleAnim =
                new ScaleAnimation(1.0f, 5f, 1.0f, 5f,
                        ScaleAnimation.RELATIVE_TO_SELF, pivotX,
                        ScaleAnimation.RELATIVE_TO_SELF, pivotY);
        scaleAnim.setDuration(4000);

        v.startAnimation(scaleAnim);
    }
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
     * @param picture
     */
    public Bitmap blurImage(int picture) {
        Bitmap blurredImage = BitmapFactory.decodeResource(getResources(), picture);
        Blur br = new Blur();
        blurredImage = br.blurImage(getContext(), closestCityObject.getImage().get(0));//blurredImage, 0.2f, 70);
        return blurredImage;
    }
    /**
     *
     */
    private View.OnClickListener chattObjectListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            getFragmentManager().beginTransaction()
                    .add(android.R.id.content, new ChatWindowFragment()).commit();
        }
    };
    /**
     *
     * @param currentLocation
     * @param objectLocation
     * @param radius
     * @return
     */
    public static boolean inRadius(Location currentLocation, Location objectLocation, int radius) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(currentLocation.getLatitude() - objectLocation.getLatitude());
        double dLng = Math.toRadians(currentLocation.getLongitude() - objectLocation.getLongitude());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(objectLocation.getLatitude())) *
                        Math.cos(Math.toRadians(currentLocation.getLatitude())) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);
        if (dist <= radius) {
            return true;
        } else {
            return false;
        }
    }
    /**
     *
     */
    public void zoomIn() {
        Animation backgroundAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        Animation resize = AnimationUtils.loadAnimation(getContext(), R.anim.fadeinzoomin);
        Animation hej = AnimationUtils.loadAnimation(getContext(), R.anim.fadeoutzoomin);

        final ImageView imgView = (ImageView)rootView.findViewById(R.id.backgroundImage);


        if (blurredBitmaps.get(closestCityObject.getImage().get(0)) == null){
            Bitmap bmp = blurImage(closestCityObject.getImage().get(0));
            blurredBitmaps.put(closestCityObject.getImage().get(0), bmp);
            imgView.setImageBitmap(blurImage(closestCityObject.getImage().get(0)));
            idOfBlurredImage = closestCityObject.getImage().get(0);
        }
        else{
            imgView.setImageBitmap(blurredBitmaps.get(closestCityObject.getImage().get(0)));
        }

        imgView.setVisibility(View.VISIBLE);

        resize.setFillAfter(true);
        resize.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                goToChatt.setVisibility(View.VISIBLE);
                isShown = true;
                firstTime = false;
               // goToChatt.setImageBitmap(getCircularBitmap(closestCityObject.getImage().get(0)));
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

    /*private boolean hasImage(@NonNull ImageView view) {
        boolean hasImage = false;
        if (view != null){
            Drawable drawable = view.getDrawable();
            hasImage = (drawable != null);

            if (hasImage && (drawable instanceof BitmapDrawable)) {
                hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
            }

        }
        return hasImage;
    }*/

    /**
     *
     */
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



    public class CircleImageTransformation implements com.squareup.picasso.Transformation {

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
    }
}
package com.example.gustaf.touchpoint.Fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gustaf.touchpoint.ChatActivity;
import com.example.gustaf.touchpoint.HelpClasses.Blur;
import com.example.gustaf.touchpoint.HelpClasses.CircleImageTransformation;
import com.example.gustaf.touchpoint.HelpClasses.CityObject;
import com.example.gustaf.touchpoint.R;
import com.squareup.picasso.Picasso;

/**
 * ChatFragment is the middle page of the app. It is here we see what object is closest to us
 * and how close it is. When it goes online, we are using a zoom animation to make it look online.
 */
public class ChatFragment extends Fragment {
    private View                        rootView;
    private ImageView             backgroundAnim;
    private ImageView                circleImage;
    private Boolean             firstTime = true;
    private Boolean              isShown = false;
    private CityObject         closestCityObject;
    private LinearLayout          circleContainer;
    private Animation                       spin;
    private ProgressBar              progressbar;
    private ColorStateList              oldColors;

    public ChatFragment() {
        // Required empty public constructor
    }
    /**
     * Starting the animation and finds all the views.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_chat, container, false);
       // rippleBackground = (RippleBackground) rootView.findViewById(R.id.content);
        circleImage = (ImageView) rootView.findViewById(R.id.go_to_chat_btn2);
        circleImage.setOnClickListener(clickListener);
        circleContainer = (LinearLayout) rootView.findViewById(R.id.chat_content);
        //rotatingImage = (ImageView) rootView.findViewById(R.id.rotatingcircle);
        backgroundAnim = (ImageView) rootView.findViewById(R.id.backgroundImage);
        progressbar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        spin = AnimationUtils.loadAnimation(getContext(), R.anim.rotation);
        spin.setRepeatCount(Animation.INFINITE);
        progressbar.startAnimation(spin);
        //rotatingImage.startAnimation(spin);

        return rootView;
    }

    /**
     * Adds a clickListener on the circle when it is online. When the user clicks the circle,
     * it vibrates and shows a toast message for a little bit. This is used to tell the user
     * it is not close enough to talk to the object.
     */
    public View.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Animation vibrate = AnimationUtils.loadAnimation(getContext(), R.anim.vibrate);
            vibrate.setFillAfter(false);
            circleContainer.startAnimation(vibrate);

            TextView hej = new TextView(getContext());
            hej.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            hej.setText(getString(R.string.help_text));
            hej.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrayDark));
            hej.setTextSize(14);

                Toast toast = new Toast(getContext());
                toast.setGravity(Gravity.CENTER_HORIZONTAL,0,-500);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(hej);
                toast.show();
        }

    };

    /**
     * Function to check where we are and if we need to change it to online/if it's a new object
     * and in that case, change the image and so on.
     */
    public void updateLocation(CityObject tPoint) {
        boolean newTouchPoint = !tPoint.equals(closestCityObject);
        TextView distance = (TextView)rootView.findViewById(R.id.objectDistance);
        if (tPoint.isOnline() && firstTime){
            //BUGG, RESOURCE NOT FOUND
            distance.setText(getString(R.string.interact_text));
        }
        else if (!tPoint.isOnline()){
            distance.setText(tPoint.getDistance());
        }

        if (newTouchPoint){
            Picasso.with(getContext()).load(tPoint.getImgs().get(0)).transform(new CircleImageTransformation()).into(circleImage);
            TextView name = (TextView)rootView.findViewById(R.id.objectName);
            name.setText(tPoint.getName());
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
     * Function to check the dominant color of the background. We use this to determine what
     * color the text should be when the object is online.
     * @param bitmap image to check
     * @return the dominant color of the image
     */
    public static int getDominantColor(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        final int color = newBitmap.getPixel(0, 0);
        newBitmap.recycle();
        return color;
    }

    /**
     * sets a clickListener if the object is online. In that case, we set it to open a new chat
     * window.
     */
    private View.OnClickListener chattObjectListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            progressbar.setVisibility(View.INVISIBLE);
            Bundle args = new Bundle();
            args.putString("cityobject",closestCityObject.getImgs().get(0));
            args.putString("name", closestCityObject.getName());


            Intent intent = new Intent(getContext(), ChatActivity.class);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), circleImage,ViewCompat.getTransitionName(circleImage));
            intent.putExtras(args);
            startActivity(intent, options.toBundle());

        }
    };

    /**
     *
     * @param colorToInvert color to invert
     * @return black or white
     */
    public static int getContrastColor(int colorToInvert) {
        double y = (299 * Color.red(colorToInvert) + 587 * Color.green(colorToInvert) + 114 * Color.blue(colorToInvert)) / 1000;
        return y >= 128 ? Color.BLACK : Color.WHITE;
    }

    /**
     * Function to use when a city object goes online. Here we change the size of the circle, adds
     * a animation to it and draws a green stroke around it to make it look active.
     */
    public void zoomIn(){
        Animation backgroundAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        final ImageView background = (ImageView)rootView.findViewById(R.id.backgroundImage);

        Blur b = new Blur();
        Picasso.with(getContext()).load(closestCityObject.getImgs().get(0)).transform(b.getTransformation(getContext(), closestCityObject.getName())).into(background, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                int color = getDominantColor(((BitmapDrawable)background.getDrawable()).getBitmap());
                TextView name = (TextView)rootView.findViewById(R.id.objectName);
                TextView distance = (TextView)rootView.findViewById(R.id.objectDistance);
                oldColors =  distance.getTextColors();
                name.setTextColor(getContrastColor(color));
                distance.setTextColor(getContrastColor(color));

            }

            @Override
            public void onError() {

            }
        });

        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(circleContainer, "scaleX", 1.3f).setDuration(1000);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(circleContainer, "scaleY", 1.3f).setDuration(1000);

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleUpX).with(scaleUpY);

        scaleDown.start();

        isShown = true;
        firstTime = false;

        //rippleBackground.stopRippleAnimation();

        ObjectAnimator progressAnimator;
        progressAnimator = ObjectAnimator.ofInt(progressbar,"progress",500);
        progressAnimator.setDuration(3000);
        progressAnimator.start();
        background.startAnimation(backgroundAnimation);

        circleImage.setClickable(true);
        circleImage.setOnTouchListener(null);
        circleImage.setOnClickListener(chattObjectListener);



    }

    /**
     * Here we reset all the stuff we change when we call the zoomIn function.
     */
    public void zoomOut(){

        ObjectAnimator scaleBackX = ObjectAnimator.ofFloat(circleContainer, "scaleX", 1.0f).setDuration(1000);
        ObjectAnimator scaleBackY = ObjectAnimator.ofFloat(circleContainer, "scaleY", 1.0f).setDuration(1000);

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleBackX).with(scaleBackY);

        scaleDown.start();


        isShown = false;
        firstTime = true;

        progressbar.setVisibility(View.VISIBLE);
        progressbar.setProgress(10);
        progressbar.startAnimation(spin);
        circleImage.setClickable(false);
        circleImage.setOnClickListener(null);
        circleImage.setOnClickListener(clickListener);

        final Animation backgroundAnimation2 = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);

        TextView name = (TextView)rootView.findViewById(R.id.objectName);
        TextView distance = (TextView)rootView.findViewById(R.id.objectDistance);


        name.setTextColor(oldColors);
        distance.setTextColor(oldColors);


        backgroundAnimation2.setFillAfter(true);

        backgroundAnim.startAnimation(backgroundAnimation2);
    }
}
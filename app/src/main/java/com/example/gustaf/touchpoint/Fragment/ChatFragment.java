package com.example.gustaf.touchpoint.Fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gustaf.touchpoint.ChatActivity;
import com.example.gustaf.touchpoint.HelpClasses.Blur;
import com.example.gustaf.touchpoint.HelpClasses.CircleImageTransformation;
import com.example.gustaf.touchpoint.HelpClasses.CityObject;
import com.example.gustaf.touchpoint.R;
import com.squareup.picasso.Picasso;

/**
 *
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

    public View.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Animation vibrate = AnimationUtils.loadAnimation(getContext(), R.anim.vibrate);
            vibrate.setFillAfter(false);
            circleContainer.startAnimation(vibrate);
        }

    };

    /**
     *
     */
    public void updateLocation(CityObject tPoint) {
        boolean newTouchPoint = !tPoint.equals(closestCityObject);
        TextView distance = (TextView)rootView.findViewById(R.id.objectDistance);
        distance.setText(tPoint.getDistance());
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
     *
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


    public void zoomIn(){
        Animation backgroundAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        final ImageView background = (ImageView)rootView.findViewById(R.id.backgroundImage);

        Blur b = new Blur();
        Picasso.with(getContext()).load(closestCityObject.getImgs().get(0)).transform(b.getTransformation(getContext(), closestCityObject.getName())).into(background);

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

    public void zoomOut(){

        ObjectAnimator scaleBackX = ObjectAnimator.ofFloat(circleContainer, "scaleX", 1.0f).setDuration(1000);
        ObjectAnimator scaleBackY = ObjectAnimator.ofFloat(circleContainer, "scaleY", 1.0f).setDuration(1000);

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleBackX).with(scaleBackY);

        scaleDown.start();


        isShown = false;
        firstTime = true;

        //rotatingImage.startAnimation(spin);
        progressbar.setVisibility(View.VISIBLE);
        progressbar.setProgress(10);
        progressbar.startAnimation(spin);
        circleImage.setClickable(false);
        circleImage.setOnClickListener(null);
        circleImage.setOnClickListener(clickListener);

        final Animation backgroundAnimation2 = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        Animation fadeandZoomOut = AnimationUtils.loadAnimation(getContext(), R.anim.fadeoutzoomout);


        backgroundAnimation2.setFillAfter(true);

        backgroundAnim.startAnimation(backgroundAnimation2);
    }
}
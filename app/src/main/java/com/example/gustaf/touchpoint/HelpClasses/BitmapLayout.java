package com.example.gustaf.touchpoint.HelpClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * This class is used to make the loading of the bitmap more dynamic. If the bitmap fails, we can
 * then set a backup image to load instead of the one we're getting from the server.
 */
public class BitmapLayout extends LinearLayout implements Target {

    public BitmapLayout(Context context) {
        super(context);
    }

    public BitmapLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BitmapLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        setBackground(new BitmapDrawable(getResources(), bitmap));
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        //Set your error drawable
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        //Set your placeholder
    }
}

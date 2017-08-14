package com.example.gustaf.touchpoint.HelpClasses;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

/**
 * CircleImageTransformation is used to transform the Picasso image to make it a circle as well
 * as cache the image so we don't have to reload it every time.
 */
public class CircleImageTransformation implements com.squareup.picasso.Transformation {

    /**
     *
     * @param source the image which will be cached
     * @return the image
     */
    @Override
    public Bitmap transform (final Bitmap source ) {
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

    /**
     *
     * @param bitmap Bitmap which will be transformed
     * @return the transformed image
     */
    private Bitmap getCircularBitmap(Bitmap bitmap) {
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

        float r;

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
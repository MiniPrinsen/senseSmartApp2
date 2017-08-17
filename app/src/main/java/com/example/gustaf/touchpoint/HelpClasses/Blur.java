package com.example.gustaf.touchpoint.HelpClasses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;


/**
 * This function is used to blur images. The background of chatFragment when the object is active
 * and the details page uses this function. This can take quite a lot of memory, so be careful
 * on how much usage this will have. The whole concept that made this function better than other
 * alternatives is that it scales down the image a lot, blurres each pixel and then scales up
 * the image again.
 */
public class Blur {
        private static final float BITMAP_SCALE = 0.1f;
        private static final float BLUR_RADIUS = 25f;


        @SuppressLint("NewApi")
        private static Bitmap blur(Context context, Bitmap image) {
            int width = Math.round(image.getWidth() * BITMAP_SCALE);
            int height = Math.round(image.getHeight() * BITMAP_SCALE);

            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height,
                    false);
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            RenderScript rs = RenderScript.create(context);
            ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs,
                    Element.U8_4(rs));
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
            theIntrinsic.setRadius(BLUR_RADIUS);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);

            width = Math.round(width/BITMAP_SCALE);
            height = Math.round(height/BITMAP_SCALE);

            outputBitmap = Bitmap.createScaledBitmap(outputBitmap, width, height,
                    false);
            return outputBitmap;
        }

        public BlurTransformation getTransformation(Context context, String key){
            return new BlurTransformation(context, key);
        }

        /**
         * Class for bluring transformations using Picasso. Caches the image
         * after transformation.
         */
        private class BlurTransformation implements com.squareup.picasso.Transformation {
            final Context context;
            private final String key;
            BlurTransformation(Context context, String key){
                super();
                this.context = context;
                this.key = key;
            }

            @Override
            public Bitmap transform ( final Bitmap source ) {
                Bitmap output = blur(context, source);

                if (source != output) {
                    source.recycle();
                }

                return output;
            }

            @Override
            public String key () {
                return key;
            }
        }



}


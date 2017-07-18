package com.aopanis.forgetmenot.helpers;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.widget.ImageView;

/**
 * Created by aopan on 7/13/2017.
 */

public class ImageScalingHelper {

    public static Point dimensionHelper(ImageView imageView, Bitmap bitmap) {
        final int actualHeight, actualWidth;
        final int imageViewHeight = imageView.getHeight(), imageViewWidth = imageView.getWidth();
        final int bitmapHeight = bitmap.getHeight(), bitmapWidth = bitmap.getWidth();
        if (imageViewHeight * bitmapWidth <= imageViewWidth * bitmapHeight) {
            actualWidth = bitmapWidth * imageViewHeight / bitmapHeight;
            actualHeight = imageViewHeight;
        } else {
            actualHeight = bitmapHeight * imageViewWidth / bitmapWidth;
            actualWidth = imageViewWidth;
        }

        return new Point(actualWidth, actualHeight);
    }

    public static float[] ratioHelper(Point actual, Bitmap bitmap){
        return new float[]{((float) actual.x)/bitmap.getWidth(), ((float) actual.y)/bitmap.getHeight()};
    }

}

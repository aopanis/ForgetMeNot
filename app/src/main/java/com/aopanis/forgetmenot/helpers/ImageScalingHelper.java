package com.aopanis.forgetmenot.helpers;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.widget.ImageView;

public class ImageScalingHelper {

    /**
     * Determine the actual dimensions of a bitmap within an ImageView
     * @param imageView the image view that contains the bitmap
     * @param bitmap the bitmap being displayed
     * @return The actual dimensions of the bitmap within the ImageView
     */
    public static Point dimensionHelper(ImageView imageView, Bitmap bitmap) {
        final int actualHeight, actualWidth;
        final int imageViewHeight = imageView.getHeight(), imageViewWidth = imageView.getWidth();
        final int bitmapHeight = bitmap.getHeight(), bitmapWidth = bitmap.getWidth();

        float xRatio = (float)imageViewWidth / (float)bitmapWidth;
        float yRatio = (float)imageViewHeight / (float)bitmapHeight;

        // If the ImageView is larger than the bitmap, then return the bitmap scaled up
        // to the ImageView size, otherwise, return scaled down
        float scaleToUse;
        if(xRatio > 1 && yRatio > 1) {
            scaleToUse = xRatio > yRatio ? xRatio : yRatio;
        } else {
            scaleToUse = xRatio < yRatio ? xRatio : yRatio;
        }

        actualWidth = (int)(bitmapWidth * scaleToUse);
        actualHeight = (int)(bitmapHeight * scaleToUse);

        return new Point(actualWidth, actualHeight);
    }

    public static float[] ratioHelper(Point actual, Bitmap bitmap) {
        return new float[]{((float) actual.x) / bitmap.getWidth(), ((float) actual.y) / bitmap.getHeight()};
    }

}

package com.aopanis.forgetmenot;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.tzutalin.dlib.Constants;
import com.tzutalin.dlib.FaceDet;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;



/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.aopanis.forgetmenot", appContext.getPackageName());
    }

    @Test
    public void faceDetector_Functions() {
        String input = "220px-Lenna.png";
        Bitmap image = BitmapFactory.decodeFile(input);
        FaceDet detector = new FaceDet(Constants.getFaceShapeModelPath());
        Log.i("TST OUT", detector.detect(image).toString());
    }
}

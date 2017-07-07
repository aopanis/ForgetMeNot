import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import com.tzutalin.dlib.FaceDet;

import java.io.File;

public class faceDetectorTest {

    @Test
    public void faceDetector_Functions() {
        String input = "C:/Users/aopan/AndroidStudioProjects/ForgetMeNot/dlib/src/test/testRes/220px-Lenna.png";
//        Bitmap image = BitmapFactory.decodeFile(input);
        FaceDet detector = new FaceDet(input);

    }

}

package com.aopanis.forgetmenot.controllers;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aopanis.forgetmenot.R;
import com.aopanis.forgetmenot.helpers.ImageScalingHelper;
import com.aopanis.forgetmenot.models.GalleryImage;
import com.tzutalin.dlib.Constants;
import com.tzutalin.dlib.FaceDet;
import com.tzutalin.dlib.VisionDetRet;

import java.io.IOException;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class ImageActivity extends AppCompatActivity {

    private static final String TAG = "ImageActivity";
    private GalleryImage displayedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ImageView imageView = (ImageView) this.findViewById(R.id.imageActionView);
        this.displayedImage = this.getIntent().getParcelableExtra(GalleryActivity.IMAGE_EXTRA);
        imageView.setImageURI(displayedImage.getUri());

//        RequestBuilder<Bitmap> requestBuilder = Glide.with(this).asBitmap()
//                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE));
//        requestBuilder.load(this.displayedImage.getUri()).into(imageView);

        ImageActivityPermissionsDispatcher.DetectFacesWithCheck(this);
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void DetectFaces() {
        new AsyncDetectFaces(this).execute(this.displayedImage.getUri());
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void showDeniedForReadExternalStorage() {
        Toast.makeText(this, R.string.gallery_read_external_storage_denied, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    protected class AsyncDetectFaces extends AsyncTask<Uri, Integer, List<VisionDetRet>> {

        private ImageActivity activity;
        private Bitmap bitmap = null;
        private Bitmap tempBm;

        public AsyncDetectFaces(ImageActivity activity) {
            this.activity = activity;
        }

        @Override
        protected List<VisionDetRet> doInBackground(Uri... params) {
            FaceDet detector = new FaceDet(Constants.getFaceShapeModelPath());

            String path = getRealPathFromURI(params[0]);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Paint p = new Paint();
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(10);
            p.setARGB(255,255,255,255);
            tempBm = bitmap.copy(bitmap.getConfig(), true);
            Canvas canvas = new Canvas(tempBm);

            List<VisionDetRet> rets = detector.detect(path);
            for(int i=0; i < rets.size(); i++) {
                VisionDetRet temp = rets.get(i);
                canvas.drawRect(temp.getLeft(), temp.getTop(), temp.getRight(), temp.getBottom(), p);
            }

            Log.i(TAG, Integer.toString(rets.get(0).getBottom()));
            return rets;
        }

        @Override
        public void onProgressUpdate(Integer... value) {

        }

        @Override
        protected void onPostExecute(List<VisionDetRet> result) {
            RelativeLayout imageLayout = (RelativeLayout) activity.findViewById(R.id.imageActionLayout);
            ImageView imageView = (ImageView) activity.findViewById(R.id.imageActionView);
            imageView.setImageBitmap(tempBm);

            Point actualDimensions = ImageScalingHelper.dimensionHelper(imageView, tempBm);
            float[] ratios = ImageScalingHelper.ratioHelper(actualDimensions, tempBm);

            float xRatio = ratios[0];
            float yRatio = ratios[1];

            for(int i=0; i<result.size(); i++) {
                Button b = new Button(activity);
                b.setX(xRatio * result.get(i).getLeft());
                b.setY(yRatio * result.get(i).getTop());
                int width = (int) Math.ceil(xRatio * (result.get(i).getRight()-result.get(i).getLeft()));
                int height = (int) Math.ceil(yRatio * (result.get(i).getBottom()-result.get(i).getTop()));
                b.setMinimumHeight(0);
                b.setMinimumWidth(0);
                b.setWidth(width);
                b.setHeight(height);
                b.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v){
                        Intent intent = new Intent(activity, PersonActivity.class);
                        startActivity(intent);
                    }
                });
                imageLayout.addView(b);
            }

            Toast.makeText(activity, "Done", Toast.LENGTH_SHORT).show();

        }
    }
}
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
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.media.ExifInterface;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aopanis.forgetmenot.R;
import com.aopanis.forgetmenot.helpers.FileHelper;
import com.aopanis.forgetmenot.helpers.GPSHelper;
import com.aopanis.forgetmenot.helpers.ImageScalingHelper;
import com.aopanis.forgetmenot.models.GalleryImage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.tzutalin.dlib.Constants;
import com.tzutalin.dlib.FaceDet;
import com.tzutalin.dlib.VisionDetRet;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class ImageActivity extends AppCompatActivity {

    private static final String TAG = "ImageActivity";
    private GalleryImage displayedImage;

    private static int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //ImageView imageView = (ImageView) this.findViewById(R.id.imageActionView);
        this.displayedImage = this.getIntent().getParcelableExtra(GalleryActivity.IMAGE_EXTRA);
/*
        RequestBuilder<Bitmap> requestBuilder = Glide.with(this).asBitmap()
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE));
        requestBuilder.load(this.displayedImage.getUri()).into(imageView);
*/
        FloatingActionButton button = (FloatingActionButton) this.findViewById(R.id.tagLocationButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchTagActivity();
            }
        });

        ImageActivityPermissionsDispatcher.DetectFacesWithCheck(this);
    }

    public void LaunchTagActivity() {
        ImageActivityPermissionsDispatcher.TagLocationWithCheck(this);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void TagLocation() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        // If the image already has location data, then set the initial bounds to
        // the location that it already contains with radius of 2000
        if (!this.displayedImage.getLongitude().isNaN() &&
                !this.displayedImage.getLatitude().isNaN()) {
            builder.setLatLngBounds(GPSHelper.toBounds(this.displayedImage.getLatitude(),
                    this.displayedImage.getLongitude(), 1000));
        }

        try {
            this.startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                this.displayedImage.setLatitude(place.getLatLng().latitude, this);
                this.displayedImage.setLongitude(place.getLatLng().longitude, this);

                ExifInterface exif = this.displayedImage.getExif(this);
                Log.d(TAG, exif.toString());
            }
        }
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

    protected class AsyncDetectFaces extends AsyncTask<Uri, Integer, List<VisionDetRet>> {

        private ImageActivity activity;
        private Bitmap bitmap = null;
        private Bitmap tempBm;

        public AsyncDetectFaces(ImageActivity activity) {
            this.activity = activity;
        }

        @Override
        protected List<VisionDetRet> doInBackground(Uri... params) {
            long sTime;
            long eTime;
            long diff;

            sTime = System.nanoTime();
            FaceDet detector = new FaceDet(Constants.getFaceShapeModelPath());
            eTime = System.nanoTime();
            diff = eTime - sTime;
            Log.i(TAG, "Loading Face Detector: " + diff / 1000000);

            sTime = System.nanoTime();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String path = FileHelper.getRealPathFromURI(displayedImage.getUri(), this.activity);
            eTime = System.nanoTime();
            diff = eTime - sTime;
            Log.i(TAG, "Loading Bitmap: " + diff / 1000000);

            sTime = System.nanoTime();
            Paint p = new Paint();
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(10);
            p.setARGB(255, 255, 255, 255);
            tempBm = bitmap.copy(bitmap.getConfig(), true);
            Canvas canvas = new Canvas(tempBm);
            eTime = System.nanoTime();
            diff = eTime - sTime;
            Log.i(TAG, "Loading Paint and Canvas: " + diff / 1000000);
/*
            // TODO: Scale down bitmap before detecting
            int bW = bitmap.getWidth();
            int bH = bitmap.getHeight();
            float scale = 0.0f;
            if(bW > 500 && bH > 500) {
                    scale = bW < bH ? 500f / bW : 500f / bH;
            }
            this.bitmap = Bitmap.createScaledBitmap(bitmap, (int)(bW * scale), (int)(bH * scale), true);*/

            sTime = System.nanoTime();
            List<VisionDetRet> rects = detector.detect(path);
            eTime = System.nanoTime();
            diff = eTime - sTime;
            Log.i(TAG, "Detecting Rects: " + diff / 1000000);

            sTime = System.nanoTime();
            for (int i = 0; i < rects.size(); i++) {
                VisionDetRet temp = rects.get(i);
                canvas.drawRect(temp.getLeft(), temp.getTop(),
                        temp.getRight(), temp.getBottom(), p);/*
                canvas.drawRect(temp.getLeft() * scale, temp.getTop() * scale,
                        temp.getRight() * scale, temp.getBottom() * scale, p);*/
            }
            eTime = System.nanoTime();
            diff = eTime - sTime;
            Log.i(TAG, "Drawing Rects: " + diff / 1000000);

            //Log.i(TAG, Integer.toString(rects.get(0).getBottom()));
            return rects;
        }

        @Override
        public void onProgressUpdate(Integer... value) {

        }

        @Override
        protected void onPostExecute(List<VisionDetRet> result) {
            ConstraintLayout imageLayout = (ConstraintLayout) activity.findViewById(R.id.imageActionLayout);
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.imageActionView);
            imageView.setImageBitmap(tempBm);

            Point actualDimensions = ImageScalingHelper.dimensionHelper(imageView, tempBm);
            float[] ratios = ImageScalingHelper.ratioHelper(actualDimensions, tempBm);

            float xRatio = ratios[0];
            float yRatio = ratios[1];
            float heightMargin = ((float)imageView.getHeight() - actualDimensions.y) / 2;
            float widthMargin = ((float)imageView.getWidth() - actualDimensions.x) / 2;

            for (int i = 0; i < result.size(); i++) {
                Button b = new Button(activity);
                b.setMinimumHeight(0);
                b.setMinimumWidth(0);
                b.setX(widthMargin + (xRatio * result.get(i).getLeft()));
                b.setY(heightMargin + (yRatio * result.get(i).getTop()));
                int width = (int) Math.ceil(xRatio * (result.get(i).getRight() - result.get(i).getLeft()));
                int height = (int) Math.ceil(yRatio * (result.get(i).getBottom() - result.get(i).getTop()));
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                        width, height);
                params.setMargins(0, 0, 0, 0);
                b.setLayoutParams(params);
                b.setWidth(width);
                b.setHeight(height);
                b.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
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

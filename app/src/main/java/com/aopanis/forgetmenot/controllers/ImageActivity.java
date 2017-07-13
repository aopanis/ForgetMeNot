package com.aopanis.forgetmenot.controllers;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.media.ExifInterface;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.aopanis.forgetmenot.R;
import com.aopanis.forgetmenot.helpers.GPSHelper;
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

        ImageView imageView = (ImageView) this.findViewById(R.id.imageActionView);
        this.displayedImage = this.getIntent().getParcelableExtra(GalleryActivity.IMAGE_EXTRA);

        RequestBuilder<Bitmap> requestBuilder = Glide.with(this).asBitmap()
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE));
        requestBuilder.load(this.displayedImage.getUri()).into(imageView);

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
        if(!this.displayedImage.getLongitude().isNaN() &&
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

        public AsyncDetectFaces(ImageActivity activity) {
            this.activity = activity;
        }

        @Override
        protected List<VisionDetRet> doInBackground(Uri... params) {
            Bitmap bitmap = null;
            try {
                MediaStore.Images.Media.getBitmap(getContentResolver(), params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            FaceDet detector = new FaceDet(Constants.getFaceShapeModelPath());
            String path = params[0].getPath();
            List<VisionDetRet> rets = detector.detect(path);
            Log.i(TAG, rets.toString());
            return rets;
        }

        @Override
        public void onProgressUpdate(Integer... value) {

        }

        @Override
        protected void onPostExecute(List<VisionDetRet> result) {

        }
    }
}
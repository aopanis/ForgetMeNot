package com.aopanis.forgetmenot.controllers;

import android.Manifest;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.media.ExifInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.aopanis.forgetmenot.R;
import com.aopanis.forgetmenot.adapters.ImageGalleryAdapter;
import com.aopanis.forgetmenot.models.GalleryImage;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class GalleryActivity extends AppCompatActivity {

    public static final String TAG = "ImageGallery";
    public static final String IMAGE_EXTRA = "IMAGE_EXTRA";

    private RecyclerView recyclerView;
    private ImageGalleryAdapter imageGalleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        GalleryActivityPermissionsDispatcher.loadImagesWithCheck(this);

        // Retrieve reference to the RecyclerView
        this.recyclerView = (RecyclerView) this.findViewById(R.id.imageGallery);
        this.recyclerView.setHasFixedSize(true);
        // TODO: Replace number of columns with a setting
        this.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        this.imageGalleryAdapter = new ImageGalleryAdapter(Glide.with(this), this);
        this.recyclerView.setAdapter(this.imageGalleryAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.imageGalleryAdapter.notifyDataSetChanged();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        int imageCount = this.imageGalleryAdapter.getItemCount();
        GalleryImage[] images = new GalleryImage[imageCount];

        for(int i = 0; i < imageCount; i++) {
            images[i] = this.imageGalleryAdapter.getImage(i);
        }

        return images;
    }

    public void AddImage(GalleryImage... images) {
        for(int i = 0; i < images.length; i++) {
            this.imageGalleryAdapter.AddImage(images[i]);
        }

        this.imageGalleryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        GalleryActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void loadImages() {
        final Object data = this.getLastCustomNonConfigurationInstance();

        if(data == null) {
            new AsyncLoadImages(this).execute();
        }
        else {
            GalleryImage[] images = (GalleryImage[]) data;

            if(images.length == 0) {
                new AsyncLoadImages(this).execute();
            }
            else {
                this.AddImage(images);

                // Load any leftover images
                // Get an array containing the image ID column that we want
                String[] projection = { MediaStore.Images.Thumbnails._ID };
                // Create a cursor pointing to the images
                Cursor cursor = getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        null,
                        null,
                        null);

                int size = cursor.getCount();

                if(size > images.length) {
                    new AsyncLoadImages(this, images.length - 1).execute();
                }
            }
        }
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void showRationaleForReadExternalStorage(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.gallery_read_external_storage_rationale)
                .setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { request.proceed(); }
                })
                .setNegativeButton(R.string.deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { request.cancel(); }
                })
                .show();
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void showDeniedForReadExternalStorage() {
        Toast.makeText(this, R.string.gallery_read_external_storage_denied, Toast.LENGTH_SHORT).show();
    }

    protected class AsyncLoadImages extends AsyncTask<Object, GalleryImage, Object> {

        private GalleryActivity activity;
        private int startPos = -1;

        public AsyncLoadImages(GalleryActivity activity) {
            this(activity, -1);
        }
        public AsyncLoadImages(GalleryActivity activity, int startPos) {
            this.activity = activity;
            this.startPos = startPos;
        }

        @Override
        protected Object doInBackground(Object... params) {

            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            // Get an array containing the image ID column that we want
            String[] projection = { MediaStore.Images.Media._ID, MediaStore.Images.Media.LATITUDE,
                    MediaStore.Images.Media.LONGITUDE };
            // Create a cursor pointing to the images
            Cursor cursor = getContentResolver().query(
                    uri,
                    projection,
                    null,
                    null,
                    null);

            int idIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int latIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.LATITUDE);
            int lonIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.LONGITUDE);
            int size = cursor.getCount();

            // If size is zero, there are no images
            // TODO: Implement "no images to display" dialogue
            if(size == 0) {
                Log.d(TAG, "No images to display");
            }

            int imageId;
            double imageLongitude;
            double imageLatitude;

            // If we are not starting from the beginning, move to the position to start from
            if(startPos != -1) {
                cursor.moveToPosition(cursor.getCount() - startPos);
            }
            else {
                cursor.moveToPosition(cursor.getCount());
            }


            while(cursor.moveToPrevious()) {
                Uri imageUri;

                imageId = cursor.getInt(idIndex);
                imageLatitude = cursor.getDouble(latIndex);
                imageLongitude = cursor.getDouble(lonIndex);
                if(imageLatitude == 0.0 && imageLongitude == 0.0) {
                    imageLatitude = Double.NaN;
                    imageLongitude = Double.NaN;
                }
                // Get the image ID based off of the index retrieved earlier
                imageUri = uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + imageId);

                this.publishProgress(new GalleryImage(imageUri, imageLatitude, imageLongitude, imageId));
            }

            // Close the cursor
            cursor.close();

            return null;
        }

        @Override
        public void onProgressUpdate(GalleryImage... value) {
            this.activity.AddImage(value);
        }

        @Override
        protected void onPostExecute(Object result) {
            // TODO: Set some "Loading Images" dialogue visibility to false
        }
    }
}
package com.aopanis.forgetmenot.controllers;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.aopanis.forgetmenot.R;
import com.aopanis.forgetmenot.adapters.ImageGalleryAdapter;
import com.aopanis.forgetmenot.helpers.Helpers;
import com.bumptech.glide.Glide;

public class GalleryActivity extends AppCompatActivity{

    public static final String TAG = "ImageGallery";

    private RecyclerView recyclerView;
    private ImageGalleryAdapter imageGalleryAdapter;

    private static final int PERMISSIONS_REQUEST = 123;
    private static final String[] PERMISSIONS = { Manifest.permission.READ_EXTERNAL_STORAGE };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // Check for permissions
        this.checkMultiplePermissions();

        // Retrieve reference to the RecyclerView
        this.recyclerView = (RecyclerView) this.findViewById(R.id.imageGallery);
        this.recyclerView.setHasFixedSize(true);
        // TODO: Replace number of columns with a setting
        this.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        this.imageGalleryAdapter = new ImageGalleryAdapter(Glide.with(this));
        this.recyclerView.setAdapter(this.imageGalleryAdapter);

        //this.loadImages();
    }

    private void checkMultiplePermissions() {

        if(!Helpers.HasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST);
        }
        else {
            this.loadImages();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    this.loadImages();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // PERMISSIONS this app might request
        }
    }

    private void loadImages() {
        final Object data = this.getLastCustomNonConfigurationInstance();

        if(data == null) {
            new AsyncLoadImages(this).execute();
        }
        else {
            Uri[] uris = (Uri[]) data;

            if(uris.length == 0) {
                new AsyncLoadImages(this).execute();
            }
            else {
                this.AddImage(uris);

                // Load any leftover images
                // Get an array containing the image ID column that we want
                String[] projection = { MediaStore.Images.Thumbnails._ID };
                // Create a cursor pointing to the images
                Cursor cursor = getContentResolver().query(
                        MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                        projection,
                        null,
                        null,
                        null);

                int size = cursor.getCount();

                if(size > uris.length) {
                    new AsyncLoadImages(this, uris.length - 1).execute();
                }
            }
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        int imageCount = this.imageGalleryAdapter.getItemCount();
        Uri[] uris = new Uri[imageCount];

        for(int i = 0; i < imageCount; i++) {
            uris[i] = this.imageGalleryAdapter.GetUri(i);
        }

        return uris;
    }

    public void AddImage(Uri... uri) {
        for(int i = 0; i < uri.length; i++) {
            this.imageGalleryAdapter.AddImage(uri[i]);
        }

        this.imageGalleryAdapter.notifyDataSetChanged();
    }

    protected class AsyncLoadImages extends AsyncTask<Object, Uri, Object> {

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

            Log.d(TAG, "Initialized variables");

            // Get an array containing the image ID column that we want
            String[] projection = { MediaStore.Images.Media._ID };
            // Create a cursor pointing to the images
            Cursor cursor = getContentResolver().query(
                    uri,
                    projection,
                    null,
                    null,
                    null);

            int columnIndex = cursor.getColumnIndexOrThrow( MediaStore.Images.Media._ID );
            int size = cursor.getCount();

            Log.d(TAG, "Loaded images");

            // If size is zero, there are no images
            // TODO: Implement "no images to display" dialogue
            if(size == 0) {
                Log.d(TAG, "No images to display");
            }

            int imageId;

            // If we are not starting from the beginning, move to the position to start from
            if(startPos != -1) {
                cursor.moveToPosition(cursor.getCount() - startPos);
            }
            else {
                cursor.moveToPosition(cursor.getCount());
            }

            while(cursor.moveToPrevious()) {
                imageId = cursor.getInt(columnIndex);
                // Get the image ID based off of the index retrieved earlier
                this.publishProgress(uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + imageId));
            }

            // Close the cursor
            cursor.close();

            return null;
        }

        @Override
        public void onProgressUpdate(Uri... value) {
            this.activity.AddImage(value);
        }

        @Override
        protected void onPostExecute(Object result) {
            // TODO: Set some "Loading Images" dialogue visibility to false
        }
    }
}

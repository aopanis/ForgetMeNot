package com.aopanis.forgetmenot.controllers;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.aopanis.forgetmenot.R;
import com.aopanis.forgetmenot.adapters.ImageGalleryAdapter;
import com.aopanis.forgetmenot.helpers.Permission;
import com.aopanis.forgetmenot.helpers.PermissionsHelper;
import com.bumptech.glide.Glide;

public class GalleryActivity extends AppCompatActivity{

    public static final String TAG = "ImageGallery";

    private RecyclerView recyclerView;
    private ImageGalleryAdapter imageGalleryAdapter;

    private static final int requestCode = 100;
    private static final Permission[] permissions = {
            Permission.PERMISSION_READ_EXTERNAL_STORAGE,
            Permission.PERMISSION_WRITE_EXTERNAL_STORAGE,
            Permission.PERMISSION_CAMERA };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // Check for permissions
        this.checkPermissions();

        // Retrieve reference to the RecyclerView
        this.recyclerView = (RecyclerView) this.findViewById(R.id.imageGallery);
        this.recyclerView.setHasFixedSize(true);
        // TODO: Replace number of columns with a setting
        this.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        this.imageGalleryAdapter = new ImageGalleryAdapter(Glide.with(this));
        this.recyclerView.setAdapter(this.imageGalleryAdapter);
    }

    private void checkPermissions() {
        if(!PermissionsHelper.HasPermissions(this, permissions)) {
            PermissionsHelper.RequestPermissions(this.findViewById(R.id.galleryActivity),
                    requestCode, this, permissions);
        }
        else {
            this.loadImages();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == requestCode) {
            for (int i = 0; i < permissions.length; i++) {
                switch (permissions[i]) {
                    case Manifest.permission.READ_EXTERNAL_STORAGE:
                        if(grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            this.loadImages();
                        }
                        break;
                }
            }
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
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
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
